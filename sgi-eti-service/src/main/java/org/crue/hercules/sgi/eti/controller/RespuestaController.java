package org.crue.hercules.sgi.eti.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.service.RespuestaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RespuestaController
 */
@RestController
@RequestMapping("/respuestas")
@Slf4j
public class RespuestaController {

  /** Respuesta service */
  private final RespuestaService service;

  /**
   * Instancia un nuevo RespuestaController.
   * 
   * @param service RespuestaService
   */
  public RespuestaController(RespuestaService service) {
    log.debug("RespuestaController(RespuestaService service) - start");
    this.service = service;
    log.debug("RespuestaController(RespuestaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Respuesta}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Respuesta>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Respuesta> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link Respuesta}.
   * 
   * @param nuevoRespuesta {@link Respuesta}. que se quiere crear.
   * @return Nuevo {@link Respuesta} creado.
   */
  @PostMapping
  public ResponseEntity<Respuesta> newRespuesta(@Valid @RequestBody Respuesta nuevoRespuesta) {
    log.debug("newRespuesta(Respuesta nuevoRespuesta) - start");
    Respuesta returnValue = service.create(nuevoRespuesta);
    log.debug("newRespuesta(Respuesta nuevoRespuesta) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Respuesta}.
   * 
   * @param updatedRespuesta {@link Respuesta} a actualizar.
   * @param id               id {@link Respuesta} a actualizar.
   * @return {@link Respuesta} actualizado.
   */
  @PutMapping("/{id}")
  Respuesta replaceRespuesta(@Valid @RequestBody Respuesta updatedRespuesta, @PathVariable Long id) {
    log.debug("replaceRespuesta(Respuesta updatedRespuesta, Long id) - start");
    updatedRespuesta.setId(id);
    Respuesta returnValue = service.update(updatedRespuesta);
    log.debug("replaceRespuesta(Respuesta updatedRespuesta, Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos de la restrospectiva en la memoria con los valores de la
   * respuesta si el formulario es de tipo M20.
   * 
   * @param id identificador de la {@link Respuesta} con los datos de la
   *           retrospectiva
   * @return {@link HttpStatus#NO_CONTENT}
   */
  @PatchMapping("/{id}/update-datos-retrospectiva")
  public ResponseEntity<Void> updateDatosRetrospectiva(@PathVariable Long id) {
    log.debug("updateDatosRetrospectiva(Long id) - start");

    service.updateDatosRetrospectiva(id);
    log.debug("updateDatosRetrospectiva(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link Respuesta} con el id indicado.
   * 
   * @param id Identificador de {@link Respuesta}.
   * @return {@link Respuesta} correspondiente al id.
   */
  @GetMapping("/{id}")
  Respuesta one(@PathVariable Long id) {
    log.debug("Respuesta one(Long id) - start");
    Respuesta returnValue = service.findById(id);
    log.debug("Respuesta one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Respuesta} con id indicado.
   * 
   * @param id Identificador de {@link Respuesta}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Devuelve el {@link Respuesta} con el id indicado.
   * 
   * @param id Identificador de {@link Respuesta}.
   * @return {@link Respuesta} correspondiente al id.
   */
  @GetMapping("/{idMemoria}/{idApartado}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-INV-ER')")
  Respuesta findByMemoriaIdAndApartadoId(@PathVariable Long idMemoria, @PathVariable Long idApartado) {
    log.debug("Respuesta findByMemoriaIdAndApartadoId(Long idMemoria, Long idApartado) - start");
    Respuesta returnValue = service.findByMemoriaIdAndApartadoId(idMemoria, idApartado);
    log.debug("Respuesta findByMemoriaIdAndApartadoId(Long idMemoria, Long idApartado) - end");
    return returnValue;
  }

  /**
   * Devuelve la última {@link Respuesta} de la Memoria con el id indicado.
   * 
   * @param id Identificador de {@link Memoria}.
   * @return {@link Respuesta} correspondiente si existe.
   */
  @GetMapping("/{idMemoria}/last")
  ResponseEntity<Respuesta> last(@PathVariable Long idMemoria) {
    log.debug("last(Long id) - start");
    Optional<Respuesta> returnValue = service.findLastByMemoriaId(idMemoria);
    if (!returnValue.isPresent()) {
      log.debug("last(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("last(Long id) - end");
    return new ResponseEntity<>(returnValue.get(), HttpStatus.OK);
  }

}
