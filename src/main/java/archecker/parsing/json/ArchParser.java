package archecker.parsing.json;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import archecker.model.Component;

public class ArchParser {

  private final HashMap<String, Component> componentMap = new HashMap<>();

  private static final String COMPONENT = "component";
  private static final String SHOULD_CALL = "should-call";

  public void parse(final String archJson) {
    final JSONArray arr = new JSONArray(archJson);
    for (int i = 0; i < arr.length(); i++) {
      final JSONObject obj = arr.getJSONObject(i);
      parseComponent(obj);
    }
  }

  @SuppressWarnings("unchecked")
  private void parseComponent(final JSONObject obj) {
    Component component = null;
    final Set<Component> shouldCall = new HashSet<>();
    final Map<String, Object> m = obj.toMap();
    for (final Entry<String, Object> e : m.entrySet()) {
      final String key = e.getKey();
      switch (key) {
        case COMPONENT:
          final String name = (String) e.getValue();
          component = componentForName(name, true);
          break;
        case SHOULD_CALL:
          final List<String> shouldCallLst = (List<String>) e.getValue();
          for (final String s : shouldCallLst) {
            final Component c = componentForName(s, true);
            shouldCall.add(c);
          }
          break;
      }
    }
    component.setShouldCall(shouldCall);
  }

  public Component componentForName(final String name, final boolean allowNull) {
    Component result = componentMap.get(name);
    if (result == null) {
      if (allowNull) {
        result = new Component(name);
        componentMap.put(name, result);
      } else
        throw new RuntimeException(
            "Found non-declared architecture component in mapping spec. [" + name + "]");
    }
    return result;
  }

}
