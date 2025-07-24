package org.crue.hercules.sgi.csp.service.impl;

import java.util.Objects;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.exceptions.AreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.AreaTematicaDescripcion;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.repository.AreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.predicate.AreaTematicaPredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.AreaTematicaSpecifications;
import org.crue.hercules.sgi.csp.service.AreaTematicaService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link AreaTematica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class AreaTematicaServiceImpl implements AreaTematicaService {

  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_LENGTH = "length";
  private static final String MSG_FIELD_ABREVIATURA = "abbreviation";
  private static final String MSG_FIELD_DESCRIPCION = "descripcion";
  private static final String MSG_FIELD_PADRE = "padre";
  private static final String MSG_MODEL_AREA_TEMATICA = "org.crue.hercules.sgi.csp.model.AreaTematica.message";
  private static final String MSG_MODEL_AREA_TEMATICA_PADRE = "org.crue.hercules.sgi.csp.model.AreaTematica.padre.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";
  private static final String MSG_PROBLEM_MAX_LENGTH = "org.springframework.util.Assert.maxLength.message";

  private final AreaTematicaRepository repository;

  public AreaTematicaServiceImpl(AreaTematicaRepository areaTematicaRepository) {
    this.repository = areaTematicaRepository;
  }

  /**
   * Guardar un nuevo {@link AreaTematica}.
   *
   * @param areaTematica la entidad {@link AreaTematica} a guardar.
   * @return la entidad {@link AreaTematica} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public AreaTematica create(@Valid AreaTematica areaTematica) {
    log.debug("create(AreaTematica areaTematica) - start");

    AssertHelper.idIsNull(areaTematica.getId(), AreaTematica.class);

    if (areaTematica.getPadre() != null) {
      if (areaTematica.getPadre().getId() == null) {
        areaTematica.setPadre(null);
      } else {
        areaTematica.setPadre(repository.findById(areaTematica.getPadre().getId())
            .orElseThrow(() -> new AreaTematicaNotFoundException(areaTematica.getPadre().getId())));
      }
    }

    if (areaTematica.getPadre() != null) {
      // nombre(back) ==> abreviatura(front)
      // descripcion(back) ==> nombre(front)
      Assert.isTrue(areaTematica.getPadre().getActivo(),
          () -> ProblemMessage.builder()
              .key(MSG_ENTITY_INACTIVO)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_AREA_TEMATICA_PADRE))
              .parameter(MSG_KEY_FIELD, areaTematica.getPadre().getNombre())
              .build());

      Assert.isTrue(
          areaTematica.getNombre().stream().allMatch(areaTematicaNombre -> areaTematicaNombre.getValue().length() <= 5),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_MAX_LENGTH)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_AREA_TEMATICA))
              .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(MSG_FIELD_ABREVIATURA))
              .parameter(MSG_KEY_LENGTH, "5")
              .build());

      AssertHelper.fieldNotNull(areaTematica.getDescripcion(), AreaTematica.class, MSG_FIELD_DESCRIPCION);

      for (AreaTematicaDescripcion areaTematicaDescripcion : areaTematica.getDescripcion()) {
        AssertHelper.fieldNotNull(areaTematicaDescripcion.getValue(), AreaTematica.class, MSG_FIELD_DESCRIPCION);
      }

      Assert.isTrue(
          areaTematica.getDescripcion().stream()
              .allMatch(areaTematicaDescripcion -> areaTematicaDescripcion.getValue().length() <= 50),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_MAX_LENGTH)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_AREA_TEMATICA))
              .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(MSG_FIELD_DESCRIPCION))
              .parameter(MSG_KEY_LENGTH, "50")
              .build());

    }

    areaTematica.setActivo(true);

    AreaTematica returnValue = repository.save(areaTematica);

    log.debug("create(AreaTematica areaTematica) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link AreaTematica}.
   *
   * @param areaTematicaActualizar la entidad {@link AreaTematica} a actualizar.
   * @return la entidad {@link AreaTematica} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public AreaTematica update(@Valid AreaTematica areaTematicaActualizar) {
    log.debug("update(AreaTematica areaTematicaActualizar) - start");

    AssertHelper.idNotNull(areaTematicaActualizar.getId(), AreaTematica.class);

    if (areaTematicaActualizar.getPadre() != null) {
      if (areaTematicaActualizar.getPadre().getId() == null) {
        areaTematicaActualizar.setPadre(null);
      } else {
        areaTematicaActualizar.setPadre(repository.findById(areaTematicaActualizar.getPadre().getId())
            .orElseThrow(() -> new AreaTematicaNotFoundException(areaTematicaActualizar.getPadre().getId())));
      }
    }

    return repository.findById(areaTematicaActualizar.getId()).map(areaTematica -> {
      if (areaTematica.getPadre() != null) {
        // nombre(back) ==> abreviatura(front)
        // descripcion(back) ==> nombre(front)
        if (!Objects.equals(areaTematica.getPadre().getId(), areaTematicaActualizar.getPadre().getId())) {
          Assert.isTrue(areaTematicaActualizar.getPadre().getActivo(),
              () -> ProblemMessage.builder()
                  .key(MSG_ENTITY_INACTIVO)
                  .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_AREA_TEMATICA_PADRE))
                  .parameter(MSG_KEY_FIELD, areaTematica.getPadre().getNombre())
                  .build());
        }

        Assert.isTrue(
            areaTematica.getNombre().stream()
                .allMatch(areaTematicaNombre -> areaTematicaNombre.getValue().length() <= 5),
            () -> ProblemMessage.builder()
                .key(MSG_PROBLEM_MAX_LENGTH)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_AREA_TEMATICA))
                .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(MSG_FIELD_ABREVIATURA))
                .parameter(MSG_KEY_LENGTH, "5")
                .build());

        for (AreaTematicaDescripcion areaTematicaDescripcion : areaTematicaActualizar.getDescripcion()) {

          AssertHelper.fieldNotBlank(StringUtils.isNotBlank(areaTematicaDescripcion.getValue()),
              AreaTematica.class,
              MSG_FIELD_DESCRIPCION);
        }

        Assert.isTrue(
            areaTematicaActualizar.getDescripcion().stream()
                .allMatch(areaTematicaDescripcion -> areaTematicaDescripcion.getValue().length() <= 50),
            () -> ProblemMessage.builder()
                .key(MSG_PROBLEM_MAX_LENGTH)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_AREA_TEMATICA))
                .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(MSG_FIELD_DESCRIPCION))
                .parameter(MSG_KEY_LENGTH, "50")
                .build());

      }

      areaTematica.setNombre(areaTematicaActualizar.getNombre());
      areaTematica.setDescripcion(areaTematicaActualizar.getDescripcion());
      areaTematica.setPadre(areaTematicaActualizar.getPadre());

      AreaTematica returnValue = repository.save(areaTematica);
      log.debug("update(AreaTematica areaTematicaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new AreaTematicaNotFoundException(areaTematicaActualizar.getId()));
  }

  /**
   * Reactiva el {@link AreaTematica}.
   *
   * @param id Id del {@link AreaTematica}.
   * @return la entidad {@link AreaTematica} persistida.
   */
  @Override
  @Transactional
  public AreaTematica enable(Long id) {
    log.debug("enable(Long id) - start");

    AssertHelper.idNotNull(id, AreaTematica.class);

    return repository.findById(id).map(areaTematica -> {
      if (Boolean.TRUE.equals(areaTematica.getActivo())) {
        // Si esta activo no se hace nada
        return areaTematica;
      }

      AssertHelper.fieldIsNull(areaTematica.getPadre(), AreaTematica.class, MSG_FIELD_PADRE);

      areaTematica.setActivo(true);

      AreaTematica returnValue = repository.save(areaTematica);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new AreaTematicaNotFoundException(id));
  }

  /**
   * Desactiva el {@link AreaTematica}
   *
   * @param id Id del {@link AreaTematica}.
   * @return la entidad {@link AreaTematica} persistida.
   */
  @Override
  @Transactional
  public AreaTematica disable(Long id) {
    log.debug("disable(Long id) - start");

    AssertHelper.idNotNull(id, AreaTematica.class);

    return repository.findById(id).map(areaTematica -> {
      if (Boolean.FALSE.equals(areaTematica.getActivo())) {
        // Si no esta activo no se hace nada
        return areaTematica;
      }

      areaTematica.setActivo(false);
      AreaTematica returnValue = repository.save(areaTematica);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new AreaTematicaNotFoundException(id));
  }

  /**
   * Obtiene {@link AreaTematica} por su id.
   *
   * @param id el id de la entidad {@link AreaTematica}.
   * @return la entidad {@link AreaTematica}.
   */
  @Override
  public AreaTematica findById(Long id) {
    log.debug("findById(Long id)  - start");
    final AreaTematica returnValue = repository.findById(id).orElseThrow(() -> new AreaTematicaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link AreaTematica} activos.
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas.
   */
  @Override
  public Page<AreaTematica> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<AreaTematica> specs = AreaTematicaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query, AreaTematicaPredicateResolver.getInstance()));

    Page<AreaTematica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los grupos activos (los {@link AreaTematica} con padre null).
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas.
   */
  @Override
  public Page<AreaTematica> findAllGrupo(String query, Pageable pageable) {
    log.debug("findAllGrupo(String query, Pageable pageable) - start");
    Specification<AreaTematica> specs = AreaTematicaSpecifications.grupos().and(AreaTematicaSpecifications.activos())
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<AreaTematica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllGrupo(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los grupos (los {@link AreaTematica} con padre null).
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas.
   */
  @Override
  public Page<AreaTematica> findAllTodosGrupo(String query, Pageable pageable) {
    log.debug("findAllTodosGrupo(String query, Pageable pageable) - start");
    Specification<AreaTematica> specs = AreaTematicaSpecifications.grupos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<AreaTematica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllTodosGrupo(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link AreaTematica} hijos directos del {@link AreaTematica} con
   * el id indicado.
   *
   * @param areaTematicaId el id de la entidad {@link AreaTematica}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas.
   */
  @Override
  public Page<AreaTematica> findAllHijosAreaTematica(Long areaTematicaId, String query, Pageable pageable) {
    log.debug("findAllHijosAreaTematica(Long areaTematicaId, String query, Pageable pageable) - start");
    Specification<AreaTematica> specs = AreaTematicaSpecifications.hijos(areaTematicaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<AreaTematica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllHijosAreaTematica(Long areaTematicaId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
