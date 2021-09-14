package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.GastoProyectoInput;
import org.crue.hercules.sgi.csp.dto.GastoProyectoOutput;
import org.crue.hercules.sgi.csp.model.GastoProyecto;
import org.crue.hercules.sgi.csp.service.GastoProyectoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * GastoProyectoController
 */
@RestController
@RequestMapping(GastoProyectoController.MAPPING)
@Slf4j
public class GastoProyectoController {
  public static final String MAPPING = "/gastosproyectos";

  private ModelMapper modelMapper;

  /** GastoProyecto service */
  private final GastoProyectoService service;

  /**
   * Instancia un nuevo GastoProyectoController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link GastoProyectoService}
   */
  public GastoProyectoController(ModelMapper modelMapper, GastoProyectoService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link GastoProyecto}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-EJEC-E', 'CSP-EJEC-V', 'CSP-EJEC-INV-VR')")
  ResponseEntity<Page<GastoProyectoOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<GastoProyecto> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Crea un nuevo {@link GastoProyecto}.
   * 
   * @param gastoProyecto {@link GastoProyecto} que se quiere crear.
   * @return Nuevo {@link GastoProyecto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-E')")
  ResponseEntity<GastoProyectoOutput> create(@Valid @RequestBody GastoProyectoInput gastoProyecto) {
    log.debug("create(GastoProyecto gastoProyecto) - start");
    GastoProyecto returnValue = service.create(convert(gastoProyecto));
    log.debug("create(GastoProyecto gastoProyecto) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link GastoProyecto} con el id indicado.
   * 
   * @param gastoProyecto {@link GastoProyecto} a actualizar.
   * @param id            id {@link GastoProyecto} a actualizar.
   * @return {@link GastoProyecto} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-E')")
  GastoProyectoOutput update(@Valid @RequestBody GastoProyectoInput gastoProyecto, @PathVariable Long id) {
    log.debug("update(GastoProyecto gastoProyecto, Long id) - start");
    GastoProyecto returnValue = service.update(convert(id, gastoProyecto));
    log.debug("update(GastoProyecto gastoProyecto, Long id) - end");
    return convert(returnValue);
  }

  private GastoProyectoOutput convert(GastoProyecto gastoProyecto) {
    return modelMapper.map(gastoProyecto, GastoProyectoOutput.class);
  }

  private GastoProyecto convert(GastoProyectoInput gastoProyectoInput) {
    return convert(null, gastoProyectoInput);
  }

  private GastoProyecto convert(Long id, GastoProyectoInput gastoProyectoInput) {
    GastoProyecto gastoProyecto = modelMapper.map(gastoProyectoInput, GastoProyecto.class);
    gastoProyecto.setId(id);
    return gastoProyecto;
  }

  private Page<GastoProyectoOutput> convert(Page<GastoProyecto> page) {
    List<GastoProyectoOutput> content = page.getContent().stream().map((gastoProyecto) -> convert(gastoProyecto))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}