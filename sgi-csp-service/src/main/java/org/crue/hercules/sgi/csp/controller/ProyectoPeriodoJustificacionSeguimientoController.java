package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.ProyectoPeriodoJustificacionSeguimientoConverter;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionSeguimientoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionSeguimientoOutput;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacionSeguimiento;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoJustificacionSeguimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoPeriodoJustificacionSeguimientoController
 */
@RestController
@RequestMapping(ProyectoPeriodoJustificacionSeguimientoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class ProyectoPeriodoJustificacionSeguimientoController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "proyectos-periodos-justificacion-seguimiento";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final ProyectoPeriodoJustificacionSeguimientoService service;
  private final ProyectoPeriodoJustificacionSeguimientoConverter converter;

  /**
   * Devuelve el {@link ProyectoPeriodoJustificacionSeguimiento} con el id
   * indicado.
   * 
   * @param id Identificador de {@link ProyectoPeriodoJustificacionSeguimiento}.
   * @return {@link ProyectoPeriodoJustificacionSeguimiento} correspondiente al
   *         id.
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public ProyectoPeriodoJustificacionSeguimientoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoPeriodoJustificacionSeguimientoOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea una entidad {@link ProyectoPeriodoJustificacionSeguimiento}.
   * 
   * @param proyectoPeriodoJustificacionSeguimiento datos de la entidad
   *                                                {@link ProyectoPeriodoJustificacionSeguimiento}
   *                                                a crear.
   * @return la entidad {@link ProyectoPeriodoJustificacionSeguimiento} creada.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public ResponseEntity<ProyectoPeriodoJustificacionSeguimientoOutput> create(
      @Valid @RequestBody ProyectoPeriodoJustificacionSeguimientoInput proyectoPeriodoJustificacionSeguimiento) {
    log.debug("create(ProyectoPeriodoJustificacionSeguimientoInput proyectoPeriodoJustificacionSeguimiento) - start");
    ProyectoPeriodoJustificacionSeguimientoOutput returnValue = converter
        .convert(service.create(converter.convert(proyectoPeriodoJustificacionSeguimiento)));

    log.debug("create(ProyectoPeriodoJustificacionSeguimientoInput proyectoPeriodoJustificacionSeguimiento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza la entidad {@link ProyectoPeriodoJustificacionSeguimiento} con id
   * indicado.
   * 
   * @param id                                      identificador de la entidad
   *                                                {@link ProyectoPeriodoJustificacionSeguimiento}.
   * @param proyectoPeriodoJustificacionSeguimiento datos de la entidad
   *                                                {@link ProyectoPeriodoJustificacionSeguimiento}
   *                                                a actualizar.
   * @return la entidad {@link ProyectoPeriodoJustificacionSeguimiento}
   *         actualizada.
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public ProyectoPeriodoJustificacionSeguimientoOutput update(@PathVariable Long id,
      @Valid @RequestBody ProyectoPeriodoJustificacionSeguimientoInput proyectoPeriodoJustificacionSeguimiento) {
    log.debug(
        "update(Long id, ProyectoPeriodoJustificacionSeguimientoInput proyectoPeriodoJustificacionSeguimiento) - start");
    ProyectoPeriodoJustificacionSeguimientoOutput returnValue = converter
        .convert(service.update(converter.convert(proyectoPeriodoJustificacionSeguimiento, id)));

    log.debug(
        "update(Long id, ProyectoPeriodoJustificacionSeguimientoInput proyectoPeriodoJustificacionSeguimiento) - end");
    return returnValue;
  }
}
