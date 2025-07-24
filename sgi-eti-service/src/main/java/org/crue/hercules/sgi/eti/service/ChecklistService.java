package org.crue.hercules.sgi.eti.service;

import java.util.Optional;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.exceptions.ChecklistNotFoundException;
import org.crue.hercules.sgi.eti.model.Checklist;
import org.crue.hercules.sgi.eti.model.Formly;
import org.crue.hercules.sgi.eti.repository.ChecklistRepository;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Reglas de negocio de la entidad Checklist.
 * <p>
 * Un Checklist son las respuestas de un usuario al formulario de Checklist.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ChecklistService {
  private ChecklistRepository repository;

  public ChecklistService(ChecklistRepository repository) {
    this.repository = repository;
  }

  /**
   * Recupera un Checlist a partir de su identificador.
   * 
   * @param id Identificador del Checlist a recuperar
   * @return Checlist El Checlist con el id solicitado
   */
  public Checklist getById(Long id) {
    log.debug("getById(Long id) - start");
    AssertHelper.idNotNull(id, Checklist.class);
    final Checklist returnValue = repository.findById(id).orElseThrow(() -> new ChecklistNotFoundException(id));
    log.debug("getById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo Checklist.
   * 
   * @param checklist Contenido del Checklist a crear
   * @return Checklist El Checlist creado
   */
  @Transactional
  public Checklist create(@Valid Checklist checklist) {
    log.debug("create(Checklist checklist) - start");

    AssertHelper.idIsNull(checklist.getId(), Checklist.class);
    AssertHelper.entityNotNull(checklist.getFormly(), Checklist.class, Formly.class);
    AssertHelper.idNotNull(checklist.getFormly().getId(), Formly.class);
    AssertHelper.fieldNotNull(checklist.getRespuesta(), Checklist.class, "respuesta");

    // formly property is not a full fledged Fromly instance, it only contains the
    // id, so we need to force "reload" the just created instance from the database.
    Checklist returnValue = repository.saveAndFlush(checklist);
    repository.refresh(returnValue);

    log.debug("create(Checklist checklist) - end");
    return returnValue;
  }

  /**
   * Actualiza la respuesta de un Checklist existente.
   * 
   * @param id        Identificador del Checlist a actualizar
   * @param respuesta Valor a actualizar
   * @return Checklist El Checlist actualizado
   */
  @Transactional
  public Checklist updateRespuesta(Long id, String respuesta) {
    log.debug("updateRespuesta(Long id, String respuesta) - start");

    AssertHelper.idNotNull(id, Checklist.class);
    AssertHelper.fieldNotNull(respuesta, Checklist.class, "respuesta");

    Optional<Checklist> checklist = repository.findById(id);
    if (!checklist.isPresent()) {
      throw new ChecklistNotFoundException(id);
    }
    checklist.get().setRespuesta(respuesta);
    Checklist returnValue = repository.saveAndFlush(checklist.get());
    log.debug("updateRespuesta(Long id, String respuesta) - end");
    return returnValue;
  }

}
