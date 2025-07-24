package org.crue.hercules.sgi.csp.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadFinanciadoraNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.FuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.FuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.TipoFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaEntidadFinanciadoraSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadFinanciadoraService;
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
 * Service Implementation para la gestión de
 * {@link ConvocatoriaEntidadFinanciadora}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaEntidadFinanciadoraServiceImpl implements ConvocatoriaEntidadFinanciadoraService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_ACTION = "action";
  private static final String MSG_FIELD_ACTION_CREAR = "action.crear";
  private static final String MSG_FIELD_ACTION_MODIFICAR = "action.modificar";
  private static final String MSG_FIELD_ACTION_ELIMINAR = "action.eliminar";
  private static final String MSG_FIELD_PORCENTAJE_NEGATIVO = "porcentajeFinanciacion.negativo";
  private static final String MSG_MODEL_CONVOCATORIA_ENTIDAD_FINANCIADORA = "org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora.message";
  private static final String MSG_PROBLEM_ACCION_DENEGADA_PERMISOS = "org.springframework.util.Assert.accion.denegada.permisos.message";
  private static final String MSG_MODEL_FUENTE_FINANCIACION = "org.crue.hercules.sgi.csp.model.FuenteFinanciacion.message";
  private static final String MSG_MODEL_TIPO_FINANCIACION = "org.crue.hercules.sgi.csp.model.TipoFinanciacion.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";

  private final ConvocatoriaEntidadFinanciadoraRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final FuenteFinanciacionRepository fuenteFinanciacionRepository;
  private final TipoFinanciacionRepository tipoFinanciacionRepository;
  private final ConvocatoriaService convocatoriaService;
  private final ConvocatoriaAuthorityHelper authorityHelper;

  public ConvocatoriaEntidadFinanciadoraServiceImpl(
      ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository,
      ConvocatoriaRepository convocatoriaRepository, FuenteFinanciacionRepository fuenteFinanciacionRepository,
      TipoFinanciacionRepository tipoFinanciacionRepository, ConvocatoriaService convocatoriaService,
      ConvocatoriaAuthorityHelper authorityHelper) {
    this.repository = convocatoriaEntidadFinanciadoraRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.fuenteFinanciacionRepository = fuenteFinanciacionRepository;
    this.tipoFinanciacionRepository = tipoFinanciacionRepository;
    this.convocatoriaService = convocatoriaService;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Guardar un nuevo {@link ConvocatoriaEntidadFinanciadora}.
   *
   * @param convocatoriaEntidadFinanciadora la entidad
   *                                        {@link ConvocatoriaEntidadFinanciadora}
   *                                        a guardar.
   * @return la entidad {@link ConvocatoriaEntidadFinanciadora} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEntidadFinanciadora create(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora) {
    log.debug("create(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora) - start");

    AssertHelper.idIsNull(convocatoriaEntidadFinanciadora.getId(), ConvocatoriaEntidadFinanciadora.class);
    AssertHelper.idNotNull(convocatoriaEntidadFinanciadora.getConvocatoriaId(), Convocatoria.class);

    Assert.isTrue(
        convocatoriaEntidadFinanciadora.getPorcentajeFinanciacion() == null
            || convocatoriaEntidadFinanciadora.getPorcentajeFinanciacion().floatValue() >= 0,
        ApplicationContextSupport.getMessage(MSG_FIELD_PORCENTAJE_NEGATIVO));

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaEntidadFinanciadora.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaEntidadFinanciadora.getConvocatoriaId()));

    // comprobar si convocatoria es modificable
    Assert.isTrue(
        convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaEntidadFinanciadora.getConvocatoriaId(),
            convocatoria.getUnidadGestionRef(), new String[] {
                ConvocatoriaAuthorityHelper.CSP_CON_C,
                ConvocatoriaAuthorityHelper.CSP_CON_E
            }),
        () -> ProblemMessage.builder()
            .key(MSG_PROBLEM_ACCION_DENEGADA_PERMISOS)
            .parameter(MSG_KEY_ENTITY,
                ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_ENTIDAD_FINANCIADORA))
            .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_CREAR))
            .build());

    if (convocatoriaEntidadFinanciadora.getFuenteFinanciacion() != null) {
      if (convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getId() == null) {
        convocatoriaEntidadFinanciadora.setFuenteFinanciacion(null);
      } else {
        convocatoriaEntidadFinanciadora.setFuenteFinanciacion(
            fuenteFinanciacionRepository.findById(convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getId())
                .orElseThrow(() -> new FuenteFinanciacionNotFoundException(
                    convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getId())));
        Assert.isTrue(convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_FUENTE_FINANCIACION))
                .parameter(MSG_KEY_FIELD, convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getNombre())
                .build());
      }
    }

    if (convocatoriaEntidadFinanciadora.getTipoFinanciacion() != null) {
      if (convocatoriaEntidadFinanciadora.getTipoFinanciacion().getId() == null) {
        convocatoriaEntidadFinanciadora.setTipoFinanciacion(null);
      } else {
        convocatoriaEntidadFinanciadora.setTipoFinanciacion(
            tipoFinanciacionRepository.findById(convocatoriaEntidadFinanciadora.getTipoFinanciacion().getId())
                .orElseThrow(() -> new TipoFinanciacionNotFoundException(
                    convocatoriaEntidadFinanciadora.getTipoFinanciacion().getId())));
        Assert.isTrue(convocatoriaEntidadFinanciadora.getTipoFinanciacion().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_FUENTE_FINANCIACION))
                .parameter(MSG_KEY_FIELD, convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getNombre())
                .build());
      }
    }

    ConvocatoriaEntidadFinanciadora returnValue = repository.save(convocatoriaEntidadFinanciadora);

    log.debug("create(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConvocatoriaEntidadFinanciadora}.
   *
   * @param convocatoriaEntidadFinanciadoraActualizar la entidad
   *                                                  {@link ConvocatoriaEntidadFinanciadora}
   *                                                  a actualizar.
   * @return la entidad {@link ConvocatoriaEntidadFinanciadora} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEntidadFinanciadora update(
      ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraActualizar) {
    log.debug("update(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraActualizar) - start");

    AssertHelper.idNotNull(convocatoriaEntidadFinanciadoraActualizar.getId(), ConvocatoriaEntidadFinanciadora.class);

    Assert.isTrue(
        convocatoriaEntidadFinanciadoraActualizar.getPorcentajeFinanciacion() == null
            || convocatoriaEntidadFinanciadoraActualizar.getPorcentajeFinanciacion().floatValue() >= 0,
        ApplicationContextSupport.getMessage(MSG_FIELD_PORCENTAJE_NEGATIVO));

    if (convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion() != null) {
      if (convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion().getId() == null) {
        convocatoriaEntidadFinanciadoraActualizar.setFuenteFinanciacion(null);
      } else {
        convocatoriaEntidadFinanciadoraActualizar.setFuenteFinanciacion(fuenteFinanciacionRepository
            .findById(convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion().getId())
            .orElseThrow(() -> new FuenteFinanciacionNotFoundException(
                convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion().getId())));
      }
    }

    if (convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion() != null) {
      if (convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion().getId() == null) {
        convocatoriaEntidadFinanciadoraActualizar.setTipoFinanciacion(null);
      } else {
        convocatoriaEntidadFinanciadoraActualizar.setTipoFinanciacion(
            tipoFinanciacionRepository.findById(convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion().getId())
                .orElseThrow(() -> new TipoFinanciacionNotFoundException(
                    convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion().getId())));
      }
    }

    return repository.findById(convocatoriaEntidadFinanciadoraActualizar.getId())
        .map(convocatoriaEntidadFinanciadora -> {
          if (convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion() != null) {
            Assert.isTrue(
                (convocatoriaEntidadFinanciadora.getFuenteFinanciacion() != null
                    && Objects.equals(convocatoriaEntidadFinanciadora.getFuenteFinanciacion()
                        .getId(), convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion().getId()))
                    || convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion().getActivo(),
                () -> ProblemMessage.builder()
                    .key(MSG_ENTITY_INACTIVO)
                    .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_FUENTE_FINANCIACION))
                    .parameter(MSG_KEY_FIELD, convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getNombre())
                    .build());
          }

          if (convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion() != null) {
            Assert.isTrue(
                (convocatoriaEntidadFinanciadora.getTipoFinanciacion() != null
                    && Objects.equals(convocatoriaEntidadFinanciadora.getTipoFinanciacion()
                        .getId(), convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion().getId()))
                    || convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion().getActivo(),
                () -> ProblemMessage.builder()
                    .key(MSG_ENTITY_INACTIVO)
                    .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FINANCIACION))
                    .parameter(MSG_KEY_FIELD,
                        convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion().getNombre())
                    .build());
          }

          // comprobar si convocatoria es modificable
          Assert.isTrue(
              convocatoriaService.isRegistradaConSolicitudesOProyectos(
                  convocatoriaEntidadFinanciadora.getConvocatoriaId(), null,
                  new String[] { ConvocatoriaAuthorityHelper.CSP_CON_E }),
              () -> ProblemMessage.builder()
                  .key(MSG_PROBLEM_ACCION_DENEGADA_PERMISOS)
                  .parameter(MSG_KEY_ENTITY,
                      ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_ENTIDAD_FINANCIADORA))
                  .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_MODIFICAR))
                  .build());

          convocatoriaEntidadFinanciadora
              .setFuenteFinanciacion(convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion());
          convocatoriaEntidadFinanciadora
              .setTipoFinanciacion(convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion());
          convocatoriaEntidadFinanciadora
              .setPorcentajeFinanciacion(convocatoriaEntidadFinanciadoraActualizar.getPorcentajeFinanciacion());
          convocatoriaEntidadFinanciadora
              .setImporteFinanciacion(convocatoriaEntidadFinanciadoraActualizar.getImporteFinanciacion());

          ConvocatoriaEntidadFinanciadora returnValue = repository.save(convocatoriaEntidadFinanciadora);
          log.debug("update(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraActualizar) - end");
          return returnValue;
        }).orElseThrow(() -> new ConvocatoriaEntidadFinanciadoraNotFoundException(
            convocatoriaEntidadFinanciadoraActualizar.getId()));
  }

  /**
   * Elimina el {@link ConvocatoriaEntidadFinanciadora}.
   *
   * @param id Id del {@link ConvocatoriaEntidadFinanciadora}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, ConvocatoriaEntidadFinanciadora.class);

    Optional<ConvocatoriaEntidadFinanciadora> entidadFinanciadora = repository.findById(id);
    if (entidadFinanciadora.isPresent()) {
      // comprobar si convocatoria es modificable
      Assert.isTrue(
          convocatoriaService.isRegistradaConSolicitudesOProyectos(entidadFinanciadora.get().getConvocatoriaId(),
              null, new String[] { ConvocatoriaAuthorityHelper.CSP_CON_E }),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_ACCION_DENEGADA_PERMISOS)
              .parameter(MSG_KEY_ENTITY,
                  ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_ENTIDAD_FINANCIADORA))
              .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_ELIMINAR))
              .build());
    } else {
      throw new ConvocatoriaEntidadFinanciadoraNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaEntidadFinanciadora} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaEntidadFinanciadora}.
   * @return la entidad {@link ConvocatoriaEntidadFinanciadora}.
   */
  @Override
  public ConvocatoriaEntidadFinanciadora findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaEntidadFinanciadora returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaEntidadFinanciadoraNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaEntidadFinanciadora} para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEntidadFinanciadora} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaEntidadFinanciadora> findAllByConvocatoria(Long convocatoriaId, String query,
      Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    Specification<ConvocatoriaEntidadFinanciadora> specs = ConvocatoriaEntidadFinanciadoraSpecifications
        .byConvocatoriaId(convocatoriaId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaEntidadFinanciadora> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
