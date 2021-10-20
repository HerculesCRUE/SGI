package org.crue.hercules.sgi.pii.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.pii.dto.InvencionIngresoInput;
import org.crue.hercules.sgi.pii.dto.InvencionIngresoOutput;
import org.crue.hercules.sgi.pii.model.InvencionIngreso;
import org.crue.hercules.sgi.pii.service.InvencionIngresoService;
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
 * InvencionIngresoController {
 * 
 */
@RestController
@RequestMapping(InvencionIngresoController.MAPPING)
@Slf4j
public class InvencionIngresoController {
  public static final String MAPPING = "/invencioningresos";

  private ModelMapper modelMapper;

  /** InvencionIngreso service */
  private final InvencionIngresoService service;

  public InvencionIngresoController(ModelMapper modelMapper, InvencionIngresoService invencionIngresoService) {
    this.modelMapper = modelMapper;
    this.service = invencionIngresoService;
  }

  /**
   * Crea un nuevo {@link InvencionIngreso}.
   * 
   * @param invencionIngreso {@link InvencionIngreso} que se quiere crear.
   * @return Nuevo {@link InvencionIngreso} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-E')")
  ResponseEntity<InvencionIngresoOutput> create(@Valid @RequestBody InvencionIngresoInput invencionIngreso) {
    log.debug("create(InvencionIngreso invencionIngreso) - start");
    InvencionIngreso returnValue = service.create(convert(invencionIngreso));
    log.debug("create(InvencionIngreso invencionIngreso) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza la {@link InvencionIngreso} con el id indicado.
   * 
   * @param invencionIngreso {@link InvencionIngreso} a actualizar.
   * @param id               id {@link InvencionIngreso} a actualizar.
   * @return {@link InvencionIngreso} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  InvencionIngresoOutput update(@Valid @RequestBody InvencionIngresoInput invencionIngreso, @PathVariable Long id) {
    log.debug("update(InvencionIngreso invencionIngreso, Long id) - start");
    InvencionIngreso returnValue = service.update(convert(id, invencionIngreso));
    log.debug("update(InvencionIngreso invencionIngreso, Long id) - end");
    return convert(returnValue);
  }

  private InvencionIngreso convert(InvencionIngresoInput invencionIngresoInput) {
    return convert(null, invencionIngresoInput);
  }

  private InvencionIngreso convert(Long id, InvencionIngresoInput invencionIngresoInput) {
    InvencionIngreso invencionIngreso = modelMapper.map(invencionIngresoInput, InvencionIngreso.class);
    invencionIngreso.setId(id);
    return invencionIngreso;
  }

  private InvencionIngresoOutput convert(InvencionIngreso invencionIngreso) {
    return modelMapper.map(invencionIngreso, InvencionIngresoOutput.class);
  }
}
