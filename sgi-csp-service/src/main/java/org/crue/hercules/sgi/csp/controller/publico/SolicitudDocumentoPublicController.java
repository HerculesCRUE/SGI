package org.crue.hercules.sgi.csp.controller.publico;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.service.SolicitudDocumentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudDocumentoPublicController
 */
@RestController
@RequestMapping(SolicitudDocumentoPublicController.REQUEST_MAPPING)
@Slf4j
public class SolicitudDocumentoPublicController {
  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER
      + "solicitudes/{solicitudId}/solicituddocumentos";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  /** SolicitudDocumentoService service */
  private final SolicitudDocumentoService service;

  /**
   * Instancia un nuevo SolicitudDocumentoPublicController.
   * 
   * @param solicitudDocumentoService {@link SolicitudDocumentoService}.
   */
  public SolicitudDocumentoPublicController(SolicitudDocumentoService solicitudDocumentoService) {
    this.service = solicitudDocumentoService;
  }

  /**
   * Crea nuevo {@link SolicitudDocumento}
   * 
   * @param solicitudId        Identificador {@link Solicitud} a actualizar.
   * @param solicitudDocumento {@link SolicitudDocumento}. que se quiere crear.
   * @return Nuevo {@link SolicitudDocumento} creado.
   */
  @PostMapping
  public ResponseEntity<SolicitudDocumento> create(@PathVariable String solicitudId,
      @Valid @RequestBody SolicitudDocumento solicitudDocumento) {
    log.debug("create(String solicitudId, SolicitudDocumento solicitudDocumento) - start");
    SolicitudDocumento returnValue = service.createByExternalUser(solicitudId, solicitudDocumento);
    log.debug("create(String solicitudId, SolicitudDocumento solicitudDocumento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudDocumento}.
   * 
   * @param solicitudId        Identificador {@link Solicitud} a actualizar.
   * @param solicitudDocumento {@link SolicitudDocumento} a actualizar.
   * @param id                 Identificador {@link SolicitudDocumento} a
   *                           actualizar.
   * @param authentication     Datos autenticación.
   * @return SolicitudDocumento {@link SolicitudDocumento} actualizado
   */
  @PutMapping("/{id}")
  public SolicitudDocumento update(@PathVariable String solicitudId,
      @Valid @RequestBody SolicitudDocumento solicitudDocumento, @PathVariable Long id,
      Authentication authentication) {
    log.debug("update(String solicitudId, SolicitudDocumento solicitudDocumento, Long id) - start");
    solicitudDocumento.setId(id);
    SolicitudDocumento returnValue = service.updateByExternalUser(solicitudId, solicitudDocumento);
    log.debug("update(String solicitudId, SolicitudDocumento solicitudDocumento, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link SolicitudDocumento} con id indicado.
   * 
   * @param solicitudId Identificador {@link Solicitud} a actualizar.
   * @param id          Identificador de {@link SolicitudDocumento}.
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable String solicitudId, @PathVariable Long id) {
    log.debug("deleteById(String solicitudId, Long id) - start");
    service.deleteByExternalUser(solicitudId, id);
    log.debug("deleteById(String solicitudId, Long id) - end");
  }

}
