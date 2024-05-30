package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.PaisValidadoOutput;
import org.crue.hercules.sgi.pii.dto.ProcedimientoOutput;
import org.crue.hercules.sgi.pii.dto.SolicitudProteccionInput;
import org.crue.hercules.sgi.pii.dto.SolicitudProteccionOutput;
import org.crue.hercules.sgi.pii.model.InvencionGasto;
import org.crue.hercules.sgi.pii.model.PaisValidado;
import org.crue.hercules.sgi.pii.model.Procedimiento;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.service.InvencionGastoService;
import org.crue.hercules.sgi.pii.service.PaisValidadoService;
import org.crue.hercules.sgi.pii.service.ProcedimientoService;
import org.crue.hercules.sgi.pii.service.SolicitudProteccionService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping(SolicitudProteccionController.MAPPING)
@Slf4j
public class SolicitudProteccionController {
  public static final String PATH_DELIMITER = "/";

  public static final String MAPPING = PATH_DELIMITER + "solicitudesproteccion";

  public static final String PATH_PROCEDIMIENTOS = PATH_DELIMITER + "{solicitudProteccionId}/procedimientos";
  public static final String PATH_PAISESVALIDADOS = PATH_DELIMITER + "{solicitudProteccionId}/paisesvalidados";
  public static final String PATH_INVENCIONGASTOS = PATH_DELIMITER + "{solicitudProteccionId}/invenciongastos";

  private final SolicitudProteccionService solicitudProteccionService;
  private final PaisValidadoService paisValidadoService;
  private final ProcedimientoService procedimientoService;
  private final InvencionGastoService invencionGastoService;
  private final ModelMapper modelMapper;

