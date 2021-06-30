package org.crue.hercules.sgi.eti.controller;

import org.crue.hercules.sgi.eti.dto.FormlyOutput;
import org.crue.hercules.sgi.eti.model.Formly;
import org.crue.hercules.sgi.eti.service.FormlyService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Controlador del API REST de la entidad Formly
 */
@RestController
@RequestMapping(FormlyController.MAPPING)
@Slf4j
public class FormlyController {
  public static final String MAPPING = "/formlies";

  private ModelMapper modelMapper;
  private FormlyService service;

  /**
   * Constructor que recibe un ModelMapper para realiar el mapeo entre entidades
   * Formly y FormlyOutput así como el FormlyService que permite ejecutar las
   * reglas de negocio asociadas a la entidad Formly.
   * 
   * @param modelMapper Realia el mapeo entre entidades
   * @param service     Ejecuta las reglas de negocio
   */
  public FormlyController(ModelMapper modelMapper, FormlyService service) {
    log.debug("FormlyController(ModelMapper modelMapper, FormlyService service) - start");
    this.modelMapper = modelMapper;
    this.service = service;
    log.debug("FormlyController(ModelMapper modelMapper, FormlyService service) - end");
  }

  /**
   * Recupera un Formly a partir de su identificador.
   * 
   * @param id Identificador del Formly a recuperar
   * @return FormlyOutput El Formly con el id solicitado
   */
  @GetMapping("/{id:[\\d]+}")
  @PreAuthorize("hasAnyAuthority('ETI-CHKLST-MOD-V', 'ETI-CHKLST-MOD-C')")
  FormlyOutput getById(@PathVariable Long id) {
    log.debug("getById(@PathVariable Long id) - start");
    Formly formly = service.getById(id);
    FormlyOutput returnValue = convert(formly);
    log.debug("getById(@PathVariable Long id) - end");
    return returnValue;
  }

  /**
   * Recupera la última versión (versión actual) del Formly con el nombre
   * solicitado.
   * 
   * @param nombre Nombre del Formly a recuperar
   * @return FormlyOutput La última versión del Formly con el nombre solicitado.
   */
  @GetMapping("/{nombre:[^\\d].*}")
  @PreAuthorize("hasAnyAuthority('ETI-CHKLST-MOD-V', 'ETI-CHKLST-MOD-C')")
  FormlyOutput getByNombre(@PathVariable String nombre) {
    log.debug("getByNombre(@PathVariable String nombre) - start");
    Formly formly = service.getByNombre(nombre);
    FormlyOutput returnValue = convert(formly);
    log.debug("getByNombre(@PathVariable String nombre) - end");
    return returnValue;
  }

  private FormlyOutput convert(Formly source) {
    return modelMapper.map(source, FormlyOutput.class);
  }

}
