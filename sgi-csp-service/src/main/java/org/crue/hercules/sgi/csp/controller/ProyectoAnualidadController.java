package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.AnualidadGastoOutput;
import org.crue.hercules.sgi.csp.dto.AnualidadIngresoOutput;
import org.crue.hercules.sgi.csp.dto.AnualidadResumen;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadInput;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadNotificacionSge;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadOutput;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.service.AnualidadGastoService;
import org.crue.hercules.sgi.csp.service.AnualidadIngresoService;
import org.crue.hercules.sgi.csp.service.ProyectoAnualidadService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoAnualidadController
 */
@RestController
@RequestMapping(ProyectoAnualidadController.MAPPING)
@Slf4j
public class ProyectoAnualidadController {
  public static final String MAPPING = "/proyectosanualidad";

  private ModelMapper modelMapper;

  /** AnualidadGastoService service */
  private final AnualidadGastoService anualidadGastoService;

  /** AnualidadIngresoService service */
  private final AnualidadIngresoService anualidadIngresoService;

  /** ProyectoAnualidad service */
  private final ProyectoAnualidadService service;

  /**
   * Instancia un nuevo ProyectoAnualidadController.
   * 
   * @param modelMapper             {@link ModelMapper}
   * @param anualidadService        {@link AnualidadGastoService}
   * @param anualidadIngresoService {@link AnualidadIngresoService}}
   * @param service                 {@link ProyectoAnualidadService}
   */
  public ProyectoAnualidadController(ModelMapper modelMapper, ProyectoAnualidadService service,
      AnualidadGastoService anualidadService, AnualidadIngresoService anualidadIngresoService) {
    this.modelMapper = modelMapper;
    this.service = service;
    this.anualidadGastoService = anualidadService;
    this.anualidadIngresoService = anualidadIngresoService;
  }

