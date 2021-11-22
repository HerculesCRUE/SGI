package org.crue.hercules.sgi.pii.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.pii.dto.RepartoEquipoInventorInput;
import org.crue.hercules.sgi.pii.dto.RepartoEquipoInventorOutput;
import org.crue.hercules.sgi.pii.model.RepartoEquipoInventor;
import org.crue.hercules.sgi.pii.service.RepartoEquipoInventorService;
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
 * RepartoEquipoInventorController {
 * 
 */
@RestController
@RequestMapping(RepartoEquipoInventorController.MAPPING)
@Slf4j
public class RepartoEquipoInventorController {
  public static final String MAPPING = "/repartoequiposinventor";

  private ModelMapper modelMapper;

  /** RepartoEquipoInventor service */
  private final RepartoEquipoInventorService service;

  public RepartoEquipoInventorController(ModelMapper modelMapper,
      RepartoEquipoInventorService repartoEquipoInventorService) {
    this.modelMapper = modelMapper;
    this.service = repartoEquipoInventorService;
  }

  /**
   * Devuelve la entidad {@link RepartoEquipoInventor} con el id indicado.
   * 
   * @param id Identificador de la entidad {@link RepartoEquipoInventor}.
   * @return {@link RepartoEquipoInventor} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-E')")
  public RepartoEquipoInventorOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    final RepartoEquipoInventor returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link RepartoEquipoInventor}.
   * 
   * @param repartoEquipoInventor {@link RepartoEquipoInventor} que se quiere
   *                              crear.
   * @return Nuevo {@link RepartoEquipoInventor} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public ResponseEntity<RepartoEquipoInventorOutput> create(
      @Valid @RequestBody RepartoEquipoInventorInput repartoEquipoInventor) {
    log.debug("create(RepartoEquipoInventor repartoEquipoInventor) - start");
    RepartoEquipoInventor returnValue = service.create(convert(repartoEquipoInventor));
    log.debug("create(RepartoEquipoInventor repartoEquipoInventor) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link RepartoEquipoInventor} con el id indicado.
   * 
   * @param repartoEquipoInventor {@link RepartoEquipoInventor} a actualizar.
   * @param id                    id {@link RepartoEquipoInventor} a actualizar.
   * @return {@link RepartoEquipoInventor} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public RepartoEquipoInventorOutput update(@Valid @RequestBody RepartoEquipoInventorInput repartoEquipoInventor,
      @PathVariable Long id) {
    log.debug("update(RepartoEquipoInventor repartoEquipoInventor, Long id) - start");
    RepartoEquipoInventor returnValue = service.update(convert(id, repartoEquipoInventor));
    log.debug("update(RepartoEquipoInventor repartoEquipoInventor, Long id) - end");
    return convert(returnValue);
  }

  private RepartoEquipoInventorOutput convert(RepartoEquipoInventor reparto) {
    return modelMapper.map(reparto, RepartoEquipoInventorOutput.class);
  }

  private RepartoEquipoInventor convert(RepartoEquipoInventorInput repartoEquipoInventorInput) {
    return convert(null, repartoEquipoInventorInput);
  }

  private RepartoEquipoInventor convert(Long id, RepartoEquipoInventorInput repartoEquipoInventorInput) {
    RepartoEquipoInventor reparto = modelMapper.map(repartoEquipoInventorInput, RepartoEquipoInventor.class);
    reparto.setId(id);
    return reparto;
  }
}
