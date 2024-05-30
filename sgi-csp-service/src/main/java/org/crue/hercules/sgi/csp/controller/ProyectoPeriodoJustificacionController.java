package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionIdentificadorJustificacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionOutput;
import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoJustificacionService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoPeriodoJustificacionController
 */
@RestController
@RequestMapping(ProyectoPeriodoJustificacionController.REQUEST_MAPPING)
@Slf4j
public class ProyectoPeriodoJustificacionController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "proyectoperiodosjustificacion";

  public static final String PATH_PARAMETER_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_IDENTIFICADOR_JUSTIFICACION = PATH_DELIMITER + "identificadorjustificacion";
  public static final String PATH_HAS_REQUERIMIENTOS_JUSTIFICACION = PATH_PARAMETER_ID + PATH_DELIMITER
      + "hasrequerimientosjustificacion";

  private final ModelMapper modelMapper;

  /** ProyectoPeriodoSeguimiento service */
  private final ProyectoPeriodoJustificacionService service;

  /**
   * Instancia un nuevo ProyectoPeriodoSeguimientoController.
   * 
   * @param service     {@link ProyectoPeriodoJustificacionService}
   * @param modelMapper {@link ModelMapper}
   */
  public ProyectoPeriodoJustificacionController(ProyectoPeriodoJustificacionService service, ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Actualiza el {@link ProyectoPeriodoJustificacion} con el id indicado.
   * 
   * @param proyectoPeriodoJustificaciones {@link ProyectoPeriodoJustificacion} a
   *                                       actualizar.
   * @param proyectoId                     id {@link Proyecto} del que se
   *                                       actualizan los
   *                                       {@link ProyectoPeriodoJustificacion}.
   * @return lista de {@link ProyectoPeriodoJustificacion} actualizado.
   */
  @PatchMapping("/{proyectoId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public List<ProyectoPeriodoJustificacionOutput> update(
      @Validated({ Update.class,
          Default.class }) @RequestBody List<ProyectoPeriodoJustificacionInput> proyectoPeriodoJustificaciones,
      @PathVariable Long proyectoId) {
    log.debug("update(List ProyectoPeriodoJustificacion proyectoPeriodoJustificacion, Long id) - start");

    proyectoPeriodoJustificaciones.stream().filter(periodo -> periodo.getProyectoId() == null)
        .forEach(periodo -> periodo.setProyectoId(proyectoId));
    List<ProyectoPeriodoJustificacion> returnValue = service.update(proyectoId,
        convert(proyectoPeriodoJustificaciones));

    log.debug("update(List ProyectoPeriodoJustificacion proyectoPeriodoJustificacion, Long id) - end");

    return convertToOutput(returnValue);
  }

  /**
   * Devuelve el {@link ProyectoPeriodoJustificacion} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoPeriodoJustificacion}.
   * @return {@link ProyectoPeriodoJustificacion} correspondiente al id.
   */
  @GetMapping(PATH_PARAMETER_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ProyectoPeriodoJustificacionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoPeriodoJustificacionOutput returnValue = convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada de todos los
   * {@link ProyectoPeriodoJustificacion}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoPeriodoJustificacion}
   *         paginados y filtrados.
   */
  @GetMapping("/{id}/proyectoperiodojustificacion")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoPeriodoJustificacionOutput>> findAllByProyectoId(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(Long id, String query, Pageable paging) - start");
    Page<ProyectoPeriodoJustificacion> page = service.findAllByProyectoId(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Actualiza el Identificador de Justificacion de la entidad
   * {@link ProyectoPeriodoJustificacion} con el id indicado.
   * 
   * @param proyectoPeriodoJustificacionIdentificadorJustificacionInput {@link ProyectoPeriodoJustificacion}
   *                                                                    a
   *                                                                    actualizar.
   * @param id                                                          id
   *                                                                    {@link ProyectoPeriodoJustificacion}.
   * @return {@link ProyectoPeriodoJustificacion} actualizado.
   */
  @PatchMapping(PATH_PARAMETER_ID + PATH_IDENTIFICADOR_JUSTIFICACION)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public ProyectoPeriodoJustificacionOutput updateIdentificadorJustificacion(
      @RequestBody @Valid ProyectoPeriodoJustificacionIdentificadorJustificacionInput proyectoPeriodoJustificacionIdentificadorJustificacionInput,
      @PathVariable Long id) {
    log.debug(
        "updateIdentificadorJustificacion(ProyectoPeriodoJustificacionIdentificadorJustificacionInput proyectoPeriodoJustificacionIdentificadorJustificacionInput, Long id) - start");

    ProyectoPeriodoJustificacion returnValue = service
        .updateIdentificadorJustificacion(convert(id, proyectoPeriodoJustificacionIdentificadorJustificacionInput));

    log.debug(
        "updateIdentificadorJustificacion(ProyectoPeriodoJustificacionIdentificadorJustificacionInput proyectoPeriodoJustificacionIdentificadorJustificacionInput, Long id) - end");

    return convert(returnValue);
  }

  /**
   * Devuelve el {@link ProyectoPeriodoJustificacion} con el Identificador de
   * justificacion indicado.
   * 
   * @param identificadorJustificacion Identificador de justificacion de
   *                                   {@link ProyectoPeriodoJustificacion}.
   * @return {@link ProyectoPeriodoJustificacion}.
   */
  @GetMapping(PATH_IDENTIFICADOR_JUSTIFICACION)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public ResponseEntity<ProyectoPeriodoJustificacionOutput> findByIdentificadorJustificacion(
      @RequestParam(required = true) String identificadorJustificacion) {
    log.debug("findByIdentificadorJustificacion(String identificadorJustificacion) - start");

    Optional<ProyectoPeriodoJustificacion> proyectoPeriodoJustificacion = service
        .findByIdentificadorJustificacion(identificadorJustificacion);
    if (proyectoPeriodoJustificacion.isPresent()) {
      log.debug("findByIdentificadorJustificacion(String identificadorJustificacion) - end");
      return new ResponseEntity<>(convert(proyectoPeriodoJustificacion.get()), HttpStatus.OK);
    }

    log.debug("findByIdentificadorJustificacion(String identificadorJustificacion) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si la entidad {@link ProyectoPeriodoJustificacion} se puede
   * eliminar.
   * 
   * @param id Identificador de {@link ProyectoPeriodoJustificacion}.
   * @return true/false.
   */
  @RequestMapping(method = RequestMethod.HEAD, path = PATH_HAS_REQUERIMIENTOS_JUSTIFICACION)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Void> hasRequerimientosJustificacion(@PathVariable Long id) {
    log.debug("hasRequerimientosJustificacion(Long id) - start");
    boolean hasRequerimientosJustificacion = service.hasRequerimientosJustificacion(id);
    log.debug("hasRequerimientosJustificacion(Long id) - end");
    return hasRequerimientosJustificacion ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  private ProyectoPeriodoJustificacionOutput convert(ProyectoPeriodoJustificacion proyectoPeriodoJustificacion) {
    return modelMapper.map(proyectoPeriodoJustificacion, ProyectoPeriodoJustificacionOutput.class);
  }

  private ProyectoPeriodoJustificacion convert(ProyectoPeriodoJustificacionInput proyectoPeriodoJustificacionInput) {
    return modelMapper.map(proyectoPeriodoJustificacionInput, ProyectoPeriodoJustificacion.class);
  }

  private ProyectoPeriodoJustificacion convert(Long id,
      ProyectoPeriodoJustificacionIdentificadorJustificacionInput proyectoPeriodoJustificacionIdentificadorJustificacionInput) {
    ProyectoPeriodoJustificacion proyectoPeriodoJustificacion = modelMapper.map(
        proyectoPeriodoJustificacionIdentificadorJustificacionInput, ProyectoPeriodoJustificacion.class);
    proyectoPeriodoJustificacion.setId(id);
    return proyectoPeriodoJustificacion;
  }

  private Page<ProyectoPeriodoJustificacionOutput> convert(Page<ProyectoPeriodoJustificacion> page) {
    List<ProyectoPeriodoJustificacionOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private List<ProyectoPeriodoJustificacion> convert(List<ProyectoPeriodoJustificacionInput> list) {

    return list.stream().map(this::convert)
        .collect(Collectors.toList());
  }

  private List<ProyectoPeriodoJustificacionOutput> convertToOutput(List<ProyectoPeriodoJustificacion> lista) {

    return lista.stream().map(this::convert)
        .collect(Collectors.toList());
  }
}
