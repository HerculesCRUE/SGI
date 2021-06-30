package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.service.ConfiguracionSolicitudService;
import org.crue.hercules.sgi.csp.service.DocumentoRequeridoSolicitudService;
import org.crue.hercules.sgi.csp.service.TipoDocumentoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConfiguracionSolicitudController
 */
@RestController
@RequestMapping("/convocatoria-configuracionsolicitudes")
@Slf4j
public class ConfiguracionSolicitudController {

  /** ConfiguracionSolicitudService service */
  private final ConfiguracionSolicitudService service;

  /** DocumentoRequeridoSolicitudService service */
  private final DocumentoRequeridoSolicitudService documentoRequeridoSolicitudService;

  /** DocumentoRequeridoSolicitudService service */
  private final TipoDocumentoService tipoDocumentoService;

  /**
   * Instancia un nuevo ConfiguracionSolicitudController.
   * 
   * @param configuracionSolicitudService      {@link ConfiguracionSolicitudService}.
   * @param documentoRequeridoSolicitudService {@link DocumentoRequeridoSolicitudService}.
   * @param tipoDocumentoService               {@link TipoDocumentoService}.
   */
  public ConfiguracionSolicitudController(ConfiguracionSolicitudService configuracionSolicitudService,
      DocumentoRequeridoSolicitudService documentoRequeridoSolicitudService,
      TipoDocumentoService tipoDocumentoService) {
    this.service = configuracionSolicitudService;
    this.documentoRequeridoSolicitudService = documentoRequeridoSolicitudService;
    this.tipoDocumentoService = tipoDocumentoService;
  }

  /**
   * Crea nuevo {@link ConfiguracionSolicitud}
   * 
   * @param configuracionSolicitud {@link ConfiguracionSolicitud}. que se quiere
   *                               crear.
   * @return Nuevo {@link ConfiguracionSolicitud} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  public ResponseEntity<ConfiguracionSolicitud> create(
      @Valid @RequestBody ConfiguracionSolicitud configuracionSolicitud) {
    log.debug("create(ConfiguracionSolicitud configuracionSolicitud) - start");
    ConfiguracionSolicitud returnValue = service.create(configuracionSolicitud);
    log.debug("create(ConfiguracionSolicitud configuracionSolicitud) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link ConfiguracionSolicitud} por el id de la
   * {@link Convocatoria}.
   * 
   * @param configuracionSolicitud {@link ConfiguracionSolicitud} a actualizar.
   * @param id                     Identificador de la {@link Convocatoria}.
   * @return ConfiguracionSolicitud {@link ConfiguracionSolicitud} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ConfiguracionSolicitud update(@Valid @RequestBody ConfiguracionSolicitud configuracionSolicitud,
      @PathVariable Long id) {
    log.debug("update(ConfiguracionSolicitud configuracionSolicitud, Long id) - start");
    ConfiguracionSolicitud returnValue = service.update(configuracionSolicitud, id);
    log.debug("update(ConfiguracionSolicitud configuracionSolicitud, Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link ConfiguracionSolicitud} por el id de la
   * {@link Convocatoria}.
   * 
   * @param id Identificador de la {@link Convocatoria}.
   * @return ConfiguracionSolicitud la entidad {@link ConfiguracionSolicitud}.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V', 'CSP-SOL-C', 'CSP-SOL-E', 'CSP-SOL-V')")
  ResponseEntity<ConfiguracionSolicitud> findByConvocatoriaId(@PathVariable Long id) {
    log.debug("ConfiguracionSolicitud findByConvocatoriaId(Long id) - start");
    ConfiguracionSolicitud returnValue = service.findByConvocatoriaId(id);

    if (returnValue == null) {
      log.debug("ConfiguracionSolicitud findByConvocatoriaId(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("ConfiguracionSolicitud findByConvocatoriaId(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * 
   * DOCUMENTO REQUERIDO SOLICITUD
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link DocumentoRequeridoSolicitud}
   * de la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/documentorequiridosolicitudes")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V', 'CSP-SOL-E', 'CSP-SOL-V')")
  ResponseEntity<Page<DocumentoRequeridoSolicitud>> findAllConvocatoriaDocumentoRequeridoSolicitud(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaDocumentoRequeridoSolicitud(Long id, String query, Pageable paging) - start");
    Page<DocumentoRequeridoSolicitud> page = documentoRequeridoSolicitudService.findAllByConvocatoria(id, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaDocumentoRequeridoSolicitud(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaDocumentoRequeridoSolicitud(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * Devuelve una lista paginada y filtrada de los {@link TipoDocumento} de la
   * {@link Convocatoria} correspondientes a la fase de presentacion.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/tipodocumentofasepresentaciones")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V')")
  ResponseEntity<Page<TipoDocumento>> findAllTipoDocumentosFasePresentacion(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTipoDocumentosFasePresentacion(Long id, Pageable paging) - start");
    Page<TipoDocumento> page = tipoDocumentoService.findAllTipoDocumentosFasePresentacionConvocatoria(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllTipoDocumentosFasePresentacion(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTipoDocumentosFasePresentacion(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

}
