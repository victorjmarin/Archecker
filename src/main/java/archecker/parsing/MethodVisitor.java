package archecker.parsing;

import java.util.ArrayList;
import java.util.List;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConst;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Type;
import archecker.model.Call;

/**
 * The simplest of method visitors, prints any invoked method signature for all method invocations.
 * 
 * Class copied with modifications from CJKM: http://www.spinellis.gr/sw/ckjm/
 */
public class MethodVisitor extends EmptyVisitor {

  JavaClass visitedClass;
  private final MethodGen mg;
  private final ConstantPoolGen cp;

  private final String caller;
  private final String callerMethod;
  private String callee;
  private String calleeMethod;
  private final List<Call> methodCalls;
  private final LineNumberTable lnt;
  private int ihPos;

  public MethodVisitor(final MethodGen m, final JavaClass jc) {
    visitedClass = jc;
    mg = m;
    cp = mg.getConstantPool();
    lnt = mg.getLineNumberTable(cp);
    caller = visitedClass.getClassName();
    callerMethod = mg.getName() + "(" + argumentList(mg.getArgumentTypes()) + ")";
    methodCalls = new ArrayList<>();
  }

  private String argumentList(final Type[] arguments) {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < arguments.length; i++) {
      if (i != 0) {
        sb.append(",");
      }
      sb.append(arguments[i].toString());
    }
    return sb.toString();
  }

  public void start() {
    if (mg.isAbstract() || mg.isNative())
      return;
    for (InstructionHandle ih = mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
      final Instruction i = ih.getInstruction();
      ihPos = ih.getPosition();
      if (!visitInstruction(i))
        i.accept(this);
    }
  }

  private boolean visitInstruction(final Instruction i) {
    final short opcode = i.getOpcode();
    return ((InstructionConst.getInstruction(opcode) != null)
        && !(i instanceof ConstantPushInstruction) && !(i instanceof ReturnInstruction));
  }

  @Override
  public void visitInvokeInstruction(final InvokeInstruction i) {
    addCalls(i);
  }

  private void addCalls(final InvokeInstruction i) {
    final int lineNumber = lnt.getSourceLine(ihPos);
    callee = i.getReferenceType(cp).toString();
    calleeMethod = i.getMethodName(cp) + "(" + argumentList(i.getArgumentTypes(cp)) + ")";
    final Call call = methodCall(calleeMethod, callee, callerMethod, caller, lineNumber);
    // If callee entity is null, then the call is to a third-party API
    if (call.getCalleeEntity() != null)
      methodCalls.add(call);
  }

  public List<Call> getMethodCalls() {
    return methodCalls;
  }

  private Call methodCall(final String calleeMethod, final String callee, final String callerMethod,
      final String caller, final int lineNumber) {
    return new Call(calleeMethod, callee, callerMethod, caller, lineNumber);
  }
}
