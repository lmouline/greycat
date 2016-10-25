package ml.ruleinference;

import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.mlx.MLXPlugin;
import org.mwg.mlx.algorithm.ruleinference.RuleNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by andrey.boytsov on 24/10/2016.
 */
public class RuleNodeTest {

    public void checkRule(String condition, boolean expectedResult){
        final Graph graph = new GraphBuilder().withPlugin(new MLXPlugin()).withScheduler(new NoopScheduler()).build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                RuleNode ruleNode = (RuleNode) graph.newTypedNode(0, 0, RuleNode.NAME);

                ruleNode.set(RuleNode.INTERNAL_CONDITION_STRING, condition);

                assertEquals(expectedResult, ruleNode.ruleTriggered());

                ruleNode.free();
                graph.disconnect(null);
            }
        });
    }

    @Test
    public void testTrueTrigger() {
        checkRule("True", true);
    }

    @Test
    public void testFalseTrigger() {
        checkRule("False", false);
    }

    @Test
    public void testConstantTrueTrigger() {
        checkRule("1.1", true);
    }

    @Test
    public void testFalseConstantTrigger() {
        checkRule("-3", false);
    }

    @Test
    public void testZeroConstantFalseTrigger() {
        checkRule("0", false);
    }

    @Test
    public void testGreaterTrueTrigger() {
        checkRule("1 > 0", true);
    }

    @Test
    public void testGreaterFalseTrigger() {
        checkRule("0 > 1", false);
    }

    @Test
    public void testGreaterFalseEqualTrigger() {
        checkRule("1 > 1", false);
    }

    @Test
    public void testGreaterEqualsTrueTrigger() {
        checkRule("1 >= 0", true);
    }

    @Test
    public void testGreaterEqualsFalseTrigger() {
        checkRule("0 >= 1", false);
    }

    @Test
    public void testGreaterEqualsTrueEqualTrigger() {
        checkRule("1 >= 1", true);
    }

    @Test
    public void testLessTrueTrigger() {
        checkRule("1 < 2", true);
    }

    @Test
    public void testLessFalseTrigger() {
        checkRule("2 < 1", false);
    }

    @Test
    public void testLessFalseEqualTrigger() {
        checkRule("2 < 2", false);
    }

    @Test
    public void testLessEqualsTrueTrigger() {
        checkRule("1 <= 2", true);
    }

    @Test
    public void testLessEqualsFalseTrigger() {
        checkRule("2 <= 1", false);
    }

    @Test
    public void testLessEqualsTrueEqualTrigger() {
        checkRule("2 <= 2", true);
    }

    @Test
    public void testAndTTSimple(){
        checkRule("True && tRue", true);
    }

    @Test
    public void testAndTFSimple(){
        checkRule("true && false", false);
    }

    @Test
    public void testAndFTSimple(){
        checkRule("falSE && tRue", false);
    }

    @Test
    public void testAndFFSimple(){
        checkRule("False && false", false);
    }

    @Test
    public void testAndTTValues(){
        checkRule("1 && 2", true);
    }

    @Test
    public void testAndTFValues(){
        checkRule("1 && -1", false);
    }

    @Test
    public void testOrTTSimple(){
        checkRule("True || tRue", true);
    }

    @Test
    public void testOrTFSimple(){
        checkRule("true || false", true);
    }

    @Test
    public void testOrFTSimple(){
        checkRule("falSE || tRue", true);
    }

    @Test
    public void testOrFFSimple(){
        checkRule("False || false", false);
    }

    @Test
    public void testOrTTValues(){
        checkRule("1 || 2", true);
    }

    @Test
    public void testOrTFValues(){
        checkRule("-2 || -1", false);
    }

    @Test
    public void testNestedTTConjuncts(){
        checkRule("(2 < 3) && (1.5 >= 0.8)", true);
    }

    @Test
    public void testNestedTFConjuncts(){
        checkRule("(2 < 3) && (1.5 >= 1.8)", false);
    }

    @Test
    public void testMultipleTConjuncts(){
        checkRule("(2 < 3) && (1.5 >= 0.8) && true && (2 == 2)", true);
    }

    @Test
    public void testMultipleTConjunctsBracketsTrick(){
        //Should properly understand 2==2 without brackets
        checkRule("(2 < 3) && (1.5 >= 0.8) && true && 2 == 2", true);
    }

    @Test
    public void testEqualsTrue(){
        checkRule("1 == 1.0", true);
    }

    @Test
    public void testEqualsFalse(){
        checkRule("1 == 1.001", false);
    }

    @Test
    public void testNonEqualsFalse(){
        checkRule("1 != 1.0", false);
    }

    @Test
    public void testNonEqualsTrue(){
        checkRule("1 != 1.001", true);
    }

    @Test
    public void testBracketsTrue(){
        checkRule("( 1 != 1.001 )", true);
    }

    @Test
    public void testMultipleBracketsTrue() {
        checkRule("(( 1 != 1.001 ) ) ", true);
    }

    @Test
    public void testBracketsFalse(){
        checkRule("( 1 != 1.00 )", false);
    }

    @Test
    public void testMultipleBracketsFalse() {
        checkRule("(( 1 != 1.00 ) ) ", false);
    }

}
