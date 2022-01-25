package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoJustificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaPeriodoJustificacionController
 */

@RestController
@RequestMapping("/convocatoriaperiodojustificaciones")
@Slf4j
public class ConvocatoriaPeriodoJustificacionController {

  /** ConvocatoriaPeriodoJustificacion service */
  private final ConvocatoriaPeriodoJustificacionService service;

  public ConvocatoriaPeriodoJustificacionController(
      ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService) {
    log.debug(
        "ConvocatoriaPeriodoJustificacionController(ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService) - start");
    this.service = convocatoriaPeriodoJustificacionService;
    log.debug(
        "ConvocatoriaPeriodoJustificacionController(ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService) - end");
  }

  /**
   * Devuelve el {@link ConvocatoriaPeriodoJustificacion} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaPeriodoJustificacion}.
   * @return {@link ConvocatoriaPeriodoJustificacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ConvocatoriaPeriodoJustificacion findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaPeriodoJustificacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link ConvocatoriaPeriodoJustificacion} de la
   * {@link Convocatoria} con el listado convocatoriaPeriodoJustificaciones
   * añadiendo, editando o eliminando los elementos segun proceda.
   * 
   * @param convocatoriaId                     Id de la {@link Convocatoria}.
   * @param convocatoriaPeriodoJustificaciones lista con los nuevos
   *                                           {@link ConvocatoriaPeriodoJustificacion}
   *                                           a guardar.
   * @return Lista actualizada con los {@link ConvocatoriaPeriodoJustificacion}.
   */
  @PatchMapping("/{convocatoriaId}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  public ResponseEntity<List<ConvocatoriaPeriodoJustificacion>> updateConvocatoriaPeriodoJustificacionesConvocatoria(
      @PathVariable Long convocatoriaId,
      @Valid @RequestBody List<ConvocatoriaPeriodoJustificacion> convocatoriaPeriodoJustificaciones) {
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long convocatoriaId, List<ConvocatoriaPeriodoJustificacion> convocatoriaPeriodoJustificaciones) - start");
    List<ConvocatoriaPeriodoJustificacion> returnValue = service
        .updateConvocatoriaPeriodoJustificacionesConvocatoria(convocatoriaId, convocatoriaPeriodoJustificaciones);
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long convocatoriaId, List<ConvocatoriaPeriodoJustificacion> convocatoriaPeriodoJustificaciones) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
