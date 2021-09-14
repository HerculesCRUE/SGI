package org.crue.hercules.sgi.pii.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.pii.dto.InformePatentabilidadInput;
import org.crue.hercules.sgi.pii.dto.InformePatentabilidadOutput;
import org.crue.hercules.sgi.pii.model.InformePatentabilidad;
import org.crue.hercules.sgi.pii.service.InformePatentabilidadService;
import org.modelmapper.ModelMapper;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * InformePatentabilidadController
 * 
 */
@RestController
@RequestMapping(InformePatentabilidadController.MAPPING)
@Slf4j
public class InformePatentabilidadController {
  public static final String MAPPING = "/informespatentabilidad";

  private ModelMapper modelMapper;
  /** InformePatentabilidadService service */
  private final InformePatentabilidadService service;

  public InformePatentabilidadController(ModelMapper modelMapper,
      InformePatentabilidadService informePatentabilidadService) {
    this.modelMapper = modelMapper;
    this.service = informePatentabilidadService;
  }

  /**
   * Devuelve el {@link InformePatentabilidad} con el id indicado.
   * 
   * @param id Identificador de {@link InformePatentabilidad}.
   * @return {@link InformePatentabilidad} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B')")
  InformePatentabilidadOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    InformePatentabilidad returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link InformePatentabilidad}.
   * 
   * @param informePatentabilidad {@link InformePatentabilidad} que se quiere
   *                              crear.
   * @return Nuevo {@link InformePatentabilidad} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-E')")
  ResponseEntity<InformePatentabilidadOutput> create(
      @Valid @RequestBody InformePatentabilidadInput informePatentabilidad) {
    log.debug("create(InformePatentabilidad informePatentabilidad) - start");
    InformePatentabilidad returnValue = service.create(convert(informePatentabilidad));
    log.debug("create(InformePatentabilidad informePatentabilidad) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link InformePatentabilidad} con el id indicado.
   * 
   * @param informePatentabilidad {@link InformePatentabilidad} a actualizar.
   * @param id                    id {@link InformePatentabilidad} a actualizar.
   * @return {@link InformePatentabilidad} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  InformePatentabilidadOutput update(@Valid @RequestBody InformePatentabilidadInput informePatentabilidad,
      @PathVariable Long id) {
    log.debug("update(InformePatentabilidad informePatentabilidad, Long id) - start");
    InformePatentabilidad returnValue = service.update(convert(id, informePatentabilidad));
    log.debug("update(InformePatentabilidad informePatentabilidad, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Elimina el {@link InformePatentabilidad} con el id indicado.
   * 
   * @param id id {@link InformePatentabilidad} a eliminar.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  private InformePatentabilidadOutput convert(InformePatentabilidad informePatentabilidad) {
    return modelMapper.map(informePatentabilidad, InformePatentabilidadOutput.class);
  }

  private InformePatentabilidad convert(InformePatentabilidadInput informePatentabilidadInput) {
    return convert(null, informePatentabilidadInput);
  }

  private InformePatentabilidad convert(Long id, InformePatentabilidadInput informePatentabilidadInput) {
    InformePatentabilidad invencion = modelMapper.map(informePatentabilidadInput, InformePatentabilidad.class);
    invencion.setId(id);
    return invencion;
  }
}
