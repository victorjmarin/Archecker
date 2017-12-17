package archecker.model;

import java.util.Set;
import archecker.graph.Vertex;

public class Component {

  private final String name;
  private Set<Component> shouldCall;
  private Vertex vertex;

  public Component(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  public Set<Component> getShouldCall() {
    return shouldCall;
  }

  public void setShouldCall(final Set<Component> shouldCall) {
    this.shouldCall = shouldCall;
  }
  
  public Vertex getVertex() {
    return vertex;
  }

  public void setVertex(final Vertex vertex) {
    this.vertex = vertex;
  }

}
