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
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
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
   * Obtiene {@link ConvocatoriaConceptoGastoCodigoEc} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaConceptoGastoCodigoEc}.
   * @return la entidad {@link ConvocatoriaConceptoGastoCodigoEc}.
   */
  @Override
  public ConvocatoriaConceptoGastoCodigoEc findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaConceptoGastoCodigoEc returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaConceptoGastoCodigoEcNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
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
            "No se puede modificar el convocatoriaConceptoGasto del ConvocatoriaConceptoGastoCodigoEc");
      }

      if (convocatoriaConceptoGastoCodigoEc.getFechaInicio() != null
          && convocatoriaConceptoGastoCodigoEc.getFechaFin() != null) {
        Assert.isTrue(
            convocatoriaConceptoGastoCodigoEc.getFechaInicio()
                .isBefore(convocatoriaConceptoGastoCodigoEc.getFechaFin()),
            "La fecha fin no puede ser superior a la fecha de inicio");

      }

      // Unicidad código económico y solapamiento de fechas
      Assert.isTrue(
          !existsConvocatoriaConceptoGastoCodigoEcConFechasSolapadas(convocatoriaConceptoGastoCodigoEc,
              convocatoriaConceptoGasto.getPermitido()),
          "El código económico '" + convocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef()
              + "' ya está presente y tiene un periodo de vigencia que se solapa con el indicado");

      Assert.isTrue(
          !existsConvocatoriaConceptoGastoCodigoEcAndConceptoGastoConFechasSolapadas(convocatoriaConceptoGastoCodigoEc,
              convocatoriaConceptoGasto.getMesInicial(), convocatoriaConceptoGasto.getMesFinal(),
              convocatoriaConceptoGasto.getConvocatoriaId()),
          "El código económico '" + convocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef()
              + "' ya está presente en otro concepto gasto de la convocatoria y tiene un periodo de vigencia que se solapa con el indicado");

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
