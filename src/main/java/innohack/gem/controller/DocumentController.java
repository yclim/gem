package innohack.gem.controller;

import innohack.gem.service.DocumentService;
import innohack.gem.entity.DocumentMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/documents")
public class DocumentController {
    private final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    @Autowired
    private DocumentService documentService;

    /**
     * Retrieves metadata for all uploaded documents
     * @return list of metadata {@link DocumentMetadata @DocumentMetadata}
     */
    @RequestMapping("/findAll/")
    public List<DocumentMetadata> getDocumentMetadataList() {
        return documentService.getDocumentMetadataList();
    }

    /**
     * Retrieves list of document metadata associated with given document name
     * @param name document name
     * @return list of metadata {@link DocumentMetadata @DocumentMetadata}
     */
    @RequestMapping("/findByName/{name:.+}")
    public List<DocumentMetadata> findByName(@PathVariable String name) {
        return documentService.findByName(name);
    }

    /**
     * Retrieves list of document metadata with given document file extension
     * @param extension file name extension
     * @return list of metadata {@link DocumentMetadata @DocumentMetadata}
     */
    @RequestMapping("/findByExtension/{extension}")
    public List<DocumentMetadata> findByExtension(@PathVariable String extension) {
        return documentService.findByExtension(extension);
    }

    /**
     * Handles file upload with correspondent document description
     * Extracts and stores document metadata {@link DocumentMetadata @DocumentMetadata}
     * @param file uploaded file {@link MultipartFile @MultipartFile}
     * @return created document metadata {@link DocumentMetadata @DocumentMetadata}
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public DocumentMetadata uploadDocument(@RequestParam(value="file") MultipartFile file) {
        return documentService.uploadDocument(file);
    }

}
