package org.crue.hercules.sgi.csp.service.impl;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.AreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaAreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.repository.AreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaAreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaAreaTematicaSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ConvocatoriaAreaTematica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaAreaTematicaServiceImpl implements ConvocatoriaAreaTematicaService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_ACTION = "action";
  private static final String MSG_FIELD_ACTION_CREAR = "action.crear";
  private static final String MSG_FIELD_ACTION_ELIMINAR = "action.eliminar";
  private static final String MSG_MODEL_CONVOCATORIA_AREA_TEMATICA = "org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica.message";
  private static final String MSG_PROBLEM_ACCION_DENEGADA_PERMISOS = "org.springframework.util.Assert.accion.denegada.permisos.message";

  private final ConvocatoriaAreaTematicaRepository repository;
  private final AreaTematicaRepository areaTematicaRepository;
  private final ConvocatoriaService convocatoriaService;
  private final ConvocatoriaAuthorityHelper authorityHelper;

  public ConvocatoriaAreaTematicaServiceImpl(ConvocatoriaAreaTematicaRepository repository,
      AreaTematicaRepository areaTematicaRepository, ConvocatoriaService convocatoriaService,
      ConvocatoriaAuthorityHelper authorityHelper) {
    this.repository = repository;
    this.areaTematicaRepository = areaTematicaRepository;
    this.convocatoriaService = convocatoriaService;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Guarda la entidad {@link ConvocatoriaAreaTematica}.
   * 
   * @param convocatoriaAreaTematica la entidad {@link ConvocatoriaAreaTematica} a
   *                                 guardar.
   * @return ConvocatoriaAreaTematica la entidad {@link ConvocatoriaAreaTematica}
   *         persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaAreaTematica create(ConvocatoriaAreaTematica convocatoriaAreaTematica) {
    log.debug("create(ConvocatoriaAreaTematica convocatoriaAreaTematica) - start");

    AssertHelper.idIsNull(convocatoriaAreaTematica.getId(), ConvocatoriaAreaTematica.class);
    AssertHelper.idNotNull(convocatoriaAreaTematica.getConvocatoriaId(), Convocatoria.class);
    AssertHelper.idNotNull(convocatoriaAreaTematica.getAreaTematica().getId(), AreaTematica.class);

    // comprobar si convocatoria es modificable
    Assert.isTrue(
        convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaAreaTematica.getConvocatoriaId(), null,
            new String[] {
                ConvocatoriaAuthorityHelper.CSP_CON_C,
                ConvocatoriaAuthorityHelper.CSP_CON_E
            }),
        () -> ProblemMessage.builder()
            .key(MSG_PROBLEM_ACCION_DENEGADA_PERMISOS)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_AREA_TEMATICA))
            .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_CREAR))
            .build());

    convocatoriaAreaTematica
        .setAreaTematica(areaTematicaRepository.findById(convocatoriaAreaTematica.getAreaTematica().getId())
            .orElseThrow(() -> new AreaTematicaNotFoundException(convocatoriaAreaTematica.getAreaTematica().getId())));

    AssertHelper.entityExists(
        !repository.findByConvocatoriaIdAndAreaTematicaId(convocatoriaAreaTematica.getConvocatoriaId(),
            convocatoriaAreaTematica.getAreaTematica().getId()).isPresent(),
        Convocatoria.class, AreaTematica.class);

    ConvocatoriaAreaTematica returnValue = repository.save(convocatoriaAreaTematica);

    log.debug("create(ConvocatoriaAreaTematica convocatoriaAreaTematica) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConvocatoriaAreaTematica}.
   *
   * @param convocatoriaAreaTematicaActualizar la entidad
   *                                           {@link ConvocatoriaAreaTematica} a
   *                                           actualizar.
   * @return la entidad {@link ConvocatoriaAreaTematica} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaAreaTematica update(ConvocatoriaAreaTematica convocatoriaAreaTematicaActualizar) {
    log.debug("update(ConvocatoriaAreaTematica convocatoriaAreaTematicaActualizar) - start");

    AssertHelper.idNotNull(convocatoriaAreaTematicaActualizar.getId(), ConvocatoriaAreaTematica.class);

    return repository.findById(convocatoriaAreaTematicaActualizar.getId()).map(convocatoriaAreaTematica -> {

      convocatoriaAreaTematica.setObservaciones(convocatoriaAreaTematicaActualizar.getObservaciones());
      ConvocatoriaAreaTematica returnValue = repository.save(convocatoriaAreaTematicaActualizar);
      log.debug("update(ConvocatoriaAreaTematica convocatoriaAreaTematicaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaAreaTematicaNotFoundException(convocatoriaAreaTematicaActualizar.getId()));
  }

  /**
   * Elimina la {@link ConvocatoriaAreaTematica}.
   *
   * @param id Id del {@link ConvocatoriaAreaTematica}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, ConvocatoriaAreaTematica.class);

    Optional<ConvocatoriaAreaTematica> areaTematica = repository.findById(id);
    if (areaTematica.isPresent()) {
      // comprobar si convocatoria es modificable
      Assert.isTrue(
          convocatoriaService.isRegistradaConSolicitudesOProyectos(areaTematica.get().getConvocatoriaId(), null,
              new String[] { ConvocatoriaAuthorityHelper.CSP_CON_E }),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_ACCION_DENEGADA_PERMISOS)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_AREA_TEMATICA))
              .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_ELIMINAR))
              .build());
    } else {
      throw new ConvocatoriaAreaTematicaNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaAreaTematica} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaAreaTematica}.
   * @return la entidad {@link ConvocatoriaAreaTematica}.
   */
  @Override
  public ConvocatoriaAreaTematica findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaAreaTematica returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaAreaTematicaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene las {@link ConvocatoriaAreaTematica} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaAreaTematica} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaAreaTematica> findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");
    Specification<ConvocatoriaAreaTematica> specs = ConvocatoriaAreaTematicaSpecifications
        .byConvocatoriaId(convocatoriaId).and(SgiRSQLJPASupport.toSpecification(query));

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    Page<ConvocatoriaAreaTematica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
