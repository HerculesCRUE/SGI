package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoCodigoEcService;
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
 * ConvocatoriaConceptoGastoCodigoEcController
 */
@RestController
@RequestMapping("/convocatoriaconceptogastocodigoecs")
@Slf4j
public class ConvocatoriaConceptoGastoCodigoEcController {

  /** ConvocatoriaConceptoGastoCodigoEc service */
  private final ConvocatoriaConceptoGastoCodigoEcService service;

  /**
   * Instancia un nuevo ConvocatoriaConceptoGastoCodigoEcController.
   * 
   * @param service {@link ConvocatoriaConceptoGastoCodigoEcService}
   */
  public ConvocatoriaConceptoGastoCodigoEcController(ConvocatoriaConceptoGastoCodigoEcService service) {
    this.service = service;
  }

  /**
   * Devuelve el {@link ConvocatoriaConceptoGastoCodigoEc} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaConceptoGastoCodigoEc}.
   * @return {@link ConvocatoriaConceptoGastoCodigoEc} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ConvocatoriaConceptoGastoCodigoEc findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaConceptoGastoCodigoEc returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link ConvocatoriaConceptoGastoCodigoEc} de la
   * {@link ConvocatoriaConceptoGasto} con el listado codigosEconomicos añadiendo,
   * editando o eliminando los elementos segun proceda.
   * 
   * @param convocatoriaConceptoGastoId Id de la
   *                                    {@link ConvocatoriaConceptoGasto}.
   * @param codigosEconomicos           lista con los nuevos
   *                                    {@link ConvocatoriaConceptoGastoCodigoEc}
   *                                    a guardar.
   * @return Lista actualizada con los {@link ConvocatoriaConceptoGastoCodigoEc}.
   */
  @PatchMapping("/{convocatoriaConceptoGastoId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ResponseEntity<List<ConvocatoriaConceptoGastoCodigoEc>> update(@PathVariable Long convocatoriaConceptoGastoId,
      @Valid @RequestBody List<ConvocatoriaConceptoGastoCodigoEc> codigosEconomicos) {
    log.debug("update(List<ConvocatoriaConceptoGastoCodigoEc> codigosEconomicos, convocatoriaConceptoGastoId) - start");
    List<ConvocatoriaConceptoGastoCodigoEc> returnValue = service.update(convocatoriaConceptoGastoId,
        codigosEconomicos);
    log.debug("update(List<ConvocatoriaConceptoGastoCodigoEc> codigosEconomicos, convocatoriaConceptoGastoId) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
