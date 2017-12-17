package archecker.compliance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import archecker.graph.Edge;
import archecker.graph.Edge.Type;
import archecker.graph.Vertex;
import archecker.graph.VertexPair;
import archecker.model.Call;
import archecker.model.Component;
import archecker.model.Entity;

public class Checker {

  private final Map<VertexPair, Edge> divergenceMap;
  private final Map<VertexPair, Edge> convergenceMap;
  private final DirectedWeightedPseudograph<Vertex, Edge> callGraph;
  private final List<Call> violations;

  public Checker() {
    divergenceMap = new HashMap<>();
    convergenceMap = new HashMap<>();
    callGraph = new DirectedWeightedPseudograph<>(Edge.class);
    violations = new ArrayList<>();
  }

  public void checkCompliance(final Set<Entity> entities) {
    for (final Entity e : entities) {
      if (e == null)
        continue;
      if (e.getCalls() == null)
        continue;
      if (e.getCalls().isEmpty())
        updateVertex(e.getMapsTo());
      for (final Call call : e.getCalls()) {
        final VertexPair vertices = updateVertices(call);
        final Component mapsTo = call.getCalleeEntity().getMapsTo();
        final Set<Component> allowedCalls = e.getAllowedCalls();
        if (!allowedCalls.contains(mapsTo)) {
          // Do not report self calls
          if (vertices.areEqual())
            continue;
          reportViolation(call);
          updateDivergence(vertices);
        } else
          updateConvergence(vertices);
      }
    }
    updateAbsences();
  }

  private void reportViolation(final Call call) {
    violations.add(call);
  }

  private Vertex updateVertex(final Component component) {
    Vertex vtx = component.getVertex();
    if (vtx == null) {
      vtx = new Vertex(component);
      component.setVertex(vtx);
      callGraph.addVertex(vtx);
    }
    return vtx;
  }

  private VertexPair updateVertices(final Call call) {
    final Component callee = call.getCalleeEntity().getMapsTo();
    final Component caller = call.getCallerEntity().getMapsTo();
    final Vertex calleeVtx = updateVertex(callee);
    final Vertex callerVtx = updateVertex(caller);
    return new VertexPair(callerVtx, calleeVtx);
  }

  private void updateDivergence(final VertexPair vp) {
    updateEdge(divergenceMap, Type.DIVERGENCE, vp);
  }

  private void updateConvergence(final VertexPair vp) {
    updateEdge(convergenceMap, Type.CONVERGENCE, vp);
  }

  private void updateAbsences() {
    for (final Vertex v : callGraph.vertexSet()) {
      final Set<Component> shouldCall = v.getComponent().getShouldCall();
      for (final Edge e : callGraph.outgoingEdgesOf(v)) {
        if (!Type.CONVERGENCE.equals(e.getType()))
          continue;
        final Component c = e.getTarget().getComponent();
        shouldCall.remove(c);
      }
      for (final Component c : shouldCall)
        callGraph.addEdge(v, c.getVertex(), new Edge(v, c.getVertex(), Type.ABSENCE));
    }
  }

  private void updateEdge(final Map<VertexPair, Edge> m, final Type type, final VertexPair vp) {
    Edge e = m.get(vp);
    if (e == null) {
      e = new Edge(vp.getV1(), vp.getV2(), type);
      e.setWeight(1);
      m.put(vp, e);
      callGraph.addEdge(vp.getV1(), vp.getV2(), e);
    } else {
      e.setWeight(e.getWeight() + 1);
    }
  }

  public List<Call> getViolations() {
    return violations;
  }

  public int getCallCount() {
    return callGraph.edgeSet().stream().mapToInt(Edge::getWeight).sum();
  }

  public DirectedWeightedPseudograph<Vertex, Edge> getCallGraph() {
    return callGraph;
  }

}
