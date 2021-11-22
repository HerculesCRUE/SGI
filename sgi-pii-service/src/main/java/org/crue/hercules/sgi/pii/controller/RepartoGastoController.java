package org.crue.hercules.sgi.pii.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.pii.dto.RepartoGastoInput;
import org.crue.hercules.sgi.pii.dto.RepartoGastoOutput;
import org.crue.hercules.sgi.pii.model.RepartoGasto;
import org.crue.hercules.sgi.pii.service.RepartoGastoService;
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
 * RepartoGastoController {
 * 
 */
@RestController
@RequestMapping(RepartoGastoController.MAPPING)
@Slf4j
public class RepartoGastoController {
  public static final String MAPPING = "/repartogastos";

  private ModelMapper modelMapper;

  /** Reparto service */
  private final RepartoGastoService service;

  public RepartoGastoController(ModelMapper modelMapper, RepartoGastoService repartoGastoService) {
    this.modelMapper = modelMapper;
    this.service = repartoGastoService;
  }

  /**
   * Devuelve la entidad {@link RepartoGasto} con el id indicado.
   * 
   * @param id Identificador de la entidad {@link RepartoGasto}.
   * @return {@link RepartoGasto} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-E')")
  public RepartoGastoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    final RepartoGasto returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link RepartoGasto}.
   * 
   * @param repartoGasto {@link RepartoGasto} que se quiere crear.
   * @return Nuevo {@link RepartoGasto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public ResponseEntity<RepartoGastoOutput> create(@Valid @RequestBody RepartoGastoInput repartoGasto) {
    log.debug("create(RepartoGasto repartoGasto) - start");
    RepartoGasto returnValue = service.create(convert(repartoGasto));
    log.debug("create(RepartoGasto repartoGasto) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link RepartoGasto} con el id indicado.
   * 
   * @param repartoGasto {@link RepartoGasto} a actualizar.
   * @param id           id {@link RepartoGasto} a actualizar.
   * @return {@link RepartoGasto} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public RepartoGastoOutput update(@Valid @RequestBody RepartoGastoInput repartoGasto, @PathVariable Long id) {
    log.debug("update(RepartoGasto repartoGasto, Long id) - start");
    RepartoGasto returnValue = service.update(convert(id, repartoGasto));
    log.debug("update(RepartoGasto repartoGasto, Long id) - end");
    return convert(returnValue);
  }

  private RepartoGastoOutput convert(RepartoGasto reparto) {
    return modelMapper.map(reparto, RepartoGastoOutput.class);
  }

  private RepartoGasto convert(RepartoGastoInput repartoGastoInput) {
    return convert(null, repartoGastoInput);
  }

  private RepartoGasto convert(Long id, RepartoGastoInput repartoGastoInput) {
    RepartoGasto reparto = modelMapper.map(repartoGastoInput, RepartoGasto.class);
    reparto.setId(id);
    return reparto;
  }
}
