package org.crue.hercules.sgi.prc.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.dto.AliasEnumeradoOutput;
import org.crue.hercules.sgi.prc.model.AliasEnumerado;
import org.crue.hercules.sgi.prc.service.AliasEnumeradoService;
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
 * AliasEnumeradoController
 */
@RestController
@RequestMapping(AliasEnumeradoController.MAPPING)
@Slf4j
public class AliasEnumeradoController {
  public static final String MAPPING = "/alias-enumerados";

  private ModelMapper modelMapper;

  /** AliasEnumeradoService service */
  private final AliasEnumeradoService service;

  /**
   * Instancia un nuevo AliasEnumeradoController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link AliasEnumeradoService}
   */
  public AliasEnumeradoController(ModelMapper modelMapper, AliasEnumeradoService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link AliasEnumerado}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link AliasEnumerado}
   *         paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<AliasEnumeradoOutput>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<AliasEnumerado> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  private Page<AliasEnumeradoOutput> convert(Page<AliasEnumerado> page) {
    List<AliasEnumeradoOutput> content = page.getContent().stream()
        .map(aliasEnumerado -> convert(aliasEnumerado)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private AliasEnumeradoOutput convert(AliasEnumerado aliasEnumerado) {
    return modelMapper.map(aliasEnumerado, AliasEnumeradoOutput.class);
  }
}
