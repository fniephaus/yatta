package yatta.ast.builtin.modules;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import yatta.YattaException;
import yatta.ast.builtin.BuiltinNode;
import yatta.runtime.*;
import yatta.runtime.exceptions.UndefinedNameException;
import yatta.runtime.stdlib.Builtins;
import yatta.runtime.stdlib.ExportedFunction;

@BuiltinModuleInfo(moduleName = "Dict")
public final class DictBuiltinModule implements BuiltinModule {
  @NodeInfo(shortName = "fold")
  abstract static class FoldBuiltin extends BuiltinNode {
    @Specialization
    public Object fold(Dict dict, Function function, Object initialValue, @CachedLibrary(limit = "3") InteropLibrary dispatch) {
      try {
        return dict.fold(initialValue, function, dispatch);
      } catch (ArityException | UnsupportedTypeException | UnsupportedMessageException e) {
        /* Execute was not successful. */
        throw UndefinedNameException.undefinedFunction(this, function);
      }
    }
  }

  @NodeInfo(shortName = "reduce")
  abstract static class ReduceBuiltin extends BuiltinNode {
    @Specialization
    public Object reduce(Dict dict, Tuple reducer, @CachedLibrary(limit = "3") InteropLibrary dispatch) {
      try {
        return dict.reduce(new Object[] {reducer.get(0), reducer.get(1), reducer.get(2)}, dispatch);
      } catch (ArityException | UnsupportedTypeException | UnsupportedMessageException e) {
        /* Execute was not successful. */
        throw new YattaException(e, this);
      }
    }
  }

  @NodeInfo(shortName = "len")
  abstract static class LengthBuiltin extends BuiltinNode {
    @Specialization
    public long length(Dict dict) {
      return dict.size();
    }
  }

  @NodeInfo(shortName = "lookup")
  abstract static class LookupBuiltin extends BuiltinNode {
    @Specialization
    public Object lookup(Object key, Dict dict) {
      return dict.lookup(key);
    }
  }

  public Builtins builtins() {
    Builtins builtins = new Builtins();
    builtins.register(new ExportedFunction(DictBuiltinModuleFactory.FoldBuiltinFactory.getInstance()));
    builtins.register(new ExportedFunction(DictBuiltinModuleFactory.ReduceBuiltinFactory.getInstance()));
    builtins.register(new ExportedFunction(DictBuiltinModuleFactory.LengthBuiltinFactory.getInstance()));
    builtins.register(new ExportedFunction(DictBuiltinModuleFactory.LookupBuiltinFactory.getInstance()));
    return builtins;
  }
}
