package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaEntidadConvocanteController
 */

@RestController
@RequestMapping("/convocatoriaentidadconvocantes")
@Slf4j
public class ConvocatoriaEntidadConvocanteController {

  /** ConvocatoriaEntidadConvocante service */
  private final ConvocatoriaEntidadConvocanteService service;

  public ConvocatoriaEntidadConvocanteController(
      ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService) {
    log.debug(
        "ConvocatoriaEntidadConvocanteController(ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService) - start");
    this.service = convocatoriaEntidadConvocanteService;
    log.debug(
        "ConvocatoriaEntidadConvocanteController(ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService) - end");
  }

  /**
   * Devuelve el {@link ConvocatoriaEntidadConvocante} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaEntidadConvocante}.
   * @return {@link ConvocatoriaEntidadConvocante} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ConvocatoriaEntidadConvocante findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaEntidadConvocante returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea nuevo {@link ConvocatoriaEntidadConvocante}.
   * 
   * @param convocatoriaEntidadConvocante {@link ConvocatoriaEntidadConvocante}.
   *                                      que se quiere crear.
   * @return Nuevo {@link ConvocatoriaEntidadConvocante} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  public ResponseEntity<ConvocatoriaEntidadConvocante> create(
      @Valid @RequestBody ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante) {
    log.debug("create(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante) - start");
    ConvocatoriaEntidadConvocante returnValue = service.create(convocatoriaEntidadConvocante);
    log.debug("create(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConvocatoriaEntidadConvocante} con el id indicado.
   * 
   * @param convocatoriaEntidadConvocante {@link ConvocatoriaEntidadConvocante} a
   *                                      actualizar.
   * @param id                            id {@link ConvocatoriaEntidadConvocante}
   *                                      a actualizar.
   * @return {@link ConvocatoriaEntidadConvocante} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  ConvocatoriaEntidadConvocante update(@Valid @RequestBody ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante, Long id) - start");
    convocatoriaEntidadConvocante.setId(id);
    ConvocatoriaEntidadConvocante returnValue = service.update(convocatoriaEntidadConvocante);
    log.debug("update(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link ConvocatoriaEntidadConvocante} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaEntidadConvocante}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }
}
