package org.mwg.mlx.algorithm.ruleinference;

import org.mwg.*;
import org.mwg.mlx.algorithm.ruleinference.nodes.*;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.NodeState;
import org.mwg.task.Task;
import org.mwg.task.TaskResult;
import org.mwg.utility.Enforcer;

import java.util.ArrayList;
import java.util.List;

import static org.mwg.task.Actions.lookup;
import static org.mwg.task.Actions.setWorld;

/**
 * Created by andrey.boytsov on 24/10/2016.
 *
 * This is a rule node for rule engine.
 *
 * Works like that:
 * - retrieves the values necessary for calculating the rule
 * - calculates the value of the rule (triggered or not)
 * - If the rule was just triggered, execute the command
 * - If the rule was just untriggered or did not change, do nothing.
 */
public class RuleNode extends AbstractNode {

    public static final String NAME = "RuleNode";

    /**
     * Attribute key - condition (in string format)
     */
    public static final String INTERNAL_CONDITION_STRING = "_condition";

    /**
     * Default condition: never triggered.
     */
    public static final String INTERNAL_CONDITION_STRING_DEF = "False";

    /**
     * Attribute key - command (in string format)
     */
    public static final String INTERNAL_COMMAND_STRING = "_command";

    /**
     * Attribute key: Whether rule is activated
     */
    public static final String RULE_ACTIVATED_KEY = "activated";

    /**
     * Attribute default: whether rule is activated.
     */
    public static final Boolean RULE_ACTIVATED_DEF = true;

    private static final Enforcer enforcer = new Enforcer()
            .asString(INTERNAL_COMMAND_STRING)
            .asString(INTERNAL_CONDITION_STRING)
            .asBool(RULE_ACTIVATED_KEY);

    @Override
    public void setProperty(String propertyName, byte propertyType, Object propertyValue) {
        enforcer.check(propertyName, propertyType, propertyValue);
        //TODO exception check? Revert if failed?
        if (INTERNAL_CONDITION_STRING.equals(propertyName)) {
            initializeCondition(propertyValue.toString());
            ruleTriggered(); //Don't care about the results, just test it
        }else if (INTERNAL_COMMAND_STRING.equals(propertyName)){
            initializeCommand(propertyValue.toString());
            ruleTriggered(); //Again, just test it
        }else if (RULE_ACTIVATED_KEY.equals(propertyName) && (propertyValue.equals(true))){
            ruleTriggered();
        }
        super.setProperty(propertyName, propertyType, propertyValue);
    }

    //TODO Validation? Assertion?

    //TODO plusNode, minusNode, etc.

    //TODO detection/protection from flattering. If one rule action triggers another, then it triggers first again, then second, etc.

    /**
     * This rule node is the final condition node. Value determines whether rule is currently triggered (+1)
     * or not (-1).
     */
    private ConditionGraphNode finalNode = null;

    private final List<String> nodeIds = new ArrayList<>();
    private final List<String> nodeProperties = new ArrayList<>();
    private final List<Object> newValues = new ArrayList<>();
    private final List<Byte> types = new ArrayList<>();

    public void initializeCondition(String condition){
        finalNode = parseRuleCondition(condition);
    }

    public void initializeCommand(String command){
        nodeIds.clear();
        nodeProperties.clear();
        newValues.clear();
        types.clear();

        parseRuleCommand(command);
    }

