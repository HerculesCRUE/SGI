package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoCodigoEcService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoConceptoGastoCodigoEcController
 */
@RestController
@RequestMapping(ProyectoConceptoGastoCodigoEcController.MAPPING)
@Slf4j
public class ProyectoConceptoGastoCodigoEcController {

  public static final String MAPPING = "/proyectoconceptogastocodigosec";

  /** ProyectoConceptoGastoCodigoEc service */
  private final ProyectoConceptoGastoCodigoEcService service;

  /**
   * Instancia un nuevo ProyectoConceptoGastoCodigoEcController.
   * 
   * @param service {@link ProyectoConceptoGastoCodigoEcService}
   */
  public ProyectoConceptoGastoCodigoEcController(ProyectoConceptoGastoCodigoEcService service) {
    this.service = service;
  }

  /**
   * Devuelve el {@link ProyectoConceptoGastoCodigoEc} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoConceptoGastoCodigoEc}.
   * @return {@link ProyectoConceptoGastoCodigoEc} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ProyectoConceptoGastoCodigoEc findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoConceptoGastoCodigoEc returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ProyectoConceptoGastoCodigoEc}
   * activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoConceptoGastoCodigoEc}
   *         paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoConceptoGastoCodigoEc>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Page<ProyectoConceptoGastoCodigoEc> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Actualiza el listado de {@link ProyectoConceptoGastoCodigoEc} del
   * {@link ProyectoConceptoGasto} con el listado codigosEconomicos añadiendo,
   * editando o eliminando los elementos segun proceda.
   * 
   * @param proyectoConceptoGastoId Id del {@link ProyectoConceptoGasto}.
   * @param codigosEconomicos       lista con los nuevos
   *                                {@link ProyectoConceptoGastoCodigoEc} a
   *                                guardar.
   * @return Lista actualizada con los {@link ProyectoConceptoGastoCodigoEc}.
   */
  @PatchMapping("/{proyectoConceptoGastoId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ResponseEntity<List<ProyectoConceptoGastoCodigoEc>> update(@PathVariable Long proyectoConceptoGastoId,
      @Valid @RequestBody List<ProyectoConceptoGastoCodigoEc> codigosEconomicos) {
    log.debug("update(List<ProyectoConceptoGastoCodigoEc> codigosEconomicos, proyectoConceptoGastoId) - start");
    List<ProyectoConceptoGastoCodigoEc> returnValue = service.update(proyectoConceptoGastoId, codigosEconomicos);
    log.debug("update(List<ProyectoConceptoGastoCodigoEc> codigosEconomicos, proyectoConceptoGastoId) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
