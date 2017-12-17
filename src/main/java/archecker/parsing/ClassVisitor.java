/*
 * Copyright (c) 2011 - Georgios Gousios <gousiosg@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package archecker.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import archecker.model.Call;

/**
 * Class copied with modifications from
 * https://github.com/gousiosg/java-callgraph/blob/master/src/main/java/gr/gousiosg/javacg/stat/ClassVisitor.java
 */
public class ClassVisitor extends EmptyVisitor {

  private final JavaClass clazz;
  private final ConstantPoolGen constants;
  private final List<Call> methodCalls;

  public ClassVisitor(final JavaClass jc) {
    clazz = jc;
    constants = new ConstantPoolGen(clazz.getConstantPool());
    methodCalls = Collections.synchronizedList(new ArrayList<>());
  }

  @Override
  public void visitJavaClass(final JavaClass jc) {
    final Method[] methods = jc.getMethods();
    Arrays.asList(methods).stream().forEach(m -> m.accept(this));
  }

  @Override
  public void visitMethod(final Method method) {
    final MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
    final MethodVisitor visitor = new MethodVisitor(mg, clazz);
    visitor.start();
    methodCalls.addAll(visitor.getMethodCalls());
  }

  public List<Call> getMethodCalls() {
    return methodCalls;
  }

  public void start() {
    visitJavaClass(clazz);
  }
}
