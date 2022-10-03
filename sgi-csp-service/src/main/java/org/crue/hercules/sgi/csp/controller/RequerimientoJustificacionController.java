package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.AlegacionRequerimientoConverter;
import org.crue.hercules.sgi.csp.converter.GastoRequerimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.converter.IncidenciaDocumentacionRequerimientoConverter;
import org.crue.hercules.sgi.csp.converter.RequerimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.dto.AlegacionRequerimientoOutput;
import org.crue.hercules.sgi.csp.dto.GastoRequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoOutput;
import org.crue.hercules.sgi.csp.dto.RequerimientoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.RequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.model.AlegacionRequerimiento;
import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.service.AlegacionRequerimientoService;
import org.crue.hercules.sgi.csp.service.GastoRequerimientoJustificacionService;
import org.crue.hercules.sgi.csp.service.IncidenciaDocumentacionRequerimientoService;
import org.crue.hercules.sgi.csp.service.RequerimientoJustificacionService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "requerimientos-justificacion";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_INCIDENCIAS_DOCUMENTACION = PATH_DELIMITER + "{id}" + PATH_DELIMITER
      + "incidencias-documentacion";
  public static final String PATH_GASTOS = PATH_ID + PATH_DELIMITER
      + "gastos";
  public static final String PATH_ALEGACION = PATH_ID + PATH_DELIMITER
      + "alegacion";

  private final RequerimientoJustificacionService service;
  private final RequerimientoJustificacionConverter converter;
  private final IncidenciaDocumentacionRequerimientoService incidenciaDocumentacionRequerimientoService;
  private final IncidenciaDocumentacionRequerimientoConverter incidenciaDocumentacionRequerimientoConverter;
  private final GastoRequerimientoJustificacionService gastoRequerimientoJustificacionService;
  private final GastoRequerimientoJustificacionConverter gastoRequerimientoJustificacionConverter;
  private final AlegacionRequerimientoService alegacionRequerimientoService;
  private final AlegacionRequerimientoConverter alegacionRequerimientoConverter;

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

  /**
   * Crea una entidad {@link RequerimientoJustificacion}.
   * 
   * @param requerimientoJustificacion datos de la entidad
   *                                   {@link RequerimientoJustificacion} a crear.
   * @return la entidad {@link RequerimientoJustificacion} creada.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public ResponseEntity<RequerimientoJustificacionOutput> create(
      @Valid @RequestBody RequerimientoJustificacionInput requerimientoJustificacion) {
    log.debug("create(RequerimientoJustificacionInput requerimientoJustificacion) - start");
    RequerimientoJustificacionOutput returnValue = converter
        .convert(service.create(converter.convert(requerimientoJustificacion)));

    log.debug("create(RequerimientoJustificacionInput requerimientoJustificacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza la entidad {@link RequerimientoJustificacion} con id indicado.
   * 
   * @param id                         identificador de la entidad
   *                                   {@link RequerimientoJustificacion}.
   * @param requerimientoJustificacion datos de la entidad
   *                                   {@link RequerimientoJustificacion} a
   *                                   actualizar.
   * @return la entidad {@link RequerimientoJustificacion} actualizada.
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public RequerimientoJustificacionOutput update(@PathVariable Long id,
      @Valid @RequestBody RequerimientoJustificacionInput requerimientoJustificacion) {
    log.debug("update(Long id, RequerimientoJustificacionInput requerimientoJustificacion) - start");
    RequerimientoJustificacionOutput returnValue = converter
        .convert(service.update(converter.convert(requerimientoJustificacion, id)));

    log.debug("update(Long id, RequerimientoJustificacionInput requerimientoJustificacion) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y/o filtrada
   * {@link IncidenciaDocumentacionRequerimiento} de
   * {@link RequerimientoJustificacion}
   * 
   * @param id     identificador de la entidad
   *               {@link RequerimientoJustificacion}.
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link IncidenciaDocumentacionRequerimiento}
   *         paginadas y/o filtradas.
   */
  @GetMapping(PATH_INCIDENCIAS_DOCUMENTACION)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public ResponseEntity<Page<IncidenciaDocumentacionRequerimientoOutput>> findIncidenciasDocumentacion(
      @PathVariable Long id,
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findIncidenciasDocumentacion(Long id, String query, Pageable paging) - start");

    Page<IncidenciaDocumentacionRequerimientoOutput> page = incidenciaDocumentacionRequerimientoConverter
        .convert(incidenciaDocumentacionRequerimientoService.findAllByRequerimientoJustificacionId(id, query, paging));

    log.debug("findIncidenciasDocumentacion(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y/o filtrada
   * {@link GastoRequerimientoJustificacion} de un
   * {@link RequerimientoJustificacion}
   * 
   * @param id     identificador de la entidad
   *               {@link RequerimientoJustificacion}.
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link GastoRequerimientoJustificacion}
   *         paginadas y filtradas.
   */
  @GetMapping(PATH_GASTOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public ResponseEntity<Page<GastoRequerimientoJustificacionOutput>> findGastos(
      @PathVariable Long id,
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findGastos(Long id, String query, Pageable paging) - start");

    Page<GastoRequerimientoJustificacionOutput> page = gastoRequerimientoJustificacionConverter
        .convert(gastoRequerimientoJustificacionService.findAllByRequerimientoJustificacionId(id, query, paging));

    log.debug("findGastos(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve la entidad {@link AlegacionRequerimiento} de un
   * {@link RequerimientoJustificacion}.
   * 
   * @param id identificador de la entidad
   *           {@link RequerimientoJustificacion}.
   * @return la entidad {@link AlegacionRequerimiento}
   */
  @GetMapping(PATH_ALEGACION)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public ResponseEntity<AlegacionRequerimientoOutput> findAlegacion(@PathVariable Long id) {
    log.debug("findAlegacion(Long id) - start");
    AlegacionRequerimiento alegacionRequerimiento = alegacionRequerimientoService
        .findByRequerimientoJustificacionId(id);

    if (alegacionRequerimiento == null) {
      log.debug("findAlegacion(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAlegacion(Long id) - end");
    return new ResponseEntity<>(alegacionRequerimientoConverter.convert(alegacionRequerimiento), HttpStatus.OK);
  }
}
