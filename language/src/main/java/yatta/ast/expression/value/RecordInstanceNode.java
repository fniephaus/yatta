package yatta.ast.expression.value;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import yatta.YattaException;
import yatta.YattaLanguage;
import yatta.ast.ExpressionNode;
import yatta.runtime.DependencyUtils;
import yatta.runtime.Tuple;
import yatta.runtime.Unit;
import yatta.runtime.YattaModule;
import yatta.runtime.exceptions.NoRecordException;
import yatta.runtime.exceptions.NoRecordFieldException;

import java.util.Arrays;
import java.util.Objects;

import static com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

@NodeInfo(shortName = "recordInstance")
public final class RecordInstanceNode extends ExpressionNode {
  @CompilationFinal
  private final String recordType;
  @Children
  private final RecordFieldValueNode[] fields;
  @Children
  private final ExpressionNode[] moduleStack;  // FQNNode or AnyValueNode

  public RecordInstanceNode(String recordType, RecordFieldValueNode[] fields, ExpressionNode[] moduleStack) {
    this.recordType = recordType;
    this.fields = fields;
    this.moduleStack = moduleStack;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RecordInstanceNode that = (RecordInstanceNode) o;
    return Objects.equals(recordType, that.recordType) &&
        Arrays.equals(fields, that.fields);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(recordType);
    result = 31 * result + Arrays.hashCode(fields);
    return result;
  }

  @Override
  public String toString() {
    return "RecordInstanceNode{" +
        "recordType=" + recordType +
        ", fields=" + Arrays.toString(fields) +
        '}';
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    try {
      return executeTuple(frame);
    } catch (UnexpectedResultException e) {
      throw new YattaException("Unexpected error when creating a record instance", e, this);
    }
  }

  @Override
  protected String[] requiredIdentifiers() {
    return DependencyUtils.catenateRequiredIdentifiers(fields);
  }

  @Override
  public Tuple executeTuple(VirtualFrame frame) throws UnexpectedResultException {
    String[] recordFields = null;
    if (moduleStack.length > 0) {
      for (int i = moduleStack.length - 1; i >= 0; i--) {
        try {
          YattaModule module = moduleStack[i].executeModule(frame);
          if (module.getRecords().contains(recordType)) {
            recordFields = (String[]) module.getRecords().lookup(recordType);
          }
        } catch (UnexpectedResultException e) {
          continue;
        } catch (YattaException e) {  // IO error
          continue;
        }
      }
    }

    if (recordFields == null) {
      throw new NoRecordException(recordType, this);
    } else {
      Object[] resultFields = new Object[recordFields.length + 1];
      Arrays.fill(resultFields, Unit.INSTANCE);
      resultFields[0] = lookupContextReference(YattaLanguage.class).get().symbol(recordType);
      for (RecordFieldValueNode field : fields) {
        Tuple fieldTuple = field.executeTuple(frame);
        setField((String) fieldTuple.get(0), fieldTuple.get(1), recordFields, resultFields);
      }

      return new Tuple(resultFields);
    }
  }

  private void setField(String fieldName, Object fieldValue, String[] recordFields, Object[] resultFields) {
    int fieldPos = -1;
    for (int i = 0; i < recordFields.length; i++) {
      if (fieldName.equals(recordFields[i])) {
        fieldPos = i;
      }
    }

    if (fieldPos == -1) {
      throw new NoRecordFieldException(recordType, fieldName, this);
    } else {
      resultFields[fieldPos + 1] = fieldValue;
    }
  }
}
