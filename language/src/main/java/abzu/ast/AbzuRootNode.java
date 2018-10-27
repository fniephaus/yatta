package abzu.ast;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;
import abzu.AbzuLanguage;

/**
 * The root of all Abzu execution trees. It is a Truffle requirement that the tree root extends the
 * class {@link RootNode}. This class is used for both builtin and user-defined functions. For
 * builtin functions, the {@link #bodyNode} is a subclass of {@link AbzuBuiltinNode}. For user-defined
 * functions, the {@link #bodyNode} is a {@link AbzuFunctionBodyNode}.
 */
@NodeInfo(language = "abzu", description = "The root of all abzu execution trees")
public class AbzuRootNode extends RootNode {
  /**
   * The function body that is executed, and specialized during execution.
   */
  @Child
  private AbzuExpressionNode bodyNode;

  /**
   * The name of the function, for printing purposes only.
   */
  private final String name;

  @CompilationFinal
  private boolean isCloningAllowed;

  private final SourceSection sourceSection;

  public AbzuRootNode(AbzuLanguage language, FrameDescriptor frameDescriptor, AbzuExpressionNode bodyNode, SourceSection sourceSection, String name) {
    super(language, frameDescriptor);
    this.bodyNode = bodyNode;
    this.name = name;
    this.sourceSection = sourceSection;
  }

  @Override
  public SourceSection getSourceSection() {
    return sourceSection;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    assert getLanguage(AbzuLanguage.class).getContextReference().get() != null;
    return bodyNode.executeGeneric(frame);
  }

  public AbzuExpressionNode getBodyNode() {
    return bodyNode;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setCloningAllowed(boolean isCloningAllowed) {
    this.isCloningAllowed = isCloningAllowed;
  }

  @Override
  public boolean isCloningAllowed() {
    return isCloningAllowed;
  }

  @Override
  public String toString() {
    return "root " + name;
  }
}