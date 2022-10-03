package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.exceptions.ProyectoSeguimientoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoSeguimientoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoSeguimientoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoSeguimientoJustificacionSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ProyectoSeguimientoJustificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProyectoSeguimientoJustificacionService {

  private final ProyectoSeguimientoJustificacionRepository repository;

  /**
   * Obtener todas las entidades {@link ProyectoSeguimientoJustificacion}
   * pertenecientes al ProyectoSgeRef paginadas y/o filtradas.
   *
   * @param proyectoSgeRef el identificador de un ProyectoSGE
   * @param pageable       la información de la paginación.
   * @param query          la información del filtro.
   * @return la lista de entidades {@link ProyectoSeguimientoJustificacion}
   *         paginadas
   *         y/o filtradas.
   */
  public Page<ProyectoSeguimientoJustificacion> findAllByProyectoSgeRef(String proyectoSgeRef, String query,
      Pageable pageable) {
    log.debug("findAllByProyectoSgeRef(String proyectoSgeRef, String query, Pageable pageable) - start");
    Specification<ProyectoSeguimientoJustificacion> specs = ProyectoSeguimientoJustificacionSpecifications
        .byProyectoProyectoSgeProyectoSgeRef(proyectoSgeRef).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoSeguimientoJustificacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyectoSgeRef(String proyectoSgeRef, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Guardar una nueva entidad {@link ProyectoSeguimientoJustificacion}.
   *
   * @param proyectoSeguimientoJustificacion la entidad
   *                                         {@link ProyectoSeguimientoJustificacion}
   *                                         a guardar.
   * @return la entidad {@link ProyectoSeguimientoJustificacion} persistida.
   */
  @Transactional
  public ProyectoSeguimientoJustificacion create(ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacion) {
    log.debug("create(ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacion) - start");
    AssertHelper.idIsNull(proyectoSeguimientoJustificacion.getId(), ProyectoSeguimientoJustificacion.class);
    AssertHelper.entityNotNull(proyectoSeguimientoJustificacion.getProyectoProyectoSge(), ProyectoProyectoSge.class,
        ProyectoProyectoSge.class);
    AssertHelper.idNotNull(proyectoSeguimientoJustificacion.getProyectoProyectoSge().getId(),
        ProyectoProyectoSge.class);

    ProyectoSeguimientoJustificacion returnValue = repository.save(proyectoSeguimientoJustificacion);

    log.debug("create(ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacion) - end");
    return returnValue;
  }

  /**
   * Actualiza una entidad {@link ProyectoSeguimientoJustificacion}.
   *
   * @param proyectoSeguimientoJustificacion la entidad
   *                                         {@link ProyectoSeguimientoJustificacion}
   *                                         a actualizar.
   * @return la entidad {@link ProyectoSeguimientoJustificacion} persistida.
   */
  @Transactional
  public ProyectoSeguimientoJustificacion update(ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacion) {
    log.debug("update(ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacion) - start");
    AssertHelper.idNotNull(proyectoSeguimientoJustificacion.getId(), ProyectoSeguimientoJustificacion.class);

    return repository.findById(proyectoSeguimientoJustificacion.getId())
        .map(proyectoSeguimientoJustificacionExistente -> {

          // // Establecemos los campos actualizables con los recibidos
          proyectoSeguimientoJustificacionExistente
              .setFechaReintegro(proyectoSeguimientoJustificacion.getFechaReintegro());
          proyectoSeguimientoJustificacionExistente
              .setImporteAceptado(proyectoSeguimientoJustificacion.getImporteAceptado());
          proyectoSeguimientoJustificacionExistente
              .setImporteAceptadoCD(proyectoSeguimientoJustificacion.getImporteAceptadoCD());
          proyectoSeguimientoJustificacionExistente
              .setImporteAceptadoCI(proyectoSeguimientoJustificacion.getImporteAceptadoCI());
          proyectoSeguimientoJustificacionExistente
              .setImporteAlegado(proyectoSeguimientoJustificacion.getImporteAlegado());
          proyectoSeguimientoJustificacionExistente
              .setImporteAlegadoCD(proyectoSeguimientoJustificacion.getImporteAlegadoCD());
          proyectoSeguimientoJustificacionExistente
              .setImporteAlegadoCI(proyectoSeguimientoJustificacion.getImporteAlegadoCI());
          proyectoSeguimientoJustificacionExistente
              .setImporteJustificado(proyectoSeguimientoJustificacion.getImporteJustificado());
          proyectoSeguimientoJustificacionExistente
              .setImporteJustificadoCD(proyectoSeguimientoJustificacion.getImporteJustificadoCD());
          proyectoSeguimientoJustificacionExistente
              .setImporteJustificadoCI(proyectoSeguimientoJustificacion.getImporteJustificadoCI());
          proyectoSeguimientoJustificacionExistente
              .setImporteNoEjecutado(proyectoSeguimientoJustificacion.getImporteNoEjecutado());
          proyectoSeguimientoJustificacionExistente
              .setImporteNoEjecutadoCD(proyectoSeguimientoJustificacion.getImporteNoEjecutadoCD());
          proyectoSeguimientoJustificacionExistente
              .setImporteNoEjecutadoCI(proyectoSeguimientoJustificacion.getImporteNoEjecutadoCI());
          proyectoSeguimientoJustificacionExistente
              .setImporteRechazado(proyectoSeguimientoJustificacion.getImporteRechazado());
          proyectoSeguimientoJustificacionExistente
              .setImporteRechazadoCD(proyectoSeguimientoJustificacion.getImporteRechazadoCD());
          proyectoSeguimientoJustificacionExistente
              .setImporteRechazadoCI(proyectoSeguimientoJustificacion.getImporteRechazadoCI());
          proyectoSeguimientoJustificacionExistente
              .setImporteReintegrado(proyectoSeguimientoJustificacion.getImporteReintegrado());
          proyectoSeguimientoJustificacionExistente
              .setImporteReintegradoCD(proyectoSeguimientoJustificacion.getImporteReintegradoCD());
          proyectoSeguimientoJustificacionExistente
              .setImporteReintegradoCI(proyectoSeguimientoJustificacion.getImporteReintegradoCI());
          proyectoSeguimientoJustificacionExistente
              .setImporteReintegrar(proyectoSeguimientoJustificacion.getImporteReintegrar());
          proyectoSeguimientoJustificacionExistente
              .setImporteReintegrarCD(proyectoSeguimientoJustificacion.getImporteReintegrarCD());
          proyectoSeguimientoJustificacionExistente
              .setImporteReintegrarCI(proyectoSeguimientoJustificacion.getImporteReintegrarCI());
          proyectoSeguimientoJustificacionExistente
              .setInteresesReintegrados(proyectoSeguimientoJustificacion.getInteresesReintegrados());
          proyectoSeguimientoJustificacionExistente
              .setInteresesReintegrar(proyectoSeguimientoJustificacion.getInteresesReintegrar());
          proyectoSeguimientoJustificacionExistente
              .setJustificanteReintegro(proyectoSeguimientoJustificacion.getJustificanteReintegro());

          // Actualizamos la entidad
          ProyectoSeguimientoJustificacion returnValue = repository.save(proyectoSeguimientoJustificacionExistente);

          log.debug("update(ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacion) - end");
          return returnValue;
        }).orElseThrow(
            () -> new ProyectoSeguimientoJustificacionNotFoundException(proyectoSeguimientoJustificacion.getId()));
  }
}
