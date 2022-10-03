package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.ProyectoSeguimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.model.ProyectoSeguimientoJustificacion;
import org.crue.hercules.sgi.csp.service.ProyectoSeguimientoJustificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoSeguimientoJustificacionController
 */
@RestController
@RequestMapping(ProyectoSeguimientoJustificacionController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class ProyectoSeguimientoJustificacionController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "proyectos-seguimiento-justificacion";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final ProyectoSeguimientoJustificacionService service;
  private final ProyectoSeguimientoJustificacionConverter converter;

  /**
   * Crea una entidad {@link ProyectoSeguimientoJustificacion}.
   * 
   * @param proyectoSeguimientoJustificacion datos de la entidad
   *                                         {@link ProyectoSeguimientoJustificacion}
   *                                         a crear.
   * @return la entidad {@link ProyectoSeguimientoJustificacion} creada.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public ResponseEntity<ProyectoSeguimientoJustificacionOutput> create(
      @Valid @RequestBody ProyectoSeguimientoJustificacionInput proyectoSeguimientoJustificacion) {
    log.debug("create(ProyectoSeguimientoJustificacionInput ProyectoSeguimientoJustificacion) - start");
    ProyectoSeguimientoJustificacionOutput returnValue = converter
        .convert(service.create(converter.convert(proyectoSeguimientoJustificacion)));

    log.debug("create(ProyectoSeguimientoJustificacionInput ProyectoSeguimientoJustificacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza la entidad {@link ProyectoSeguimientoJustificacion} con id
   * indicado.
   * 
   * @param id                               identificador de la entidad
   *                                         {@link ProyectoSeguimientoJustificacion}.
   * @param proyectoSeguimientoJustificacion datos de la entidad
   *                                         {@link ProyectoSeguimientoJustificacion}
   *                                         a actualizar.
   * @return la entidad {@link ProyectoSeguimientoJustificacion} actualizada.
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public ProyectoSeguimientoJustificacionOutput update(@PathVariable Long id,
      @Valid @RequestBody ProyectoSeguimientoJustificacionInput proyectoSeguimientoJustificacion) {
    log.debug("update(Long id, ProyectoSeguimientoJustificacionInput ProyectoSeguimientoJustificacion) - start");
    ProyectoSeguimientoJustificacionOutput returnValue = converter
        .convert(service.update(converter.convert(proyectoSeguimientoJustificacion, id)));

    log.debug("update(Long id, ProyectoSeguimientoJustificacionInput ProyectoSeguimientoJustificacion) - end");
    return returnValue;
  }
}
