package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.TipoCaducidadOutput;
import org.crue.hercules.sgi.pii.model.TipoCaducidad;
import org.crue.hercules.sgi.pii.service.TipoCaducidadService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * TipoCaducidadController
 */
@RestController
@RequestMapping(TipoCaducidadController.MAPPING)
@Slf4j
public class TipoCaducidadController {
  public static final String MAPPING = "/tiposcaducidad";

  private ModelMapper modelMapper;

  /** TipoCaducidadService service */
  private final TipoCaducidadService service;

  /**
   * Instancia un nuevo TipoCaducidadController.
   * 
   * @param service     {@link TipoCaducidadService}
   * @param modelMapper mapper
   */
  public TipoCaducidadController(ModelMapper modelMapper, TipoCaducidadService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoCaducidad}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link TipoCaducidad} paginadas y/o filtradas.
   */
  @GetMapping
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R')")
  ResponseEntity<Page<TipoCaducidadOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoCaducidad> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  private TipoCaducidadOutput convert(TipoCaducidad tipoCaducidad) {
    return modelMapper.map(tipoCaducidad, TipoCaducidadOutput.class);
  }

  private Page<TipoCaducidadOutput> convert(Page<TipoCaducidad> page) {
    List<TipoCaducidadOutput> content = page.getContent().stream().map((sectorAplicacion) -> convert(sectorAplicacion))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

}
