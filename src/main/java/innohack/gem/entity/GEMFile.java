package innohack.gem.entity;

import com.google.common.collect.Lists;
import innohack.gem.entity.gem.data.AbstractFeature;
import innohack.gem.entity.gem.data.CsvFeature;
import innohack.gem.entity.gem.data.TikaFeature;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collection;

public class GEMFile {
    private String name;
    private String contentType;
    private Long size;
    private String extension;

    private String directory;
    private Collection<AbstractFeature> data;

    public GEMFile(String name, String directory) {
        this.directory = directory;
        this.name = name;
    }

    public GEMFile(MultipartFile file) {
        this.name = file.getOriginalFilename();
        this.size = file.getSize();
        this.extension = FilenameUtils.getExtension(file.getOriginalFilename());
        this.contentType = file.getContentType();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getAbsolutePath() {
        return getAbsolutePath(this.getName(), this.getDirectory());
    }

    public static String getAbsolutePath(String name, String directory) {
        return directory+"/"+name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
    public void addAllData(Collection<AbstractFeature> data) {
        if(this.data == null){
            this.data = Lists.newArrayList();
        }
        this.data.addAll(data);
    }

    public void addData(AbstractFeature data) {
        if(this.data == null){
            this.data = Lists.newArrayList();
        }
        this.data.add(data);
    }

    public Collection<AbstractFeature> getData() {
        return data;
    }

    public void setData(Collection<AbstractFeature> data) {
        this.data = data;
    }

    // Perform extraction
    public GEMFile extract(){
        // TODO extract file's data
        File f = new File(getAbsolutePath());
        extension = FilenameUtils.getExtension(f.getName());
        
        CsvFeature extractedData1  = new CsvFeature();
        extractedData1.extract(f);
        addData(extractedData1);

        TikaFeature extractedData2  = new TikaFeature();
        extractedData2.extract(f);
        addData(extractedData2);

        return this;
    }
}
