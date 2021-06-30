package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.InvencionOutput;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.service.InvencionService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * InvencionController {
 * 
 */
@RestController
@RequestMapping(InvencionController.MAPPING)
@Slf4j
public class InvencionController {
  public static final String MAPPING = "/invenciones";

  private ModelMapper modelMapper;

  /** Invencion service */
  private final InvencionService service;

  public InvencionController(ModelMapper modelMapper, InvencionService invencionService) {
    this.modelMapper = modelMapper;
    this.service = invencionService;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Invencion} activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link Invencion} paginadas y/o filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R')")
  ResponseEntity<Page<InvencionOutput>> findActivos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<Invencion> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Invencion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link Invencion} paginadas y/o filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R')")
  ResponseEntity<Page<InvencionOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<Invencion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve la {@link Invencion} con el id indicado.
   * 
   * @param id Identificador de {@link Invencion}.
   * @return {@link Invencion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R')")
  InvencionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    Invencion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  private InvencionOutput convert(Invencion invencion) {
    return modelMapper.map(invencion, InvencionOutput.class);
  }

  private Page<InvencionOutput> convert(Page<Invencion> page) {
    List<InvencionOutput> content = page.getContent().stream().map((invencion) -> convert(invencion))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}
