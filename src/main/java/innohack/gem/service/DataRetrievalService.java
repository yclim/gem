package innohack.gem.service;

import innohack.gem.dao.BasicGEMFileDao;
import innohack.gem.dao.IGEMFileDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.gem.data.TikaData;
import innohack.gem.service.event.EventListener.Event;
import innohack.gem.service.event.NewEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DataRetrievalService extends NewEvent implements Runnable {
    Collection<GEMFile> files;
    @Autowired
    private IGEMFileDao gemDao;

    public DataRetrievalService(Collection<GEMFile> files){
        this.files = files;
    }

    @Override
    public void run() {
        for (GEMFile f: files){
            extractData(f);
        }
    }

    //TODO implement extraction logic and store
    public void extractData(GEMFile f){
        TikaData d1 = new TikaData();
        d1.getMetadata().put("Compression Type","8 bits");
        f.getData().add(d1);
        newEvent(Event.NEW_FILE,f);
        gemDao.saveFiles(f); // --> trigger new file event
    }
}