package org.crue.hercules.sgi.csp.service;

import java.time.Instant;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.GastoProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoGastoProyecto;
import org.crue.hercules.sgi.csp.model.GastoProyecto;
import org.crue.hercules.sgi.csp.repository.EstadoGastoProyectoRepository;
import org.crue.hercules.sgi.csp.repository.GastoProyectoRepository;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link GastoProyecto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class GastoProyectoService {

  private final GastoProyectoRepository repository;
  private final EstadoGastoProyectoRepository estadoGastoProyectoRepository;

  public GastoProyectoService(GastoProyectoRepository gastoProyectoRepository,
      EstadoGastoProyectoRepository estadoGastoProyectoRepository) {
    this.repository = gastoProyectoRepository;
    this.estadoGastoProyectoRepository = estadoGastoProyectoRepository;
  }

  /**
   * Guardar un nuevo {@link GastoProyecto}.
   *
   * @param gastoProyecto la entidad {@link GastoProyecto} a guardar.
   * @return la entidad {@link GastoProyecto} persistida.
   */
  @Transactional
  @Validated({ GastoProyecto.OnCrear.class })
  public GastoProyecto create(@Valid GastoProyecto gastoProyecto) {
    log.debug("create(GastoProyecto gastoProyecto) - start");

    GastoProyecto gastoProyectoSinEstado = repository.save(gastoProyecto);

    EstadoGastoProyecto estadoGastoProyectoNuevo = new EstadoGastoProyecto();
    estadoGastoProyectoNuevo.setComentario(gastoProyecto.getEstado().getComentario());
    estadoGastoProyectoNuevo.setFechaEstado(Instant.now());
    estadoGastoProyectoNuevo.setEstado(gastoProyecto.getEstado().getEstado());
    estadoGastoProyectoNuevo.setGastoProyectoId(gastoProyecto.getId());

    EstadoGastoProyecto returnValueEstadoGastoProyecto = estadoGastoProyectoRepository.save(estadoGastoProyectoNuevo);
    gastoProyectoSinEstado.setEstado(returnValueEstadoGastoProyecto);

    GastoProyecto returnValue = repository.save(gastoProyectoSinEstado);

    log.debug("create(GastoProyecto gastoProyecto) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link GastoProyecto}.
   *
   * @param gastoProyecto la entidad {@link GastoProyecto} a actualizar.
   * @return la entidad {@link GastoProyecto} persistida.
   */
  @Transactional
  @Validated({ GastoProyecto.OnActualizar.class })
  public GastoProyecto update(@Valid GastoProyecto gastoProyecto) {
    log.debug("update(GastoProyecto gastoProyecto) - start");

    return repository.findById(gastoProyecto.getId()).map(gastoProyectoExistente -> {

      // Si hay modificaciones en el estado o en su comentario asociado, creamos un
      // nuevo estado para reflejar el cambio en el histórico de estados
      if (gastoProyectoExistente.getEstado().getEstado() != gastoProyecto.getEstado().getEstado()
          || !gastoProyectoExistente.getEstado().getComentario().equals(gastoProyecto.getEstado().getComentario())) {

        EstadoGastoProyecto estadoGastoProyectoNuevo = new EstadoGastoProyecto();
        estadoGastoProyectoNuevo.setComentario(gastoProyecto.getEstado().getComentario());
        estadoGastoProyectoNuevo.setFechaEstado(Instant.now());
        estadoGastoProyectoNuevo.setEstado(gastoProyecto.getEstado().getEstado());
        estadoGastoProyectoNuevo.setGastoProyectoId(gastoProyecto.getId());

        EstadoGastoProyecto returnValueEstadoGastoProyecto = estadoGastoProyectoRepository
            .save(estadoGastoProyectoNuevo);
        gastoProyectoExistente.setEstado(returnValueEstadoGastoProyecto);
      }

      // Establecemos los campos actualizables con los recibidos
      gastoProyectoExistente.setConceptoGasto(gastoProyecto.getConceptoGasto());
      gastoProyectoExistente.setFechaCongreso(gastoProyecto.getFechaCongreso());
      gastoProyectoExistente.setImporteInscripcion(gastoProyecto.getImporteInscripcion());
      gastoProyectoExistente.setObservaciones(gastoProyecto.getObservaciones());

      // Actualizamos la entidad
      GastoProyecto returnValue = repository.save(gastoProyectoExistente);
      log.debug("update(GastoProyecto gastoProyecto) - end");
      return returnValue;
    }).orElseThrow(() -> new GastoProyectoNotFoundException(gastoProyecto.getId()));

  }

  /**
   * Obtener todas las entidades {@link GastoProyecto} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link GastoProyecto} paginadas y/o filtradas.
   */
  public Page<GastoProyecto> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<GastoProyecto> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<GastoProyecto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

}