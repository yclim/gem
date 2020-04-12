package innohack.gem.dao;

import com.google.common.collect.Lists;
import innohack.gem.entity.DocumentMetadata;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DocumentDao implements IDocumentDao {

    @Override
    public List<DocumentMetadata> getDocumentMetadataList() {
        return Lists.newArrayList(mockDocumentMetadata());
    }

    @Override
    public List<DocumentMetadata> findByName(String name) {
        return Lists.newArrayList(mockDocumentMetadata());
    }

    @Override
    public List<DocumentMetadata> findByExtension(String extension) {
        return Lists.newArrayList(mockDocumentMetadata());
    }

    @Override
    public DocumentMetadata uploadDocument(DocumentMetadata documentMetadata) {
        return mockDocumentMetadata();
    }

    private DocumentMetadata mockDocumentMetadata() {
        DocumentMetadata d = new DocumentMetadata();
        d.setName("single doc");
        d.setSize(100L);
        return d;
    }
}