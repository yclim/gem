package innohack.gem.service;

import innohack.gem.dao.IDocumentDao;
import innohack.gem.entity.DocumentMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class DocumentService {
    private final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    @Autowired
    private IDocumentDao documentDao;

    /**
     * Retrieves metadata for all uploaded documents
     * @return list of metadata {@link DocumentMetadata @DocumentMetadata}
     */
    public List<DocumentMetadata> getDocumentMetadataList() {
        return documentDao.getDocumentMetadataList();
    }

    /**
     * Handles file upload with correspondent document description
     * Extracts and stores document metadata {@link DocumentMetadata @DocumentMetadata}
     * @param file uploaded file {@link MultipartFile @MultipartFile}
     * @return created document metadata {@link DocumentMetadata @DocumentMetadata}
     */
    public DocumentMetadata uploadDocument(MultipartFile file) {
        DocumentMetadata documentMetadata = new DocumentMetadata(file);
        return documentDao.uploadDocument(documentMetadata);
    }

    /**
     * Retrieves list of document metadata associated with given document name
     * @param name document name
     * @return list of metadata {@link DocumentMetadata @DocumentMetadata}
     */
    public List<DocumentMetadata> findByName(String name) {
        return documentDao.findByName(name);
    }

    /**
     * Retrieves list of document metadata with given document file extension
     * @param extension file name extension
     * @return list of metadata {@link DocumentMetadata @DocumentMetadata}
     */
    public List<DocumentMetadata> findByExtension(String extension) {
        return documentDao.findByExtension(extension);
    }

}