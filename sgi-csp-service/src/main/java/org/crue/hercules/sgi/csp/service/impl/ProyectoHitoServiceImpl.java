package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ProyectoHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.repository.ModeloTipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoHitoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoHitoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoHitoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProyectoHito}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoHitoServiceImpl implements ProyectoHitoService {

  private final ProyectoHitoRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final ModeloTipoHitoRepository modeloTipoHitoRepository;

  public ProyectoHitoServiceImpl(ProyectoHitoRepository proyectoHitoRepository, ProyectoRepository proyectoRepository,
      ModeloTipoHitoRepository modeloTipoHitoRepository) {
    this.repository = proyectoHitoRepository;
    this.proyectoRepository = proyectoRepository;
    this.modeloTipoHitoRepository = modeloTipoHitoRepository;
  }

  /**
   * Guarda la entidad {@link ProyectoHito}.
   * 
   * @param proyectoHito la entidad {@link ProyectoHito} a guardar.
   * @return ProyectoHito la entidad {@link ProyectoHito} persistida.
   */
  @Override
  @Transactional
  public ProyectoHito create(ProyectoHito proyectoHito) {
    log.debug("create(ProyectoHito ProyectoHito) - start");

    Assert.isNull(proyectoHito.getId(), "ProyectoHito id tiene que ser null para crear un nuevo ProyectoHito");
    this.validarRequeridosProyectoHito(proyectoHito);
    this.validarProyectoHito(proyectoHito, null);

    ProyectoHito returnValue = repository.save(proyectoHito);

    log.debug("create(ProyectoHito ProyectoHito) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ProyectoHito}.
   * 
   * @param proyectoHitoActualizar la entidad {@link ProyectoHito} a guardar.
   * @return ProyectoHito la entidad {@link ProyectoHito} persistida.
   */
  @Override
  @Transactional
  public ProyectoHito update(ProyectoHito proyectoHitoActualizar) {
    log.debug("update(ProyectoHito ProyectoHitoActualizar) - start");

    Assert.notNull(proyectoHitoActualizar.getId(), "ProyectoHito id no puede ser null para actualizar un ProyectoHito");
    this.validarRequeridosProyectoHito(proyectoHitoActualizar);

    return repository.findById(proyectoHitoActualizar.getId()).map(proyectoHito -> {

      validarProyectoHito(proyectoHitoActualizar, proyectoHito);

      proyectoHito.setFecha(proyectoHitoActualizar.getFecha());
      proyectoHito.setComentario(proyectoHitoActualizar.getComentario());
      proyectoHito.setTipoHito(proyectoHitoActualizar.getTipoHito());
      proyectoHito.setGeneraAviso(proyectoHitoActualizar.getGeneraAviso());

      ProyectoHito returnValue = repository.save(proyectoHito);
      log.debug("update(ProyectoHito ProyectoHitoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoHitoNotFoundException(proyectoHitoActualizar.getId()));

  }

  /**
   * Elimina la {@link ProyectoHito}.
   *
   * @param id Id del {@link ProyectoHito}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ProyectoHito id no puede ser null para eliminar un ProyectoHito");
    if (!repository.existsById(id)) {
      throw new ProyectoHitoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene {@link ProyectoHito} por su id.
   *
   * @param id el id de la entidad {@link ProyectoHito}.
   * @return la entidad {@link ProyectoHito}.
   */
  @Override
  public ProyectoHito findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoHito returnValue = repository.findById(id).orElseThrow(() -> new ProyectoHitoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene los {@link ProyectoHito} para un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoHito} del {@link Proyecto}
   *         paginadas.
   */
  @Override
  public Page<ProyectoHito> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoHito> specs = ProyectoHitoSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoHito> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Realiza las validaciones necesarias para la creación y modificación de
   * {@link ProyectoHito}
   * 
   * @param datosProyectoHito
   * @param datosOriginales
   */
  private void validarProyectoHito(ProyectoHito datosProyectoHito, ProyectoHito datosOriginales) {
    log.debug("validarProyectoHito(ProyectoHito datosProyectoHito, ProyectoHito datosOriginales) - start");

    // Se comprueba la existencia del proyecto
    Long proyectoId = datosProyectoHito.getProyectoId();
    if (!proyectoRepository.existsById(proyectoId)) {
      throw new ProyectoNotFoundException(proyectoId);
    }
    // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
    Optional<ModeloEjecucion> modeloEjecucion = proyectoRepository.getModeloEjecucion(proyectoId);
    Long modeloEjecucionId = modeloEjecucion.isPresent() ? modeloEjecucion.get().getId() : null;

    // TipoHito
    Optional<ModeloTipoHito> modeloTipoHito = modeloTipoHitoRepository
        .findByModeloEjecucionIdAndTipoHitoId(modeloEjecucionId, datosProyectoHito.getTipoHito().getId());

    // Está asignado al ModeloEjecucion
    Assert.isTrue(modeloTipoHito.isPresent(),
        "TipoHito '" + datosProyectoHito.getTipoHito().getNombre() + "' no disponible para el ModeloEjecucion '"
            + ((modeloEjecucion.isPresent()) ? modeloEjecucion.get().getNombre() : "Proyecto sin modelo asignado")
            + "'");

    // La asignación al ModeloEjecucion está activa
    Assert.isTrue(modeloTipoHito.get().getActivo(), "ModeloTipoHito '" + modeloTipoHito.get().getTipoHito().getNombre()
        + "' no está activo para el ModeloEjecucion '" + modeloTipoHito.get().getModeloEjecucion().getNombre() + "'");

    // El TipoHito está activo
    Assert.isTrue(modeloTipoHito.get().getTipoHito().getActivo(),
        "TipoHito '" + modeloTipoHito.get().getTipoHito().getNombre() + "' no está activo");

    datosProyectoHito.setTipoHito(modeloTipoHito.get().getTipoHito());

    // Si en el campo Fecha se ha indicado una fecha ya pasada, el campo "generar
    // aviso" tomará el valor false, y no será editable.
    if (datosProyectoHito.getFecha().isBefore(Instant.now())) {
      datosProyectoHito.setGeneraAviso(false);
    }

    repository.findByProyectoIdAndFechaAndTipoHitoId(datosProyectoHito.getProyectoId(), datosProyectoHito.getFecha(),
        datosProyectoHito.getTipoHito().getId()).ifPresent((convocatoriaHitoExistente) -> {
          Assert.isTrue(datosProyectoHito.getId() == convocatoriaHitoExistente.getId(),
              "Ya existe un Hito con el mismo tipo en esa fecha");
        });

    log.debug("validarProyectoHito(ProyectoHito datosProyectoHito, ProyectoHito datosOriginales) - end");
  }

  /**
   * Comprueba la presencia de los datos requeridos al crear o modificar
   * {@link ProyectoHito}
   * 
   * @param datosProyectoHito
   */
  private void validarRequeridosProyectoHito(ProyectoHito datosProyectoHito) {
    log.debug("validarRequeridosProyectoHito(ProyectoHito datosProyectoHito) - start");

    Assert.isTrue(datosProyectoHito.getProyectoId() != null,
        "Id Proyecto no puede ser null para realizar la acción sobre ProyectoHito");

    Assert.isTrue(datosProyectoHito.getTipoHito() != null && datosProyectoHito.getTipoHito().getId() != null,
        "Id Tipo Hito no puede ser null para realizar la acción sobre ProyectoHito");

    Assert.notNull(datosProyectoHito.getFecha(), "Fecha no puede ser null para realizar la acción sobre ProyectoHito");

    log.debug("validarRequeridosProyectoHito(ProyectoHito datosProyectoHito) - end");

  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoHito}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return si existe o no el proyecto
   */
  @Override
  public boolean existsByProyecto(Long proyectoId) {
    return repository.existsByProyectoId(proyectoId);
  }

}
