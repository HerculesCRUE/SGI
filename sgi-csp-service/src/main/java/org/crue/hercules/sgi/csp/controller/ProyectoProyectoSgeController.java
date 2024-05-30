package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.ProyectoProyectoSgeConverter;
import org.crue.hercules.sgi.csp.dto.ProyectoProyectoSgeInput;
import org.crue.hercules.sgi.csp.dto.ProyectoProyectoSgeOutput;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.service.ProyectoProyectoSgeService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoProyectoSgeController
 */
@RestController
@RequestMapping(ProyectoProyectoSgeController.REQUEST_MAPPING)
@RequiredArgsConstructor
@Slf4j
public class ProyectoProyectoSgeController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "proyecto-proyectos-sge";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  public static final String PATH_REASIGNAR = PATH_ID + PATH_DELIMITER + "reasignar";

  private final ProyectoProyectoSgeService service;
  private final ProyectoProyectoSgeConverter converter;

  /**
   * Devuelve el {@link ProyectoProyectoSge} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoProyectoSge}.
   * @return {@link ProyectoProyectoSge} correspondiente al id.
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-EJEC-V', 'CSP-EJEC-E')")
  public ProyectoProyectoSgeOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoProyectoSgeOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea nuevo {@link ProyectoProyectoSge}.
   * 
   * @param proyectoProyectoSge {@link ProyectoProyectoSge} que se quiere crear.
   * @return Nuevo {@link ProyectoProyectoSge} creado.
   */
  @PostMapping
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAnyAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoProyectoSgeOutput> create(
      @Valid @RequestBody ProyectoProyectoSgeInput proyectoProyectoSge) {
    log.debug("create(ProyectoProyectoSgeInput proyectoProyectoSge) - start");
    ProyectoProyectoSgeOutput returnValue = converter.convert(service.create(converter.convert(proyectoProyectoSge)));
    log.debug("create(ProyectoProyectoSgeInput proyectoProyectoSge) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina el {@link ProyectoProyectoSge} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoProyectoSge}.
   */
  @DeleteMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ProyectoProyectoSge} que se
   * encuentren dentro de la unidad de gestión del usuario logueado
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link ProyectoProyectoSge} activas paginadas
   *         y filtradas.
   */
  @GetMapping()
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAnyAuthorityForAnyUO('CSP-EJEC-V', 'CSP-EJEC-E')")
  public ResponseEntity<Page<ProyectoProyectoSgeOutput>> findAll(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<ProyectoProyectoSgeOutput> page = converter.convert(service.findAll(query, paging));
    log.debug("findAll(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Reasigna {@link ProyectoProyectoSge#proyectoId} al nuevo
   * {@link ProyectoProyectoSge#proyectoSgeRef}.
   * 
   * @param id                  Identificador del {@link ProyectoProyectoSge}
   * @param proyectoProyectoSge {@link ProyectoProyectoSge} con el
   *                            {@link ProyectoProyectoSge#proyectoSgeRef}
   *                            actualizado
   * @return {@link ProyectoProyectoSge} actualizado.
   */
  @PatchMapping(PATH_REASIGNAR)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAnyAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoProyectoSgeOutput> reasignarProyectoSge(
      @PathVariable Long id, @Valid @RequestBody ProyectoProyectoSgeInput proyectoProyectoSge) {
    log.debug("create(ProyectoProyectoSgeInput proyectoProyectoSge) - start");
    ProyectoProyectoSgeOutput returnValue = converter
        .convert(service.reasignar(converter.convert(proyectoProyectoSge, id)));
    log.debug("create(ProyectoProyectoSgeInput proyectoProyectoSge) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

}
