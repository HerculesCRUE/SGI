package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.InvencionInventorOutput;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionInventor;
import org.crue.hercules.sgi.pii.service.InvencionInventorService;
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
 * InvencionInventorController
 * 
 */
@RestController
@RequestMapping(InvencionInventorController.MAPPING)
@Slf4j
public class InvencionInventorController {
  public static final String MAPPING = "/invencion-inventores";

  private ModelMapper modelMapper;

  /** InvencionInventor service */
  private final InvencionInventorService service;
  /** Invencion service */
  private final InvencionService invencionService;

  public InvencionInventorController(ModelMapper modelMapper, InvencionInventorService invencionInventorService,
      InvencionService invencionService) {
    this.modelMapper = modelMapper;
    this.service = invencionInventorService;
    this.invencionService = invencionService;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link InvencionInventor} activos.
   * 
   * @param query       filtro de búsqueda.
   * @param invencionId el identificador de la invención cuyos inventores se
   *                    desean recuperar
   * @param paging      pageable.
   * @return la lista de entidades {@link Invencion} paginadas y/o filtradas.
   */
  @GetMapping("/{id}/inventores")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R')")
  public ResponseEntity<Page<InvencionInventorOutput>> findActiveInvencionInventores(@PathVariable Long invencionId,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug(
        "findActiveInvencionInventores(@PathVariable Long invencionId, @RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - start");

    if (this.invencionService.existsById(invencionId)) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    Page<InvencionInventor> page = this.service.findActivosByInvencion(invencionId, query, paging);

    if (page.isEmpty()) {
      log.debug(
          "findActiveInvencionInventores(@PathVariable Long invencionId, @RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug(
        "findActiveInvencionInventores(@PathVariable Long invencionId, @RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve la entidad {@link InvencionInventor} con el id indicado.
   * 
   * @param id Identificador de la entidad {@link InvencionInventor}.
   * @return {@link InvencionInventor} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-E')")
  public InvencionInventorOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    final InvencionInventor returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  private InvencionInventorOutput convert(InvencionInventor invencionInventor) {
    return modelMapper.map(invencionInventor, InvencionInventorOutput.class);
  }

  private Page<InvencionInventorOutput> convert(Page<InvencionInventor> page) {

    List<InvencionInventorOutput> content = page.getContent().stream().map(this::convert)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

}
