package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.converter.ProyectoPeriodoSeguimientoConverter;
import org.crue.hercules.sgi.csp.converter.ProyectoSeguimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.converter.RequerimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoSeguimientoOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoEjecucionEconomica;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.dto.RequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoSeguimientoJustificacion;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoService;
import org.crue.hercules.sgi.csp.service.ProyectoSeguimientoJustificacionService;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.csp.service.RequerimientoJustificacionService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SeguimientoEjecucionEconomicaController
 */
@RestController
@RequestMapping(SeguimientoEjecucionEconomicaController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class SeguimientoEjecucionEconomicaController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "seguimiento-ejecucion-economica";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_PROYECTOS = PATH_DELIMITER + PATH_ID + PATH_DELIMITER + "proyectos";
  public static final String PATH_PERIODO_JUSTIFICACION = PATH_DELIMITER + PATH_ID + PATH_DELIMITER
      + "periodos-justificacion";
  public static final String PATH_PERIODO_SEGUIMIENTO = PATH_DELIMITER + PATH_ID + PATH_DELIMITER
      + "periodos-seguimiento";
  public static final String PATH_REQUERIMIENTO_JUSTIFICACION = PATH_DELIMITER + PATH_ID + PATH_DELIMITER
      + "requerimientos-justificacion";
  public static final String PATH_SEGUIMIENTO_JUSTIFICACION = PATH_DELIMITER + PATH_ID + PATH_DELIMITER
      + "seguimientos-justificacion";

  private final ModelMapper modelMapper;
  private final ProyectoService proyectoService;
  private final ProyectoPeriodoJustificacionService proyectoPeriodoJustificacionService;
  private final ProyectoPeriodoSeguimientoService proyectoPeriodoSeguimientoService;
  private final ProyectoPeriodoSeguimientoConverter proyectoPeriodoSeguimientoConverter;
  private final RequerimientoJustificacionService requerimientoJustificacionService;
  private final RequerimientoJustificacionConverter requerimientoJustificacionConverter;
  private final ProyectoSeguimientoJustificacionService proyectoSeguimientoJustificacionService;
  private final ProyectoSeguimientoJustificacionConverter proyectoSeguimientoJustificacionConverter;

  /**
   * Devuelve una lista paginada y filtrada
   * {@link ProyectoSeguimientoEjecucionEconomica} del ProyectoSGE que se
   * encuentren dentro de la unidad de gestión del usuario logueado
   * 
   * @param id     identificador del proyectoSGE
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link ProyectoSeguimientoEjecucionEconomica}
   *         de {@link Proyecto} activos paginadas y filtradas.
   */
  @GetMapping(PATH_PROYECTOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public ResponseEntity<Page<ProyectoSeguimientoEjecucionEconomica>> findProyectosSeguimientoEjecucionEconomica(
      @PathVariable String id,
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findProyectosSeguimientoEjecucionEconomica(String id, String query, Pageable paging) - start");

    Page<ProyectoSeguimientoEjecucionEconomica> page = proyectoService.findProyectosSeguimientoEjecucionEconomica(id,
        query, paging);

    log.debug("findProyectosSeguimientoEjecucionEconomica(String id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de todos los
   * {@link ProyectoPeriodoJustificacion} asociados a un ProyectoSGE.
   * 
   * @param id     identificador del proyectoSGE
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoPeriodoJustificacion}
   *         paginados y filtrados.
   */
  @GetMapping(PATH_PERIODO_JUSTIFICACION)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public ResponseEntity<Page<ProyectoPeriodoJustificacionOutput>> findProyectoPeriodosJustificacion(
      @PathVariable String id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findProyectoPeriodosJustificacion(String id, String query, Pageable paging) - start");
    Page<ProyectoPeriodoJustificacion> page = proyectoPeriodoJustificacionService.findAllByProyectoSgeRef(id, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findProyectoPeriodosJustificacion(String id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findProyectoPeriodosJustificacion(String id, String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  private Page<ProyectoPeriodoJustificacionOutput> convert(Page<ProyectoPeriodoJustificacion> page) {
    List<ProyectoPeriodoJustificacionOutput> content = page.getContent().stream()
        .map(proyectoPeriodoJustificacion -> convert(proyectoPeriodoJustificacion)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ProyectoPeriodoJustificacionOutput convert(ProyectoPeriodoJustificacion proyectoPeriodoJustificacion) {
    return modelMapper.map(proyectoPeriodoJustificacion, ProyectoPeriodoJustificacionOutput.class);
  }

  /**
   * Devuelve una lista paginada de todos los
   * {@link ProyectoPeriodoSeguimiento} asociados a un ProyectoSGE.
   * 
   * @param id     identificador del proyectoSGE
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoPeriodoSeguimiento}
   *         paginados y filtrados.
   */
  @GetMapping(PATH_PERIODO_SEGUIMIENTO)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public ResponseEntity<Page<ProyectoPeriodoSeguimientoOutput>> findProyectoPeriodosSeguimiento(
      @PathVariable String id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findProyectoPeriodosSeguimiento(String id, String query, Pageable paging) - start");
    Page<ProyectoPeriodoSeguimiento> page = proyectoPeriodoSeguimientoService.findAllByProyectoSgeRef(id, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findProyectoPeriodosSeguimiento(String id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findProyectoPeriodosSeguimiento(String id, String query, Pageable paging) - end");
    return new ResponseEntity<>(proyectoPeriodoSeguimientoConverter.convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de todos los
   * {@link RequerimientoJustificacion} asociados a un ProyectoSGE.
   * 
   * @param id     identificador del proyectoSGE
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link RequerimientoJustificacion}
   *         paginados y filtrados.
   */
  @GetMapping(PATH_REQUERIMIENTO_JUSTIFICACION)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public ResponseEntity<Page<RequerimientoJustificacionOutput>> findRequerimientosJustificacion(
      @PathVariable String id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findRequerimientosJustificacion(String id, String query, Pageable paging) - start");
    Page<RequerimientoJustificacion> page = requerimientoJustificacionService.findAllByProyectoSgeRef(id, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findRequerimientosJustificacion(String id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findRequerimientosJustificacion(String id, String query, Pageable paging) - end");
    return new ResponseEntity<>(requerimientoJustificacionConverter.convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de todos los
   * {@link ProyectoSeguimientoJustificacion} asociados a un ProyectoSGE.
   * 
   * @param id     identificador del proyectoSGE
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoSeguimientoJustificacion}
   *         paginados y filtrados.
   */
  @GetMapping(PATH_SEGUIMIENTO_JUSTIFICACION)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public ResponseEntity<Page<ProyectoSeguimientoJustificacionOutput>> findSeguimientosJustificacion(
      @PathVariable String id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findSeguimientosJustificacion(String id, String query, Pageable paging) - start");
    Page<ProyectoSeguimientoJustificacion> page = proyectoSeguimientoJustificacionService.findAllByProyectoSgeRef(
        id, query, paging);

    if (page.isEmpty()) {
      log.debug("findSeguimientosJustificacion(String id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findSeguimientosJustificacion(String id, String query, Pageable paging) - end");
    return new ResponseEntity<>(proyectoSeguimientoJustificacionConverter.convert(page), HttpStatus.OK);
  }
}
