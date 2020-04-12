package innohack.gem.dao;

import innohack.gem.entity.DocumentMetadata;

import java.util.ArrayList;
import java.util.List;

public interface IDocumentDao {
    /**
     * Find document metadata by document file name
     * @param name document file name
     * @return list of document metadata
     */
    List<DocumentMetadata> findByName(String name);

    /**
     * Find document metadata by document file extension
     * @param extension document file extension
     * @return list of document metadata
     */
    List<DocumentMetadata> findByExtension(String extension);

    /**
     * Save document metadata
     * @param documentMetadata document metadata
     * @return document metadata
     */
    DocumentMetadata uploadDocument(DocumentMetadata documentMetadata);

    /**
     * Retrieves metadata for all uploaded documents
     * @return list of metadata {@link DocumentMetadata @DocumentMetadata}
     */
    List<DocumentMetadata> getDocumentMetadataList();

}