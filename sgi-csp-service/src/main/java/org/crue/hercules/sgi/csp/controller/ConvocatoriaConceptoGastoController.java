package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoCodigoEcService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaConceptoGastoController
 */
@RestController
@RequestMapping("/convocatoriaconceptogastos")
@Slf4j
public class ConvocatoriaConceptoGastoController {

  /** ConvocatoriaConceptoGasto service */
  private final ConvocatoriaConceptoGastoService service;
  /** ConvocatoriaConceptoGastoCodigoEc service */
  private final ConvocatoriaConceptoGastoCodigoEcService convocatoriaConceptoGastoCodigoEcService;

  /**
   * Instancia un nuevo ConvocatoriaConceptoGastoController.
   * 
   * @param service                                  {@link ConvocatoriaConceptoGastoService}
   * @param convocatoriaConceptoGastoCodigoEcService {@link ConvocatoriaConceptoGastoCodigoEcService}
   */
  public ConvocatoriaConceptoGastoController(ConvocatoriaConceptoGastoService service,
      ConvocatoriaConceptoGastoCodigoEcService convocatoriaConceptoGastoCodigoEcService) {
    this.service = service;
    this.convocatoriaConceptoGastoCodigoEcService = convocatoriaConceptoGastoCodigoEcService;
  }

  /**
   * Devuelve el {@link ConvocatoriaConceptoGasto} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaConceptoGasto}.
   * @return {@link ConvocatoriaConceptoGasto} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  public ConvocatoriaConceptoGasto findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaConceptoGasto returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link ConvocatoriaConceptoGasto} con el id
   * indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaConceptoGasto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  public ResponseEntity<Void> exists(@PathVariable Long id) {
    log.debug("exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaConceptoGasto}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link ConvocatoriaConceptoGasto} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-V')")
  public ResponseEntity<Page<ConvocatoriaConceptoGasto>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<ConvocatoriaConceptoGasto> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea un nuevo {@link ConvocatoriaConceptoGasto}.
   * 
   * @param convocatoriaConceptoGasto {@link ConvocatoriaConceptoGasto} que se
   *                                  quiere crear.
   * @return Nuevo {@link ConvocatoriaConceptoGasto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ResponseEntity<ConvocatoriaConceptoGasto> create(
      @Valid @RequestBody ConvocatoriaConceptoGasto convocatoriaConceptoGasto) {
    log.debug("create(ConvocatoriaConceptoGasto convocatoriaConceptoGasto) - start");
    ConvocatoriaConceptoGasto returnValue = service.create(convocatoriaConceptoGasto);
    log.debug("create(ConvocatoriaConceptoGasto convocatoriaConceptoGasto) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConvocatoriaConceptoGasto} con el id indicado.
   * 
   * @param convocatoriaConceptoGasto {@link ConvocatoriaConceptoGasto} a
   *                                  actualizar.
   * @param id                        id {@link ConvocatoriaConceptoGasto} a
   *                                  actualizar.
   * @return {@link ConvocatoriaConceptoGasto} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ConvocatoriaConceptoGasto update(
      @Validated({ Update.class, Default.class }) @RequestBody ConvocatoriaConceptoGasto convocatoriaConceptoGasto,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaConceptoGasto convocatoriaConceptoGasto, Long id) - start");
    convocatoriaConceptoGasto.setId(id);
    ConvocatoriaConceptoGasto returnValue = service.update(convocatoriaConceptoGasto);
    log.debug("update(ConvocatoriaConceptoGasto convocatoriaConceptoGasto, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ConvocatoriaConceptoGasto} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaConceptoGasto}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * 
   * CONVOCATORIA GASTOS CÓDIGO ECONÓMICO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaConceptoGastoCodigoEc} permitidos de la
   * {@link ConvocatoriaConceptoGasto}.
   * 
   * @param id     Identificador de {@link ConvocatoriaConceptoGasto}.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaConceptoGastoCodigoEc}
   *         paginadas y filtradas de la {@link ConvocatoriaConceptoGasto}.
   */
  @GetMapping("/{id}/convocatoriagastocodigoec")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E','CSP-CON-V','CSP-CON-INV-V')")
  public ResponseEntity<Page<ConvocatoriaConceptoGastoCodigoEc>> findAllConvocatoriaGastosCodigoEc(
      @PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaGastosCodigoEcPermitidos(Long id, Pageable paging) - start");
    Page<ConvocatoriaConceptoGastoCodigoEc> page = convocatoriaConceptoGastoCodigoEcService
        .findAllByConvocatoriaConceptoGasto(id, paging);
    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaGastosCodigoEcPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaGastosCodigoEcPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Indica si {@link ConvocatoriaConceptoGasto} tiene asignados
   * {@link ConvocatoriaConceptoGastoCodigoEc}
   * 
   * @param id Identificador de {@link ConvocatoriaConceptoGasto}.
   * @return true/false
   */
  @RequestMapping(path = "/{id}/convocatoriagastocodigoec", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ResponseEntity<Void> existsCodigosEconomicos(@PathVariable Long id) {
    log.debug("existsCodigosEconomicos(Long id) - start");
    boolean returnValue = convocatoriaConceptoGastoCodigoEcService.existsByConvocatoriaConceptoGasto(id);

    log.debug("existsCodigosEconomicos(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
