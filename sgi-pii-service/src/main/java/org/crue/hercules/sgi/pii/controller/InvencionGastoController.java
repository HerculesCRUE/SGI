package org.crue.hercules.sgi.pii.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.pii.dto.InvencionGastoInput;
import org.crue.hercules.sgi.pii.dto.InvencionGastoOutput;
import org.crue.hercules.sgi.pii.model.InvencionGasto;
import org.crue.hercules.sgi.pii.service.InvencionGastoService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * InvencionGastoController {
 * 
 */
@RestController
@RequestMapping(InvencionGastoController.MAPPING)
@Slf4j
public class InvencionGastoController {
  public static final String MAPPING = "/invenciongastos";

  private ModelMapper modelMapper;

  /** InvencionGasto service */
  private final InvencionGastoService service;

  public InvencionGastoController(ModelMapper modelMapper, InvencionGastoService invencionGastoService) {
    this.modelMapper = modelMapper;
    this.service = invencionGastoService;
  }

  /**
   * Crea un nuevo {@link InvencionGasto}.
   * 
   * @param invencionGasto {@link InvencionGasto} que se quiere crear.
   * @return Nuevo {@link InvencionGasto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-E')")
  ResponseEntity<InvencionGastoOutput> create(@Valid @RequestBody InvencionGastoInput invencionGasto) {
    log.debug("create(InvencionGasto invencionGasto) - start");
    InvencionGasto returnValue = service.create(convert(invencionGasto));
    log.debug("create(InvencionGasto invencionGasto) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza la {@link InvencionGasto} con el id indicado.
   * 
   * @param invencionGasto {@link InvencionGasto} a actualizar.
   * @param id             id {@link InvencionGasto} a actualizar.
   * @return {@link InvencionGasto} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  InvencionGastoOutput update(@Valid @RequestBody InvencionGastoInput invencionGasto, @PathVariable Long id) {
    log.debug("update(InvencionGasto invencionGasto, Long id) - start");
    InvencionGasto returnValue = service.update(convert(id, invencionGasto));
    log.debug("update(InvencionGasto invencionGasto, Long id) - end");
    return convert(returnValue);
  }

  private InvencionGasto convert(InvencionGastoInput invencionGastoInput) {
    return convert(null, invencionGastoInput);
  }

  private InvencionGasto convert(Long id, InvencionGastoInput invencionGastoInput) {
    InvencionGasto invencionGasto = modelMapper.map(invencionGastoInput, InvencionGasto.class);
    invencionGasto.setId(id);
    return invencionGasto;
  }

  private InvencionGastoOutput convert(InvencionGasto invencionGasto) {
    return modelMapper.map(invencionGasto, InvencionGastoOutput.class);
  }
}
