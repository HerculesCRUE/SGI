package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.service.AreaTematicaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AreaTematicaController
 */
@RestController
@RequestMapping("/areatematicas")
@Slf4j
@RequiredArgsConstructor
public class AreaTematicaController {

  /** AreaTematica service */
  private final AreaTematicaService service;

  /**
   * Devuelve el {@link AreaTematica} con el id indicado.
   * 
   * @param id Identificador de {@link AreaTematica}.
   * @return {@link AreaTematica} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('CSP-AREA-E')")
  AreaTematica findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    AreaTematica returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link AreaTematica}.
   * 
   * @param areaTematica {@link AreaTematica} que se quiere crear.
   * @return Nuevo {@link AreaTematica} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('CSP-AREA-C')")
  ResponseEntity<AreaTematica> create(@Valid @RequestBody AreaTematica areaTematica) {
    log.debug("create(AreaTematica areaTematica) - start");
    AreaTematica returnValue = service.create(areaTematica);
    log.debug("create(AreaTematica areaTematica) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link AreaTematica} con el id indicado.
   * 
   * @param areaTematica {@link AreaTematica} a actualizar.
   * @param id           id {@link AreaTematica} a actualizar.
   * @return {@link AreaTematica} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-AREA-E')")
  AreaTematica update(@Validated({ Update.class, Default.class }) @RequestBody AreaTematica areaTematica,
      @PathVariable Long id) {
    log.debug("update(AreaTematica areaTematica, Long id) - start");
    areaTematica.setId(id);
    AreaTematica returnValue = service.update(areaTematica);
    log.debug("update(AreaTematica areaTematica, Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link AreaTematica} con id indicado.
   * 
   * @param id Identificador de {@link AreaTematica}.
   * @return {@link AreaTematica} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  @PreAuthorize("hasAuthority('CSP-AREA-R')")
  AreaTematica reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    AreaTematica returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link AreaTematica} con id indicado.
   * 
   * @param id Identificador de {@link AreaTematica}.
   * @return {@link AreaTematica} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AREA-E', 'CSP-AREA-B')")
  AreaTematica desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    AreaTematica returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve todas las entidades {@link AreaTematica} activos paginadas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-INV-V', 'CSP-SOL-INV-C', 'CSP-SOL-INV-ER')")
  ResponseEntity<Page<AreaTematica>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<AreaTematica> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve todas las entidades {@link AreaTematica} activos con padre null (los
   * grupo) paginadas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas
   */
  @GetMapping("/grupo")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V', 'CSP-CON-C', 'CSP-CON-E','CSP-CON-R','CSP-CON-B','CSP-PRO-E', 'CSP-SOL-INV-C' ,'CSP-SOL-INV-ER')")
  ResponseEntity<Page<AreaTematica>> findAllGrupo(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllGrupo(String query, Pageable paging) - start");
    Page<AreaTematica> page = service.findAllGrupo(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllGrupo(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllGrupo(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve todas las entidades {@link AreaTematica} con padre null (los grupos)
   * paginadas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas
   */
  @GetMapping("/grupo/todos")
  @PreAuthorize("hasAnyAuthority('CSP-AREA-V', 'CSP-AREA-C', 'CSP-AREA-E', 'CSP-AREA-B', 'CSP-AREA-R', 'CSP-SOL-INV-C', 'CSP-SOL-INV-ER')")
  ResponseEntity<Page<AreaTematica>> findAllTodosGrupo(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodosGrupo(String query, Pageable paging) - start");
    Page<AreaTematica> page = service.findAllTodosGrupo(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodosGrupo(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodosGrupo(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve todas las entidades {@link AreaTematica} hijos directos del
   * {@link AreaTematica} con el id indicado paginadas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas
   */
  @GetMapping("/{id}/hijos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E', 'CSP-PRO-E', 'CSP-AREA-E', 'CSP-SOL-INV-C', 'CSP-SOL-INV-ER')")
  ResponseEntity<Page<AreaTematica>> findAllHijosAreaTematica(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllHijosAreaTematica(String query, Pageable paging) - start");
    Page<AreaTematica> page = service.findAllHijosAreaTematica(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllHijosAreaTematica(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllHijosAreaTematica(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}