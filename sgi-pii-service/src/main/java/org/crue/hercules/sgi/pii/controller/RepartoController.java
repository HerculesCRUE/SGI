package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.RepartoCreateInput;
import org.crue.hercules.sgi.pii.dto.RepartoEquipoInventorOutput;
import org.crue.hercules.sgi.pii.dto.RepartoGastoOutput;
import org.crue.hercules.sgi.pii.dto.RepartoIngresoOutput;
import org.crue.hercules.sgi.pii.dto.RepartoInput;
import org.crue.hercules.sgi.pii.dto.RepartoOutput;
import org.crue.hercules.sgi.pii.model.Reparto;
import org.crue.hercules.sgi.pii.model.RepartoEquipoInventor;
import org.crue.hercules.sgi.pii.model.RepartoGasto;
import org.crue.hercules.sgi.pii.model.RepartoIngreso;
import org.crue.hercules.sgi.pii.model.Reparto.Estado;
import org.crue.hercules.sgi.pii.service.RepartoEquipoInventorService;
import org.crue.hercules.sgi.pii.service.RepartoGastoService;
import org.crue.hercules.sgi.pii.service.RepartoIngresoService;
import org.crue.hercules.sgi.pii.service.RepartoService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RepartoController {
 * 
 */
@RestController
@RequestMapping(RepartoController.MAPPING)
@Slf4j
public class RepartoController {
  public static final String MAPPING = "/repartos";
  public static final String PATH_GASTOS = "/{repartoId}/gastos";
  public static final String PATH_INGRESOS = "/{repartoId}/ingresos";
  public static final String PATH_EQUIPO_INVENTOR = "/{repartoId}/equiposinventor";

  private ModelMapper modelMapper;

  /** Reparto service */
  private final RepartoService service;

  /** RepartoGasto service */
  private final RepartoGastoService repartoGastoService;

  /** RepartoIngreso service */
  private final RepartoIngresoService repartoIngresoService;

  /** RepartoEquipoInventor service */
  private final RepartoEquipoInventorService repartoEquipoInventorService;

  public RepartoController(ModelMapper modelMapper, RepartoService repartoService,
      RepartoGastoService repartoGastoService, RepartoIngresoService repartoIngresoService,
      RepartoEquipoInventorService repartoEquipoInventorService) {
    this.modelMapper = modelMapper;
    this.service = repartoService;
    this.repartoGastoService = repartoGastoService;
    this.repartoIngresoService = repartoIngresoService;
    this.repartoEquipoInventorService = repartoEquipoInventorService;
  }

  /**
   * Devuelve la entidad {@link Reparto} con el id indicado.
   * 
   * @param id Identificador de la entidad {@link Reparto}.
   * @return {@link Reparto} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-E')")
  public RepartoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    final Reparto returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link Reparto}.
   * 
   * @param reparto {@link Reparto} que se quiere crear.
   * @return Nuevo {@link Reparto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public ResponseEntity<RepartoOutput> create(@Valid @RequestBody RepartoCreateInput reparto) {
    log.debug("create(Reparto reparto) - start");
    Reparto returnValue = service.create(reparto);
    log.debug("create(Reparto reparto) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link Reparto} con el id indicado.
   * 
   * @param reparto {@link Reparto} a actualizar.
   * @param id      id {@link Reparto} a actualizar.
   * @return {@link Reparto} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public RepartoOutput update(@Valid @RequestBody RepartoInput reparto, @PathVariable Long id) {
    log.debug("update(Reparto reparto, Long id) - start");
    Reparto returnValue = service.update(convert(id, reparto));
    log.debug("update(Reparto reparto, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Devuelve los {@link RepartoGasto} asociados a la entidad {@link Reparto} con
   * el id indicado paginadas y/o filtradas.
   * 
   * @param repartoId Identificador de {@link Reparto}
   * @param query     filtro de búsqueda.
   * @param paging    pageable.
   * @return {@link RepartoGasto} asociados a la entidad {@link Reparto} paginadas
   *         y/o filtradas.
   */
  @GetMapping(PATH_GASTOS)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V')")
  public ResponseEntity<Page<RepartoGastoOutput>> findGastos(@PathVariable Long repartoId,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findGastos(Long repartoId, String query) - start");

    Page<RepartoGasto> page = repartoGastoService.findByRepartoId(repartoId, query, paging);

    if (page.isEmpty()) {
      log.debug("findGastos(Long repartoId, String query) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findGastos(Long repartoId, String query) - end");
    return new ResponseEntity<>(convertGastos(page), HttpStatus.OK);
  }

  /**
   * Devuelve los {@link RepartoIngreso} asociados a la entidad {@link Reparto}
   * con el id indicado paginadas y/o filtradas.
   * 
   * @param repartoId Identificador de {@link Reparto}
   * @param query     filtro de búsqueda.
   * @param paging    pageable.
   * @return {@link RepartoIngreso} asociados a la entidad {@link Reparto}
   *         paginadas y/o filtradas.
   */
  @GetMapping(PATH_INGRESOS)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V')")
  public ResponseEntity<Page<RepartoIngresoOutput>> findIngresos(@PathVariable Long repartoId,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findIngresos(Long repartoId, String query, Pageable paging) - start");

    Page<RepartoIngreso> page = repartoIngresoService.findByRepartoId(repartoId, query, paging);

    if (page.isEmpty()) {
      log.debug("findIngresos(Long repartoId, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findIngresos(Long repartoId, String query, Pageable paging) - end");
    return new ResponseEntity<>(convertIngresos(page), HttpStatus.OK);
  }

  /**
   * Devuelve los {@link RepartoEquipoInventor} asociados a la entidad
   * {@link Reparto} con el id indicado paginadas y/o filtradas.
   * 
   * @param repartoId Identificador de {@link Reparto}
   * @param query     filtro de búsqueda.
   * @param paging    pageable.
   * @return {@link RepartoEquipoInventor} asociados a la entidad {@link Reparto}
   *         paginadas y/o filtradas.
   */
  @GetMapping(PATH_EQUIPO_INVENTOR)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V')")
  public ResponseEntity<Page<RepartoEquipoInventorOutput>> findEquipoInventor(@PathVariable Long repartoId,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findEquipoInventor(Long repartoId, String query, Pageable paging) - start");

    Page<RepartoEquipoInventor> page = repartoEquipoInventorService.findByRepartoId(repartoId, query, paging);

    if (page.isEmpty()) {
      log.debug("findEquipoInventor(Long repartoId, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findEquipoInventor(Long repartoId, String query, Pageable paging) - end");
    return new ResponseEntity<>(convertEquiposInventor(page), HttpStatus.OK);
  }

  /**
   * Realiza el cambio de estado de la entidad {@link Reparto} al {@link Estado}
   * EJECUTADO.
   * 
   * @param id Identificador de la entidad {@link Reparto}.
   * @return {@link Reparto} actualizado.
   */
  @PatchMapping("/{id}/ejecutar")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public RepartoOutput ejecutar(@PathVariable Long id) {
    log.debug("ejecutar(Long id) - start");

    Reparto returnValue = service.ejecutar(id);

    log.debug("ejecutar(Long id) - end");
    return convert(returnValue);
  }

  // Converters

  private RepartoOutput convert(Reparto reparto) {
    return modelMapper.map(reparto, RepartoOutput.class);
  }

  private Reparto convert(Long id, RepartoInput repartoInput) {
    Reparto reparto = modelMapper.map(repartoInput, Reparto.class);
    reparto.setId(id);
    return reparto;
  }

  private Page<RepartoGastoOutput> convertGastos(Page<RepartoGasto> page) {
    List<RepartoGastoOutput> content = page.getContent().stream().map(repartoGasto -> convertGasto(repartoGasto))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private RepartoGastoOutput convertGasto(RepartoGasto repartoGasto) {
    return modelMapper.map(repartoGasto, RepartoGastoOutput.class);
  }

  private Page<RepartoIngresoOutput> convertIngresos(Page<RepartoIngreso> page) {
    List<RepartoIngresoOutput> content = page.getContent().stream()
        .map(repartoIngreso -> convertIngreso(repartoIngreso)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private RepartoIngresoOutput convertIngreso(RepartoIngreso repartoIngreso) {
    return modelMapper.map(repartoIngreso, RepartoIngresoOutput.class);
  }

  private Page<RepartoEquipoInventorOutput> convertEquiposInventor(Page<RepartoEquipoInventor> page) {
    List<RepartoEquipoInventorOutput> content = page.getContent().stream()
        .map(repartoEquipoInventor -> convertEquipoInventor(repartoEquipoInventor)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private RepartoEquipoInventorOutput convertEquipoInventor(RepartoEquipoInventor repartoEquipoInventor) {
    return modelMapper.map(repartoEquipoInventor, RepartoEquipoInventorOutput.class);
  }
}
