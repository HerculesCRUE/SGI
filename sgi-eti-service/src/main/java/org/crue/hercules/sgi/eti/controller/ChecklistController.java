package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.crue.hercules.sgi.eti.dto.ChecklistInput;
import org.crue.hercules.sgi.eti.dto.ChecklistOutput;
import org.crue.hercules.sgi.eti.model.Checklist;
import org.crue.hercules.sgi.eti.service.ChecklistService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Controlador del API REST de la entidad Checklist
 */
@RestController
@RequestMapping(ChecklistController.MAPPING)
@Slf4j
public class ChecklistController {
  public static final String MAPPING = "/checklists";

  private ModelMapper modelMapper;
  private ChecklistService service;

  public ChecklistController(ModelMapper modelMapper, ChecklistService service) {
    log.debug("ChecklistController(ModelMapper modelMapper, ChecklistService service) - start");
    this.modelMapper = modelMapper;
    this.service = service;
    // customize the modelMapper
    this.modelMapper.typeMap(Checklist.class, ChecklistOutput.class).addMappings(mapper -> {
      mapper.map(Checklist::getCreationDate, ChecklistOutput::setFechaCreacion);
    });
    log.debug("ChecklistController(ModelMapper modelMapper, ChecklistService service) - end");
  }

  /**
   * Recupera un Checklist a partir de su identificador.
   * 
   * @param id Identificador del Checklist a recuperar
   * @return ChecklistOutput El Checklist con el id solicitado
   */
  @GetMapping("/{id}")
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-eti')) or hasAnyAuthority('ETI-CHKLST-MOD-V', 'ETI-CHKLST-MOD-C')")
  ChecklistOutput getById(@PathVariable Long id) {
    log.debug("getById(@PathVariable Long id) - start");
    Checklist checklist = service.getById(id);
    ChecklistOutput returnValue = convert(checklist);
    log.debug("getById(@PathVariable Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link Checklist}.
   * 
   * @param checklist {@link Checklist} que se quiere crear
   * @return Nuevo {@link Checklist} creado
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthority('ETI-CHKLST-MOD-C', 'ETI-CHK-INV-E')")
  ResponseEntity<ChecklistOutput> create(@Valid @RequestBody ChecklistInput checklist) {
    log.debug("create(ChecklistInput checklist) - start");
    Checklist created = service.create(convert(checklist));
    ChecklistOutput returnValue = convert(created);
    log.debug("create(ChecklistInput checklist) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza la respuesta de un {@link Checklist}.
   * 
   * @param id        Identificador del Checklist a actualizar
   * @param respuesta La respuesta a actualizar
   * @return El {@link Checklist} actualizado
   */
  @PatchMapping("/{id}/respuesta")
  @PreAuthorize("hasAnyAuthority('ETI-CHKLST-MOD-C', 'ETI-CHK-INV-E')")
  ResponseEntity<ChecklistOutput> updateRespuesta(@PathVariable Long id, @NotEmpty @RequestBody String respuesta) {
    log.debug("update(Long id, String respuesta) - start");
    Checklist updated = service.updateRespuesta(id, respuesta);
    ChecklistOutput returnValue = convert(updated);
    log.debug("update(Long id, String respuesta) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  private ChecklistOutput convert(Checklist source) {
    ChecklistOutput target = modelMapper.map(source, ChecklistOutput.class);
    return target;
  }

  private Checklist convert(ChecklistInput source) {
    return convert(null, source);
  }

  private Checklist convert(Long id, ChecklistInput source) {
    Checklist target = modelMapper.map(source, Checklist.class);
    target.setId(id);
    return target;
  }
}
