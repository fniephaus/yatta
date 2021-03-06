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
import yatta.runtime.Function;
import yatta.runtime.Seq;
import yatta.runtime.Tuple;
import yatta.runtime.exceptions.UndefinedNameException;
import yatta.runtime.stdlib.Builtins;
import yatta.runtime.stdlib.ExportedFunction;

@BuiltinModuleInfo(moduleName = "Seq")
public final class SeqBuiltinModule implements BuiltinModule {
  @NodeInfo(shortName = "foldl")
  abstract static class FoldLeftBuiltin extends BuiltinNode {
    @Specialization
    public Object foldLeft(Seq sequence, Function function, Object initialValue, @CachedLibrary(limit = "3") InteropLibrary dispatch) {
      try {
        return sequence.foldLeft(initialValue, function, dispatch);
      } catch (ArityException | UnsupportedTypeException | UnsupportedMessageException e) {
        /* Execute was not successful. */
        throw UndefinedNameException.undefinedFunction(this, function);
      }
    }
  }

  @NodeInfo(shortName = "foldr")
  abstract static class FoldRightBuiltin extends BuiltinNode {
    @Specialization
    public Object foldRight(Seq sequence, Function function, Object initialValue, @CachedLibrary(limit = "3") InteropLibrary dispatch) {
      try {
        return sequence.foldRight(initialValue, function, dispatch);
      } catch (ArityException | UnsupportedTypeException | UnsupportedMessageException e) {
        /* Execute was not successful. */
        throw UndefinedNameException.undefinedFunction(this, function);
      }
    }
  }

  @NodeInfo(shortName = "reducel")
  abstract static class ReduceLeftBuiltin extends BuiltinNode {
    @Specialization
    public Object reduceLeft(Seq sequence, Tuple reducer, @CachedLibrary(limit = "3") InteropLibrary dispatch) {
      try {
        return sequence.reduceLeft(new Object[]{reducer.get(0), reducer.get(1), reducer.get(2)}, dispatch);
      } catch (ArityException | UnsupportedTypeException | UnsupportedMessageException e) {
        /* Execute was not successful. */
        throw new YattaException(e, this);
      }
    }
  }

  @NodeInfo(shortName = "reducer")
  abstract static class ReduceRightBuiltin extends BuiltinNode {
    @Specialization
    public Object reduceRight(Seq sequence, Tuple reducer, @CachedLibrary(limit = "3") InteropLibrary dispatch) {
      try {
        return sequence.reduceRight(new Object[]{reducer.get(0), reducer.get(1), reducer.get(2)}, dispatch);
      } catch (ArityException | UnsupportedTypeException | UnsupportedMessageException e) {
        /* Execute was not successful. */
        throw new YattaException(e, this);
      }
    }
  }

  @NodeInfo(shortName = "len")
  abstract static class LengthBuiltin extends BuiltinNode {
    @Specialization
    public long length(Seq sequence) {
      return sequence.length();
    }
  }

  @NodeInfo(shortName = "split")
  abstract static class SplitBuiltin extends BuiltinNode {
    @Specialization
    public Tuple length(Seq sequence, long idx) {
      return new Tuple((Object[]) sequence.split(idx, this));
    }
  }

  @NodeInfo(shortName = "is_string")
  abstract static class IsStringBuiltin extends BuiltinNode {
    @Specialization
    public boolean length(Seq sequence) {
      return sequence.isString();
    }
  }

  @NodeInfo(shortName = "lookup")
  abstract static class LookupBuiltin extends BuiltinNode {
    @Specialization
    public Object length(long idx, Seq sequence) {
      return sequence.lookup(idx, this);
    }
  }

  public Builtins builtins() {
    Builtins builtins = new Builtins();
    builtins.register(new ExportedFunction(SeqBuiltinModuleFactory.LengthBuiltinFactory.getInstance()));
    builtins.register(new ExportedFunction(SeqBuiltinModuleFactory.FoldLeftBuiltinFactory.getInstance()));
    builtins.register(new ExportedFunction(SeqBuiltinModuleFactory.FoldRightBuiltinFactory.getInstance()));
    builtins.register(new ExportedFunction(SeqBuiltinModuleFactory.ReduceLeftBuiltinFactory.getInstance()));
    builtins.register(new ExportedFunction(SeqBuiltinModuleFactory.ReduceRightBuiltinFactory.getInstance()));
    builtins.register(new ExportedFunction(SeqBuiltinModuleFactory.SplitBuiltinFactory.getInstance()));
    builtins.register(new ExportedFunction(SeqBuiltinModuleFactory.IsStringBuiltinFactory.getInstance()));
    builtins.register(new ExportedFunction(SeqBuiltinModuleFactory.LookupBuiltinFactory.getInstance()));
    return builtins;
  }
}
