package innohack.gem.entity.gem.data;

import innohack.gem.entity.Rule;
import innohack.gem.entity.Target;

import java.util.HashMap;

/**
 * Object to hold extracted CSV Data
 *
 */

public class CsvData extends AbstractData{
    private  String []headers;
    private String [][]data;

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public String[][] getData() {
        return data;
    }

    public void setData(String[][] data) {
        this.data = data;
    }
    @Override
    public Rule matchRule(Rule rule) {
        return rule;
    }

    @Override
    public Target getTarget() {
        return Target.CSV;
    }
}
