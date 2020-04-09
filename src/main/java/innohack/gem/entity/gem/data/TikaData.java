package innohack.gem.entity.gem.data;

import innohack.gem.entity.Rule;
import innohack.gem.entity.Target;

import java.util.HashMap;

/**
 * Object to hold extracted Tika Data
 *
 */
public class TikaData extends AbstractData{

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    @Override
    public Rule matchRule(Rule rule) {
        return rule;
    }

    @Override
    public Target getTarget() { return Target.TIKA; }
}
