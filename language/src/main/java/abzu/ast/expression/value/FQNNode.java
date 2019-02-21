package abzu.ast.expression.value;

import abzu.ast.ExpressionNode;
import abzu.ast.call.ModuleCacheNode;
import abzu.ast.call.ModuleCacheNodeGen;
import abzu.runtime.Module;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import java.util.Arrays;
import java.util.Objects;

@NodeInfo
public final class FQNNode extends ExpressionNode {
  @Child
  private ModuleCacheNode moduleCacheNode;
  private final String[] packageParts;
  private final String moduleName;

  public FQNNode(String[] packageParts, String moduleName) {
    this.packageParts = packageParts;
    this.moduleName = moduleName;
    this.moduleCacheNode = ModuleCacheNodeGen.create();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FQNNode fqnNode = (FQNNode) o;
    return Arrays.equals(packageParts, fqnNode.packageParts) &&
        Objects.equals(moduleName, fqnNode.moduleName);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(moduleName);
    result = 31 * result + Arrays.hashCode(packageParts);
    return result;
  }

  @Override
  public String toString() {
    return "FQNNode{" +
        "packageParts=" + Arrays.toString(packageParts) +
        ", moduleName='" + moduleName + '\'' +
        '}';
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return moduleCacheNode.executeLoad(packageParts, moduleName);
  }

  @Override
  public String executeString(VirtualFrame frame) throws UnexpectedResultException {
    return ModuleCacheNode.getFQN(packageParts, moduleName);
  }

  @Override
  public Module executeModule(VirtualFrame frame) throws UnexpectedResultException {
    return moduleCacheNode.executeLoad(packageParts, moduleName);
  }
}
