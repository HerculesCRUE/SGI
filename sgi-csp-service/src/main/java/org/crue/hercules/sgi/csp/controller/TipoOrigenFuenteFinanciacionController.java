package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.TipoOrigenFuenteFinanciacionInput;
import org.crue.hercules.sgi.csp.dto.TipoOrigenFuenteFinanciacionOutput;
import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.crue.hercules.sgi.csp.service.TipoOrigenFuenteFinanciacionService;
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
@RequestMapping(TipoOrigenFuenteFinanciacionController.MAPPING)
@Slf4j
public class TipoOrigenFuenteFinanciacionController {
  public static final String MAPPING = "/tipoorigenfuentefinanciaciones";

  private ModelMapper modelMapper;

  /** TipoOrigenFuenteFinanciacionService service */
  private final TipoOrigenFuenteFinanciacionService service;

  /**
   * Instancia un nuevo TipoOrigenFuenteFinanciacionController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link TipoOrigenFuenteFinanciacionService}
   */
  public TipoOrigenFuenteFinanciacionController(
      ModelMapper modelMapper, TipoOrigenFuenteFinanciacionService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoOrigenFuenteFinanciacion}
   * activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link TipoOrigenFuenteFinanciacion}
   *         paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('CSP-FNT-V', 'CSP-FNT-C', 'CSP-FNT-E')")
  public ResponseEntity<Page<TipoOrigenFuenteFinanciacionOutput>> findActivos(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoOrigenFuenteFinanciacion> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoOrigenFuenteFinanciacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link TipoOrigenFuenteFinanciacion}
   *         paginadas y filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('CSP-TOFF-V', 'CSP-TOFF-E', 'CSP-TOFF-B', 'CSP-TOFF-R')")
  public ResponseEntity<Page<TipoOrigenFuenteFinanciacionOutput>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<TipoOrigenFuenteFinanciacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoOrigenFuenteFinanciacion} con el id indicado.
   * 
   * @param id Identificador de {@link TipoOrigenFuenteFinanciacion}.
   * @return {@link TipoOrigenFuenteFinanciacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('CSP-TOFF-E')")
  public TipoOrigenFuenteFinanciacionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    TipoOrigenFuenteFinanciacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link TipoOrigenFuenteFinanciacion}.
   * 
   * @param tipoOrigenfuenteFinanciacion {@link TipoOrigenFuenteFinanciacion} que
   *                                     se quiere crear.
   * @return Nuevo {@link TipoOrigenFuenteFinanciacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('CSP-TOFF-C')")
  public ResponseEntity<TipoOrigenFuenteFinanciacionOutput> create(
      @Valid @RequestBody TipoOrigenFuenteFinanciacionInput tipoOrigenfuenteFinanciacion) {
    log.debug("create(TipoOrigenFuenteFinanciacion fuenteFinanciacion) - start");
    TipoOrigenFuenteFinanciacion returnValue = service.create(convert(tipoOrigenfuenteFinanciacion));
    log.debug("create(TipoOrigenFuenteFinanciacion fuenteFinanciacion) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link TipoOrigenFuenteFinanciacion} con el id indicado.
   * 
   * @param tipoOrigenfuenteFinanciacion {@link TipoOrigenFuenteFinanciacion} a
   *                                     actualizar.
   * @param id                           id {@link TipoOrigenFuenteFinanciacion} a
   *                                     actualizar.
   * @return {@link TipoOrigenFuenteFinanciacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('CSP-TOFF-E')")
  public TipoOrigenFuenteFinanciacionOutput update(
      @Valid @RequestBody TipoOrigenFuenteFinanciacionInput tipoOrigenfuenteFinanciacion,
      @PathVariable Long id) {
    log.debug("update(TipoOrigenFuenteFinanciacion fuenteFinanciacion, Long id) - start");
    TipoOrigenFuenteFinanciacion returnValue = service.update(convert(id, tipoOrigenfuenteFinanciacion));
    log.debug("update(FuenteFinanciacion fuenteFinanciacion, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Activa la {@link TipoOrigenFuenteFinanciacion} con id indicado.
   * 
   * @param id Identificador de {@link TipoOrigenFuenteFinanciacion}.
   * @return {@link TipoOrigenFuenteFinanciacion} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('CSP-TOFF-R')")
  public TipoOrigenFuenteFinanciacionOutput activar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    TipoOrigenFuenteFinanciacion returnValue = service.activar(id);
    log.debug("reactivar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva el {@link TipoOrigenFuenteFinanciacion} con id indicado.
   * 
   * @param id Identificador de {@link TipoOrigenFuenteFinanciacion}.
   * @return {@link TipoOrigenFuenteFinanciacion} desactivada.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('CSP-TOFF-B')")
  public TipoOrigenFuenteFinanciacionOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    TipoOrigenFuenteFinanciacion returnValue = service.desactivar(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  private TipoOrigenFuenteFinanciacionOutput convert(TipoOrigenFuenteFinanciacion tipoOrigenfuenteFinanciacion) {
    return modelMapper.map(tipoOrigenfuenteFinanciacion, TipoOrigenFuenteFinanciacionOutput.class);
  }

  private TipoOrigenFuenteFinanciacion convert(TipoOrigenFuenteFinanciacionInput tipoOrigenfuenteFinanciacionInput) {
    return convert(null, tipoOrigenfuenteFinanciacionInput);
  }

  private TipoOrigenFuenteFinanciacion convert(Long id,
      TipoOrigenFuenteFinanciacionInput tipoOrigenfuenteFinanciacionInput) {
    TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion = modelMapper.map(tipoOrigenfuenteFinanciacionInput,
        TipoOrigenFuenteFinanciacion.class);
    tipoOrigenFuenteFinanciacion.setId(id);
    return tipoOrigenFuenteFinanciacion;
  }

  private Page<TipoOrigenFuenteFinanciacionOutput> convert(Page<TipoOrigenFuenteFinanciacion> page) {
    List<TipoOrigenFuenteFinanciacionOutput> content = page.getContent().stream()
        .map(fuenteFinanciacion -> convert(fuenteFinanciacion)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}