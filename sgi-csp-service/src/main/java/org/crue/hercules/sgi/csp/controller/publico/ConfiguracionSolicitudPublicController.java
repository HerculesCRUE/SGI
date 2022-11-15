package org.crue.hercules.sgi.csp.controller.publico;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.service.DocumentoRequeridoSolicitudService;
import org.crue.hercules.sgi.csp.service.TipoDocumentoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConfiguracionSolicitudPublicController
 */
@RestController
@RequestMapping(ConfiguracionSolicitudPublicController.REQUEST_MAPPING)
@Slf4j
public class ConfiguracionSolicitudPublicController {

  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER + "convocatoria-configuracionsolicitudes";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_DOCUMENTOS_REQUERIDOS = PATH_ID + "/documentorequiridosolicitudes";
  public static final String PATH_TIPO_DOCUMENTO_FASE_PRESENTACION = PATH_ID + "/tipodocumentofasepresentaciones";

  /** DocumentoRequeridoSolicitudService service */
  private final DocumentoRequeridoSolicitudService documentoRequeridoSolicitudService;

  /** DocumentoRequeridoSolicitudService service */
  private final TipoDocumentoService tipoDocumentoService;

  /**
   * Instancia un nuevo ConfiguracionSolicitudController.
   * 
   * @param documentoRequeridoSolicitudService {@link DocumentoRequeridoSolicitudService}.
   * @param tipoDocumentoService               {@link TipoDocumentoService}.
   */
  public ConfiguracionSolicitudPublicController(
      DocumentoRequeridoSolicitudService documentoRequeridoSolicitudService,
      TipoDocumentoService tipoDocumentoService) {
    this.documentoRequeridoSolicitudService = documentoRequeridoSolicitudService;
    this.tipoDocumentoService = tipoDocumentoService;
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link DocumentoRequeridoSolicitud}
   * de la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link DocumentoRequeridoSolicitud}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_DOCUMENTOS_REQUERIDOS)
  public ResponseEntity<Page<DocumentoRequeridoSolicitud>> findAllConvocatoriaDocumentoRequeridoSolicitud(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaDocumentoRequeridoSolicitud(Long id, String query, Pageable paging) - start");
    Page<DocumentoRequeridoSolicitud> page = documentoRequeridoSolicitudService.findAllByConvocatoria(id, query,
        paging);
    log.debug("findAllConvocatoriaDocumentoRequeridoSolicitud(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * Devuelve una lista paginada y filtrada de los {@link TipoDocumento} de la
   * {@link Convocatoria} correspondientes a la fase de presentacion.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link TipoDocumento}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_TIPO_DOCUMENTO_FASE_PRESENTACION)
  public ResponseEntity<Page<TipoDocumento>> findAllTipoDocumentosFasePresentacion(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTipoDocumentosFasePresentacion(Long id, Pageable paging) - start");
    Page<TipoDocumento> page = tipoDocumentoService.findAllTipoDocumentosFasePresentacionConvocatoria(id, paging);
    log.debug("findAllConvocatoriaDocumentoRequeridoSolicitud(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

}
