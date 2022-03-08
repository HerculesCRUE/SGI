package org.crue.hercules.sgi.prc.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.ConfiguracionCampoNotFoundException;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;
import org.crue.hercules.sgi.prc.repository.ConfiguracionCampoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link ConfiguracionCampo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ConfiguracionCampoService {

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_NAME = "name";

  private final ConfiguracionCampoRepository repository;

  public ConfiguracionCampoService(
      ConfiguracionCampoRepository configuracionCampoRepository) {
    this.repository = configuracionCampoRepository;
  }

  /**
   * Guardar un nuevo {@link ConfiguracionCampo}.
   *
   * @param configuracionCampo la entidad {@link ConfiguracionCampo}
   *                           a guardar.
   * @return la entidad {@link ConfiguracionCampo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public ConfiguracionCampo create(@Valid ConfiguracionCampo configuracionCampo) {

    log.debug("create(ConfiguracionCampo configuracionCampo) - start");

    Assert.isNull(configuracionCampo.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ConfiguracionCampo.class))
            .build());

    ConfiguracionCampo returnValue = repository.save(configuracionCampo);

    log.debug("create(ConfiguracionCampo configuracionCampo) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConfiguracionCampo}.
   *
   * @param configuracionCampo la entidad {@link ConfiguracionCampo}
   *                           a actualizar.
   * @return la entidad {@link ConfiguracionCampo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public ConfiguracionCampo update(@Valid ConfiguracionCampo configuracionCampo) {
    log.debug("update(ConfiguracionCampo configuracionCampo) - start");

    Assert.notNull(configuracionCampo.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ConfiguracionCampo.class))
            .build());

    return repository.findById(configuracionCampo.getId())
        .map(configuracionCampoExistente -> {

          // Establecemos los configuracionCampos actualizables con los recibidos
          configuracionCampoExistente.setCodigoCVN(configuracionCampo.getCodigoCVN());
          configuracionCampoExistente.setTipoFormato(configuracionCampo.getTipoFormato());
          configuracionCampoExistente.setFechaReferenciaInicio(configuracionCampo.getFechaReferenciaInicio());
          configuracionCampoExistente.setFechaReferenciaFin(configuracionCampo.getFechaReferenciaFin());
          configuracionCampoExistente.setEpigrafeCVN(configuracionCampo.getEpigrafeCVN());
          configuracionCampoExistente.setValidacionAdicional(configuracionCampo.getValidacionAdicional());

          // Actualizamos la entidad
          ConfiguracionCampo returnValue = repository.save(configuracionCampoExistente);
          log.debug("update(ConfiguracionCampo configuracionCampo) - end");
          return returnValue;
        }).orElseThrow(
            () -> new ConfiguracionCampoNotFoundException(configuracionCampo.getId()));
  }

  /**
   * Obtener todas las entidades {@link ConfiguracionCampo} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ConfiguracionCampo} paginadas y/o
   *         filtradas.
   */
  public Page<ConfiguracionCampo> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<ConfiguracionCampo> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<ConfiguracionCampo> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link ConfiguracionCampo} por su id.
   *
   * @param id el id de la entidad {@link ConfiguracionCampo}.
   * @return la entidad {@link ConfiguracionCampo}.
   */
  public ConfiguracionCampo findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConfiguracionCampo returnValue = repository.findById(id)
        .orElseThrow(() -> new ConfiguracionCampoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Elimina la {@link ConfiguracionCampo}.
   *
   * @param id Id del {@link ConfiguracionCampo}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ConfiguracionCampo.class))
            .build());
    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }
}