package org.crue.hercules.sgi.csp.service;

import java.time.Instant;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.GastoProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.Configuracion;
import org.crue.hercules.sgi.csp.model.EstadoGastoProyecto;
import org.crue.hercules.sgi.csp.model.GastoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
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
  private final ConfiguracionService configuracionService;

  public GastoProyectoService(GastoProyectoRepository gastoProyectoRepository,
      EstadoGastoProyectoRepository estadoGastoProyectoRepository,
      ConfiguracionService configuracionService) {
    this.repository = gastoProyectoRepository;
    this.estadoGastoProyectoRepository = estadoGastoProyectoRepository;
    this.configuracionService = configuracionService;

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

    EstadoGastoProyecto estadoGastoProyectoToCreate = gastoProyecto.getEstado();
    // Si el estado viene cubierto con datos que no existen en la BBDD genera la
    // siguiente excepcion:
    // org.springframework.dao.InvalidDataAccessApiUsageException:
    // org.hibernate.TransientPropertyValueException: object references an unsaved
    // transient instance - save the transient instance before flushing :
    // org.crue.hercules.sgi.csp.model.GastoProyecto.estado
    // por la llamada al configuracionService.findConfiguracion()
    gastoProyecto.setEstado(null);

    GastoProyecto gastoProyectoCreated = repository.save(gastoProyecto);

    Configuracion configuracion = configuracionService.findConfiguracion();
    if (estadoGastoProyectoToCreate != null && Configuracion.ValidacionClasificacionGastos.VALIDACION
        .equals(configuracion.getValidacionClasificacionGastos())) {
      estadoGastoProyectoToCreate.setFechaEstado(Instant.now());
      estadoGastoProyectoToCreate.setGastoProyectoId(gastoProyectoCreated.getId());

      EstadoGastoProyecto newEstadoGastoProyecto = estadoGastoProyectoRepository.save(estadoGastoProyectoToCreate);
      gastoProyectoCreated.setEstado(newEstadoGastoProyecto);

      GastoProyecto gastoProyectoUpdatedWithEstado = repository.save(gastoProyectoCreated);

      log.debug("create(GastoProyecto gastoProyecto) - end");
      return gastoProyectoUpdatedWithEstado;
    }

    log.debug("create(GastoProyecto gastoProyecto) - end");
    return gastoProyectoCreated;
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
      if ((gastoProyectoExistente.getEstado() != null && gastoProyecto.getEstado() != null) && (gastoProyectoExistente
          .getEstado().getEstado() != gastoProyecto.getEstado().getEstado()
          || !gastoProyectoExistente.getEstado().getComentario().equals(gastoProyecto.getEstado().getComentario()))) {

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
      gastoProyectoExistente.setProyectoId(gastoProyecto.getProyectoId());
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

  /**
   * Indica si existen {@link GastoProyecto} de un {@link Proyecto}
   * 
   * @param proyectoId identificador de la {@link Proyecto}
   * @return si existen {@link GastoProyecto} asociados al {@link Proyecto}
   */
  public boolean existsByProyecto(Long proyectoId) {
    log.debug("existsByProyecto(Long proyectoId) - start");
    boolean returnValue = repository.existsByProyectoId(proyectoId);
    log.debug("existsByProyecto(Long proyectoId) - end");
    return returnValue;
  }

}