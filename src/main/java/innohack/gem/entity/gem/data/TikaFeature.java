package innohack.gem.entity.gem.data;

import java.io.File;

/**
 * Object to hold wrap extracted tika data
 *
 */
public class TikaFeature extends AbstractFeature {
    public TikaFeature() {
        super(Target.TIKA);
    }

    @Override
    public void extract(File f) {
        //TODO extraction method for TIKA
        addMetadata("TikaMetadata1", "AAAAAA");
    }
}
