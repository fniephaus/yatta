package abzu.ast.expression.value;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import abzu.ast.expression.ValueNode;

import java.util.Objects;

@NodeInfo
public final class IntegerNode extends ValueNode<Long> {
  public final long value;

  public IntegerNode(Long value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IntegerNode integerNode = (IntegerNode) o;
    return Objects.equals(value, integerNode.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "IntegerNode{" +
        "value=" + value +
        '}';
  }

  @Override
  public Long executeValue(VirtualFrame frame) {
    return value;
  }
}