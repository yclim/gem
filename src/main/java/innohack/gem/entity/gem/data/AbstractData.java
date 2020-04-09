package innohack.gem.entity.gem.data;

import innohack.gem.entity.Rule;
import innohack.gem.entity.Target;

import java.util.HashMap;

public abstract class AbstractData {
    private HashMap<String, String> Metadata = new HashMap<>();

    public HashMap<String, String> getMetadata() {
        return Metadata;
    }

    public void setMetadata(HashMap<String, String> metadata) {
        Metadata = metadata;
    }
    // TODO implement matching on extracted data against rule
    public abstract Rule matchRule(Rule rule);
    public abstract Target getTarget();
}
