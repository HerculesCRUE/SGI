package org.crue.hercules.sgi.pii.service;

import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_NOTNULL;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_FIELD;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.InformePatentabilidadNotFoundException;
import org.crue.hercules.sgi.pii.model.InformePatentabilidad;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.repository.InformePatentabilidadRepository;
import org.crue.hercules.sgi.pii.repository.specification.InformePatentabilidadSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad InformePatentabilidad.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class InformePatentabilidadService {

  private final Validator validator;
  private final InformePatentabilidadRepository repository;

  public InformePatentabilidadService(Validator validator,
      InformePatentabilidadRepository informePatentabilidadRepository) {
    this.validator = validator;
    this.repository = informePatentabilidadRepository;
  }

  /**
   * Obtiene los {@link InformePatentabilidad} para una {@link Invencion}.
   *
   * @param invencionId el id de la {@link Invencion}.
   * @return la lista de {@link InformePatentabilidad} de la {@link Invencion}.
   */
  public List<InformePatentabilidad> findByInvencion(Long invencionId) {
    log.debug("findByInvencion(Long invencionId) - start");

    Specification<InformePatentabilidad> specs = InformePatentabilidadSpecifications.byInvencionId(invencionId);

    List<InformePatentabilidad> returnValue = repository.findAll(specs);
    log.debug("findByInvencion(Long invencionId) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link InformePatentabilidad} por su id.
   *
   * @param id el id de la entidad {@link InformePatentabilidad}.
   * @return la entidad {@link InformePatentabilidad}.
   */
  public InformePatentabilidad findById(Long id) {
    log.debug("findById(Long id) - start");

    final InformePatentabilidad returnValue = repository.findById(id)
        .orElseThrow(() -> new InformePatentabilidadNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link InformePatentabilidad}.
   *
   * @param informePatentabilidad la entidad {@link InformePatentabilidad} a
   *                              guardar.
   * @return la entidad {@link InformePatentabilidad} persistida.
   */
  @Transactional
  @Validated({ InformePatentabilidad.OnCrear.class })
  public InformePatentabilidad create(@Valid InformePatentabilidad informePatentabilidad) {
    log.debug("create(InformePatentabilidad informePatentabilidad) - start");
    Assert.isNull(informePatentabilidad.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(InformePatentabilidad.class))
            .build());
    Assert.notNull(informePatentabilidad.getResultadoInformePatentabilidad(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD,
                ApplicationContextSupport.getMessage(ResultadoInformePatentabilidadService.class))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(InformePatentabilidad.class))
            .build());
    Assert.notNull(informePatentabilidad.getResultadoInformePatentabilidad().getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ResultadoInformePatentabilidadService.class))
            .build());

    InformePatentabilidad returnValue = repository.save(informePatentabilidad);

    log.debug("create(InformePatentabilidad informePatentabilidad) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link InformePatentabilidad}.
   *
   * @param informePatentabilidad la entidad {@link InformePatentabilidad} a
   *                              actualizar.
   * @return la entidad {@link InformePatentabilidad} persistida.
   */
  @Transactional
  public InformePatentabilidad update(InformePatentabilidad informePatentabilidad) {
    log.debug("update(InformePatentabilidad informePatentabilidad) - start");

    Assert.notNull(informePatentabilidad.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(InformePatentabilidad.class))
            .build());
    Assert.notNull(informePatentabilidad.getResultadoInformePatentabilidad(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD,
                ApplicationContextSupport.getMessage(ResultadoInformePatentabilidadService.class))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(InformePatentabilidad.class))
            .build());
    Assert.notNull(informePatentabilidad.getResultadoInformePatentabilidad().getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ResultadoInformePatentabilidadService.class))
            .build());

    return repository.findById(informePatentabilidad.getId()).map(informePatentabilidadExistente -> {

      if (!informePatentabilidadExistente.getResultadoInformePatentabilidad().getId()
          .equals(informePatentabilidad.getResultadoInformePatentabilidad().getId())) {
        // Si estamos mofificando el ResultadoInformePatentabilidad invocar validaciones
        // asociadas
        // a OnActualizarResultadoInformePatentabilidad
        Set<ConstraintViolation<InformePatentabilidad>> result = validator.validate(informePatentabilidad,
            InformePatentabilidad.OnActualizarResultadoInformePatentabilidad.class);
        if (!result.isEmpty()) {
          throw new ConstraintViolationException(result);
        }
      }

      // Establecemos los campos actualizables con los recibidos
      informePatentabilidadExistente.setInvencionId(informePatentabilidad.getInvencionId());
      informePatentabilidadExistente.setFecha(informePatentabilidad.getFecha());
      informePatentabilidadExistente.setNombre(informePatentabilidad.getNombre());
      informePatentabilidadExistente.setDocumentoRef(informePatentabilidad.getDocumentoRef());
      informePatentabilidadExistente
          .setResultadoInformePatentabilidad(informePatentabilidad.getResultadoInformePatentabilidad());
      informePatentabilidadExistente.setEntidadCreadoraRef(informePatentabilidad.getEntidadCreadoraRef());
      informePatentabilidadExistente.setContactoEntidadCreadora(informePatentabilidad.getContactoEntidadCreadora());
      informePatentabilidadExistente.setContactoExaminador(informePatentabilidad.getContactoExaminador());
      informePatentabilidadExistente.setComentarios(informePatentabilidad.getComentarios());

      // Actualizamos la entidad
      InformePatentabilidad returnValue = repository.save(informePatentabilidadExistente);
      log.debug("update(InformePatentabilidad informePatentabilidad) - end");
      return returnValue;
    }).orElseThrow(() -> new InformePatentabilidadNotFoundException(informePatentabilidad.getId()));
  }

  /**
   * Elimina el {@link InformePatentabilidad} con el id indicado.
   * 
   * @param id el id de la entidad {@link InformePatentabilidad}.
   */
  @Transactional
  public void deleteById(Long id) {
    log.debug("deleteById(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(InformePatentabilidad.class))
            .build());
    if (!repository.existsById(id)) {
      throw new InformePatentabilidadNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("deleteById(Long id) - end");
  }
}
