package org.mwg.mlx.algorithm.ruleinference;

import org.mwg.Graph;
import org.mwg.mlx.algorithm.ruleinference.nodes.*;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.NodeState;
import org.mwg.utility.Enforcer;

import java.util.ArrayList;
import java.util.List;

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
     * Default condition: bever triggered.
     */
    public static final String INTERNAL_CONDITION_STRING_DEF = "False";

    /**
     * Attribute key - command (in string format)
     */
    public static final String INTERNAL_COMMAND_STRING = "_command";

    /**
     * Default command: do nothing.
     */
    public static final String INTERNAL_COMMAND_STRING_DEF = "";

    private static final Enforcer enforcer = new Enforcer()
            .asString(INTERNAL_COMMAND_STRING)
            .asString(INTERNAL_CONDITION_STRING);

    @Override
    public void setProperty(String propertyName, byte propertyType, Object propertyValue) {
        enforcer.check(propertyName, propertyType, propertyValue);
        //TODO exception check? Revert if failed?
        if (INTERNAL_CONDITION_STRING.equals(propertyName)) {
            initializeCondition(propertyValue.toString());
        }else if (INTERNAL_COMMAND_STRING.equals(propertyName)){
            initializeCommand(propertyValue.toString());
        }
        super.setProperty(propertyName, propertyType, propertyValue);
    }

    //TODO Validation? Assertion?

    //TODO plusNode, minusNode, derivativeNode, etc.

    //TODO detection/protection from flattering. If one rule action triggers another, then it triggers first again, then second, etc.

    /**
     * This rule node is the final condition node. Value determines whether rule is currently triggered (+1)
     * or not (-1).
     */
    private ConditionGraphNode finalNode = null;

    public void initializeCondition(String condition){
        finalNode = parseRuleCondition(condition);
    }

    public void initializeCommand(String command){
        //TODO
    }

    /**
     * Test function, checks whether rule is triggered now.
     *
     * @return whether rule is triggered.
     */
    public boolean ruleTriggered(){
        if (finalNode == null){
            initializeCondition(unphasedState().getFromKeyWithDefault(INTERNAL_CONDITION_STRING, INTERNAL_CONDITION_STRING_DEF));
        }
        return finalNode.getValue() > 0;
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
    public static String[] splitByOperation(String condition, String operation){
        List<String> res = new ArrayList<String>();
        StringBuilder curComponent = new StringBuilder();
        int bracketCounter = 0;
        int lastIndex = 0;
        for (int i=0;i<condition.length()-operation.length();i++){
            if (condition.charAt(i) == '('){
                bracketCounter++;
            }
            if (condition.charAt(i) == ')'){
                bracketCounter--;
            }
            if ((bracketCounter == 0)&&(operation.equals(condition.substring(i, i+operation.length())))){
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
     * Recursive rule condition parser.
     *
     * @param condition Condition string
     * @return Node corresponding to rule condition.
     */
    public static ConditionGraphNode parseRuleCondition(String condition){
        String cleanCondition = condition.trim();
        //If it is in brackets, remove the brackets.

        //If the thing starts with brackets, ends with bracket, and those brackets are the part of the same thing,
        // remove the brackets. Recursively if necessary.
        boolean entirelyInBrackets = false;

        do{
            entirelyInBrackets = false;

            if (cleanCondition.startsWith("(") && cleanCondition.endsWith(")")){
                int bracketCounter = 0;
                for (int i=0;i<cleanCondition.length();i++){
                    if (cleanCondition.charAt(i) == '('){
                        bracketCounter++;
                    }
                    if (cleanCondition.charAt(i) == ')'){
                        bracketCounter--;
                    }
                    if (bracketCounter==0){
                        if (i < cleanCondition.length()-1){
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
                cleanCondition = cleanCondition.substring(1, cleanCondition.length()-1);
                //Again removing spaces from the beginning and the end
                cleanCondition = cleanCondition.trim();
            }
        }while(entirelyInBrackets);

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
        // reference to attribute/derivative.

        //If the value is "true", "false" (both - case insensitive) or numeric, it is a constant node
        try{
            //We don't expect the rule to change frequently, so exception is OK here
            double numValue = new Double(cleanCondition);
            return new ConstantNode(numValue);
        }catch (NumberFormatException e){
            //Do nothing. It can happen, it is normal
        }
        if ("true".equals(cleanCondition.toLowerCase())){
            return new ConstantNode(1.0);
        }
        if ("false".equals(cleanCondition.toLowerCase())){
            return new ConstantNode(-1.0);
        }

        // Not a constant. Then, whatever we got, it is the name of other property, derivative, etc.
        return new ConstantNode(1.0); //TODO This is a stub. Do it properly.
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

    //TODO Command (everything related)?

}
