package abzu.ast.pattern;

import com.oracle.truffle.api.frame.VirtualFrame;

public class UnderscoreMatchNode extends MatchNode {
  public static UnderscoreMatchNode INSTANCE = new UnderscoreMatchNode();

  private UnderscoreMatchNode() {
  }

  @Override
  public String toString() {
    return "UnderscoreMatchNode{}";
  }

  @Override
  public MatchResult match(Object value, VirtualFrame frame) {
    return new MatchResult(true);
  }
}
