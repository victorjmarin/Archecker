package archecker.parsing.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import archecker.model.Component;
import archecker.model.Entity;

public class MappingParser {

  private final ArchParser archParser;
  private final Map<String, Entity> entityMap;

  private static final String COMPONENT = "component";
  private static final String ENTITIES = "entities";

  public MappingParser(final ArchParser archParser) {
    this.archParser = archParser;
    entityMap = new ConcurrentHashMap<>();
  }

  public void parse(final String mappingJson) {
    final JSONArray arr = new JSONArray(mappingJson);
    for (int i = 0; i < arr.length(); i++) {
      final JSONObject obj = arr.getJSONObject(i);
      parseComponent(obj);
    }
  }

  @SuppressWarnings("unchecked")
  private void parseComponent(final JSONObject obj) {
    Component component = null;
    final Map<String, Object> m = obj.toMap();
    String name = null;
    for (final Entry<String, Object> e : m.entrySet()) {
      final String key = e.getKey();
      switch (key) {
        case COMPONENT:
          name = (String) e.getValue();
          component = archParser.componentForName(name, true);
          break;
        case ENTITIES:
          final List<String> mappingsLst = (List<String>) e.getValue();
          for (final String s : mappingsLst) {
            final Entity entity = entityForName(s);
            entity.setMapsTo(component);
          }
          break;
      }
    }
  }

  public synchronized Entity entityForName(final String name) {
    Entity result = entityMap.get(name);
    if (result == null) {
      result = new Entity(name);
      entityMap.put(name, result);
    }
    return result;
  }

  public boolean containsEntity(final String name) {
    return entityMap.containsKey(name);
  }

  public Collection<Entity> getEntities() {
    return entityMap.values();
  }

}
