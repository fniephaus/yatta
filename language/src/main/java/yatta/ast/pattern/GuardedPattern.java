package yatta.ast.pattern;

import com.oracle.truffle.api.frame.VirtualFrame;
import yatta.ast.ExpressionNode;
import yatta.ast.expression.AliasNode;
import yatta.ast.expression.ConditionNode;
import yatta.ast.expression.ThrowNode;

import java.util.Objects;

public class GuardedPattern extends ExpressionNode implements PatternMatchable {
  @Child
  public MatchNode matchExpression;
  @Child
  public ConditionNode conditionNode;

  public GuardedPattern(MatchNode matchExpression, ExpressionNode guardExpression, ExpressionNode valueExpression) {
    this.matchExpression = matchExpression;
    this.conditionNode = new ConditionNode(guardExpression, valueExpression, new ThrowNode(MatchControlFlowException.INSTANCE));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GuardedPattern that = (GuardedPattern) o;
    return Objects.equals(matchExpression, that.matchExpression) &&
        Objects.equals(conditionNode, that.conditionNode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(matchExpression, conditionNode);
  }

  @Override
  public String toString() {
    return "GuardedPattern{" +
        "matchExpression=" + matchExpression +
        ", conditionNode=" + conditionNode +
        '}';
  }

  @Override
  public void setIsTail(boolean isTail) {
    super.setIsTail(isTail);
    conditionNode.setIsTail(isTail);
  }

  @Override
  public Object patternMatch(Object value, VirtualFrame frame) throws MatchControlFlowException {
    MatchResult matchResult = matchExpression.match(value, frame);
    if (matchResult.isMatches()) {
      for (AliasNode nameAliasNode : matchResult.getAliases()) {
        nameAliasNode.executeGeneric(frame);
      }
      return conditionNode.executeGeneric(frame);
    } else {
      throw MatchControlFlowException.INSTANCE;
    }
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return null;
  }
}
