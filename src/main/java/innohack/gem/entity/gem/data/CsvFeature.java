package innohack.gem.entity.gem.data;

import java.io.File;

/**
 * Object to hold wrap csv data
 *
 */

public class CsvFeature extends AbstractFeature {
    public CsvFeature() {
        super(Target.CSV);
    }

    @Override
    public void extract(File f) {
        //TODO extraction method for CSV
        addMetadata("CsvMetadata1", "BBBBB");
    }
}
