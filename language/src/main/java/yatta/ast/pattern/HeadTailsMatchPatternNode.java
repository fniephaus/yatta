package yatta.ast.pattern;

import com.oracle.truffle.api.frame.VirtualFrame;
import yatta.ast.ExpressionNode;
import yatta.ast.expression.AliasNode;
import yatta.ast.expression.IdentifierNode;
import yatta.ast.expression.value.AnyValueNode;
import yatta.ast.expression.value.EmptySequenceNode;
import yatta.runtime.Seq;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class HeadTailsMatchPatternNode extends MatchNode {
  @Children
  public MatchNode headNodes[];
  @Child
  public ExpressionNode tailsNode;

  public HeadTailsMatchPatternNode(MatchNode headNodes[], ExpressionNode tailsNode) {
    this.headNodes = headNodes;
    this.tailsNode = tailsNode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HeadTailsMatchPatternNode that = (HeadTailsMatchPatternNode) o;
    return Objects.equals(headNodes, that.headNodes) &&
        Objects.equals(tailsNode, that.tailsNode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(headNodes, tailsNode);
  }

  @Override
  public String toString() {
    return "HeadTailsMatchPatternNode{" +
        "headNodes=" + headNodes +
        ", tailsNode=" + tailsNode +
        '}';
  }

  @Override
  public MatchResult match(Object value, VirtualFrame frame) {
    if (value instanceof Seq) {
      Seq sequence = (Seq) value;
      List<AliasNode> aliases = new ArrayList<>();

      if (headNodes.length > sequence.length()) {
        return MatchResult.FALSE;
      }

      if (sequence.length() > 0) {
        for (int i = 0; i < headNodes.length; i++) {
          MatchNode headNode = headNodes[i];
          MatchResult headMatches = headNode.match(sequence.first(this), frame);
          if (headMatches.isMatches()) {
            for (AliasNode aliasNode : headMatches.getAliases()) {
              aliases.add(aliasNode);
            }
            sequence = sequence.removeFirst(this);
          } else {
            return MatchResult.FALSE;
          }
        }

        // YattaParser.g4: tails : identifier | emptySequence | underscore ;
        if (tailsNode instanceof IdentifierNode) {
          IdentifierNode identifierNode = (IdentifierNode) tailsNode;

          if (identifierNode.isBound(frame)) {
            Seq identifierValue = (Seq) identifierNode.executeGeneric(frame);

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
