package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.eti.dto.ActaWithNumEvaluaciones;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.BaseEntity.Update;
import org.crue.hercules.sgi.eti.service.ActaService;
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
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ActaController
 */
@RestController
@RequestMapping("/actas")
@Slf4j
public class ActaController {

  /** Acta service */
  private final ActaService service;

  /**
   * Instancia un nuevo ActaController.
   * 
   * @param service {@link ActaService}
   */
  public ActaController(ActaService service) {
    log.debug("ActaController(ActaService service) - start");
    this.service = service;
    log.debug("ActaController(ActaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ActaWithNumEvaluaciones}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return la lista de {@link ActaWithNumEvaluaciones} paginadas y/o filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACT-V')")
  ResponseEntity<Page<ActaWithNumEvaluaciones>> findAllActaWithNumEvaluaciones(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllActaWithNumEvaluaciones(String query, Pageable paging) - start");
    Page<ActaWithNumEvaluaciones> page = service.findAllActaWithNumEvaluaciones(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllActaWithNumEvaluaciones(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllActaWithNumEvaluaciones(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link Acta}.
   * 
   * @param nuevoActa {@link Acta} que se quiere crear.
   * @return Nuevo {@link Acta} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACT-C')")
  public ResponseEntity<Acta> newActa(@Valid @RequestBody Acta nuevoActa) {
    log.debug("newActa(Acta nuevoActa) - start");
    Acta returnValue = service.create(nuevoActa);
    log.debug("newActa(Acta nuevoActa) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Acta}.
   * 
   * @param updatedActa {@link Acta} a actualizar.
   * @param id          id {@link Acta} a actualizar.
   * @return {@link Acta} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACT-E')")
  Acta replaceActa(@Validated({ Update.class, Default.class }) @RequestBody Acta updatedActa, @PathVariable Long id) {
    log.debug("replaceActa(Acta updatedActa, Long id) - start");
    updatedActa.setId(id);
    Acta returnValue = service.update(updatedActa);
    log.debug("replaceActa(Acta updatedActa, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Acta} con el id indicado.
   * 
   * @param id Identificador de {@link Acta}.
   * @return {@link Acta} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACT-V')")
  Acta one(@PathVariable Long id) {
    log.debug("Acta one(Long id) - start");
    Acta returnValue = service.findById(id);
    log.debug("Acta one(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link Acta} con el id indicado.
   * 
   * @param id Identificador de {@link Acta}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("Acta exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("Acta exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("Acta exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Elimina {@link Acta} con id indicado.
   * 
   * @param id Identificador de {@link Acta}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACT-B')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Acta acta = this.one(id);
    acta.setActivo(Boolean.FALSE);
    service.update(acta);
    log.debug("delete(Long id) - end");
  }

  /**
   * 
   */
  @PutMapping("/{id}/finalizar")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACT-FIN')")
  void finishActa(@PathVariable Long id) {

    log.debug("finalizarActa(Long id) - start");
    service.finishActa(id);
    log.debug("finalizarActa(Long id) - end");

  }
}
