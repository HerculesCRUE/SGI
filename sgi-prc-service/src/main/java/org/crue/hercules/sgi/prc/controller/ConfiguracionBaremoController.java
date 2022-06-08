package org.crue.hercules.sgi.prc.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.dto.ConfiguracionBaremoOutput;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.service.ConfiguracionBaremoService;
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
 * ConfiguracionBaremoController
 */
@RestController
@RequestMapping(ConfiguracionBaremoController.MAPPING)
@Slf4j
public class ConfiguracionBaremoController {
  public static final String MAPPING = "/configuracionesbaremos";

  private ModelMapper modelMapper;

  /** ConfiguracionBaremoService service */
  private final ConfiguracionBaremoService service;

  /**
   * Instancia un nuevo ConfiguracionBaremoController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link ConfiguracionBaremoService}
   */
  public ConfiguracionBaremoController(ModelMapper modelMapper, ConfiguracionBaremoService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ConfiguracionBaremo} activas.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConfiguracionBaremo} activas
   *         paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('PRC-CON-V', 'PRC-CON-C','PRC-CON-E')")
  public ResponseEntity<Page<ConfiguracionBaremoOutput>> findActivos(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findActivos(String query, Pageable paging) - start");
    Page<ConfiguracionBaremo> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug("findActivos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findActivos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  private Page<ConfiguracionBaremoOutput> convert(Page<ConfiguracionBaremo> page) {
    List<ConfiguracionBaremoOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ConfiguracionBaremoOutput convert(ConfiguracionBaremo configuracionBaremo) {
    return modelMapper.map(configuracionBaremo, ConfiguracionBaremoOutput.class);
  }
}
