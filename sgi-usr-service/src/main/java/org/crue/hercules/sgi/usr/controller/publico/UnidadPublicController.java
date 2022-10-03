package org.crue.hercules.sgi.usr.controller.publico;

import org.crue.hercules.sgi.usr.model.Unidad;
import org.crue.hercules.sgi.usr.service.UnidadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * UnidadController
 */
@RestController
@RequestMapping(UnidadPublicController.REQUEST_MAPPING)
@Slf4j
public class UnidadPublicController {

  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER + "unidades";

  /** Unidad service */
  private final UnidadService service;

  /**
   * Instancia un nuevo UnidadController.
   * 
   * @param service {@link UnidadService}
   */
  public UnidadPublicController(UnidadService service) {
    this.service = service;
  }

  /**
   * Devuelve el {@link Unidad} con el id indicado.
   * 
   * @param id Identificador de {@link Unidad}.
   * @return {@link Unidad} correspondiente al id.
   */
  @GetMapping("/{id}")
  public Unidad findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    Unidad returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }
}
