package org.crue.hercules.sgi.pii.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.pii.dto.RepartoIngresoInput;
import org.crue.hercules.sgi.pii.dto.RepartoIngresoOutput;
import org.crue.hercules.sgi.pii.model.RepartoIngreso;
import org.crue.hercules.sgi.pii.service.RepartoIngresoService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RepartoIngresoController {
 * 
 */
@RestController
@RequestMapping(RepartoIngresoController.MAPPING)
@Slf4j
public class RepartoIngresoController {
  public static final String MAPPING = "/repartoingresos";

  private ModelMapper modelMapper;

  /** Reparto service */
  private final RepartoIngresoService service;

  public RepartoIngresoController(ModelMapper modelMapper, RepartoIngresoService repartoIngresoService) {
    this.modelMapper = modelMapper;
    this.service = repartoIngresoService;
  }

  /**
   * Devuelve la entidad {@link RepartoIngreso} con el id indicado.
   * 
   * @param id Identificador de la entidad {@link RepartoIngreso}.
   * @return {@link RepartoIngreso} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-E')")
  public RepartoIngresoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    final RepartoIngreso returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link RepartoIngreso}.
   * 
   * @param repartoIngreso {@link RepartoIngreso} que se quiere crear.
   * @return Nuevo {@link RepartoIngreso} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public ResponseEntity<RepartoIngresoOutput> create(@Valid @RequestBody RepartoIngresoInput repartoIngreso) {
    log.debug("create(RepartoIngreso repartoIngreso) - start");
    RepartoIngreso returnValue = service.create(convert(repartoIngreso));
    log.debug("create(RepartoIngreso repartoIngreso) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link RepartoIngreso} con el id indicado.
   * 
   * @param repartoIngreso {@link RepartoIngreso} a actualizar.
   * @param id             id {@link RepartoIngreso} a actualizar.
   * @return {@link RepartoIngreso} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public RepartoIngresoOutput update(@Valid @RequestBody RepartoIngresoInput repartoIngreso, @PathVariable Long id) {
    log.debug("update(RepartoIngreso repartoIngreso, Long id) - start");
    RepartoIngreso returnValue = service.update(convert(id, repartoIngreso));
    log.debug("update(RepartoIngreso repartoIngreso, Long id) - end");
    return convert(returnValue);
  }

  private RepartoIngresoOutput convert(RepartoIngreso reparto) {
    return modelMapper.map(reparto, RepartoIngresoOutput.class);
  }

  private RepartoIngreso convert(RepartoIngresoInput repartoIngresoInput) {
    return convert(null, repartoIngresoInput);
  }

  private RepartoIngreso convert(Long id, RepartoIngresoInput repartoIngresoInput) {
    RepartoIngreso reparto = modelMapper.map(repartoIngresoInput, RepartoIngreso.class);
    reparto.setId(id);
    return reparto;
  }
}