  /**
   * Crea un nuevo {@link ProyectoAnualidad}.
   * 
   * @param proyectoAnualidad {@link ProyectoAnualidad} que se quiere crear.
   * @return Nuevo {@link ProyectoAnualidad} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoAnualidadOutput> create(@Valid @RequestBody ProyectoAnualidadInput proyectoAnualidad) {
    log.debug("create(ProyectoAnualidad proyectoAnualidad) - start");
    ProyectoAnualidad returnValue = service.create(convert(proyectoAnualidad));
    log.debug("create(ProyectoAnualidad proyectoAnualidad) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Elimina {@link ProyectoAnualidad} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoAnualidad}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link AnualidadGasto} del
   * {@link ProyectoAnualidad}.
   * 
   * @param id     Identificador de {@link ProyectoAnualidad}.
   * @param query  filtro de b??squeda.
   * @param paging pageable.
   * @return el listado de entidades {@link AnualidadGastoOutput}
   *         paginadas y filtradas del {@link ProyectoAnualidad}.
   */
  @GetMapping("/{id}/anualidadgastos")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Page<AnualidadGastoOutput>> findAllAnualidadGasto(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllAnualidadGasto(Long id, String query, Pageable paging) - start");
    Page<AnualidadGasto> page = anualidadGastoService.findAllByProyectoAnualidad(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllAnualidadGasto(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllAnualiadadGfindAllAnualidadGastoasto(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(convertPageAnualidadGasto(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link AnualidadIngreso} del
   * {@link ProyectoAnualidad}.
   * 
   * @param id     Identificador de {@link ProyectoAnualidad}.
   * @param query  filtro de b??squeda.
   * @param paging pageable.
   * @return el listado de entidades {@link AnualidadIngresoOutput}
   *         paginadas y filtradas del {@link ProyectoAnualidad}.
   */
  @GetMapping("/{id}/anualidadingresos")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Page<AnualidadIngresoOutput>> findAllAnualidadIngreso(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllAnualidadIngreso(Long id, String query, Pageable paging) - start");
    Page<AnualidadIngreso> page = anualidadIngresoService.findAllByProyectoAnualiadad(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllAnualidadIngreso(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllAnualidadIngreso(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(convertPageAnualidadIngreso(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoAnualidad}
   * 
   * @param query  filtro de b??squeda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoAnualidad}
   *         paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-EJEC-E', 'CSP-EJEC-V', 'CSP-EJEC-INV-VR')")
  public ResponseEntity<Page<ProyectoAnualidadOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<ProyectoAnualidad> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link ProyectoAnualidad} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoAnualidad}.
   * @return Proyecto {@link ProyectoAnualidad} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ProyectoAnualidad findById(@PathVariable Long id) {
    log.debug("ProyectoAnualidad findById(Long id) - start");

    ProyectoAnualidad returnValue = service.findById(id);
    log.debug("ProyectoAnualidad findById(Long id) - end");
    return returnValue;
  }

  /**
   * Recupera el resumen de {@link AnualidadGasto} y {@link AnualidadIngreso}
   * ({@link AnualidadResumen}) de una {@link ProyectoAnualidad}.
   * 
   * @param id Identificador de {@link ProyectoAnualidad}.
   * @return Listado del resumen de {@link AnualidadResumen}.
   */
  @GetMapping("/{id}/partidas-resumen")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public List<AnualidadResumen> getPartidasResumen(@PathVariable Long id) {
    log.debug(" Page<AnualidadResumen> getPartidasResumen(Long id) - start");

    List<AnualidadResumen> returnValue = service.getPartidasResumen(id);
    log.debug(" Page<AnualidadResumen> getPartidasResumen(Long id) - end");
    return returnValue;
  }

  /**
   * Recupera los {@link ProyectoAnualidadNotificacionSge} que cumplan las
   * condiciones de b??squeda y tengan a true el indicador presupuestar.
   * 
   * @param query filtro de b??squeda.
   * @return Listado de {@link ProyectoAnualidadNotificacionSge}.
   */
  @GetMapping("/notificaciones-sge")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-E')")
  public List<ProyectoAnualidadNotificacionSge> findAllNotificacionesSge(
      @RequestParam(name = "q", required = false) String query) {
    log.debug("findAllNotificacionesSge(String query, Pageable paging) - start");

    List<ProyectoAnualidadNotificacionSge> returnValue = service.findAllNotificacionesSge(query);
    log.debug("findAllNotificacionesSge(String query, Pageable paging) - start");
    return returnValue;
  }

  /**
   * Actualiza el flag notificadoSge del {@link ProyectoAnualidad} con id
   * indicado.
   *
   * @param id Identificador de {@link ProyectoAnualidad}.
   * @return {@link ProyectoAnualidad} actualizado.
   */
  @PatchMapping("/{id}/notificarsge")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-E')")
  public ProyectoAnualidad notificarSge(@PathVariable Long id) {
    log.debug("notificarSge(Long id) - start");
    ProyectoAnualidad returnValue = service.notificarSge(id);
    log.debug("notificarSge(Long id) - end");
    return returnValue;
  }

  private ProyectoAnualidadOutput convert(ProyectoAnualidad proyectoAnualidad) {
    return modelMapper.map(proyectoAnualidad, ProyectoAnualidadOutput.class);
  }

  private ProyectoAnualidad convert(ProyectoAnualidadInput proyectoAnualidadInput) {
    return convert(null, proyectoAnualidadInput);
  }

  private ProyectoAnualidad convert(Long id, ProyectoAnualidadInput proyectoAnualidadInput) {
    ProyectoAnualidad proyectoAnualidad = modelMapper.map(proyectoAnualidadInput, ProyectoAnualidad.class);
    proyectoAnualidad.setId(id);
    return proyectoAnualidad;
  }

  private Page<ProyectoAnualidadOutput> convert(Page<ProyectoAnualidad> page) {
    List<ProyectoAnualidadOutput> content = page.getContent().stream()
        .map(proyectoAnualidad -> convert(proyectoAnualidad)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private Page<AnualidadGastoOutput> convertPageAnualidadGasto(Page<AnualidadGasto> page) {
    List<AnualidadGastoOutput> content = page.getContent().stream()
        .map(anualidadGasto -> modelMapper.map(anualidadGasto, AnualidadGastoOutput.class))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private Page<AnualidadIngresoOutput> convertPageAnualidadIngreso(Page<AnualidadIngreso> page) {
    List<AnualidadIngresoOutput> content = page.getContent().stream()
        .map(anualidadIngreso -> modelMapper.map(anualidadIngreso, AnualidadIngresoOutput.class))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

}