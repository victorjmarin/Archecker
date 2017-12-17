package archecker.graph;

import java.util.Objects;

public class VertexPair {

  private final Vertex v1;
  private final Vertex v2;

  public VertexPair(final Vertex v1, final Vertex v2) {
    this.v1 = v1;
    this.v2 = v2;
  }

  public Vertex getV1() {
    return v1;
  }

  public Vertex getV2() {
    return v2;
  }

  public boolean areEqual() {
    return v1 == v2;
  }

  @Override
  public int hashCode() {
    return Objects.hash(v1, v2);
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof VertexPair))
      return false;
    final VertexPair vp = (VertexPair) o;
    return vp.getV1() == v1 && vp.getV2() == v2;
  }

}
