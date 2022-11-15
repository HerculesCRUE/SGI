package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.SeguimientoJustificacionAnualidad;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoJustificacionSeguimientoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacionSeguimiento;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoJustificacionSeguimientoRepository;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion
 * {@link ProyectoPeriodoJustificacionSeguimiento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProyectoPeriodoJustificacionSeguimientoService {

  private final ProyectoPeriodoJustificacionSeguimientoRepository repository;

  /**
   * Obtener todas las entidades {@link SeguimientoJustificacionAnualidad}
   * pertenecientes al ProyectoSgeRef.
   *
   * @param proyectoSgeRef el identificador de un ProyectoSGE
   * @return la lista de entidades {@link RequerimientoJustificacion}.
   */
  public List<SeguimientoJustificacionAnualidad> findSeguimientosJustificacionAnualidadByProyectoSgeRef(
      String proyectoSgeRef) {
    log.debug("findSeguimientosJustificacionAnualidadByProyectoSgeRef(String proyectoSgeRef) - start");

    List<SeguimientoJustificacionAnualidad> returnValue = repository
        .findSeguimientosJustificacionAnualidadByProyectoSgeRef(proyectoSgeRef);
    log.debug("findSeguimientosJustificacionAnualidadByProyectoSgeRef(String proyectoSgeRef) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link ProyectoPeriodoJustificacionSeguimiento} por su id.
   *
   * @param id el id de la entidad
   *           {@link ProyectoPeriodoJustificacionSeguimiento}.
   * @return la entidad {@link ProyectoPeriodoJustificacionSeguimiento}.
   */
  public ProyectoPeriodoJustificacionSeguimiento findById(Long id) {
    log.debug("findById(Long id)  - start");
    AssertHelper.idNotNull(id, ProyectoPeriodoJustificacionSeguimiento.class);

    final ProyectoPeriodoJustificacionSeguimiento returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoPeriodoJustificacionSeguimientoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar una nueva entidad {@link ProyectoPeriodoJustificacionSeguimiento}.
   *
   * @param proyectoPeriodoJustificacionSeguimiento la entidad
   *                                                {@link ProyectoPeriodoJustificacionSeguimiento}
   *                                                a guardar.
   * @return la entidad {@link ProyectoPeriodoJustificacionSeguimiento}
   *         persistida.
   */
  @Transactional
  public ProyectoPeriodoJustificacionSeguimiento create(
      ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimiento) {
    log.debug("create(ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimiento) - start");
    AssertHelper.idIsNull(proyectoPeriodoJustificacionSeguimiento.getId(),
        ProyectoPeriodoJustificacionSeguimiento.class);

    ProyectoPeriodoJustificacionSeguimiento returnValue = repository.save(proyectoPeriodoJustificacionSeguimiento);

    log.debug("create(ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimiento) - end");
    return returnValue;
  }

  /**
   * Actualiza una entidad {@link ProyectoPeriodoJustificacionSeguimiento}.
   *
   * @param proyectoPeriodoJustificacionSeguimiento la entidad
   *                                                {@link ProyectoPeriodoJustificacionSeguimiento}
   *                                                a actualizar.
   * @return la entidad {@link ProyectoPeriodoJustificacionSeguimiento}
   *         persistida.
   */
  @Transactional
  public ProyectoPeriodoJustificacionSeguimiento update(
      ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimiento) {
    log.debug("update(ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimiento) - start");
    AssertHelper.idNotNull(proyectoPeriodoJustificacionSeguimiento.getId(),
        ProyectoPeriodoJustificacionSeguimiento.class);

    return repository.findById(proyectoPeriodoJustificacionSeguimiento.getId())
        .map(proyectoPeriodoJustificacionSeguimientoExistente -> {

          // Establecemos los campos actualizables con los recibidos
          proyectoPeriodoJustificacionSeguimientoExistente
              .setProyectoAnualidadId(proyectoPeriodoJustificacionSeguimiento.getProyectoAnualidadId());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setFechaReintegro(proyectoPeriodoJustificacionSeguimiento.getFechaReintegro());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteAceptado(proyectoPeriodoJustificacionSeguimiento.getImporteAceptado());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteAceptadoCD(proyectoPeriodoJustificacionSeguimiento.getImporteAceptadoCD());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteAceptadoCI(proyectoPeriodoJustificacionSeguimiento.getImporteAceptadoCI());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteAlegado(proyectoPeriodoJustificacionSeguimiento.getImporteAlegado());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteAlegadoCD(proyectoPeriodoJustificacionSeguimiento.getImporteAlegadoCD());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteAlegadoCI(proyectoPeriodoJustificacionSeguimiento.getImporteAlegadoCI());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteJustificado(proyectoPeriodoJustificacionSeguimiento.getImporteJustificado());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteJustificadoCD(proyectoPeriodoJustificacionSeguimiento.getImporteJustificadoCD());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteJustificadoCI(proyectoPeriodoJustificacionSeguimiento.getImporteJustificadoCI());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteNoEjecutado(proyectoPeriodoJustificacionSeguimiento.getImporteNoEjecutado());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteNoEjecutadoCD(proyectoPeriodoJustificacionSeguimiento.getImporteNoEjecutadoCD());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteNoEjecutadoCI(proyectoPeriodoJustificacionSeguimiento.getImporteNoEjecutadoCI());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteRechazado(proyectoPeriodoJustificacionSeguimiento.getImporteRechazado());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteRechazadoCD(proyectoPeriodoJustificacionSeguimiento.getImporteRechazadoCD());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteRechazadoCI(proyectoPeriodoJustificacionSeguimiento.getImporteRechazadoCI());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteReintegrado(proyectoPeriodoJustificacionSeguimiento.getImporteReintegrado());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteReintegradoCD(proyectoPeriodoJustificacionSeguimiento.getImporteReintegradoCD());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteReintegradoCI(proyectoPeriodoJustificacionSeguimiento.getImporteReintegradoCI());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteReintegrar(proyectoPeriodoJustificacionSeguimiento.getImporteReintegrar());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteReintegrarCD(proyectoPeriodoJustificacionSeguimiento.getImporteReintegrarCD());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setImporteReintegrarCI(proyectoPeriodoJustificacionSeguimiento.getImporteReintegrarCI());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setInteresesReintegrados(proyectoPeriodoJustificacionSeguimiento.getInteresesReintegrados());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setInteresesReintegrar(proyectoPeriodoJustificacionSeguimiento.getInteresesReintegrar());
          proyectoPeriodoJustificacionSeguimientoExistente
              .setJustificanteReintegro(proyectoPeriodoJustificacionSeguimiento.getJustificanteReintegro());

          // Actualizamos la entidad
          ProyectoPeriodoJustificacionSeguimiento returnValue = repository
              .save(proyectoPeriodoJustificacionSeguimientoExistente);

          log.debug("update(ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimiento) - end");
          return returnValue;
        }).orElseThrow(
            () -> new ProyectoPeriodoJustificacionSeguimientoNotFoundException(
                proyectoPeriodoJustificacionSeguimiento.getId()));
  }

  /**
   * Elimina las entidades {@link ProyectoPeriodoJustificacionSeguimiento} con el
   * proyectoPeriodoJustificacionId indicado.
   *
   * @param proyectoPeriodoJustificacionId Identificador de la entidad
   *                                       {@link ProyectoPeriodoJustificacion}.
   * @return el número de registros eliminados.
   */
  @Transactional
  public int deleteByProyectoPeriodoJustificacionId(Long proyectoPeriodoJustificacionId) {
    log.debug("deleteByProyectoPeriodoJustificacionId(Long proyectoPeriodoJustificacionId) - start");
    AssertHelper.idNotNull(proyectoPeriodoJustificacionId, ProyectoPeriodoJustificacionSeguimiento.class);

    int rowsDeleted = repository.deleteInBulkByProyectoPeriodoJustificacionId(proyectoPeriodoJustificacionId);

    log.debug("deleteByProyectoPeriodoJustificacionId(Long proyectoPeriodoJustificacionId) - end");
    return rowsDeleted;
  }
}
