package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.RolSocioInput;
import org.crue.hercules.sgi.csp.dto.RolSocioOutput;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.service.RolSocioService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * RolSocioController
 */
@RestController
@RequestMapping("/rolsocios")
@Slf4j
@RequiredArgsConstructor
public class RolSocioController {

  /** RolSocioService service */
  private final RolSocioService service;

  /** Model mapper */
  private final ModelMapper modelMapper;

  /**
   * Devuelve una lista paginada y filtrada {@link RolSocio}
   * activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link RolSocio}
   *         paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-SOL-E', 'CSP-SOL-V')")
  public ResponseEntity<Page<RolSocioOutput>> findActivos(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findActivos(String query, Pageable paging) - start");
    Page<RolSocio> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug("findActivos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findActivos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link RolSocio} con el id indicado.
   * 
   * @param id Identificador de {@link RolSocio}.
   * @return {@link RolSocio} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-ROLS-E')")
  public RolSocioOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    RolSocio returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RolSocio}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link RolSocio}
   *         paginadas y filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('CSP-ROLS-V', 'CSP-ROLS-E', 'CSP-ROLS-B', 'CSP-ROLS-R')")
  public ResponseEntity<Page<RolSocioOutput>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<RolSocio> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Crea un nuevo {@link RolSocio}.
   * 
   * @param rolSocioInput {@link RolSocio} que
   *                      se quiere crear.
   * @return Nuevo {@link RolSocio} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('CSP-ROLS-C')")
  public ResponseEntity<RolSocioOutput> create(
      @Valid @RequestBody RolSocioInput rolSocioInput) {
    log.debug("create(RolSocioInput rolSocioInput) - start");
    RolSocio returnValue = service.create(convert(rolSocioInput));
    log.debug("create(RolSocioInput rolSocioInput) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link RolSocio} con el id indicado.
   * 
   * @param rolSocioInput {@link RolSocio} a
   *                      actualizar.
   * @param id            id {@link RolSocio} a
   *                      actualizar.
   * @return {@link RolSocio} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('CSP-ROLS-E')")
  public RolSocioOutput update(
      @Valid @RequestBody RolSocioInput rolSocioInput,
      @PathVariable Long id) {
    log.debug("update(RolSocioInput rolSocioInput, Long id) - start");
    RolSocio returnValue = service.update(convert(id, rolSocioInput));
    log.debug("update(RolSocioInput rolSocioInput, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Activa la {@link RolSocio} con id indicado.
   * 
   * @param id Identificador de {@link RolSocio}.
   * @return {@link RolSocio} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('CSP-ROLS-R')")
  public RolSocioOutput activar(@PathVariable Long id) {
    log.debug("activar(Long id) - start");
    RolSocio returnValue = service.activar(id);
    log.debug("activar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva el {@link RolSocio} con id indicado.
   * 
   * @param id Identificador de {@link RolSocio}.
   * @return {@link RolSocio} desactivada.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('CSP-ROLS-B')")
  public RolSocioOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    RolSocio returnValue = service.desactivar(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  private RolSocioOutput convert(RolSocio rolSocio) {
    return modelMapper.map(rolSocio, RolSocioOutput.class);
  }

  private RolSocio convert(RolSocioInput rolSocioInput) {
    return convert(null, rolSocioInput);
  }

  private RolSocio convert(Long id, RolSocioInput rolSocioInput) {
    RolSocio rolSocio = modelMapper.map(rolSocioInput, RolSocio.class);
    rolSocio.setId(id);
    return rolSocio;
  }

  private Page<RolSocioOutput> convert(Page<RolSocio> page) {
    List<RolSocioOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());
    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}
