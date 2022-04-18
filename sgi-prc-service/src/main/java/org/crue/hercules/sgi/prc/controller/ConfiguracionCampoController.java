package org.crue.hercules.sgi.prc.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.dto.ConfiguracionCampoOutput;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;
import org.crue.hercules.sgi.prc.service.ConfiguracionCampoService;
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
 * ConfiguracionCampoController
 */
@RestController
@RequestMapping(ConfiguracionCampoController.MAPPING)
@Slf4j
public class ConfiguracionCampoController {
  public static final String MAPPING = "/configuraciones-campos";

  private ModelMapper modelMapper;

  /** ConfiguracionCampo service */
  private final ConfiguracionCampoService service;

  /**
   * Instancia un nuevo CampoProduccionCientificaController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link ConfiguracionCampoService}
   */
  public ConfiguracionCampoController(ModelMapper modelMapper, ConfiguracionCampoService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ConfiguracionCampo}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConfiguracionCampo}
   *         paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<ConfiguracionCampoOutput>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<ConfiguracionCampo> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  private Page<ConfiguracionCampoOutput> convert(Page<ConfiguracionCampo> page) {
    List<ConfiguracionCampoOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ConfiguracionCampoOutput convert(ConfiguracionCampo configuracionCampo) {
    return modelMapper.map(configuracionCampo, ConfiguracionCampoOutput.class);
  }
}
