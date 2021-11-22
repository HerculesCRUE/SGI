package org.crue.hercules.sgi.eti.service;

import java.util.Optional;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.exceptions.ChecklistNotFoundException;
import org.crue.hercules.sgi.eti.model.Checklist;
import org.crue.hercules.sgi.eti.model.Formly;
import org.crue.hercules.sgi.eti.repository.ChecklistRepository;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
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
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Checklist.class)).build());
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

    Assert.isNull(checklist.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Checklist.class)).build());
    Assert.notNull(checklist.getFormly(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage(Formly.class))
            .parameter("entity", ApplicationContextSupport.getMessage(Checklist.class)).build());
    Assert.notNull(checklist.getFormly().getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Formly.class)).build());
    Assert.notNull(checklist.getRespuesta(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("respuesta"))
            .parameter("entity", ApplicationContextSupport.getMessage(Checklist.class)).build());

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

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Checklist.class)).build());
    Assert.notNull(respuesta,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("respuesta"))
            .parameter("entity", ApplicationContextSupport.getMessage(Checklist.class)).build());

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
