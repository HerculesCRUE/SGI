package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoCodigoEcNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaConceptoGastoCodigoEcSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoCodigoEcService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para {@link ConvocatoriaConceptoGastoCodigoEc}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaConceptoGastoCodigoEcServiceImpl implements ConvocatoriaConceptoGastoCodigoEcService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_ACTION = "action";
  private static final String MSG_KEY_OTHER_ENTITY = "otherEntity";
  private static final String MSG_FIELD_ACTION_MODIFICAR = "action.modificar";
  private static final String MSG_FIELD_CODIGO_ECONOMICO = "codigoEconomico";
  private static final String MSG_MODEL_CONVOCATORIA_CONCEPTO_GASTO = "org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto.message";
  private static final String MSG_MODEL_CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC = "org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc.message";
  private static final String MSG_PROBLEM_ACCION_DENEGADA = "org.springframework.util.Assert.accion.denegada.message";
  private static final String MSG_PROBLEM_DATE_OVERLOAP = "org.springframework.util.Assert.date.overloap.message";
  private static final String MSG_PROBLEM_DATE_OVERLOAP_OTHER_ENTITY = "org.springframework.util.Assert.date.overloap.other.entity.message";

  private final ConvocatoriaConceptoGastoCodigoEcRepository repository;
  private final ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository;
  private final ConvocatoriaAuthorityHelper authorityHelper;

  public ConvocatoriaConceptoGastoCodigoEcServiceImpl(ConvocatoriaConceptoGastoCodigoEcRepository repository,
      ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository,
      ConvocatoriaAuthorityHelper authorityHelper) {
    this.repository = repository;
    this.convocatoriaConceptoGastoRepository = convocatoriaConceptoGastoRepository;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Obtiene las {@link ConvocatoriaConceptoGastoCodigoEc} permitidos para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGastoCodigoEc} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaConceptoGastoCodigoEc> findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId,
      Pageable pageable) {
    log.debug(
        "findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Boolean permitido, Pageable pageable)) - start");

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoria = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoria(convocatoriaId);
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConceptoGastoActivo = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoriaConceptoGastoPermitido = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoriaConceptoGastoPermitido(true);

    Specification<ConvocatoriaConceptoGastoCodigoEc> specs = Specification.where(specByConvocatoria)
        .and(specByConceptoGastoActivo).and(specByConvocatoriaConceptoGastoPermitido);
    Page<ConvocatoriaConceptoGastoCodigoEc> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Boolean permitido, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaConceptoGastoCodigoEc} NO permitidos para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGastoCodigoEc} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaConceptoGastoCodigoEc> findAllByConvocatoriaAndPermitidoFalse(Long convocatoriaId,
      Pageable pageable) {
    log.debug(
        "findAllByConvocatoriaAndPermitidoFalse(Long convocatoriaId, Boolean permitido, Pageable pageable)) - start");

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoria = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoria(convocatoriaId);
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConceptoGastoActivo = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoriaConceptoGastoPermitido = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoriaConceptoGastoPermitido(false);

    Specification<ConvocatoriaConceptoGastoCodigoEc> specs = Specification.where(specByConvocatoria)
        .and(specByConceptoGastoActivo).and(specByConvocatoriaConceptoGastoPermitido);
    Page<ConvocatoriaConceptoGastoCodigoEc> returnValue = repository.findAll(specs, pageable);
    log.debug(
        "findAllByConvocatoriaAndPermitidoFalse(Long convocatoriaId, Boolean permitido, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaConceptoGastoCodigoEc} NO permitidos para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaConceptoGastoId el id de la {@link Convocatoria}.
   * @param pageable                    la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGastoCodigoEc} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaConceptoGastoCodigoEc> findAllByConvocatoriaConceptoGasto(Long convocatoriaConceptoGastoId,
      Pageable pageable) {
    log.debug(
        "findAllByConvocatoriaAndPermitidoFalse(Long convocatoriaId, Boolean permitido, Pageable pageable)) - start");
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoriaConceptoGasto = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoriaConceptoGasto(convocatoriaConceptoGastoId);
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConceptoGastoActivo = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();

    Specification<ConvocatoriaConceptoGastoCodigoEc> specs = Specification.where(specByConvocatoriaConceptoGasto)
        .and(specByConceptoGastoActivo);

    return repository.findAll(specs, pageable);
  }

  /**
   * Se valida la unicidad del código económico. Para una
   * {@link ConvocatoriaConceptoGasto} el mismo código económico solo puede
   * aparecer una vez, salvo que lo haga en periodos de vigencia no solapados.
   * 
   * @param convocatoriaConceptoGastoId id {@link ConvocatoriaConceptoGasto}
   * @param conceptoGastoPermitido      permitido o no
   * 
   * @return true validación correcta/ false validacion incorrecta
   */
  private boolean existsConvocatoriaConceptoGastoCodigoEcConFechasSolapadas(
      ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc, Boolean conceptoGastoPermitido) {
    log.debug(
        "existsConvocatoriaConceptoGastoCodigoEcConFechasSolapadas(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc)");

    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoriaConceptoGasto = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoriaConceptoGasto(convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGastoId());
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConceptoGastoCodigoEcActivo = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByCodigoEconomicoRef = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byCodigoEconomicoRef(convocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef());
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByRangoFechaSolapados = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byRangoFechaSolapados(convocatoriaConceptoGastoCodigoEc.getFechaInicio(),
            convocatoriaConceptoGastoCodigoEc.getFechaFin());
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByIdNotEqual = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byIdNotEqual(convocatoriaConceptoGastoCodigoEc.getId());
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoriaConceptoGastoPermitido = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoriaConceptoGastoPermitido(conceptoGastoPermitido);

    Specification<ConvocatoriaConceptoGastoCodigoEc> specs = Specification.where(specByConvocatoriaConceptoGasto)
        .and(specByRangoFechaSolapados).and(specByConceptoGastoCodigoEcActivo).and(specByCodigoEconomicoRef)
        .and(specByIdNotEqual).and(specByConvocatoriaConceptoGastoPermitido);

    Page<ConvocatoriaConceptoGastoCodigoEc> convocatoriaConceptoGastoCodigoEcs = repository.findAll(specs,
        Pageable.unpaged());

    Boolean returnValue = !convocatoriaConceptoGastoCodigoEcs.isEmpty();
    log.debug(
        "existsConvocatoriaConceptoGastoCodigoEcConFechasSolapadas(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc) - end");

    return returnValue;
  }

  /**
   * Se valida la unicidad del código económico y concepto gasto, salvo que lo
   * haga en periodos de vigencia no solapados
   * (independientemente del valor del campo "permitido").
   * 
   * @param convocatoriaConceptoGastoCodigoEc {@link ConvocatoriaConceptoGastoCodigoEc}
   * @param mesInicioConceptoGasto            mes inicial del
   *                                          {@link ConvocatoriaConceptoGasto}
   * @param mesFinConceptoGasto               mes final del
   *                                          {@link ConvocatoriaConceptoGasto}
   * @param convocatoriaId                    Identifiacdor de la
   *                                          {@link Convocatoria}
   * @return true validación correcta/ false validacion incorrecta
   */
  private boolean existsConvocatoriaConceptoGastoCodigoEcAndConceptoGastoConFechasSolapadas(
      ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc,
      Integer mesInicioConceptoGasto, Integer mesFinConceptoGasto, Long convocatoriaId) {

    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoria = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoria(convocatoriaId);
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConceptoGastoCodigoEcActivo = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();

    Specification<ConvocatoriaConceptoGastoCodigoEc> specByCodigoEconomicoRef = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byCodigoEconomicoRef(convocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef());

    Specification<ConvocatoriaConceptoGastoCodigoEc> specByRangoMesesSolapados = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byRangoMesesConceptoGastoSolapados(mesInicioConceptoGasto, mesFinConceptoGasto);
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByIdNotEqual = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byIdNotEqual(convocatoriaConceptoGastoCodigoEc.getId());
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConceptoGasto = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConceptoGasto(convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGastoId());

    Specification<ConvocatoriaConceptoGastoCodigoEc> specs = Specification.where(specByConvocatoria)
        .and(specByRangoMesesSolapados).and(specByConceptoGastoCodigoEcActivo).and(specByCodigoEconomicoRef)
        .and(specByIdNotEqual).and(specByConceptoGasto);

    return repository.count(specs) > 0;
  }

  /**
   * Actualiza el listado de {@link ConvocatoriaConceptoGastoCodigoEc} de la
   * {@link ConvocatoriaConceptoGasto} con el listado
   * convocatoriaConceptoGastoCodigoEcs añadiendo, editando o eliminando los
   * elementos segun proceda.
   *
   * @param convocatoriaConceptoGastoId        Id de la
   *                                           {@link ConvocatoriaConceptoGasto}.
   * @param convocatoriaConceptoGastoCodigoEcs lista con los nuevos
   *                                           {@link ConvocatoriaConceptoGastoCodigoEc}
   *                                           a guardar.
   * @return la entidad {@link ConvocatoriaConceptoGastoCodigoEc} persistida.
   */
  @Override
  @Transactional
  public List<ConvocatoriaConceptoGastoCodigoEc> update(Long convocatoriaConceptoGastoId,
      List<ConvocatoriaConceptoGastoCodigoEc> convocatoriaConceptoGastoCodigoEcs) {
    log.debug(
        "update(Long convocatoriaConceptoGastoId, List<ConvocatoriaConceptoGastoCodigoEc> convocatoriaConceptoGastoCodigoEcs) - start");

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = convocatoriaConceptoGastoRepository
        .findById(convocatoriaConceptoGastoId)
        .orElseThrow(() -> new ConvocatoriaConceptoGastoNotFoundException(convocatoriaConceptoGastoId));

    List<ConvocatoriaConceptoGastoCodigoEc> convocatoriaConceptoGastoCodigoEcsBD = repository
        .findAllByConvocatoriaConceptoGastoId(convocatoriaConceptoGastoId);

    // Periodos eliminados
    List<ConvocatoriaConceptoGastoCodigoEc> convocatoriaConceptoGastoCodigoEcsEliminar = convocatoriaConceptoGastoCodigoEcsBD
        .stream().filter(periodo -> convocatoriaConceptoGastoCodigoEcs.stream()
            .map(ConvocatoriaConceptoGastoCodigoEc::getId).noneMatch(id -> Objects.equals(id, periodo.getId())))
        .collect(Collectors.toList());

    if (!convocatoriaConceptoGastoCodigoEcsEliminar.isEmpty()) {
      repository.deleteAll(convocatoriaConceptoGastoCodigoEcsEliminar);
    }

    // Ordena los códigos económico spor fecha de inicio
    convocatoriaConceptoGastoCodigoEcs.sort(Comparator.comparing(ConvocatoriaConceptoGastoCodigoEc::getFechaInicio,
        Comparator.nullsLast(Comparator.naturalOrder())));

    // Validaciones
    List<ConvocatoriaConceptoGastoCodigoEc> returnValue = new ArrayList<>();
    for (ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc : convocatoriaConceptoGastoCodigoEcs) {

      // actualizando
      if (convocatoriaConceptoGastoCodigoEc.getId() != null) {
        ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcBD = convocatoriaConceptoGastoCodigoEcsBD
            .stream().filter(periodo -> Objects.equals(periodo.getId(), convocatoriaConceptoGastoCodigoEc.getId()))
            .findFirst()
            .orElseThrow(() -> new ConvocatoriaConceptoGastoCodigoEcNotFoundException(
                convocatoriaConceptoGastoCodigoEc.getId()));

        Assert.isTrue(
            Objects.equals(convocatoriaConceptoGastoCodigoEcBD.getConvocatoriaConceptoGastoId(),
                convocatoriaConceptoGastoCodigoEc
                    .getConvocatoriaConceptoGastoId()),
            () -> ProblemMessage.builder()
                .key(MSG_PROBLEM_ACCION_DENEGADA)
                .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(
                    MSG_MODEL_CONVOCATORIA_CONCEPTO_GASTO))
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                    MSG_MODEL_CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC))
                .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_MODIFICAR))
                .build());
      }

      if (convocatoriaConceptoGastoCodigoEc.getFechaInicio() != null
          && convocatoriaConceptoGastoCodigoEc.getFechaFin() != null) {
        AssertHelper.isBefore(
            convocatoriaConceptoGastoCodigoEc.getFechaInicio()
                .isBefore(convocatoriaConceptoGastoCodigoEc.getFechaFin()));

      }

      // Unicidad código económico y solapamiento de fechas
      Assert.isTrue(
          !existsConvocatoriaConceptoGastoCodigoEcConFechasSolapadas(convocatoriaConceptoGastoCodigoEc,
              convocatoriaConceptoGasto.getPermitido()),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_DATE_OVERLOAP)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                  MSG_FIELD_CODIGO_ECONOMICO))
              .parameter(MSG_KEY_FIELD, convocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef())
              .build());

      Assert.isTrue(
          !existsConvocatoriaConceptoGastoCodigoEcAndConceptoGastoConFechasSolapadas(convocatoriaConceptoGastoCodigoEc,
              convocatoriaConceptoGasto.getMesInicial(), convocatoriaConceptoGasto.getMesFinal(),
              convocatoriaConceptoGasto.getConvocatoriaId()),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_DATE_OVERLOAP_OTHER_ENTITY)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                  MSG_FIELD_CODIGO_ECONOMICO))
              .parameter(MSG_KEY_FIELD, convocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef())
              .parameter(MSG_KEY_OTHER_ENTITY, ApplicationContextSupport.getMessage(
                  MSG_MODEL_CONVOCATORIA_CONCEPTO_GASTO))
              .build());

      returnValue.add(repository.save(convocatoriaConceptoGastoCodigoEc));
    }

    log.debug(
        "updateConvocatoriaConceptoGastoCodigoEcsConvocatoria(Long convocatoriaConceptoGastoId, List<ConvocatoriaConceptoGastoCodigoEc> convocatoriaConceptoGastoCodigoEcs) - end");

    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link ConvocatoriaConceptoGastoCodigoEc} por id
   * de {@link ConvocatoriaConceptoGasto}
   *
   * @param id el id de la entidad {@link ConvocatoriaConceptoGasto}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsByConvocatoriaConceptoGasto(final Long id) {
    log.debug("existsByConvocatoriaConceptoGasto(final Long id)  - start", id);
    final boolean existe = repository.existsByConvocatoriaConceptoGastoId(id);
    log.debug("existsByConvocatoriaConceptoGasto(final Long id)  - end", id);
    return existe;
  }

}
