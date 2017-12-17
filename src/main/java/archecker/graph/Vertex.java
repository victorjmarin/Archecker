package archecker.graph;

import archecker.model.Component;

public class Vertex {

  private final Component component;

  public Vertex(final Component component) {
    this.component = component;
  }

  public Component getComponent() {
    return component;
  }

  @Override
  public String toString() {
    return component.toString();
  }

}
