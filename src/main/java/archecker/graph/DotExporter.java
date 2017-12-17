package archecker.graph;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.ComponentNameProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.ExportException;
import org.jgrapht.ext.IntegerComponentNameProvider;

public class DotExporter {

  private static final ComponentNameProvider<Vertex> vertexLabelProvider =
      new ComponentNameProvider<Vertex>() {
        @Override
        public String getName(final Vertex component) {
          return component.getComponent().toString();
        }
      };

  private static final ComponentNameProvider<Edge> edgeLabelProvider =
      new ComponentNameProvider<Edge>() {
        @Override
        public String getName(final Edge component) {
          return String.valueOf(component.getWeight());
        }
      };

  private static final ComponentAttributeProvider<Vertex> vertexAttrProvider =
      new ComponentAttributeProvider<Vertex>() {

        @Override
        public Map<String, String> getComponentAttributes(final Vertex component) {
          final Map<String, String> result = new HashMap<>();
          result.put("fontname", "courier");
          result.put("height", ".65");
          result.put("shape", "box");
          return result;
        }
      };

  private static final ComponentAttributeProvider<Edge> edgeAttrProvider =
      new ComponentAttributeProvider<Edge>() {

        @Override
        public Map<String, String> getComponentAttributes(final Edge component) {
          final Map<String, String> result = new HashMap<>();
          result.put("fontname", "courier");
          result.put("fontsize", "11");
          result.put("labeldistance", "1.3");
          // result.put("taillabel", String.valueOf(component.getWeight()));
          if (component.getType() != null) {
            switch (component.getType()) {
              case CONVERGENCE:
                result.put("style", "solid");
                break;
              case DIVERGENCE:
                result.put("style", "dashed");
                break;
              case ABSENCE:
                result.put("style", "dotted");
                break;
            }
          }
          return result;
        }
      };

  private static final DOTExporter<Vertex, Edge> exporter =
      new DOTExporter<>(new IntegerComponentNameProvider<>(), vertexLabelProvider,
          edgeLabelProvider, vertexAttrProvider, edgeAttrProvider);

  public static void export(final Graph<Vertex, Edge> graph, final String path,
      final String fileName) {
    try {
      final String filePath = path + "/" + fileName + ".dot";
      final File dotFile = new File(filePath);
      exporter.exportGraph(graph, dotFile);

      final Graphviz gv = new Graphviz();
      gv.readSource(filePath);

      final String type = "png";
      final String repesentationType = "dot";
      final File out = new File(path + "/" + fileName + "." + type);
      final int result =
          gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type, repesentationType), out);
      if (result == -1)
        System.err.print("You need to have GraphViz installed to generate the graph image.");

      dotFile.delete();
    } catch (final ExportException e) {
      e.printStackTrace();
    }
  }

}
