package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.FormlyNotFoundException;
import org.crue.hercules.sgi.eti.model.Formly;
import org.crue.hercules.sgi.eti.repository.FormlyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Reglas de negocio de la entidad Formly.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class FormlyService {
  FormlyRepository repository;

  public FormlyService(FormlyRepository repository) {
    this.repository = repository;
  }

  /**
   * Recupera un Formly a partir de su identificador.
   * 
   * @param id Identificador del Formly a recuperar
   * @return Formly El Formly con el id solicitado
   */
  public Formly getById(Long id) {
    log.debug("getById(Long id) - start");
    final Formly returnValue = repository.findById(id).orElseThrow(() -> new FormlyNotFoundException(id));
    log.debug("getById(Long id) - end");
    return returnValue;
  }

  /**
   * Recupera la última versión (versión actual) del Formly con el nombre
   * solicitado.
   * 
   * @param nombre Nombre del Formly a recuperar
   * @return Formly La última versión del Formly con el nombre solicitado.
   */
  public Formly getByNombre(String nombre) {
    log.debug("getByNombre(String nombre) - start");
    final Formly returnValue = repository.findFirstByNombreOrderByVersionDesc(nombre)
        .orElseThrow(() -> new FormlyNotFoundException(nombre));
    log.debug("getByNombre(String nombre) - end");
    return returnValue;
  }

}
