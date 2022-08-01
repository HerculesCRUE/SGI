package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.converter.RequerimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.dto.RequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.service.RequerimientoJustificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SeguimientoEjecucionEconomicaController
 */
@RestController
@RequestMapping(RequerimientoJustificacionController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class RequerimientoJustificacionController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "requerimientosjustificacion";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final RequerimientoJustificacionService service;
  private final RequerimientoJustificacionConverter converter;

  /**
   * Devuelve el {@link RequerimientoJustificacion} con el id indicado.
   * 
   * @param id Identificador de {@link RequerimientoJustificacion}.
   * @return {@link RequerimientoJustificacion} correspondiente al id.
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public RequerimientoJustificacionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    RequerimientoJustificacionOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link RequerimientoJustificacion} con el id indicado.
   * 
   * @param id Identificador de {@link RequerimientoJustificacion}.
   */
  @DeleteMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.deleteById(id);
    log.debug("deleteById(Long id) - end");
  }
}
