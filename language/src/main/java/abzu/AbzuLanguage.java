package abzu;

import abzu.ast.builtin.BuiltinNode;
import abzu.ast.local.LexicalScope;
import abzu.runtime.Context;
import abzu.runtime.Function;
import abzu.runtime.Unit;
import com.oracle.truffle.api.*;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import java.util.*;

@TruffleLanguage.Registration(id = AbzuLanguage.ID, name = "smol", defaultMimeType = AbzuLanguage.MIME_TYPE, characterMimeTypes = AbzuLanguage.MIME_TYPE, contextPolicy = TruffleLanguage.ContextPolicy.SHARED)
@ProvidedTags({StandardTags.CallTag.class, StandardTags.StatementTag.class, StandardTags.RootTag.class, StandardTags.ExpressionTag.class, DebuggerTags.AlwaysHalt.class})
public class AbzuLanguage extends TruffleLanguage<Context> {
  public static volatile int counter;

  public static final String ID = "abzu";
  public static final String MIME_TYPE = "application/x-abzu";

  public AbzuLanguage() {
    counter++;
  }

  @Override
  protected Context createContext(Env env) {
    return new Context(this, env, new ArrayList<>(EXTERNAL_BUILTINS));
  }

  @Override
  protected CallTarget parse(ParsingRequest request) throws Exception {
    Source source = request.getSource();
    RootCallTarget rootCallTarget;
    /*
     * Parse the provided source. At this point, we do not have a Context yet. Registration of
     * the functions with the Context happens lazily in AbzuEvalRootNode.
     */
    rootCallTarget = AbzuParser.parseAbzu(this, source);

    return Truffle.getRuntime().createCallTarget(rootCallTarget.getRootNode());
  }

  @Override
  protected boolean isVisible(Context context, Object value) {
    return value != Unit.INSTANCE;
  }

  @Override
  protected boolean isObjectOfLanguage(Object object) {
    if (!(object instanceof TruffleObject)) {
      return false;
    }
    TruffleObject truffleObject = (TruffleObject) object;
    return truffleObject instanceof Function || Context.isAbzuObject(truffleObject);
  }

  @Override
  protected String toString(Context context, Object value) {
    if (value == Unit.INSTANCE) {
      return "NONE";
    }
    if (value instanceof Long) {
      return Long.toString((Long) value);
    }
    return super.toString(context, value);
  }

  @Override
  protected Object findMetaObject(Context context, Object value) {
    if (value instanceof Number) {
      return "Number";
    }
    if (value instanceof Boolean) {
      return "Boolean";
    }
    if (value instanceof String) {
      return "String";
    }
    if (value == Unit.INSTANCE) {
      return "None";
    }
    if (value instanceof Function) {
      return "Function";
    }
    return "Object";
  }

  @Override
  protected SourceSection findSourceLocation(Context context, Object value) {
    if (value instanceof Function) {
      Function f = (Function) value;
      return f.getCallTarget().getRootNode().getSourceSection();
    }
    return null;
  }

  @Override
  public Iterable<Scope> findLocalScopes(Context context, Node node, Frame frame) {
    final LexicalScope scope = LexicalScope.createScope(node);
    return new Iterable<Scope>() {
      @Override
      public Iterator<Scope> iterator() {
        return new Iterator<Scope>() {
          private LexicalScope previousScope;
          private LexicalScope nextScope = scope;

          @Override
          public boolean hasNext() {
            if (nextScope == null) {
              nextScope = previousScope.findParent();
            }
            return nextScope != null;
          }

          @Override
          public Scope next() {
            if (!hasNext()) {
              throw new NoSuchElementException();
            }
            Scope vscope = Scope.newBuilder(nextScope.getName(), nextScope.getVariables(frame)).node(nextScope.getNode()).arguments(nextScope.getArguments(frame)).build();
            previousScope = nextScope;
            nextScope = null;
            return vscope;
          }
        };
      }
    };
  }

  @Override
  protected Iterable<Scope> findTopScopes(Context context) {
    return context.getTopScopes();
  }

  public static Context getCurrentContext() {
    return getCurrentContext(AbzuLanguage.class);
  }

  private static final List<NodeFactory<? extends BuiltinNode>> EXTERNAL_BUILTINS = Collections.synchronizedList(new ArrayList<>());

  public static void installBuiltin(NodeFactory<? extends BuiltinNode> builtin) {
    EXTERNAL_BUILTINS.add(builtin);
  }
}