    private void parseRuleCommand(String command){
        String cleanCommand = removeSurroundingBrackets(command);

        String andSplit[] = splitByOperation(cleanCommand, "&&");
        if (andSplit.length > 1){
            //Multiple commands
            for (int i=0;i<andSplit.length;i++){
                parseRuleCommand(andSplit[i]);
            }
            return ;
        }

        final String equalSplit[] = splitByOperation(cleanCommand, "=");
        if (equalSplit.length!=2) {
            //TODO Warning - command with syntax error is ignored
            return ;
        }
        String nodeAndPropertyStr = equalSplit[0].trim();
        if (nodeAndPropertyStr.startsWith("{") && nodeAndPropertyStr.endsWith("}")){
            //Using {-brackets is acceptable for command, but unnecessary
            nodeAndPropertyStr = nodeAndPropertyStr.substring(1,nodeAndPropertyStr.length()-1).trim();
        }
        final String nodeAndProperty[] = nodeAndPropertyStr.split("\\.");
        final String nodeID = nodeAndProperty[0];
        final String property = nodeAndProperty[1];
        byte type;
        Object value;

        //TODO More types: arrays, etc.

        String valueStr = equalSplit[1].trim();
        if (valueStr.startsWith("'") && valueStr.endsWith("'") && !valueStr.substring(1,valueStr.length()-1).contains("'")){
            //Value like this is guaranteed to be String

            //Value in '' is acceptable (and it is the only option if it contains &&, for example).
            //But, again, it is not required.
            value = valueStr.substring(1,valueStr.length()-1);
            type = Type.STRING;
        }else{
            //Might be not string
            if (valueStr.toLowerCase().equals("true")){
                type = Type.BOOL;
                value = new Boolean(true);
            }else if (valueStr.toLowerCase().equals("false")){
                type = Type.BOOL;
                value = new Boolean(false);
            }else {
                try{
                    value = new Double(valueStr);
                    type = Type.DOUBLE;
                }catch(NumberFormatException e){
                    value = valueStr;
                    type = Type.STRING;
                }
            }
        }

        nodeIds.add(nodeID);
        nodeProperties.add(property);
        newValues.add(value);
        types.add(type);
    }

    private synchronized void executeCommands(){
        //TODO Assert that all lists are of the same length ?
        Task preparedTask = setWorld(""+world()).setTime(""+ Constants.END_OF_TIME);
        for (int i=0;i<nodeIds.size();i++){
            final String nodeID = nodeIds.get(i);
            final String property = nodeProperties.get(i);
            final Object value = newValues.get(i);
            final byte type = types.get(i);
            TaskResult result = preparedTask.lookup(nodeID).executeSync(graph());
            if (result.size() > 0){
                Node resolvedNode = (Node) result.get(0);
                resolvedNode.jump(resolvedNode.lastModification(), result1 -> result1.setProperty(property, type, value));
            }
        }
    }

    /**
     * Test function, checks whether rule is triggered now.
     *
     * @return whether rule is triggered.
     */
    public final boolean ruleTriggered(){
        NodeState state = unphasedState();
        final String condition = state.getFromKeyWithDefault(INTERNAL_CONDITION_STRING, INTERNAL_CONDITION_STRING_DEF);
        final Object command = state.getFromKey(INTERNAL_COMMAND_STRING);

        if (state.getFromKeyWithDefault(RULE_ACTIVATED_KEY, RULE_ACTIVATED_DEF) == false){
            //Rule is deactivated. Deactivated rules never trigger.
            return false;
        }

        if (finalNode == null){
            //If node is not initialized (e.g. after phasing), then BOTH condition and command are not initialized
            initializeCondition(condition);
            if (command != null){
                initializeCommand(command.toString());
            }
        }
        boolean triggered = finalNode.getBooleanValue();
        if (triggered){
            //Commands not set? Whatever, you will just have empty list
            executeCommands();
        }
        return triggered;
    }

