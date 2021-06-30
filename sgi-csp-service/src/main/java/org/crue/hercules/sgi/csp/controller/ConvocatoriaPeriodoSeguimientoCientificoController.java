package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoSeguimientoCientificoService;
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
 * ConvocatoriaPeriodoSeguimientoCientificoController
 */

@RestController
@RequestMapping("/convocatoriaperiodoseguimientocientificos")
@Slf4j
public class ConvocatoriaPeriodoSeguimientoCientificoController {

  /** ConvocatoriaPeriodoSeguimientoCientifico service */
  private final ConvocatoriaPeriodoSeguimientoCientificoService service;

  public ConvocatoriaPeriodoSeguimientoCientificoController(
      ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService) {
    log.debug(
        "ConvocatoriaPeriodoSeguimientoCientificoController(ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService) - start");
    this.service = convocatoriaPeriodoSeguimientoCientificoService;
    log.debug(
        "ConvocatoriaPeriodoSeguimientoCientificoController(ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService) - end");
  }

  /**
   * Actualiza el listado de {@link ConvocatoriaPeriodoSeguimientoCientifico} de
   * la {@link Convocatoria} con el listado
   * convocatoriaPeriodoSeguimientoCientificos añadiendo, editando o eliminando
   * los elementos segun proceda.
   * 
   * @param convocatoriaId                            Id de la
   *                                                  {@link Convocatoria}.
   * @param convocatoriaPeriodoSeguimientoCientificos lista con los nuevos
   *                                                  {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *                                                  a guardar.
   * @return Lista actualizada con los
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   */
  @PatchMapping("/{convocatoriaId}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  public ResponseEntity<List<ConvocatoriaPeriodoSeguimientoCientifico>> updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(
      @PathVariable Long convocatoriaId,
      @Valid @RequestBody List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriaPeriodoSeguimientoCientificos) {
    log.debug(
        "updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriaPeriodoSeguimientoCientificoes, convocatoriaId) - start");
    List<ConvocatoriaPeriodoSeguimientoCientifico> returnValue = service
        .updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            convocatoriaPeriodoSeguimientoCientificos);
    log.debug(
        "updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriaPeriodoSeguimientoCientificoes, convocatoriaId) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Devuelve el {@link ConvocatoriaPeriodoSeguimientoCientifico} con el id
   * indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * @return ConvocatoriaPeriodoSeguimientoCientifico
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico} correspondiente al
   *         id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ConvocatoriaPeriodoSeguimientoCientifico findById(@PathVariable Long id) {
    log.debug("ConvocatoriaPeriodoSeguimientoCientifico findById(Long id) - start");
    ConvocatoriaPeriodoSeguimientoCientifico returnValue = service.findById(id);
    log.debug("ConvocatoriaPeriodoSeguimientoCientifico findById(Long id) - end");
    return returnValue;
  }

}
