package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.eti.dto.ActaWithNumEvaluaciones;
import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.dto.MemoriaEvaluada;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.BaseEntity.Update;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.service.ActaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
   * @param query          filtro de búsqueda.
   * @param paging         pageable
   * @param authentication Authentication
   * @return la lista de {@link ActaWithNumEvaluaciones} paginadas y/o filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-V','ETI-ACT-INV-ER', 'ETI-ACT-ER')")
  public ResponseEntity<Page<ActaWithNumEvaluaciones>> findAllActaWithNumEvaluaciones(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging,
      Authentication authentication) {
    log.debug("findAllActaWithNumEvaluaciones(String query, Pageable paging) - start");
    String personaRef = authentication.getName();
    Page<ActaWithNumEvaluaciones> page = service.findAllActaWithNumEvaluaciones(query, paging, personaRef);
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
  public Acta replaceActa(@Validated({ Update.class, Default.class }) @RequestBody Acta updatedActa,
      @PathVariable Long id) {
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-V','ETI-ACT-INV-ER','ETI-ACT-ER')")
  public Acta one(@PathVariable Long id) {
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-V','ETI-ACT-INV-ER','ETI-ACT-ER')")
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
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACT-DES')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Acta acta = this.one(id);
    acta.setActivo(Boolean.FALSE);
    service.update(acta);
    log.debug("delete(Long id) - end");
  }

  @PutMapping("/{id}/finalizar")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-FIN','ETI-ACT-DESR')")
  public void finishActa(@PathVariable Long id) {

    log.debug("finalizarActa(Long id) - start");
    service.finishActa(id);
    log.debug("finalizarActa(Long id) - end");

  }

  /**
   * Obtiene el número de {@link Evaluacion} nuevas de un {@link Acta}
   * 
   * @param idActa id {@link Acta}
   * @return Número de evaluaciones nuevas
   */
  @GetMapping("/{idActa}/numero-evaluaciones-nuevas")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-ACT-DES', 'ETI-ACT-DESR', 'ETI-ACT-INV-DESR')")
  public ResponseEntity<Long> countEvaluacionesNuevas(@PathVariable Long idActa) {
    log.debug("countEvaluacionesNuevas(@PathVariable Long idActa) - start");
    Long countNumEvaluaciones = service.countEvaluacionesNuevas(idActa);
    log.debug("countEvaluacionesNuevas(@PathVariable Long idActa) - end");
    return new ResponseEntity<>(countNumEvaluaciones, HttpStatus.OK);
  }

  /**
   * Obtiene el número de {@link Evaluacion} de revisión sin las de revision
   * mínima de un {@link Acta}
   * 
   * @param idActa id {@link Acta}
   * @return Número de evaluaciones
   */
  @GetMapping("/{idActa}/numero-evaluaciones-revision-sin-minima")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-ACT-DES', 'ETI-ACT-DESR', 'ETI-ACT-INV-DESR')")
  public ResponseEntity<Long> countEvaluacionesRevisionSinMinima(@PathVariable Long idActa) {
    log.debug("countEvaluacionesRevisionSinMinima(@PathVariable Long idActa) - start");
    Long countNumEvaluaciones = service.countEvaluacionesRevisionSinMinima(idActa);
    log.debug("countEvaluacionesRevisionSinMinima(@PathVariable Long idActa) - end");
    return new ResponseEntity<>(countNumEvaluaciones, HttpStatus.OK);
  }

  /**
   * Devuelve una lista de {@link MemoriaEvaluada} sin las de revisión mínima para
   * una determinada {@link Acta}
   * 
   * @param idActa Id de {@link Acta}.
   * @return lista de memorias evaluadas
   */
  @GetMapping("/{idActa}/memorias-evaluadas")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-ACT-DES', 'ETI-ACT-DESR', 'ETI-ACT-INV-DESR')")
  public List<MemoriaEvaluada> findAllMemoriasEvaluadasSinRevMinimaByActaId(@PathVariable Long idActa) {
    return service.findAllMemoriasEvaluadasSinRevMinimaByActaId(idActa);
  }

  /**
   * Obtiene el documento de un {@link Acta}
   * 
   * @param idActa id {@link Acta}
   * @return el documento del acta
   */
  @GetMapping("/{idActa}/documento")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-DES', 'ETI-ACT-DESR', 'ETI-ACT-INV-DESR')")
  public ResponseEntity<DocumentoOutput> documentoActa(@PathVariable Long idActa) {
    log.debug("documentoActa(@PathVariable Long idActa) - start");
    DocumentoOutput documento = service.generarDocumentoActa(idActa);
    log.debug("documentoActa(@PathVariable Long idActa) - end");
    return new ResponseEntity<>(documento, HttpStatus.OK);
  }

  /**
   * Comprueba si la persona es miembro activo del comité del {@link Acta}
   * 
   * @param id             Identificador de {@link Acta}.
   * @param authentication Authentication
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/miembro-comite", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACT-INV-ER')")
  public ResponseEntity<?> isMiembroActivoComiteActa(@PathVariable Long id, Authentication authentication) {
    log.debug("isMiembroActivoComiteActa(Long id, Authentication authentication) - start");
    String personaRef = authentication.getName();
    if (service.isMiembroComiteActa(personaRef, id)) {
      log.debug("isMiembroActivoComiteActa(Long id, Authentication authentication) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("isResponsableOrCreador(Long id, Authentication authentication) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si el registro blockchain ha sido confirmado correctamente o ha
   * sido alterado
   * 
   * @param id Identificador de {@link Acta}.
   * @return HTTP 200 si es correcto y HTTP 204 si se ha alterado
   */
  @RequestMapping(path = "/{id}/confirmar-registro-blockchain", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-V','ETI-ACT-INV-ER','ETI-ACT-ER')")
  public ResponseEntity<?> confirmarRegistroBlockchain(@PathVariable Long id) {
    log.debug("Acta confirmarRegistroBlockchain(Long id) - start");
    if (service.confirmarRegistroBlockchain(id).booleanValue()) {
      log.debug("Acta confirmarRegistroBlockchain(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("Acta confirmarRegistroBlockchain(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
