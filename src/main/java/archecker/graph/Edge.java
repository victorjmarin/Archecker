package archecker.graph;

public class Edge {

  public enum Type {
    CONVERGENCE, DIVERGENCE, ABSENCE
  }

  private final Vertex source;
  private final Vertex target;
  private final Type type;
  private int weight;

  public Edge(final Vertex source, final Vertex target, final Type type) {
    this.source = source;
    this.target = target;
    this.type = type;
    weight = 0;
  }

  public Vertex getSource() {
    return source;
  }

  public Vertex getTarget() {
    return target;
  }

  public Type getType() {
    return type;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(final int weight) {
    this.weight = weight;
  }

  @Override
  public String toString() {
    return source.toString() + "-(" + weight + ")-" + target.toString();
  }


}
