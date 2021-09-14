package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.TramoRepartoInput;
import org.crue.hercules.sgi.pii.dto.TramoRepartoOutput;
import org.crue.hercules.sgi.pii.model.TramoReparto;
import org.crue.hercules.sgi.pii.service.TramoRepartoService;
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
 * TramoRepartoController
 */
@RestController
@RequestMapping(TramoRepartoController.MAPPING)
@Slf4j
public class TramoRepartoController {
  public static final String MAPPING = "/tramosreparto";

  private ModelMapper modelMapper;
  /** TramoReparto service */
  private final TramoRepartoService service;

  /**
   * Instancia un nuevo TramoRepartoController.
   * 
   * @param service     {@link TramoRepartoService}
   * @param modelMapper mapper
   */
  public TramoRepartoController(ModelMapper modelMapper, TramoRepartoService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TramoReparto} activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link TramoReparto} paginadas y/o filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('PII-TRE-V', 'PII-TRE-C', 'PII-TRE-E', 'PII-TRE-B', 'PII-TRE-R')")
  ResponseEntity<Page<TramoRepartoOutput>> findActivos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TramoReparto> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TramoReparto}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link TramoReparto} paginadas y/o filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('PII-TRE-V', 'PII-TRE-C', 'PII-TRE-E', 'PII-TRE-B', 'PII-TRE-R')")
  ResponseEntity<Page<TramoRepartoOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<TramoReparto> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TramoReparto} con el id indicado.
   * 
   * @param id Identificador de {@link TramoReparto}.
   * @return {@link TramoReparto} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-TRE-V', 'PII-TRE-C', 'PII-TRE-E', 'PII-TRE-B', 'PII-TRE-R')")
  TramoRepartoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    TramoReparto returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link TramoReparto}.
   * 
   * @param tramoReparto {@link TramoReparto} que se quiere crear.
   * @return Nuevo {@link TramoReparto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-TRE-C')")
  ResponseEntity<TramoRepartoOutput> create(@Valid @RequestBody TramoRepartoInput tramoReparto) {
    log.debug("create(TramoReparto tramoReparto) - start");
    TramoReparto returnValue = service.create(convert(tramoReparto));
    log.debug("create(TramoReparto tramoReparto) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link TramoReparto} con el id indicado.
   * 
   * @param tramoReparto {@link TramoReparto} a actualizar.
   * @param id           id {@link TramoReparto} a actualizar.
   * @return {@link TramoReparto} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-TRE-E')")
  TramoRepartoOutput update(@Valid @RequestBody TramoRepartoInput tramoReparto, @PathVariable Long id) {
    log.debug("update(TramoReparto tramoReparto, Long id) - start");
    TramoReparto returnValue = service.update(convert(id, tramoReparto));
    log.debug("update(TramoReparto tramoReparto, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Activa la entidad {@link TramoReparto} con id indicado.
   * 
   * @param id Identificador de {@link TramoReparto}.
   * @return {@link TramoReparto} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('PII-TRE-R')")
  TramoRepartoOutput activar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    TramoReparto returnValue = service.activar(id);
    log.debug("reactivar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva la entidad {@link TramoReparto} con id indicado.
   * 
   * @param id Identificador de {@link TramoReparto}.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('PII-TRE-B')")
  TramoRepartoOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    TramoReparto returnValue = service.desactivar(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  private TramoRepartoOutput convert(TramoReparto tramoReparto) {
    return modelMapper.map(tramoReparto, TramoRepartoOutput.class);
  }

  private Page<TramoRepartoOutput> convert(Page<TramoReparto> page) {
    List<TramoRepartoOutput> content = page.getContent().stream().map((sectorAplicacion) -> convert(sectorAplicacion))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private TramoReparto convert(TramoRepartoInput tramoRepartoInput) {
    return convert(null, tramoRepartoInput);
  }

  private TramoReparto convert(Long id, TramoRepartoInput tramoRepartoInput) {
    TramoReparto tramoReparto = modelMapper.map(tramoRepartoInput, TramoReparto.class);
    tramoReparto.setId(id);
    return tramoReparto;
  }
}
