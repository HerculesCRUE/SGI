package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.FuenteFinanciacionInput;
import org.crue.hercules.sgi.csp.dto.FuenteFinanciacionOutput;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.service.FuenteFinanciacionService;
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

import lombok.extern.slf4j.Slf4j;

/**
 * FuenteFinanciacionController
 */
@RestController
@RequestMapping(FuenteFinanciacionController.MAPPING)
@Slf4j
public class FuenteFinanciacionController {
  public static final String MAPPING = "/fuentesfinanciacion";

  private ModelMapper modelMapper;

  /** FuenteFinanciacion service */
  private final FuenteFinanciacionService service;

  /**
   * Instancia un nuevo FuenteFinanciacionController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link FuenteFinanciacionService}
   */
  public FuenteFinanciacionController(ModelMapper modelMapper, FuenteFinanciacionService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link FuenteFinanciacion} activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V','CSP-CON-E','CSP-CON-C','CSP-CON-INV-V','CSP-SOL-V', 'CSP-SOL-C', 'CSP-SOL-E', 'CSP-SOL-B', 'CSP-PRO-C', 'CSP-SOL-R', 'CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-R')")
  ResponseEntity<Page<FuenteFinanciacionOutput>> findActivos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<FuenteFinanciacion> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link FuenteFinanciacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('CSP-FNT-V', 'CSP-FNT-C', 'CSP-FNT-E', 'CSP-FNT-B', 'CSP-FNT-R')")
  ResponseEntity<Page<FuenteFinanciacionOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<FuenteFinanciacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link FuenteFinanciacion} con el id indicado.
   * 
   * @param id Identificador de {@link FuenteFinanciacion}.
   * @return {@link FuenteFinanciacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  FuenteFinanciacionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    FuenteFinanciacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link FuenteFinanciacion}.
   * 
   * @param fuenteFinanciacion {@link FuenteFinanciacion} que se quiere crear.
   * @return Nuevo {@link FuenteFinanciacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('CSP-FNT-C')")
  ResponseEntity<FuenteFinanciacionOutput> create(@Valid @RequestBody FuenteFinanciacionInput fuenteFinanciacion) {
    log.debug("create(FuenteFinanciacion fuenteFinanciacion) - start");
    FuenteFinanciacion returnValue = service.create(convert(fuenteFinanciacion));
    log.debug("create(FuenteFinanciacion fuenteFinanciacion) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link FuenteFinanciacion} con el id indicado.
   * 
   * @param fuenteFinanciacion {@link FuenteFinanciacion} a actualizar.
   * @param id                 id {@link FuenteFinanciacion} a actualizar.
   * @return {@link FuenteFinanciacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('CSP-FNT-E')")
  FuenteFinanciacionOutput update(@Valid @RequestBody FuenteFinanciacionInput fuenteFinanciacion,
      @PathVariable Long id) {
    log.debug("update(FuenteFinanciacion fuenteFinanciacion, Long id) - start");
    FuenteFinanciacion returnValue = service.update(convert(id, fuenteFinanciacion));
    log.debug("update(FuenteFinanciacion fuenteFinanciacion, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Activa la {@link FuenteFinanciacion} con id indicado.
   * 
   * @param id Identificador de {@link FuenteFinanciacion}.
   * @return {@link FuenteFinanciacion} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-FNT-R')")
  FuenteFinanciacionOutput activar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    FuenteFinanciacion returnValue = service.activar(id);
    log.debug("reactivar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva el {@link FuenteFinanciacion} con id indicado.
   * 
   * @param id Identificador de {@link FuenteFinanciacion}.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('CSP-FNT-B')")
  FuenteFinanciacionOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    FuenteFinanciacion returnValue = service.desactivar(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  private FuenteFinanciacionOutput convert(FuenteFinanciacion fuenteFinanciacion) {
    return modelMapper.map(fuenteFinanciacion, FuenteFinanciacionOutput.class);
  }

  private FuenteFinanciacion convert(FuenteFinanciacionInput fuenteFinanciacionInput) {
    return convert(null, fuenteFinanciacionInput);
  }

  private FuenteFinanciacion convert(Long id, FuenteFinanciacionInput fuenteFinanciacionInput) {
    FuenteFinanciacion fuenteFinanciacion = modelMapper.map(fuenteFinanciacionInput, FuenteFinanciacion.class);
    fuenteFinanciacion.setId(id);
    return fuenteFinanciacion;
  }

  private Page<FuenteFinanciacionOutput> convert(Page<FuenteFinanciacion> page) {
    List<FuenteFinanciacionOutput> content = page.getContent().stream()
        .map((fuenteFinanciacion) -> convert(fuenteFinanciacion)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}