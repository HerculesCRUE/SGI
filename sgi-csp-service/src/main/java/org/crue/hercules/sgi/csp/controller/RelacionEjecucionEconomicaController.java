package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.service.GrupoService;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RelacionEjecucionEconomicaController
 */
@RestController
@RequestMapping(RelacionEjecucionEconomicaController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class RelacionEjecucionEconomicaController {
  public static final String REQUEST_MAPPING = "/relaciones-ejecucion-economica";
  public static final String PATH_GRUPOS = "/grupos";
  public static final String PATH_PROYECTOS = "/proyectos";

  private final ProyectoService proyectoService;
  private final GrupoService grupoService;

  /**
   * Devuelve una lista paginada y filtrada {@link RelacionEjecucionEconomica} de
   * {@link Grupo}
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} de los
   *         {@link Grupo}
   *         activos paginadas y filtradas.
   */
  @GetMapping(PATH_GRUPOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-EJEC-V', 'CSP-EJEC-E')")
  public ResponseEntity<Page<RelacionEjecucionEconomica>> findRelacionesGrupos(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findRelacionesGrupos(String query, Pageable paging) - start");

    Page<RelacionEjecucionEconomica> page = grupoService.findRelacionesEjecucionEconomicaGrupos(query, paging);

    log.debug("findRelacionesGrupos(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RelacionEjecucionEconomica} de
   * {@link Proyecto} que se encuentren dentro de la unidad de gestión del usuario
   * logueado
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} de
   *         {@link Proyecto} activos paginadas y filtradas.
   */
  @GetMapping(PATH_PROYECTOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-EJEC-V', 'CSP-EJEC-E')")
  public ResponseEntity<Page<RelacionEjecucionEconomica>> findRelacionesProyectos(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findRelacionesProyectos(String query, Pageable paging) - start");

    Page<RelacionEjecucionEconomica> page = proyectoService.findRelacionesEjecucionEconomicaProyectos(query, paging);

    log.debug("findRelacionesProyectos(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

}
