package org.crue.hercules.sgi.pii.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.pii.dto.PaisValidadoInput;
import org.crue.hercules.sgi.pii.dto.PaisValidadoOutput;
import org.crue.hercules.sgi.pii.model.PaisValidado;
import org.crue.hercules.sgi.pii.service.PaisValidadoService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * PaisValidadoController {
 * 
 */
@RestController
@RequestMapping(PaisValidadoController.MAPPING)
@Slf4j
public class PaisValidadoController {
  public static final String MAPPING = "/paisesvalidados";

  private ModelMapper modelMapper;

  /** InvencionGasto service */
  private final PaisValidadoService service;

  public PaisValidadoController(ModelMapper modelMapper, PaisValidadoService paisValidadoService) {
    this.modelMapper = modelMapper;
    this.service = paisValidadoService;
  }

  /**
   * Crea un nuevo {@link PaisValidado}.
   *
   * @param paisValidado {@link PaisValidado} a crear.
   * @return {@link PaisValidado} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public ResponseEntity<PaisValidadoOutput> create(@Valid @RequestBody PaisValidadoInput paisValidado) {
    log.debug("create(@Valid @RequestBody PaisValidadoInput invencionGasto) - start");
    PaisValidado returnValue = this.service.create(convert(paisValidado));
    log.debug("create(@Valid @RequestBody PaisValidadoInput invencionGasto) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link PaisValidado} con el id indicado.
   *
   * @param paisValidado {@link PaisValidado} a actualizar.
   * @param id           Id {@link PaisValidado} a actualizar.
   * @return {@link PaisValidado} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public PaisValidadoOutput update(@Valid @RequestBody PaisValidadoInput paisValidado, @PathVariable Long id) {
    log.debug("update(@Valid @RequestBody PaisValidadoInput paisValidado, @PathVariable Long id) - start");
    PaisValidado returnValue = this.service.update(convert(id, paisValidado));
    log.debug("update(@Valid @RequestBody PaisValidadoInput paisValidado, @PathVariable Long id) - end");
    return convert(returnValue);
  }

  /**
   * Elimina el {@link PaisValidado} con el id indicado.
   * 
   * @param id Id del {@link PaisValidado} a eliminar.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    this.service.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  private PaisValidado convert(PaisValidadoInput paisValidadoInput) {
    return convert(null, paisValidadoInput);
  }

  private PaisValidado convert(Long id, PaisValidadoInput paisValidadoInput) {
    PaisValidado paisValidado = modelMapper.map(paisValidadoInput, PaisValidado.class);
    paisValidado.setId(id);
    return paisValidado;
  }

  private PaisValidadoOutput convert(PaisValidado paisValidado) {
    return modelMapper.map(paisValidado, PaisValidadoOutput.class);
  }
}
