package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.LineaInvestigacionInput;
import org.crue.hercules.sgi.csp.dto.LineaInvestigacionOutput;
import org.crue.hercules.sgi.csp.model.LineaInvestigacion;
import org.crue.hercules.sgi.csp.service.LineaInvestigacionService;
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
 * LineaInvestigacionController
 */

@RestController
@RequestMapping(LineaInvestigacionController.MAPPING)
@Slf4j
public class LineaInvestigacionController {
  public static final String MAPPING = "/lineasinvestigacion";

  private ModelMapper modelMapper;

  /** LineaInvestigacion service */
  private final LineaInvestigacionService service;

  public LineaInvestigacionController(ModelMapper modelMapper, LineaInvestigacionService lineaInvestigacionService) {
    log.debug(
        "LineaInvestigacionController(ModelMapper modelMapper, LineaInvestigacionService lineaInvestigacionService) - start");
    this.service = lineaInvestigacionService;
    this.modelMapper = modelMapper;
    log.debug(
        "LineaInvestigacionController(ModelMapper modelMapper, LineaInvestigacionService lineaInvestigacionService) - end");
  }

  /**
   * Crea nuevo {@link LineaInvestigacion}.
   * 
   * @param lineaInvestigacion {@link LineaInvestigacion}. que se quiere crear.
   * @return Nuevo {@link LineaInvestigacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-LIN-C')")
  public ResponseEntity<LineaInvestigacionOutput> create(
      @Valid @RequestBody LineaInvestigacionInput lineaInvestigacion) {
    log.debug("create(LineaInvestigacion lineaInvestigacion) - start");
    LineaInvestigacion returnValue = service.create(convert(lineaInvestigacion));
    log.debug("create(LineaInvestigacion lineaInvestigacion) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link LineaInvestigacion}.
   * 
   * @param lineaInvestigacion {@link LineaInvestigacion} a actualizar.
   * @param id                 Identificador {@link LineaInvestigacion} a
   *                           actualizar.
   * @return LineaInvestigacion {@link LineaInvestigacion} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-LIN-E')")
  public LineaInvestigacionOutput update(@Valid @RequestBody LineaInvestigacionInput lineaInvestigacion,
      @PathVariable Long id) {
    log.debug("update(LineaInvestigacion lineaInvestigacion, Long id) - start");
    LineaInvestigacion returnValue = service.update(convert(id, lineaInvestigacion));
    log.debug("update(LineaInvestigacion lineaInvestigacion, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Reactiva el {@link LineaInvestigacion} con id indicado.
   * 
   * @param id Identificador de {@link LineaInvestigacion}.
   * @return {@link LineaInvestigacion} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-LIN-R')")
  public LineaInvestigacionOutput reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    LineaInvestigacion returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva el {@link LineaInvestigacion} con id indicado.
   * 
   * @param id Identificador de {@link LineaInvestigacion}.
   * @return {@link LineaInvestigacion} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-LIN-B')")
  public LineaInvestigacionOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    LineaInvestigacion returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link LineaInvestigacion} activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link LineaInvestigacion} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-LIN-C', 'CSP-LIN-E', 'CSP-LIN-B')")
  public ResponseEntity<Page<LineaInvestigacionOutput>> findAll(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<LineaInvestigacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link LineaInvestigacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link LineaInvestigacion} paginadas y
   *         filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-LIN-C', 'CSP-LIN-E', 'CSP-LIN-B', 'CSP-LIN-R', 'CSP-GIN-V')")
  public ResponseEntity<Page<LineaInvestigacionOutput>> findAllTodos(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<LineaInvestigacion> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link LineaInvestigacion} con el id indicado.
   * 
   * @param id Identificador de {@link LineaInvestigacion}.
   * @return LineaInvestigacion {@link LineaInvestigacion} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public LineaInvestigacionOutput findById(@PathVariable Long id) {
    log.debug("LineaInvestigacion findById(Long id) - start");
    LineaInvestigacion returnValue = service.findById(id);
    log.debug("LineaInvestigacion findById(Long id) - end");
    return convert(returnValue);
  }

  private LineaInvestigacionOutput convert(LineaInvestigacion lineaInvestigacion) {
    return modelMapper.map(lineaInvestigacion, LineaInvestigacionOutput.class);
  }

  private LineaInvestigacion convert(LineaInvestigacionInput lineaInvestigacionInput) {
    return convert(null, lineaInvestigacionInput);
  }

  private LineaInvestigacion convert(Long id, LineaInvestigacionInput lineaInvestigacionInput) {
    LineaInvestigacion lineaInvestigacion = modelMapper.map(lineaInvestigacionInput, LineaInvestigacion.class);
    lineaInvestigacion.setId(id);
    return lineaInvestigacion;
  }

  private Page<LineaInvestigacionOutput> convert(Page<LineaInvestigacion> page) {
    List<LineaInvestigacionOutput> content = page.getContent().stream()
        .map(lineaInvestigacion -> convert(lineaInvestigacion)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}
