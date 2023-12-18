package org.crue.hercules.sgi.sgdoc.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.sgdoc.model.Documento;
import org.crue.hercules.sgi.sgdoc.service.DocumentoService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * DocumentoController
 */
@RestController
@RequestMapping("/documentos")
@Slf4j
public class DocumentoController {

  /** Documento service */
  private final DocumentoService service;

  /**
   * Instancia un nuevo DocumentoController.
   * 
   * @param documentoService {@link DocumentoService}.
   */
  public DocumentoController(DocumentoService documentoService) {
    this.service = documentoService;
  }

  /**
   * Crea una nuevo {@link Documento}
   * 
   * @param archivo el documento.
   * @return {@link Documento} creado.
   */
  @PostMapping
  public ResponseEntity<Documento> create(@RequestPart("archivo") MultipartFile archivo) {
    log.debug("create(MultipartFile archivo) - start");
    Documento documento = new Documento();
    documento.setNombre(archivo.getOriginalFilename());
    documento.setFechaCreacion(LocalDateTime.now());
    documento.setAutorRef("anonymous");
    documento.setVersion(1);

    String[] contentType = (archivo.getContentType().split(";"));
    documento.setTipo(contentType[0]);
    Documento documentoCreado = service.create(documento, archivo.getResource());

    log.debug("create(MultipartFile archivo) - end");

    return new ResponseEntity<>(documentoCreado, HttpStatus.CREATED);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link Documento}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Documento} paginadas y filtradas.
   */
  @GetMapping()
  public ResponseEntity<Page<Documento>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Page<Documento> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve la {@link Documento} con el id indicado.
   * 
   * @param id Identificador de {@link Documento}.
   * @return {@link Documento} correspondiente al id
   */
  @GetMapping("/{id}")
  public Documento findById(@PathVariable String id) {
    log.debug("findById(String id) - start");
    Documento returnValue = service.findById(id);
    log.debug("findById(String id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Resource} del {@link Documento} con el id indicado.
   * 
   * @param id Identificador de {@link Documento}.
   * @return {@link Resource} correspondiente al id del {@link Documento}.
   */
  @GetMapping("/{id}/archivo")
  public ResponseEntity<Resource> findDocumentoArchivo(@PathVariable String id) {
    log.debug("findDocumentoArchivo(String id) - start");

    Documento documento = service.findById(id);

    Resource resource = service.getDocumentoResource(documento);

    HttpHeaders headers = new HttpHeaders();

    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documento.getNombre() + "\"");
    ResponseEntity<Resource> response = ResponseEntity.ok().headers(headers)
        .contentType(
            MediaType.parseMediaType(documento.getTipo()))
        .body(resource);

    log.debug("findDocumentoArchivo(String id) - end");

    return response;
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
  @GetMapping("/bydocumentorefs/{ids}")
  public ResponseEntity<Page<Documento>> findByDocumentoIds(@PathVariable String ids,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findByDocumentoIds(String ids, String query, Pageable paging) - start");
    List<String> idsList = Arrays.asList(ids.split("\\|"));
    Page<Documento> page = service.findByDocumentoIds(idsList, query, paging);

    if (page.isEmpty()) {
      log.debug("findByDocumentoIds(String ids, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findByDocumentoIds(String ids, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Elimina el {@link Documento} con id indicado.
   * 
   * @param id Identificador de {@link Documento}.
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable String id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

}
