package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.SectorAplicacionInput;
import org.crue.hercules.sgi.pii.dto.SectorAplicacionOutput;
import org.crue.hercules.sgi.pii.model.SectorAplicacion;
import org.crue.hercules.sgi.pii.service.SectorAplicacionService;
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
 * SectorAplicacionController
 */
@RestController
@RequestMapping(SectorAplicacionController.MAPPING)
@Slf4j
public class SectorAplicacionController {
  public static final String MAPPING = "/sectoresaplicacion";

  private ModelMapper modelMapper;

  /** SectorAplicacion service */
  private final SectorAplicacionService service;

  /**
   * Instancia un nuevo SectorAplicacionController.
   * 
   * @param service     {@link SectorAplicacionService}
   * @param modelMapper mapper
   */
  public SectorAplicacionController(ModelMapper modelMapper, SectorAplicacionService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link SectorAplicacion} activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link SectorAplicacion} paginadas y/o
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('PII-SEA-V', 'PII-SEA-C', 'PII-SEA-E', 'PII-SEA-B', 'PII-SEA-R', 'PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R')")
  ResponseEntity<Page<SectorAplicacionOutput>> findActivos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<SectorAplicacion> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link SectorAplicacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link SectorAplicacion} paginadas y/o
   *         filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('PII-SEA-V', 'PII-SEA-C', 'PII-SEA-E', 'PII-SEA-B', 'PII-SEA-R', 'PII-INV-E')")
  ResponseEntity<Page<SectorAplicacionOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<SectorAplicacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link SectorAplicacion} con el id indicado.
   * 
   * @param id Identificador de {@link SectorAplicacion}.
   * @return {@link SectorAplicacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-SEA-V', 'PII-SEA-C', 'PII-SEA-E', 'PII-SEA-B', 'PII-SEA-R')")
  SectorAplicacionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SectorAplicacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link SectorAplicacion}.
   * 
   * @param sectorAplicacion {@link SectorAplicacion} que se quiere crear.
   * @return Nuevo {@link SectorAplicacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-SEA-C')")
  ResponseEntity<SectorAplicacionOutput> create(@Valid @RequestBody SectorAplicacionInput sectorAplicacion) {
    log.debug("create(SectorAplicacion sectorAplicacion) - start");
    SectorAplicacion returnValue = service.create(convert(sectorAplicacion));
    log.debug("create(SectorAplicacion sectorAplicacion) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link SectorAplicacion} con el id indicado.
   * 
   * @param sectorAplicacion {@link SectorAplicacion} a actualizar.
   * @param id               id {@link SectorAplicacion} a actualizar.
   * @return {@link SectorAplicacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-SEA-E')")
  SectorAplicacionOutput update(@Valid @RequestBody SectorAplicacionInput sectorAplicacion, @PathVariable Long id) {
    log.debug("update(SectorAplicacion sectorAplicacion, Long id) - start");
    SectorAplicacion returnValue = service.update(convert(id, sectorAplicacion));
    log.debug("update(SectorAplicacion sectorAplicacion, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Activa la {@link SectorAplicacion} con id indicado.
   * 
   * @param id Identificador de {@link SectorAplicacion}.
   * @return {@link SectorAplicacion} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('PII-SEA-R')")
  SectorAplicacionOutput activar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    SectorAplicacion returnValue = service.activar(id);
    log.debug("reactivar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva el {@link SectorAplicacion} con id indicado.
   * 
   * @param id Identificador de {@link SectorAplicacion}.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('PII-SEA-B')")
  SectorAplicacionOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    SectorAplicacion returnValue = service.desactivar(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  private SectorAplicacionOutput convert(SectorAplicacion sectorAplicacion) {
    return modelMapper.map(sectorAplicacion, SectorAplicacionOutput.class);
  }

  private SectorAplicacion convert(SectorAplicacionInput sectorAplicacionInput) {
    return convert(null, sectorAplicacionInput);
  }

  private SectorAplicacion convert(Long id, SectorAplicacionInput sectorAplicacionInput) {
    SectorAplicacion sectorAplicacion = modelMapper.map(sectorAplicacionInput, SectorAplicacion.class);
    sectorAplicacion.setId(id);
    return sectorAplicacion;
  }

  private Page<SectorAplicacionOutput> convert(Page<SectorAplicacion> page) {
    List<SectorAplicacionOutput> content = page.getContent().stream()
        .map((sectorAplicacion) -> convert(sectorAplicacion)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}
