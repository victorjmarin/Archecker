package archecker.model;

import archecker.compliance.JarChecker;

public class Call {

  private final Entity calleeEntity;
  private final Entity callerEntity;
  private final String callee;
  private final String calleeMethod;
  private final String caller;
  private final String callerMethod;
  private final int lineNumber;

  public Call(final String calleeMethod, final String callee, final String callerMethod,
      final String caller, final int lineNumber) {
    this.calleeMethod = calleeMethod;
    this.callee = callee;
    calleeEntity = JarChecker.classEntityMap.get(callee);
    this.callerMethod = callerMethod;
    callerEntity = JarChecker.classEntityMap.get(caller);
    this.caller = caller;
    this.lineNumber = lineNumber;
  }

  public String getCallee() {
    return callee;
  }

  public Entity getCalleeEntity() {
    return calleeEntity;
  }
  
  public Entity getCallerEntity() {
    return callerEntity;
  }

  public String getCalleeMethod() {
    return calleeMethod;
  }

  public String getCallerMethod() {
    return callerMethod;
  }

  public String getCaller() {
    return caller;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public String toString() {
    return callee + "." + calleeMethod;
  }


}
