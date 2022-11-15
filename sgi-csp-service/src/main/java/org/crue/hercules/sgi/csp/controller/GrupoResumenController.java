package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.converter.GrupoConverter;
import org.crue.hercules.sgi.csp.dto.GrupoResumenOutput;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.service.GrupoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GrupoResumenController
 */
@RestController
@RequestMapping(GrupoResumenController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class GrupoResumenController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "grupos-resumen";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  // Services
  private final GrupoService service;
  // Converters
  private final GrupoConverter converter;

  /**
   * Devuelve el {@link Grupo} con el id indicado.
   * 
   * @param id Identificador de {@link Grupo}.
   * @return {@link Grupo} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('PRC-VAL-INV-ER', 'CSP-GIN-PRC-V')")
  public GrupoResumenOutput findById(@PathVariable Long id) {
    log.debug("Proyecto findById(Long id) - start");
    Grupo returnValue = service.findGrupoResumenById(id);
    log.debug("Proyecto findById(Long id) - end");
    return converter.convertToGrupoResumenOutput(returnValue);
  }
}
