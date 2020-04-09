package innohack.gem.entity;

import innohack.gem.entity.gem.data.AbstractData;

import java.util.Collection;

public class GEMFile {

    private String filename;
    private Collection<AbstractData> data;

    public Collection<AbstractData> getData() {
        return data;
    }
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setData(Collection<AbstractData> data) {
        this.data = data;
    }

}
