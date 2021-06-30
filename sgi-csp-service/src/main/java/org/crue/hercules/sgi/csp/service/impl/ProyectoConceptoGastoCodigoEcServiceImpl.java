package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
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

  private final ProyectoConceptoGastoCodigoEcRepository repository;
  private final ProyectoConceptoGastoRepository proyectoConceptoGastoRepository;

  public ProyectoConceptoGastoCodigoEcServiceImpl(ProyectoConceptoGastoCodigoEcRepository repository,
      ProyectoConceptoGastoRepository proyectoConceptoGastoRepository) {
    this.repository = repository;
    this.proyectoConceptoGastoRepository = proyectoConceptoGastoRepository;
  }

  /**
   * Obtiene {@link ProyectoConceptoGastoCodigoEc} por su id.
   *
   * @param id el id de la entidad {@link ProyectoConceptoGastoCodigoEc}.
   * @return la entidad {@link ProyectoConceptoGastoCodigoEc}.
   */
  @Override
  public ProyectoConceptoGastoCodigoEc findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoConceptoGastoCodigoEc returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoConceptoGastoCodigoEcNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
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
    Page<ProyectoConceptoGastoCodigoEc> returnValue = repository.findAll(specs, pageable);
    return returnValue;
  }

  /**
   * Se valida la unicidad del código económico. Para un
   * {@link ProyectoConceptoGasto} el mismo código económico solo puede aparecer
   * una vez, salvo que lo haga en periodos de vigencia no solapados
   * (independientemente del valor del campo "permitido").
   * 
   * @param proyectoConceptoGastoId id {@link ProyectoConceptoGasto}
   * @param fechaInicio             fecha inicial
   * @param fechaFin                fecha final
   * @param excluirId               identificadores a excluir de la busqueda
   * @return true validación correcta/ false validacion incorrecta
   */
  private boolean existsProyectoConceptoGastoCodigoEcConFechasSolapadas(
      ProyectoConceptoGastoCodigoEc proyectoConceptoGastoCodigoEc, Boolean conceptoGastoPermitido) {
    log.debug(
        "existsProyectoConceptoGastoCodigoEcConFechasSolapadas(ProyectoConceptoGastoCodigoEc proyectoConceptoGastoCodigoEc)");

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

    Page<ProyectoConceptoGastoCodigoEc> proyectoConceptoGastoCodigoEcs = repository.findAll(specs, Pageable.unpaged());

    Boolean returnValue = !proyectoConceptoGastoCodigoEcs.isEmpty();
    log.debug(
        "existsProyectoConceptoGastoCodigoEcConFechasSolapadas(ProyectoConceptoGastoCodigoEc proyectoConceptoGastoCodigoEc) - end");

    return returnValue;
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
        .stream().filter(periodo -> !proyectoConceptoGastoCodigoEcs.stream().map(ProyectoConceptoGastoCodigoEc::getId)
            .anyMatch(id -> id == periodo.getId()))
        .collect(Collectors.toList());

    if (!proyectoConceptoGastoCodigoEcsEliminar.isEmpty()) {
      repository.deleteAll(proyectoConceptoGastoCodigoEcsEliminar);
    }

    // Ordena los códigos económicos por fecha de inicio
    proyectoConceptoGastoCodigoEcs.sort(Comparator.comparing(ProyectoConceptoGastoCodigoEc::getFechaInicio,
        Comparator.nullsLast(Comparator.naturalOrder())));

    // Validaciones
    List<ProyectoConceptoGastoCodigoEc> returnValue = new ArrayList<ProyectoConceptoGastoCodigoEc>();
    for (ProyectoConceptoGastoCodigoEc proyectoConceptoGastoCodigoEc : proyectoConceptoGastoCodigoEcs) {

      // actualizando
      if (proyectoConceptoGastoCodigoEc.getId() != null) {
        ProyectoConceptoGastoCodigoEc proyectoConceptoGastoCodigoEcBD = proyectoConceptoGastoCodigoEcsBD.stream()
            .filter(periodo -> periodo.getId() == proyectoConceptoGastoCodigoEc.getId()).findFirst().orElseThrow(
                () -> new ProyectoConceptoGastoCodigoEcNotFoundException(proyectoConceptoGastoCodigoEc.getId()));

        Assert.isTrue(
            proyectoConceptoGastoCodigoEcBD.getProyectoConceptoGastoId() == proyectoConceptoGastoCodigoEc
                .getProyectoConceptoGastoId(),
            "No se puede modificar el proyectoConceptoGasto del ProyectoConceptoGastoCodigoEc");
      }

      if (proyectoConceptoGastoCodigoEc.getFechaInicio() != null
          && proyectoConceptoGastoCodigoEc.getFechaFin() != null) {
        Assert.isTrue(
            proyectoConceptoGastoCodigoEc.getFechaInicio().isBefore(proyectoConceptoGastoCodigoEc.getFechaFin()),
            "La fecha fin no puede ser superior a la fecha de inicio");

      }

      // Unicidad código económico y solapamiento de fechas
      Assert.isTrue(
          !existsProyectoConceptoGastoCodigoEcConFechasSolapadas(proyectoConceptoGastoCodigoEc,
              proyectoConceptoGasto.getPermitido()),
          "El código económico '" + proyectoConceptoGastoCodigoEc.getCodigoEconomicoRef()
              + "' ya está presente y tiene un periodo de vigencia que se solapa con el indicado");

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
