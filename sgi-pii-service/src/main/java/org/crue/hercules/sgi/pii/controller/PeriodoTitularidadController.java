package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.PeriodoTitularidadInput;
import org.crue.hercules.sgi.pii.dto.PeriodoTitularidadOutput;
import org.crue.hercules.sgi.pii.dto.PeriodoTitularidadTitularInput;
import org.crue.hercules.sgi.pii.dto.PeriodoTitularidadTitularOutput;
import org.crue.hercules.sgi.pii.exceptions.InvencionNotFoundException;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular;
import org.crue.hercules.sgi.pii.service.InvencionService;
import org.crue.hercules.sgi.pii.service.PeriodoTitularidadService;
import org.crue.hercules.sgi.pii.service.PeriodoTitularidadTitularService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * PeriodoTitularidadController
 */
@RestController
@RequestMapping(PeriodoTitularidadController.MAPPING)
@Slf4j
public class PeriodoTitularidadController {
  public static final String MAPPING = "/periodostitularidad";

  private ModelMapper modelMapper;

  /** PeriodoTitularidad service */
  private final PeriodoTitularidadService service;
  /** Invencion service */
  private final InvencionService invencionService;
  /** PeriodoTitularidadTitular service */
  private final PeriodoTitularidadTitularService periodoTitularidadTitularService;

