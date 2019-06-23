package yatta.ast.pattern;

import yatta.ast.ExpressionNode;
import yatta.ast.expression.AliasNode;
import yatta.ast.expression.IdentifierNode;
import yatta.ast.expression.value.AnyValueNode;
import yatta.ast.expression.value.EmptySequenceNode;
import yatta.runtime.Sequence;
import com.oracle.truffle.api.frame.VirtualFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TailsHeadMatchPatternNode extends MatchNode {
  @Children
  public MatchNode[] headNodes;
  @Child
  public ExpressionNode tailsNode;

  public TailsHeadMatchPatternNode(ExpressionNode tailsNode, MatchNode[] headNodes) {
    this.headNodes = headNodes;
    this.tailsNode = tailsNode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TailsHeadMatchPatternNode that = (TailsHeadMatchPatternNode) o;
    return Objects.equals(headNodes, that.headNodes) &&
        Objects.equals(tailsNode, that.tailsNode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(headNodes, tailsNode);
  }

  @Override
  public String toString() {
    return "TailsHeadMatchPatternNode{" +
        "headNode=" + headNodes +
        ", tailsNode=" + tailsNode +
        '}';
  }

  @Override
  public MatchResult match(Object value, VirtualFrame frame) {
    if (value instanceof Sequence) {
      Sequence sequence = (Sequence) value;
      List<AliasNode> aliases = new ArrayList<>();

      if (headNodes.length > sequence.length()) {
        return MatchResult.FALSE;
      }

      if (sequence.length() > 0) {
        for (int i = headNodes.length - 1; i >= 0; i--) {
          MatchNode headNode = headNodes[i];
          MatchResult headMatches = headNode.match(sequence.last(), frame);
          if (headMatches.isMatches()) {
            for (AliasNode aliasNode : headMatches.getAliases()) {
              aliases.add(aliasNode);
            }
            sequence = sequence.removeLast();
          } else {
            return MatchResult.FALSE;
          }
        }

        // Yatta.g4: tails : identifier | emptySequence | underscore ;
        if (tailsNode instanceof IdentifierNode) {
          IdentifierNode identifierNode = (IdentifierNode) tailsNode;

          if (identifierNode.isBound(frame)) {
            Sequence identifierValue = (Sequence) identifierNode.executeGeneric(frame);

            if (!Objects.equals(identifierValue, sequence)) {
              return MatchResult.FALSE;
            }
          } else {
            aliases.add(new AliasNode(identifierNode.name(), new AnyValueNode(sequence)));
          }
        } else if (tailsNode instanceof EmptySequenceNode) {
          if (sequence.length() > 0) {
            return MatchResult.FALSE;
          }
        }

        for (AliasNode aliasNode : aliases) {
          aliasNode.executeGeneric(frame);
        }

        return MatchResult.TRUE;
      }
    }

    return MatchResult.FALSE;
  }
}