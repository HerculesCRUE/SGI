package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoCodigoEcService;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoService;
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
 * ProyectoConceptoGastoController
 */
@RestController
@RequestMapping("/proyectoconceptosgasto")
@Slf4j
public class ProyectoConceptoGastoController {

  /** ProyectoConceptoGasto service */
  private final ProyectoConceptoGastoService service;
  /** ProyectoConceptoGastoCodigoEc service */
  private final ProyectoConceptoGastoCodigoEcService proyectoConceptoGastoCodigoEcService;

  /**
   * Instancia un nuevo ProyectoConceptoGastoController.
   * 
   * @param service                              {@link ProyectoConceptoGastoService}
   * @param proyectoConceptoGastoCodigoEcService {@link ProyectoConceptoGastoCodigoEcService}
   */
  public ProyectoConceptoGastoController(ProyectoConceptoGastoService service,
      ProyectoConceptoGastoCodigoEcService proyectoConceptoGastoCodigoEcService) {
    this.service = service;
    this.proyectoConceptoGastoCodigoEcService = proyectoConceptoGastoCodigoEcService;
  }

  /**
   * Devuelve el {@link ProyectoConceptoGasto} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoConceptoGasto}.
   * @return {@link ProyectoConceptoGasto} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ProyectoConceptoGasto findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoConceptoGasto returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link ProyectoConceptoGasto} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoConceptoGasto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoConceptoGasto}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link ProyectoConceptoGasto} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ProyectoConceptoGasto>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<ProyectoConceptoGasto> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea un nuevo {@link ProyectoConceptoGasto}.
   * 
   * @param proyectoConceptoGasto {@link ProyectoConceptoGasto} que se quiere
   *                              crear.
   * @return Nuevo {@link ProyectoConceptoGasto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<ProyectoConceptoGasto> create(@Valid @RequestBody ProyectoConceptoGasto proyectoConceptoGasto) {
    log.debug("create(ProyectoConceptoGasto proyectoConceptoGasto) - start");
    ProyectoConceptoGasto returnValue = service.create(proyectoConceptoGasto);
    log.debug("create(ProyectoConceptoGasto proyectoConceptoGasto) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProyectoConceptoGasto} con el id indicado.
   * 
   * @param proyectoConceptoGasto {@link ProyectoConceptoGasto} a actualizar.
   * @param id                    id {@link ProyectoConceptoGasto} a actualizar.
   * @return {@link ProyectoConceptoGasto} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ProyectoConceptoGasto update(
      @Validated({ Update.class, Default.class }) @RequestBody ProyectoConceptoGasto proyectoConceptoGasto,
      @PathVariable Long id) {
    log.debug("update(ProyectoConceptoGasto proyectoConceptoGasto, Long id) - start");
    proyectoConceptoGasto.setId(id);
    ProyectoConceptoGasto returnValue = service.update(proyectoConceptoGasto);
    log.debug("update(ProyectoConceptoGasto proyectoConceptoGasto, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ProyectoConceptoGasto} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoConceptoGasto}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * 
   * PROYECTO CONCEPTOS GASTO CÓDIGO ECONÓMICO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ProyectoConceptoGastoCodigoEc} permitidos del
   * {@link ProyectoConceptoGasto}.
   * 
   * @param id     Identificador de {@link ProyectoConceptoGasto}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoconceptogastocodigosec")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ProyectoConceptoGastoCodigoEc>> findAllProyectoGastosCodigoEc(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoGastosCodigoEcPermitidos(Long id, Pageable paging) - start");
    Page<ProyectoConceptoGastoCodigoEc> page = proyectoConceptoGastoCodigoEcService.findAllByProyectoConceptoGasto(id,
        paging);
    if (page.isEmpty()) {
      log.debug("findAllProyectoGastosCodigoEcPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoGastosCodigoEcPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Indica si el {@link ProyectoConceptoGasto} tiene asignados
   * {@link ProyectoConceptoGastoCodigoEc}
   * 
   * @param id Identificador de {@link ProyectoConceptoGasto}.
   * @return true/false
   */
  @RequestMapping(path = "/{id}/proyectoconceptogastocodigosec", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<?> existsCodigosEconomicos(@PathVariable Long id) {
    log.debug("existsCodigosEconomicos(Long id) - start");
    boolean returnValue = proyectoConceptoGastoCodigoEcService.existsByProyectoConceptoGasto(id);

    log.debug("existsCodigosEconomicos(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si existen diferencias entre los codigos economicos del
   * {@link ProyectoConceptoGasto} y el {@link ConvocatoriaConceptoGasto}
   * relacionado.
   * 
   * @param id Identificador de {@link ConvocatoriaConceptoGasto}.
   * @return HTTP 200 si tiene diferencias y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/proyectoconceptogastocodigosec/differences", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<?> hasDifferencesCodigosEcConvocatoria(@PathVariable Long id) {
    log.debug("hasDifferencesCodigosEcConvocatoria(Long id) - start");
    if (service.hasDifferencesCodigosEcConvocatoria(id)) {
      log.debug("hasDifferencesCodigosEcConvocatoria(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("hasDifferencesCodigosEcConvocatoria(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
