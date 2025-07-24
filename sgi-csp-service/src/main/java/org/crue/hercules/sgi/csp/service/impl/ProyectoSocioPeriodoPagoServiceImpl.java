package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoPagoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoSocioPeriodoPagoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoPagoService;
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
 * Service Implementation para gestion {@link ProyectoSocioPeriodoPagoService}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoSocioPeriodoPagoServiceImpl implements ProyectoSocioPeriodoPagoService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_ACTION = "action";
  private static final String MSG_FIELD_ACTION_MODIFICAR = "action.modificar";
  private static final String MSG_MODEL_PROYECTO_SOCIO = "org.crue.hercules.sgi.csp.model.ProyectoSocio.message";
  private static final String MSG_MODEL_PROYECTO_SOCIO_PERIODO_PAGO = "org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago.message";
  private static final String MSG_PROBLEM_ACCION_DENEGADA = "org.springframework.util.Assert.accion.denegada.message";

  /** Repository {@link ProyectoSocioPeriodoPagoRepository} */
  private final ProyectoSocioPeriodoPagoRepository repository;

  /** Repository {@link ProyectoSocioRepository} */
  private final ProyectoSocioRepository proyectoSocioRepository;

  /**
   * {@link ProyectoSocioPeriodoPagoServiceImpl}
   * 
   * @param repository              {@link ProyectoSocioPeriodoPagoRepository}
   * @param proyectoSocioRepository {@link ProyectoSocioRepository}
   */
  public ProyectoSocioPeriodoPagoServiceImpl(ProyectoSocioPeriodoPagoRepository repository,
      ProyectoSocioRepository proyectoSocioRepository) {
    this.repository = repository;
    this.proyectoSocioRepository = proyectoSocioRepository;

  }

  /**
   * Actualiza el listado de {@link ProyectoSocioPeriodoPago} del
   * {@link ProyectoSocio} con el listado solicitudPeriodoPagos añadiendo,
   * editando o eliminando los elementos segun proceda.
   *
   * @param proyectoSocioId           Id de la {@link ProyectoSocio}.
   * @param proyectoSocioPeriodoPagos lista con los nuevos
   *                                  {@link ProyectoSocioPeriodoPago} a guardar.
   * @return la entidad {@link ProyectoSocioPeriodoPago} persistida.
   */
  @Override
  @Transactional
  public List<ProyectoSocioPeriodoPago> update(Long proyectoSocioId,
      List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagos) {
    log.debug("update(Long proyectoSocioId, List<ProyectoSocioPeriodoPago> solicitudPeriodoPagos) - start");

    if (!proyectoSocioRepository.existsById(proyectoSocioId)) {
      throw new ProyectoSocioNotFoundException(proyectoSocioId);
    }

    List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagosBD = repository.findAllByProyectoSocioId(proyectoSocioId);

    // Periodos pago eliminados
    List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagoEliminar = proyectoSocioPeriodoPagosBD.stream()
        .filter(periodo -> proyectoSocioPeriodoPagos.stream().map(ProyectoSocioPeriodoPago::getId)
            .noneMatch(id -> Objects.equals(id, periodo.getId())))
        .collect(Collectors.toList());

    if (!proyectoSocioPeriodoPagoEliminar.isEmpty()) {
      repository.deleteAll(proyectoSocioPeriodoPagoEliminar);
    }

    if (proyectoSocioPeriodoPagos.isEmpty()) {
      return new ArrayList<>();
    }

    // Ordena los periodos por mesInicial
    proyectoSocioPeriodoPagos.sort(Comparator.comparing(ProyectoSocioPeriodoPago::getFechaPrevistaPago));

    AtomicInteger numPeriodo = new AtomicInteger(0);

    for (ProyectoSocioPeriodoPago proyectoSocioPeriodoPago : proyectoSocioPeriodoPagos) {
      // Actualiza el numero de periodo
      proyectoSocioPeriodoPago.setNumPeriodo(numPeriodo.incrementAndGet());

      // Si tiene id se valida que exista y que tenga la proyectoSocioPeriodoPago de
      // la que se estan actualizando los periodos
      if (proyectoSocioPeriodoPago.getId() != null) {
        ProyectoSocioPeriodoPago proyectoSocioPeriodoPagoBD = proyectoSocioPeriodoPagosBD.stream()
            .filter(periodo -> Objects.equals(periodo.getId(), proyectoSocioPeriodoPago.getId())).findFirst()
            .orElseThrow(() -> new ProyectoSocioPeriodoPagoNotFoundException(proyectoSocioPeriodoPago.getId()));

        Assert.isTrue(
            Objects.equals(proyectoSocioPeriodoPagoBD.getProyectoSocioId(),
                proyectoSocioPeriodoPago.getProyectoSocioId()),
            () -> ProblemMessage.builder()
                .key(MSG_PROBLEM_ACCION_DENEGADA)
                .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(
                    MSG_MODEL_PROYECTO_SOCIO))
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                    MSG_MODEL_PROYECTO_SOCIO_PERIODO_PAGO))
                .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_MODIFICAR))
                .build());
      }
    }

    List<ProyectoSocioPeriodoPago> returnValue = repository.saveAll(proyectoSocioPeriodoPagos);
    log.debug("update(Long proyectoSocioPeriodoPagoId, List<ProyectoSocioPeriodoPago> solicitudPeriodoPagos) - end");

    return returnValue;
  }

  /**
   * Obtiene una entidad {@link ProyectoSocioPeriodoPago} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoSocioPeriodoPago}.
   * @return ProyectoSocioPeriodoPago la entidad {@link ProyectoSocioPeriodoPago}.
   */
  @Override
  public ProyectoSocioPeriodoPago findById(Long id) {
    log.debug("findById(Long id) - start");
    final ProyectoSocioPeriodoPago returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoSocioPeriodoPagoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ProyectoSocioPeriodoPago} para un {@link ProyectoSocio}.
   *
   * @param idProyectoSocio el id del {@link ProyectoSocio}.
   * @param query           la información del filtro.
   * @param paging          la información de la paginación.
   * @return la lista de entidades {@link ProyectoSocioPeriodoPago} del
   *         {@link ProyectoSocio} paginadas.
   */
  @Override
  public Page<ProyectoSocioPeriodoPago> findAllByProyectoSocio(Long idProyectoSocio, String query, Pageable paging) {
    log.debug("findAllByProyectoSocio(Long idProyectoSocioPeriodoPago, String query, Pageable paging) - start");

    Specification<ProyectoSocioPeriodoPago> specs = ProyectoSocioPeriodoPagoSpecifications
        .byProyectoSocioId(idProyectoSocio).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoSocioPeriodoPago> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByProyectoSocio(Long idProyectoSocioPeriodoPago, String query, Pageable paging) - end");
    return returnValue;
  }

}