    public RuleNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    /**
     * Splits by operation, but does not count operations in brackets.
     * For example, string like "(A &amp;&amp; B || C) &amp;&amp; T &gt;= 30" and operation "&amp;&amp;" will be split into
     * array with 2 elements - "(A &amp;&amp; B || C)" and "T &gt;= 30"
     *
     * @param condition Condition string
     * @param operation Operation to split upon
     * @return Split string. If length == 1, it means that split has failed.
     */
    private String[] splitByOperation(String condition, String operation){
        List<String> res = new ArrayList<String>();
        StringBuilder curComponent = new StringBuilder();
        int bracketCounter = 0;
        boolean inQuotes = false;
        int lastIndex = 0;
        for (int i=0;i<condition.length()-operation.length();i++){
            if (condition.charAt(i) == '\''){
                inQuotes = !inQuotes;
            }
            if (!inQuotes){
                if (condition.charAt(i) == '('){
                    bracketCounter++;
                }
                if (condition.charAt(i) == ')'){
                    bracketCounter--;
                }
            }
            if ((!inQuotes)&&(bracketCounter == 0)&&(operation.equals(condition.substring(i, i+operation.length())))){
                //Got it. Save this component, start next one
                res.add(curComponent.toString());
                curComponent = new StringBuilder();
                i += operation.length(); //Shift by operation length, start after operation signs
            }else{
                //We are OK. Just go further
                curComponent.append(condition.charAt(i));
            }
            lastIndex = i; //Due to jumps of i keeping a track of last symbol can be tricky
        }
        if (lastIndex < condition.length()-1){
            curComponent.append(condition.substring(lastIndex+1, condition.length()));
        }
        if (curComponent.length() > 0){
            res.add(curComponent.toString());
        }
        return res.toArray(new String[0]);
    }

    /**
     * @param arg Any string.
     * @return Same string trimmed, with all surrounding brackets removed. String like "(  ( something ))" will
     * be transformed into "something". If string is not in brackets, or the number of brackets mismatch, or there
     * is something before first or after last bracket (other than spaces), then the string will remain unchanged.
     *
     * @throws NullPointerException if <code>arg<code/> is null.
     */
    private static String removeSurroundingBrackets(String arg){
        //If the thing starts with brackets, ends with bracket, and those brackets are the part of the same thing,
        // remove the brackets. Recursively if necessary.
        String result = arg.trim();
        boolean entirelyInBrackets = false;

        do{
            entirelyInBrackets = false;

            if (result.startsWith("(") && result.endsWith(")")){
                int bracketCounter = 0;
                for (int i=0;i<result.length();i++){
                    if (result.charAt(i) == '('){
                        bracketCounter++;
                    }
                    if (result.charAt(i) == ')'){
                        bracketCounter--;
                    }
                    if (bracketCounter==0){
                        if (i < result.length()-1){
                            entirelyInBrackets = false; //That starting bracket was somewhere else
                            break ;
                        }else{
                            //Only at the very end we reached bracketcounter of 0
                            //It means, we have something like ( someCondition ).
                            //Remove brackets and try again, we can have ( ( someCondition ) ), after all
                            entirelyInBrackets = true;
                        }

                    }
                }
            }

            if (entirelyInBrackets){
                //Removing the brackets
                result = result.substring(1, result.length()-1);
                //Again removing spaces from the beginning and the end
                result = result.trim();
            }
        }while(entirelyInBrackets);

        return result;
    }

