package org.crue.hercules.sgi.csp.controller.publico;

import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.service.ProgramaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProgramaPublicController
 */
@RestController
@RequestMapping(ProgramaPublicController.REQUEST_MAPPING)
@Slf4j
public class ProgramaPublicController {

  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER + "programas";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_HIJOS = PATH_ID + "/hijos";

  /** Programa service */
  private final ProgramaService service;

  /**
   * Instancia un nuevo ProgramaController.
   * 
   * @param service {@link ProgramaService}
   */
  public ProgramaPublicController(ProgramaService service) {
    this.service = service;
  }

  /**
   * Devuelve todas las entidades {@link Programa} hijos directos del
   * {@link Programa} con el id indicado paginadas
   *
   * @param id     id del {@link Programa} padre.
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas
   */
  @GetMapping(PATH_HIJOS)
  public ResponseEntity<Page<Programa>> findAllHijosPrograma(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllHijosPrograma(String query, Pageable paging) - start");
    Page<Programa> page = service.findAllHijosPrograma(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllHijosPrograma(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllHijosPrograma(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}