  /**
   * Instancia un nuevo {@link PeriodoTitularidadController}.
   * 
   * @param service     Instancia de {@link PeriodoTitularidadService}
   * @param modelMapper Instancia de {@link ModelMapper}
   */
  public PeriodoTitularidadController(ModelMapper modelMapper, PeriodoTitularidadService service,
      InvencionService invencionService, PeriodoTitularidadTitularService periodoTitularidadTitularService) {
    this.modelMapper = modelMapper;
    this.service = service;
    this.invencionService = invencionService;
    this.periodoTitularidadTitularService = periodoTitularidadTitularService;
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link PeriodoTitularidad}.
   * 
   * @param query  Filtro de búsqueda.
   * @param paging Información de Paginado.
   * @return Lista de entidades {@link PeriodoTitularidad} paginadas y/o
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('PII-INV-C', 'PII-INV-E', 'PII-INV-V')")
  ResponseEntity<Page<PeriodoTitularidadOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug(
        "findAll(@RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - start");
    Page<PeriodoTitularidad> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug(
          "findAll(@RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug(
        "findAll(@RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - end");
    return ResponseEntity.ok().body(convertToPeriodoTitularidadPage(page));
  }

  /**
   * Crea un nuevo {@link PeriodoTitularidad}.
   * 
   * @param periodoTitularidad {@link PeriodoTitularidad} que se quiere crear.
   * @return Nuevo {@link PeriodoTitularidad} creado.
   */
  @PostMapping()
  @PreAuthorize("hasAuthority('PII-INV-C')")
  ResponseEntity<PeriodoTitularidadOutput> create(@Valid @RequestBody PeriodoTitularidadInput periodoTitularidad) {
    log.debug("create(@Valid @RequestBody PeriodoTitularidadInput periodoTitularidad) - start");

    PeriodoTitularidad returnValue = this.service.create(convert(periodoTitularidad),
        periodoTitularidad.getFechaFinPrevious());

    log.debug("create(@Valid @RequestBody PeriodoTitularidadInput periodoTitularidad) - end");
    return ResponseEntity.status(HttpStatus.CREATED).body(convert(returnValue));
  }

  /**
   * Modifica el {@link PeriodoTitularidad} pasado por parámetros
   * 
   * @param periodoTitularidadId
   * @param periodoTitularidadInput
   * @return
   */
  @PutMapping("/{periodoTitularidadId}")
  @PreAuthorize("hasAnyAuthority('PII-INV-C', 'PII-INV-E')")
  ResponseEntity<PeriodoTitularidadOutput> updatePeriodoTitularidadInvencionId(@PathVariable Long periodoTitularidadId,
      @Valid @RequestBody PeriodoTitularidadInput periodoTitularidadInput) {

    if (!this.invencionService.existsById(periodoTitularidadInput.getInvencionId())) {
      throw new InvencionNotFoundException(periodoTitularidadInput.getInvencionId());
    }
    PeriodoTitularidad returnValue = convert(periodoTitularidadId, periodoTitularidadInput);
    returnValue = this.service.update(returnValue);

    return ResponseEntity.ok().body(convert(returnValue));
  }

  private Page<PeriodoTitularidadOutput> convertToPeriodoTitularidadPage(Page<PeriodoTitularidad> page) {
    List<PeriodoTitularidadOutput> content = page.getContent().stream()
        .map((periodoTitularidad) -> convert(periodoTitularidad)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  /**
   * Elimina el {@link PeriodoTitularidad} pasado por parámetros.
   * 
   * @param periodoTitularidadId
   * @return Respuesta vacía
   */
  @DeleteMapping("/{periodoTitularidadId}")
  @PreAuthorize("hasAnyAuthority('PII-INV-E','PII-INV-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  @Transactional
  void eliminarPeriodoTitularidad(@PathVariable Long periodoTitularidadId) {
    this.service.deleteById(periodoTitularidadId);
  }

  @GetMapping("{periodoTitularidadId}/titulares")
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V', 'PII-INV-B')")
  public ResponseEntity<List<PeriodoTitularidadTitularOutput>> findPeriodosTitularidadTitulares(
      @PathVariable Long periodoTitularidadId) {
    log.debug("findPeriodosTitularidadTitulares(@PathVariable Long periodoTitularidadId) - start");

    List<PeriodoTitularidadTitularOutput> returnValue = convertPeriodoTitularidadTitular(
        periodoTitularidadTitularService.findAllByPeriodoTitularidadId(periodoTitularidadId));

    log.debug("findPeriodosTitularidadTitulares(@PathVariable Long periodoTitularidadId) - end");
    return ResponseEntity.ok().body(returnValue);
  }

  @PatchMapping("{periodoTitularidadId}/titulares")
  @PreAuthorize("hasAnyAuthority('PII-INV-C', 'PII-INV-E')")
  ResponseEntity<List<PeriodoTitularidadTitularOutput>> updateTitularesByPeriodoTitularidad(
      @PathVariable Long periodoTitularidadId,
      @Valid @RequestBody List<PeriodoTitularidadTitularInput> periodoTitularidadTitularesInput) {

    List<PeriodoTitularidadTitular> periodoTitularidadTitulares = convertPeriodoTitularidadTitularesInput(
        periodoTitularidadTitularesInput);

    return ResponseEntity.ok().body(convertPeriodoTitularidadTitular(this.periodoTitularidadTitularService
        .saveUpdateOrDeleteBatchMode(periodoTitularidadId, periodoTitularidadTitulares)));
  }

  private List<PeriodoTitularidadTitular> convertPeriodoTitularidadTitularesInput(
      List<PeriodoTitularidadTitularInput> inputs) {
    return inputs.stream().map((input) -> convert(input)).collect(Collectors.toList());
  }

  private PeriodoTitularidadTitular convert(PeriodoTitularidadTitularInput periodoTitularidadTitularInput) {
    return modelMapper.map(periodoTitularidadTitularInput, PeriodoTitularidadTitular.class);
  }

  private List<PeriodoTitularidadTitularOutput> convertPeriodoTitularidadTitular(
      List<PeriodoTitularidadTitular> entities) {
    return entities.stream().map((entity) -> convert(entity)).collect(Collectors.toList());
  }

  private PeriodoTitularidadTitularOutput convert(PeriodoTitularidadTitular periodoTitularidadTitular) {
    return modelMapper.map(periodoTitularidadTitular, PeriodoTitularidadTitularOutput.class);
  }

  private PeriodoTitularidadOutput convert(PeriodoTitularidad periodoTitularidad) {
    return modelMapper.map(periodoTitularidad, PeriodoTitularidadOutput.class);
  }

  private PeriodoTitularidad convert(PeriodoTitularidadInput periodoTitularidadInput) {
    return convert(null, periodoTitularidadInput);
  }

  private PeriodoTitularidad convert(Long id, PeriodoTitularidadInput periodoTitularidadInput) {

    PeriodoTitularidad periodoTitularidad = modelMapper.map(periodoTitularidadInput, PeriodoTitularidad.class);
    periodoTitularidad.setId(id);

    return periodoTitularidad;
  }

}