    /**
     * Recursive rule condition parser.
     *
     * @param condition Condition string
     * @return Node corresponding to rule condition.
     */
    public ConditionGraphNode parseRuleCondition(String condition){
        String cleanCondition = removeSurroundingBrackets(condition);

        //Is it a large string constant?
        //Should begin and end with ', but not contain ' inbetween (i.e. string like 'abc' != 'def' should not be counted)
        if (cleanCondition.startsWith("'") && cleanCondition.endsWith("'") && !cleanCondition.substring(1,cleanCondition.length()-1).contains("'")){
            return new ConstantStringNode(cleanCondition.substring(1,cleanCondition.length()-1));
        }

        //Or and AND are executed last
        //It is important that OR goes before AND here
        String orSplit[] = splitByOperation(cleanCondition, "||");
        if (orSplit.length > 1){
            ConditionGraphNode conditions[] = new ConditionGraphNode[orSplit.length];
            for (int i=0;i<orSplit.length;i++){
                conditions[i] = parseRuleCondition(orSplit[i]);
            }
            return new OrNode(conditions);
        }
        String andSplit[] = splitByOperation(cleanCondition, "&&");
        if (andSplit.length > 1){
            ConditionGraphNode conditions[] = new ConditionGraphNode[andSplit.length];
            for (int i=0;i<andSplit.length;i++){
                conditions[i] = parseRuleCondition(andSplit[i]);
            }
            return new AndNode(conditions);
        }

        //TODO Allow chains like A >= B >= C < D ?
        //At first glance, not worth it. Same logic can be expressed like (A >= B)&&(B >= C)&&(C < D)

        //So, it is not a constant node. Looking for relation.
        String geSplit[] = splitByOperation(cleanCondition, ">=");
        if (geSplit.length==2){ //This operation allows only 2 arguments
            return new GreaterEqualsNode(parseRuleCondition(geSplit[0].trim()),
                    parseRuleCondition(geSplit[1].trim()));
        }
        String leSplit[] = splitByOperation(cleanCondition, "<=");
        if (leSplit.length == 2){
            return new LessEqualsNode(parseRuleCondition(leSplit[0].trim()),
                    parseRuleCondition(leSplit[1].trim()));
        }
        String gSplit[] = splitByOperation(cleanCondition, ">");
        if (gSplit.length == 2){
            return new GreaterNode(parseRuleCondition(gSplit[0].trim()),
                    parseRuleCondition(gSplit[1].trim()));
        }
        String lSplit[] = splitByOperation(cleanCondition, "<");
        if (lSplit.length == 2){
            return new LessNode(parseRuleCondition(lSplit[0].trim()),
                    parseRuleCondition(lSplit[1].trim()));
        }
        String eSplit[] = splitByOperation(cleanCondition, "==");
        if (eSplit.length == 2){
            return new EqualsNode(parseRuleCondition(eSplit[0].trim()),
                    parseRuleCondition(eSplit[1].trim()));
        }
        String neSplit[] = splitByOperation(cleanCondition, "!=");
        if (neSplit.length == 2){
            return new NotEqualsNode(parseRuleCondition(neSplit[0].trim()),
                    parseRuleCondition(neSplit[1].trim()));
        }

        //So, if we reached here, then it is not a formula. It is either constant node or
        //reference to attribute/derivative.
        if (cleanCondition.startsWith("{") && cleanCondition.endsWith("}")){
            //This is a value node.
            String valueInfo = cleanCondition.substring(1,cleanCondition.length()-1).trim();
            String idAndAttr[] = valueInfo.split("\\.");
            //TODO enforce 2-length values
            return new ValueNode(idAndAttr[0], idAndAttr[1], graph(), ""+world());
        }
        if (cleanCondition.startsWith("d{") && cleanCondition.endsWith("}")){
            //This is a derivative node.
            String valueInfo = cleanCondition.substring(2,cleanCondition.length()-1).trim();
            String idAndAttr[] = valueInfo.split("\\.");
            //TODO enforce 2-length values
            return new DerivativeNode(idAndAttr[0], idAndAttr[1], graph(), ""+world());
        }

        if (cleanCondition.startsWith("!")){
            String value = cleanCondition.substring(1,cleanCondition.length()).trim();
            return new NotNode(parseRuleCondition(value));
        }

        //If the value is "true", "false" (both - case insensitive) or numeric, it is a constant node
        if ("true".equals(cleanCondition.toLowerCase())){
            return new ConstantBooleanNode(true);
        }
        if ("false".equals(cleanCondition.toLowerCase())){
            return new ConstantBooleanNode(false);
        }
        try{
            //We don't expect the rule to change frequently, so exception is OK here
            double numValue = new Double(cleanCondition);
            return new ConstantDoubleNode(numValue);
        }catch (NumberFormatException e){
            //Do nothing. It can happen, it is normal
        }

        // Not a constant. Then, whatever we got, it is the name of other property, derivative, etc.
        //TODO Special command node (like "it is daytime")

        return new ConstantStringNode(cleanCondition); //TODO This is a stub. Do it properly.
    }

    /**
     * @param p_world World
     * @param p_time Time
     * @param p_id ID
     * @param p_graph Graph
     * @param condition Rule condition string
     * @param command Rule command string
     */
    public RuleNode(long p_world, long p_time, long p_id, Graph p_graph, String condition, String command) {
        super(p_world, p_time, p_id, p_graph);
        initializeCondition(condition);
        initializeCommand(command);
    }

}
