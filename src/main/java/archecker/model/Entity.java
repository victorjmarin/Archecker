package archecker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Entity {

  private final String name;
  private final List<Call> calls;
  private Component mapsTo;

  public Entity(final String name) {
    this.name = name;
    calls = new ArrayList<>();
  }

  public Set<Component> getAllowedCalls() {
    return mapsTo.getShouldCall();
  }

  public String getName() {
    return name;
  }

  public List<Call> getCalls() {
    return calls;
  }

  public synchronized void addCalls(final List<Call> calls) {
    this.calls.addAll(calls);
  }

  public Component getMapsTo() {
    return mapsTo;
  }

  public void setMapsTo(final Component mapsTo) {
    this.mapsTo = mapsTo;
  }

  @Override
  public String toString() {
    return name;
  }

}