  /**
   * Devuelve la {@link SolicitudProteccion} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProteccion}.
   * @return {@link SolicitudProteccion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R')")
  public ResponseEntity<SolicitudProteccionOutput> findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SolicitudProteccion returnValue = this.solicitudProteccionService.findById(id);
    log.debug("findById(Long id) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.OK);
  }

  /**
   * Crea un nuevo {@link SolicitudProteccion}.
   * 
   * @param solicitudProteccionInput {@link SolicitudProteccion} que se quiere
   *                                 crear.
   * @return Nuevo {@link SolicitudProteccion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public ResponseEntity<SolicitudProteccionOutput> create(
      @Valid @RequestBody SolicitudProteccionInput solicitudProteccionInput) {

    return new ResponseEntity<>(
        convert(this.solicitudProteccionService.create(convert(null, solicitudProteccionInput))), HttpStatus.CREATED);
  }

  /**
   * Actualiza la {@link SolicitudProteccion} con el id indicado.
   * 
   * @param solicitudProteccion {@link SolicitudProteccionInput} a actualizar.
   * @param id                  id {@link SolicitudProteccion} a actualizar.
   * @return {@link SolicitudProteccion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public SolicitudProteccionOutput update(@Valid @RequestBody SolicitudProteccionInput solicitudProteccion,
      @PathVariable Long id) {
    log.debug("update(@Valid @RequestBody SolicitudProteccionInput invencion, @PathVariable Long id) - start");
    SolicitudProteccion returnValue = this.solicitudProteccionService.update(convert(id, solicitudProteccion));
    log.debug("update(@Valid @RequestBody SolicitudProteccionInput invencion, @PathVariable Long id) - end");
    return convert(returnValue);
  }

  /**
   * Elimina la {@link SolicitudProteccion} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudProteccion}.
   * @return {@link HttpStatus#NO_CONTENT}
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    solicitudProteccionService.delete(id);
    log.debug("deleteById(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba la existencia de {@link PaisValidado} relacionados con el
   * id recibido.
   *
   * @param solicitudProteccionId Identificador de {@link SolicitudProteccion}.
   * @return HTTP 200 si existe alguna relación y HTTP 204 si no.
   */
  @RequestMapping(path = PATH_PAISESVALIDADOS, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V')")
  public ResponseEntity<Void> hasPaisesValidados(@PathVariable Long solicitudProteccionId) {
    log.debug("hasPaisesValidados(Long solicitudProteccionId) - start");
    boolean hasPaisesValidaddos = paisValidadoService.existsBySolicitudProteccionId(solicitudProteccionId);
    log.debug("hasPaisesValidados(Long solicitudProteccionId) - end");
    return hasPaisesValidaddos ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve los {@link PaisValidado} asociados a la {@link SolicitudProteccion}
   * con el id indicado.
   * 
   * @param solicitudProteccionId Identificador de {@link SolicitudProteccion}
   * @param paging                Información de paginación
   * @return {@link PaisValidado} asociados a la {@link SolicitudProteccion}
   */
  @GetMapping(PATH_PAISESVALIDADOS)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V', 'PII-INV-B')")
  public ResponseEntity<Page<PaisValidadoOutput>> findPaisesValidados(@PathVariable Long solicitudProteccionId,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug(
        "findPaisesValidados(@PathVariable Long solicitudProteccionId, @RequestPageable(sort = 's') Pageable paging) - start");

    Page<PaisValidado> page = paisValidadoService.findBySolicitudProteccionId(solicitudProteccionId, paging);

    if (page.isEmpty()) {
      log.debug(
          "findPaisesValidados(@PathVariable Long solicitudProteccionId, @RequestPageable(sort = 's') Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug(
        "findPaisesValidados(@PathVariable Long solicitudProteccionId, @RequestPageable(sort = 's') Pageable paging) - end");
    return ResponseEntity.ok().body(convertToPaisValidadoPage(page));
  }

  /**
   * Comprueba la existencia de {@link Procedimiento} relacionados con el
   * id recibido.
   *
   * @param solicitudProteccionId Identificador de {@link SolicitudProteccion}.
   * @return HTTP 200 si existe alguna relación y HTTP 204 si no.
   */
  @RequestMapping(path = PATH_PROCEDIMIENTOS, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V')")
  public ResponseEntity<Void> hasProcedimientos(@PathVariable Long solicitudProteccionId) {
    log.debug("hasProcedimientos(Long solicitudProteccionId) - start");
    boolean hasPaisesValidaddos = procedimientoService.existsBySolicitudProteccionId(solicitudProteccionId);
    log.debug("hasProcedimientos(Long solicitudProteccionId) - end");
    return hasPaisesValidaddos ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve los {@link Procedimiento} asociados a la {@link SolicitudProteccion}
   * con el id indicado.
   * 
   * @param solicitudProteccionId Identificador de {@link SolicitudProteccion}
   * @param paging                Información de paginación
   * @return Elementos paginados de tipo {@link Procedimiento} asociados a la
   *         {@link SolicitudProteccion}
   */
  @GetMapping(PATH_PROCEDIMIENTOS)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-C')")
  public ResponseEntity<Page<ProcedimientoOutput>> findProcedimientosBySolicitudProteccion(
      @PathVariable Long solicitudProteccionId, @RequestPageable(sort = "s") Pageable paging) {
    log.debug(
        "findProcedimientosBySolicitudProteccion(@PathVariable Long solicitudProteccionId, @RequestParam(name = 'q' @RequestPageable(sort = 's') Pageable paging) - start");

    Page<Procedimiento> page = procedimientoService.findAllBySolicitudProteccionId(solicitudProteccionId, paging);

    if (page.isEmpty()) {
      log.debug(
          "findProcedimientosBySolicitudProteccion(@PathVariable Long solicitudProteccionId, @RequestParam(name = 'q' @RequestPageable(sort = 's') Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug(
        "findProcedimientosBySolicitudProteccion(@PathVariable Long solicitudProteccionId, @RequestParam(name = 'q' @RequestPageable(sort = 's') Pageable paging) - end");
    return ResponseEntity.ok().body(convertToProcedimientoPage(page));
  }

  /**
   * Comprueba la existencia de {@link InvencionGasto} relacionados con el
   * id recibido.
   *
   * @param solicitudProteccionId Identificador de {@link SolicitudProteccion}.
   * @return HTTP 200 si existe alguna relación y HTTP 204 si no.
   */
  @RequestMapping(path = PATH_INVENCIONGASTOS, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V')")
  public ResponseEntity<Void> hasInvencionGastos(@PathVariable Long solicitudProteccionId) {
    log.debug("hasInvencionGastos(Long solicitudProteccionId) - start");
    boolean hasPaisesValidaddos = invencionGastoService.existsBySolicitudProteccionId(solicitudProteccionId);
    log.debug("hasInvencionGastos(Long solicitudProteccionId) - end");
    return hasPaisesValidaddos ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /****************/
  /* CONVERTERS */
  /****************/

  private Page<PaisValidadoOutput> convertToPaisValidadoPage(Page<PaisValidado> page) {
    List<PaisValidadoOutput> content = page.getContent().stream().map(this::convert)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private PaisValidadoOutput convert(PaisValidado paisValidado) {
    return this.modelMapper.map(paisValidado, PaisValidadoOutput.class);
  }

  private SolicitudProteccionOutput convert(SolicitudProteccion solicitudProteccion) {
    return this.modelMapper.map(solicitudProteccion, SolicitudProteccionOutput.class);
  }

  private SolicitudProteccion convert(Long id, SolicitudProteccionInput solicitudProteccionInput) {
    SolicitudProteccion solicitudProteccion = modelMapper.map(solicitudProteccionInput, SolicitudProteccion.class);
    solicitudProteccion.setId(id);
    return solicitudProteccion;
  }

  private Page<ProcedimientoOutput> convertToProcedimientoPage(Page<Procedimiento> page) {
    List<ProcedimientoOutput> content = page.getContent().stream().map(this::convert)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ProcedimientoOutput convert(Procedimiento procedimiento) {
    return modelMapper.map(procedimiento, ProcedimientoOutput.class);
  }

}
