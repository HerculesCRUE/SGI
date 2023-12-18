package org.crue.hercules.sgi.sgdoc.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.sgdoc.config.StoreProperties;
import org.crue.hercules.sgi.sgdoc.exceptions.ArchivoNotFoundException;
import org.crue.hercules.sgi.sgdoc.exceptions.DocumentoNotFoundException;
import org.crue.hercules.sgi.sgdoc.model.Documento;
import org.crue.hercules.sgi.sgdoc.repository.DocumentoRepository;
import org.crue.hercules.sgi.sgdoc.repository.specification.DocumentoSpecifications;
import org.crue.hercules.sgi.sgdoc.utils.StoreUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class DocumentoService {

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_DOCUMENTO_REF = "documentoRef";

  /** Documento repository */
  private final DocumentoRepository repository;
  /** Store Properties */
  private final StoreProperties storeProperties;

  public DocumentoService(DocumentoRepository repository, StoreProperties storeProperties) {
    this.repository = repository;
    this.storeProperties = storeProperties;
  }

  /**
   * Guarda la entidad {@link Documento}.
   *
   * @param documento la entidad {@link Documento} a guardar.
   * @param file      el {@link Resource} del {@link Documento}.
   * @return la entidad {@link Documento} persistida.
   */
  @Transactional
  public Documento create(Documento documento, Resource file) {
    log.debug("create(Documento documento) - start");

    Assert.isNull(documento.getDocumentoRef(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_DOCUMENTO_REF))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Documento.class))
            .build());

    documento.setDocumentoRef(UUID.randomUUID().toString());

    try {
      File fileStore = StoreUtils.getResource(storeProperties.getPath(), documento).getFile();

      fileStore.getParentFile().mkdirs();
      if (!fileStore.exists()) {
        Files.copy(file.getInputStream(), fileStore.toPath());
        documento.setHash(getHashFile(fileStore));
      } else {
        throw new IllegalArgumentException("File exist!");
      }
    } catch (IOException | NoSuchAlgorithmException io) {
      throw new RuntimeException(io);
    }

    Documento returnValue = repository.save(documento);

    log.debug("create(Documento documento) - end");

    return returnValue;
  }

  /**
   * Elimina el {@link Documento}.
   *
   * @param id identificador del {@link Documento} a guardar.
   */
  @Transactional
  public void delete(String id) {
    log.debug("delete(String id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_DOCUMENTO_REF))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Documento.class))
            .build());

    Documento documento = repository.findById(id).orElseThrow(() -> new DocumentoNotFoundException(id));

    repository.deleteById(id);

    Resource file = StoreUtils.getResource(storeProperties.getPath(), documento);
    if (file.exists() && !documento.getDocumentoRef().startsWith(StoreUtils.SAMPLE_DATA_PREFIX)) {
      try {
        file.getFile().delete();
      } catch (IOException io) {
        throw new RuntimeException(io);
      }
    }

    log.debug("delete(String id) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link Documento}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Documento} paginadas y filtradas.
   */
  public Page<Documento> findAll(String query, Pageable paging) {
    Specification<Documento> spec = SgiRSQLJPASupport.toSpecification(query);

    Page<Documento> returnValue = repository.findAll(spec, paging);
    return returnValue;
  }

  /**
   * Devuelve el {@link Documento} con el id indicado.
   * 
   * @param id Identificador de {@link Documento}.
   * @return {@link Documento} correspondiente al id
   */
  public Documento findById(String id) {
    log.debug("findById(String id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_DOCUMENTO_REF))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Documento.class))
            .build());

    Documento returnValue = repository.findById(id).orElseThrow(() -> new DocumentoNotFoundException(id));
    log.debug("findById(String id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link Documento} que tienen alguno
   * de los ids de la lista.
   * 
   * @param ids    identificadores de {@link Documento}.
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Documento} paginadas y filtradas.
   */
  public Page<Documento> findByDocumentoIds(List<String> ids, String query, Pageable paging) {
    Specification<Documento> specByQuery = SgiRSQLJPASupport.toSpecification(query);
    Specification<Documento> specByDocumentoRefs = DocumentoSpecifications.byDocumentoRefs(ids);

    Specification<Documento> specs = Specification.where(specByDocumentoRefs).and(specByQuery);

    Page<Documento> returnValue = repository.findAll(specs, paging);
    return returnValue;
  }

  /**
   * Devuelve el {@link Resource} del {@link Documento} con el id indicado.
   * 
   * @return {@link Resource} correspondiente al id del {@link Documento}.
   */
  public Resource getDocumentoResource(Documento documento) {
    Resource resource = StoreUtils.getResource(storeProperties.getPath(), documento);

    if (!resource.exists()) {
      throw new ArchivoNotFoundException(documento.getDocumentoRef());
    }
    return resource;
  }

  private String getHashFile(File file) throws IOException, NoSuchAlgorithmException {
    // Use SHA-256 algorithm
    MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
    return StoreUtils.getFileChecksum(shaDigest, file);
  }
}