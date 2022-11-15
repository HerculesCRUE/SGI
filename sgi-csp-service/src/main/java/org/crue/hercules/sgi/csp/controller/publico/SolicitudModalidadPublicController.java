package org.crue.hercules.sgi.csp.controller.publico;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.service.SolicitudModalidadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudModalidadPublicController
 */
@RestController
@RequestMapping(SolicitudModalidadPublicController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class SolicitudModalidadPublicController {
  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER
      + "solicitudes/{solicitudId}/solicitudmodalidades";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final SolicitudModalidadService service;

  /**
   * Crea nuevo {@link SolicitudModalidad}
   * 
   * @param solicitudId        Identificador {@link Solicitud} a actualizar.
   * @param solicitudModalidad {@link SolicitudModalidad}. que se quiere crear.
   * @return Nuevo {@link SolicitudModalidad} creado.
   */
  @PostMapping()
  public ResponseEntity<SolicitudModalidad> createSolicitudModalidad(@PathVariable String solicitudId,
      @Valid @RequestBody SolicitudModalidad solicitudModalidad) {
    log.debug("createSolicitudModalidad(String solicitudId, SolicitudModalidad solicitudModalidad) - start");
    SolicitudModalidad returnValue = service.createByExternalUser(solicitudId, solicitudModalidad);
    log.debug("createSolicitudModalidad(String solicitudId, SolicitudModalidad solicitudModalidad) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudModalidad}.
   * 
   * @param solicitudId        Identificador {@link Solicitud} a actualizar.
   * @param solicitudModalidad {@link SolicitudModalidad} a actualizar.
   * @param id                 Identificador {@link SolicitudModalidad} a
   *                           actualizar.
   * @return SolicitudModalidad {@link SolicitudModalidad} actualizado
   */
  @PutMapping(PATH_ID)
  public SolicitudModalidad updateSolicitudModalidad(@PathVariable String solicitudId,
      @Valid @RequestBody SolicitudModalidad solicitudModalidad, @PathVariable Long id) {
    log.debug("updateSolicitudModalidad(String solicitudId, SolicitudModalidad solicitudModalidad, Long id) - start");
    solicitudModalidad.setId(id);
    SolicitudModalidad returnValue = service.updateByExternalUser(solicitudId, solicitudModalidad);
    log.debug("updateSolicitudModalidad(String solicitudId, SolicitudModalidad solicitudModalidad, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link SolicitudModalidad} con id indicado.
   * 
   * @param publicId Identificador {@link Solicitud} a actualizar.
   * @param id       Identificador de {@link SolicitudModalidad}.
   */
  @DeleteMapping(PATH_ID)
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteSolicitudModalidad(@PathVariable String publicId, @PathVariable Long id) {
    log.debug("deleteSolicitudModalidad(String publicId, Long id) - start");
    service.deleteByExternalUser(publicId, id);
    log.debug("deleteSolicitudModalidad(String publicId, Long id) - end");
  }

}
