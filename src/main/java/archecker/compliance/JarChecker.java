package archecker.compliance;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import archecker.Utils;
import archecker.graph.DotExporter;
import archecker.model.Call;
import archecker.model.Entity;
import archecker.parsing.ClassVisitor;
import archecker.parsing.json.ArchParser;
import archecker.parsing.json.MappingParser;

public class JarChecker {

  private static final String BASE_DIR = "output";
  private static final String CALL_GRAPH = "callgraph";
  private static final String OUT_FILE = BASE_DIR + "/violations";

  public static final Map<String, Entity> classEntityMap = new ConcurrentHashMap<>();

  public static void main(final String[] args) throws IOException {
    if (args.length < 3) {
      System.err.println("Expected three parameters: jarPath archPath mappingsPath");
      return;
    }

    final String jarPath = args[0];
    final String archPath = args[1];
    final String mappingPath = args[2];

    final String archJson = Utils.read(archPath);
    final ArchParser archParser = new ArchParser();
    archParser.parse(archJson);

    final String mappingJson = Utils.read(mappingPath);
    final MappingParser mappingParser = new MappingParser(archParser);
    mappingParser.parse(mappingJson);

    long t0 = System.currentTimeMillis();

    final File f = new File(jarPath);
    if (!f.exists())
      System.err.println("Jar file " + jarPath + " does not exist");

    final JarFile jar = new JarFile(f);
    System.out.println("Parsing contents of " + jarPath + "...");

    final Set<JavaClass> parsedClasses = jar.stream().parallel().map(entry -> {

      if (entry.isDirectory())
        return null;
      if (!entry.getName().endsWith(".class"))
        return null;

      final ClassParser cp = new ClassParser(jarPath, entry.getName());
      JavaClass result = null;

      try {
        result = cp.parse();
      } catch (ClassFormatException | IOException e) {
        e.printStackTrace();
      }

      final String className = result.getClassName();
      String entityName = result.getPackageName();

      // Attempt to retrieve entity related to package. If not, try to get entity associated with
      // class.
      if (!(mappingParser.containsEntity(entityName))) {
        if (!(mappingParser.containsEntity(className))) {
          System.out.println("[WARN] No mapping provided for " + className);
          return null;
        }
        entityName = className;
      }

      final Entity entity = mappingParser.entityForName(entityName);
      classEntityMap.put(className, entity);

      return result;

    }).collect(Collectors.toSet());

    final Set<Entity> entities = parsedClasses.parallelStream().map(cls -> {

      if (cls == null)
        return null;

      final ClassVisitor visitor = new ClassVisitor(cls);
      visitor.start();

      final List<Call> methodCalls = visitor.getMethodCalls();
      final Entity entity = classEntityMap.get(cls.getClassName());
      entity.addCalls(methodCalls);

      return entity;

    }).collect(Collectors.toSet());

    jar.close();

    System.out.println();
    System.out.println("Walk .class files in jar: " + (System.currentTimeMillis() - t0) + " ms.");

    t0 = System.currentTimeMillis();

    final Checker checkr = new Checker();
    final Set<Entity> E = entities.stream().filter(e -> e != null).collect(Collectors.toSet());
    checkr.checkCompliance(E);

    System.out.println("Check calls & build graph: " + (System.currentTimeMillis() - t0) + " ms.");
    System.out.println();

    System.out.println("Number of calls: " + checkr.getCallCount());
    System.out.println("Number of unallowed calls: " + checkr.getViolations().size());
    System.out.println();

    DotExporter.export(checkr.getCallGraph(), BASE_DIR, CALL_GRAPH);
    System.out.println("Call graph saved to " + BASE_DIR + "/" + CALL_GRAPH + ".png");

    Utils.saveResult(OUT_FILE,
        checkr.getViolations().stream()
            .map(c -> "(line " + c.getLineNumber() + ") " + c.getCaller() + "."
                + c.getCallerMethod() + " -> " + c.getCallee() + "." + c.getCalleeMethod())
            .collect(Collectors.joining("\n")));

    System.out.println("Unallowed calls saved to " + OUT_FILE + ".txt");

  }

}
