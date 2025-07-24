package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoConceptoGastoCodigoEcNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.repository.ProyectoConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.predicate.ProyectoConceptoGastoCodigoEcPredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoConceptoGastoCodigoEcSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoCodigoEcService;
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

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para {@link ProyectoConceptoGastoCodigoEc}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoConceptoGastoCodigoEcServiceImpl implements ProyectoConceptoGastoCodigoEcService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_OTHER_ENTITY = "otherEntity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_ACTION = "action";
  private static final String MSG_FIELD_ACTION_MODIFICAR = "action.modificar";
  private static final String MSG_PROBLEM_ACCION_DENEGADA = "org.springframework.util.Assert.accion.denegada.message";
  private static final String MSG_MODEL_CODIGO_ECONOMICO = "codigoEconomico";
  private static final String MSG_MODEL_PROYECTO_CONCEPTO_GASTO = "org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto.message";
  private static final String MSG_MODEL_PROYECTO_CONCEPTO_GASTO_CODIGO_EC = "org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc.message";
  private static final String MSG_PROBLEM_DATE_OVERLOAP = "org.springframework.util.Assert.date.overloap.message";
  private static final String MSG_PROBLEM_DATE_OVERLOAP_OTHER_ENTITY = "org.springframework.util.Assert.date.overloap.other.entity.message";

  private final ProyectoConceptoGastoCodigoEcRepository repository;
  private final ProyectoConceptoGastoRepository proyectoConceptoGastoRepository;

  public ProyectoConceptoGastoCodigoEcServiceImpl(ProyectoConceptoGastoCodigoEcRepository repository,
      ProyectoConceptoGastoRepository proyectoConceptoGastoRepository) {
    this.repository = repository;
    this.proyectoConceptoGastoRepository = proyectoConceptoGastoRepository;
  }

  /**
   * Obtener todas las entidades {@link ProyectoConceptoGastoCodigoEc} activos
   * paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ProyectoConceptoGastoCodigoEc} paginadas
   *         y/o filtradas.
   */
  @Override
  public Page<ProyectoConceptoGastoCodigoEc> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<ProyectoConceptoGastoCodigoEc> specs = SgiRSQLJPASupport.toSpecification(query,
        ProyectoConceptoGastoCodigoEcPredicateResolver.getInstance());
    Page<ProyectoConceptoGastoCodigoEc> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ProyectoConceptoGastoCodigoEc} permitidos para un
   * {@link Proyecto}.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoConceptoGastoCodigoEc} del
   *         {@link Proyecto} paginadas.
   */
  @Override
  public Page<ProyectoConceptoGastoCodigoEc> findAllByProyectoAndPermitidoTrue(Long proyectoId, Pageable pageable) {
    log.debug("findAllByProyectoAndPermitidoTrue(Long proyectoId, Boolean permitido, Pageable pageable)) - start");
    Specification<ProyectoConceptoGastoCodigoEc> specByProyecto = ProyectoConceptoGastoCodigoEcSpecifications
        .byProyecto(proyectoId);
    Specification<ProyectoConceptoGastoCodigoEc> specByConceptoGastoActivo = ProyectoConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();
    Specification<ProyectoConceptoGastoCodigoEc> specByProyectoConceptoGastoPermitido = ProyectoConceptoGastoCodigoEcSpecifications
        .byProyectoConceptoGastoPermitido(true);

    Specification<ProyectoConceptoGastoCodigoEc> specs = Specification.where(specByProyecto)
        .and(specByConceptoGastoActivo).and(specByProyectoConceptoGastoPermitido);
    Page<ProyectoConceptoGastoCodigoEc> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyectoAndPermitidoTrue(Long proyectoId, Boolean permitido, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ProyectoConceptoGastoCodigoEc} NO permitidos para un
   * {@link Proyecto}.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoConceptoGastoCodigoEc} del
   *         {@link Proyecto} paginadas.
   */
  @Override
  public Page<ProyectoConceptoGastoCodigoEc> findAllByProyectoAndPermitidoFalse(Long proyectoId, Pageable pageable) {
    log.debug("findAllByProyectoAndPermitidoFalse(Long proyectoId, Boolean permitido, Pageable pageable)) - start");
    Specification<ProyectoConceptoGastoCodigoEc> specByProyecto = ProyectoConceptoGastoCodigoEcSpecifications
        .byProyecto(proyectoId);
    Specification<ProyectoConceptoGastoCodigoEc> specByConceptoGastoActivo = ProyectoConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();
    Specification<ProyectoConceptoGastoCodigoEc> specByProyectoConceptoGastoPermitido = ProyectoConceptoGastoCodigoEcSpecifications
        .byProyectoConceptoGastoPermitido(false);

    Specification<ProyectoConceptoGastoCodigoEc> specs = Specification.where(specByProyecto)
        .and(specByConceptoGastoActivo).and(specByProyectoConceptoGastoPermitido);
    Page<ProyectoConceptoGastoCodigoEc> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyectoAndPermitidoFalse(Long proyectoId, Boolean permitido, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ProyectoConceptoGastoCodigoEc} NO permitidos para un
   * {@link Proyecto}.
   *
   * @param proyectoConceptoGastoId el id del {@link Proyecto}.
   * @param pageable                la información de la paginación.
   * @return la lista de entidades {@link ProyectoConceptoGastoCodigoEc} del
   *         {@link Proyecto} paginadas.
   */
  @Override
  public Page<ProyectoConceptoGastoCodigoEc> findAllByProyectoConceptoGasto(Long proyectoConceptoGastoId,
      Pageable pageable) {
    log.debug("findAllByProyectoAndPermitidoFalse(Long proyectoId, Boolean permitido, Pageable pageable)) - start");
    Specification<ProyectoConceptoGastoCodigoEc> specByProyectoConceptoGasto = ProyectoConceptoGastoCodigoEcSpecifications
        .byProyectoConceptoGasto(proyectoConceptoGastoId);
    Specification<ProyectoConceptoGastoCodigoEc> specByConceptoGastoActivo = ProyectoConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();

    Specification<ProyectoConceptoGastoCodigoEc> specs = Specification.where(specByProyectoConceptoGasto)
        .and(specByConceptoGastoActivo);
    return repository.findAll(specs, pageable);
  }

  /**
   * Obtiene los {@link ProyectoConceptoGastoCodigoEc} para un
   * {@link ProyectoConceptoGasto}
   *
   * @param proyectoConceptoGastoId el id del {@link ProyectoConceptoGasto}.
   * @return la lista de entidades {@link ProyectoConceptoGastoCodigoEc} del
   *         {@link ProyectoConceptoGasto}.
   */
  @Override
  public List<ProyectoConceptoGastoCodigoEc> findAllByProyectoConceptoGasto(Long proyectoConceptoGastoId) {
    log.debug("findAllByProyectoConceptoGasto(Long proyectoConceptoGastoId)) - start");
    Specification<ProyectoConceptoGastoCodigoEc> specByProyectoConceptoGasto = ProyectoConceptoGastoCodigoEcSpecifications
        .byProyectoConceptoGasto(proyectoConceptoGastoId);
    Specification<ProyectoConceptoGastoCodigoEc> specByConceptoGastoActivo = ProyectoConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();

    Specification<ProyectoConceptoGastoCodigoEc> specs = Specification.where(specByProyectoConceptoGasto)
        .and(specByConceptoGastoActivo);
    List<ProyectoConceptoGastoCodigoEc> proyectoConceptoGastoCodigosEc = repository.findAll(specs);
    log.debug("findAllByProyectoConceptoGasto(Long proyectoConceptoGastoId)) - end");
    return proyectoConceptoGastoCodigosEc;
  }

  /**
   * Se valida la unicidad del código económico. Para un
   * {@link ProyectoConceptoGasto} el mismo código económico solo puede aparecer
   * una vez, salvo que lo haga en periodos de vigencia no solapados.
   * 
   * @param proyectoConceptoGastoCodigoEc {@link ProyectoConceptoGastoCodigoEc}
   * @param conceptoGastoPermitido        permitido o no
   * 
   * @return true validación correcta/ false validacion incorrecta
   */
  @Override
  public boolean existsProyectoConceptoGastoCodigoEcConFechasSolapadas(
      ProyectoConceptoGastoCodigoEc proyectoConceptoGastoCodigoEc, Boolean conceptoGastoPermitido) {

    Specification<ProyectoConceptoGastoCodigoEc> specByProyectoConceptoGasto = ProyectoConceptoGastoCodigoEcSpecifications
        .byProyectoConceptoGasto(proyectoConceptoGastoCodigoEc.getProyectoConceptoGastoId());
    Specification<ProyectoConceptoGastoCodigoEc> specByConceptoGastoCodigoEcActivo = ProyectoConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();
    Specification<ProyectoConceptoGastoCodigoEc> specByCodigoEconomicoRef = ProyectoConceptoGastoCodigoEcSpecifications
        .byCodigoEconomicoRef(proyectoConceptoGastoCodigoEc.getCodigoEconomicoRef());
    Specification<ProyectoConceptoGastoCodigoEc> specByRangoFechaSolapados = ProyectoConceptoGastoCodigoEcSpecifications
        .byRangoFechaSolapados(proyectoConceptoGastoCodigoEc.getFechaInicio(),
            proyectoConceptoGastoCodigoEc.getFechaFin());
    Specification<ProyectoConceptoGastoCodigoEc> specByIdNotEqual = ProyectoConceptoGastoCodigoEcSpecifications
        .byIdNotEqual(proyectoConceptoGastoCodigoEc.getId());
    Specification<ProyectoConceptoGastoCodigoEc> specByProyectoConceptoGastoPermitido = ProyectoConceptoGastoCodigoEcSpecifications
        .byProyectoConceptoGastoPermitido(conceptoGastoPermitido);

    Specification<ProyectoConceptoGastoCodigoEc> specs = Specification.where(specByProyectoConceptoGasto)
        .and(specByRangoFechaSolapados).and(specByConceptoGastoCodigoEcActivo).and(specByCodigoEconomicoRef)
        .and(specByIdNotEqual).and(specByProyectoConceptoGastoPermitido);

    return repository.count(specs) > 0;
  }

  /**
   * Se valida la unicidad del código económico y concepto gasto, salvo que lo
   * haga en periodos de vigencia no solapados
   * (independientemente del valor del campo "permitido").
   * 
   * @param proyectoConceptoGastoCodigoEc {@link ProyectoConceptoGastoCodigoEc}
   * @param fechaInicioConceptoGasto      fecha inicial del
   *                                      {@link ProyectoConceptoGasto}
   * @param fechaFinConceptoGasto         fecha final del
   *                                      {@link ProyectoConceptoGasto}
   * @param proyectoId                    Identifiacdor del {@link Proyecto}
   * @return true validación correcta/ false validacion incorrecta
   */
  @Override
  public boolean existsProyectoConceptoGastoCodigoEcAndConceptoGastoConFechasSolapadas(
      ProyectoConceptoGastoCodigoEc proyectoConceptoGastoCodigoEc,
      Instant fechaInicioConceptoGasto, Instant fechaFinConceptoGasto, Long proyectoId) {

    Specification<ProyectoConceptoGastoCodigoEc> specByProyecto = ProyectoConceptoGastoCodigoEcSpecifications
        .byProyecto(proyectoId);
    Specification<ProyectoConceptoGastoCodigoEc> specByConceptoGastoCodigoEcActivo = ProyectoConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();
    Specification<ProyectoConceptoGastoCodigoEc> specByCodigoEconomicoRef = ProyectoConceptoGastoCodigoEcSpecifications
        .byCodigoEconomicoRef(proyectoConceptoGastoCodigoEc.getCodigoEconomicoRef());
    Specification<ProyectoConceptoGastoCodigoEc> specByRangoFechaSolapados = ProyectoConceptoGastoCodigoEcSpecifications
        .byRangoFechaConceptoGastoSolapados(fechaInicioConceptoGasto, fechaFinConceptoGasto);
    Specification<ProyectoConceptoGastoCodigoEc> specByIdNotEqual = ProyectoConceptoGastoCodigoEcSpecifications
        .byIdNotEqual(proyectoConceptoGastoCodigoEc.getId());
    Specification<ProyectoConceptoGastoCodigoEc> specByConceptoGasto = ProyectoConceptoGastoCodigoEcSpecifications
        .byConceptoGasto(proyectoConceptoGastoCodigoEc.getProyectoConceptoGastoId());

    Specification<ProyectoConceptoGastoCodigoEc> specs = Specification.where(specByProyecto)
        .and(specByRangoFechaSolapados).and(specByConceptoGastoCodigoEcActivo).and(specByCodigoEconomicoRef)
        .and(specByIdNotEqual).and(specByConceptoGasto);

    return repository.count(specs) > 0;
  }

  /**
   * Actualiza el listado de {@link ProyectoConceptoGastoCodigoEc} del
   * {@link ProyectoConceptoGasto} con el listado proyectoConceptoGastoCodigoEcs
   * añadiendo, editando o eliminando los elementos segun proceda.
   *
   * @param proyectoConceptoGastoId        Id de la {@link ProyectoConceptoGasto}.
   * @param proyectoConceptoGastoCodigoEcs lista con los nuevos
   *                                       {@link ProyectoConceptoGastoCodigoEc} a
   *                                       guardar.
   * @return la lista de entidades {@link ProyectoConceptoGastoCodigoEc}
   *         persistidas.
   */
  @Override
  @Transactional
  public List<ProyectoConceptoGastoCodigoEc> update(Long proyectoConceptoGastoId,
      List<ProyectoConceptoGastoCodigoEc> proyectoConceptoGastoCodigoEcs) {
    log.debug(
        "update(Long proyectoConceptoGastoId, List<ProyectoConceptoGastoCodigoEc> proyectoConceptoGastoCodigoEcs) - start");

    ProyectoConceptoGasto proyectoConceptoGasto = proyectoConceptoGastoRepository.findById(proyectoConceptoGastoId)
        .orElseThrow(() -> new ProyectoConceptoGastoNotFoundException(proyectoConceptoGastoId));

    List<ProyectoConceptoGastoCodigoEc> proyectoConceptoGastoCodigoEcsBD = repository
        .findAllByProyectoConceptoGastoId(proyectoConceptoGastoId);

    // Códigos econcómicos eliminados
    List<ProyectoConceptoGastoCodigoEc> proyectoConceptoGastoCodigoEcsEliminar = proyectoConceptoGastoCodigoEcsBD
        .stream().filter(periodo -> proyectoConceptoGastoCodigoEcs.stream().map(ProyectoConceptoGastoCodigoEc::getId)
            .noneMatch(id -> Objects.equals(id, periodo.getId())))
        .collect(Collectors.toList());

    if (!proyectoConceptoGastoCodigoEcsEliminar.isEmpty()) {
      repository.deleteAll(proyectoConceptoGastoCodigoEcsEliminar);
    }

    // Ordena los códigos económicos por fecha de inicio
    proyectoConceptoGastoCodigoEcs.sort(Comparator.comparing(ProyectoConceptoGastoCodigoEc::getFechaInicio,
        Comparator.nullsLast(Comparator.naturalOrder())));

    // Validaciones
    List<ProyectoConceptoGastoCodigoEc> returnValue = new ArrayList<>();
    for (ProyectoConceptoGastoCodigoEc proyectoConceptoGastoCodigoEc : proyectoConceptoGastoCodigoEcs) {

      // actualizando
      if (proyectoConceptoGastoCodigoEc.getId() != null) {
        ProyectoConceptoGastoCodigoEc proyectoConceptoGastoCodigoEcBD = proyectoConceptoGastoCodigoEcsBD.stream()
            .filter(periodo -> Objects.equals(periodo.getId(), proyectoConceptoGastoCodigoEc.getId())).findFirst()
            .orElseThrow(
                () -> new ProyectoConceptoGastoCodigoEcNotFoundException(proyectoConceptoGastoCodigoEc.getId()));

        Assert.isTrue(
            Objects.equals(proyectoConceptoGastoCodigoEcBD.getProyectoConceptoGastoId(), proyectoConceptoGastoCodigoEc
                .getProyectoConceptoGastoId()),
            () -> ProblemMessage.builder()
                .key(MSG_PROBLEM_ACCION_DENEGADA)
                .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(
                    MSG_MODEL_PROYECTO_CONCEPTO_GASTO))
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                    MSG_MODEL_PROYECTO_CONCEPTO_GASTO_CODIGO_EC))
                .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_MODIFICAR))
                .build());
      }

      if (proyectoConceptoGastoCodigoEc.getFechaInicio() != null
          && proyectoConceptoGastoCodigoEc.getFechaFin() != null) {
        AssertHelper.isBefore(
            proyectoConceptoGastoCodigoEc.getFechaInicio().isBefore(proyectoConceptoGastoCodigoEc.getFechaFin()));

      }

      // Unicidad código económico y solapamiento de fechas
      Assert.isTrue(
          !existsProyectoConceptoGastoCodigoEcConFechasSolapadas(proyectoConceptoGastoCodigoEc,
              proyectoConceptoGasto.getPermitido()),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_DATE_OVERLOAP)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_CODIGO_ECONOMICO))
              .parameter(MSG_KEY_FIELD, proyectoConceptoGastoCodigoEc.getCodigoEconomicoRef())
              .build());

      Assert.isTrue(
          !existsProyectoConceptoGastoCodigoEcAndConceptoGastoConFechasSolapadas(proyectoConceptoGastoCodigoEc,
              proyectoConceptoGasto.getFechaInicio(),
              proyectoConceptoGasto.getFechaFin(),
              proyectoConceptoGasto.getProyectoId()),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_DATE_OVERLOAP_OTHER_ENTITY)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_CODIGO_ECONOMICO))
              .parameter(MSG_KEY_FIELD, proyectoConceptoGastoCodigoEc.getCodigoEconomicoRef())
              .parameter(MSG_KEY_OTHER_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_CONCEPTO_GASTO))
              .build());

      returnValue.add(repository.save(proyectoConceptoGastoCodigoEc));
    }

    log.debug(
        "updateProyectoConceptoGastoCodigoEcsProyecto(Long proyectoConceptoGastoId, List<ProyectoConceptoGastoCodigoEc> proyectoConceptoGastoCodigoEcs) - end");

    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link ProyectoConceptoGastoCodigoEc} por id de
   * {@link ProyectoConceptoGasto}
   *
   * @param id el id de la entidad {@link ProyectoConceptoGasto}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsByProyectoConceptoGasto(final Long id) {
    log.debug("existsByProyectoConceptoGasto(final Long id)  - start", id);
    final boolean existe = repository.existsByProyectoConceptoGastoId(id);
    log.debug("existsByProyectoConceptoGasto(final Long id)  - end", id);
    return existe;
  }
}
