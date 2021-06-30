package org.crue.hercules.sgi.csp.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.exceptions.ProyectoEntidadGestoraNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.repository.ProyectoEntidadGestoraRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEntidadGestoraSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadGestoraService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ProyectoEntidadGestora}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoEntidadGestoraServiceImpl implements ProyectoEntidadGestoraService {

  private final ProyectoEntidadGestoraRepository repository;
  private final ProyectoRepository proyectoRepository;

  public ProyectoEntidadGestoraServiceImpl(ProyectoEntidadGestoraRepository repository,
      ProyectoRepository proyectoRepository) {
    this.repository = repository;
    this.proyectoRepository = proyectoRepository;
  }

  /**
   * Guarda la entidad {@link ProyectoEntidadGestora}.
   * 
   * @param proyectoEntidadGestora la entidad {@link ProyectoEntidadGestora} a
   *                               guardar.
   * @return ProyectoEntidadGestora la entidad {@link ProyectoEntidadGestora}
   *         persistida.
   */
  @Override
  @Transactional
  public ProyectoEntidadGestora create(ProyectoEntidadGestora proyectoEntidadGestora) {
    log.debug("create(ProyectoEntidadGestora proyectoEntidadGestora) - start");

    Assert.isNull(proyectoEntidadGestora.getId(),
        "ProyectoEntidadGestora id tiene que ser null para crear un nuevo ProyectoEntidadGestora");

    this.validarRequeridosProyectoEntidadGestora(proyectoEntidadGestora);
    this.validarProyectoEntidadGestora(proyectoEntidadGestora, null);

    ProyectoEntidadGestora returnValue = repository.save(proyectoEntidadGestora);

    log.debug("create(ProyectoEntidadGestora proyectoEntidadGestora) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link ProyectoEntidadGestora}.
   * 
   * @param proyectoEntidadGestoraActualizar proyectoEntidadGestora
   *                                         {@link ProyectoEntidadGestora} con
   *                                         los datos actualizados.
   * @return {@link ProyectoEntidadGestora} actualizado.
   */
  @Override
  @Transactional
  public ProyectoEntidadGestora update(ProyectoEntidadGestora proyectoEntidadGestoraActualizar) {
    log.debug("update(ProyectoEntidadGestora proyectoEntidadGestoraActualizar) - start");

    Assert.notNull(proyectoEntidadGestoraActualizar.getId(),
        "ProyectoEntidadGestora id no puede ser null para actualizar un ProyectoEntidadGestora");

    this.validarRequeridosProyectoEntidadGestora(proyectoEntidadGestoraActualizar);

    return repository.findById(proyectoEntidadGestoraActualizar.getId()).map(proyectoEntidadGestora -> {

      this.validarProyectoEntidadGestora(proyectoEntidadGestoraActualizar, proyectoEntidadGestora);

      proyectoEntidadGestora.setEntidadRef(proyectoEntidadGestoraActualizar.getEntidadRef());
      ProyectoEntidadGestora returnValue = repository.save(proyectoEntidadGestora);
      log.debug("update(ProyectoEntidadGestora proyectoEntidadGestoraActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoEntidadGestoraNotFoundException(proyectoEntidadGestoraActualizar.getId()));
  }

  /**
   * Elimina la {@link ProyectoEntidadGestora}.
   *
   * @param id Id del {@link ProyectoEntidadGestora}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ProyectoEntidadGestora id no puede ser null para eliminar un ProyectoEntidadGestora");
    if (!repository.existsById(id)) {
      throw new ProyectoEntidadGestoraNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las {@link ProyectoEntidadGestora} para una {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoEntidadGestora} de la
   *         {@link Proyecto} paginadas.
   */
  public Page<ProyectoEntidadGestora> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoEntidadGestora> specs = ProyectoEntidadGestoraSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoEntidadGestora> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Realiza las validaciones necesarias para la creación y modificación de
   * {@link ProyectoEntidadGestora}
   * 
   * @param datosProyectoEntidadGestora
   * @param datosOriginales
   */
  private void validarProyectoEntidadGestora(ProyectoEntidadGestora datosProyectoEntidadGestora,
      ProyectoEntidadGestora datosOriginales) {
    log.debug(
        "validarProyectoEntidadGestora(ProyectoEntidadGestora datosProyectoEntidadGestora, ProyectoEntidadGestora datosOriginales) - start");

    // Se comprueba la existencia del proyecto
    Long proyectoId = datosProyectoEntidadGestora.getProyectoId();
    if (!proyectoRepository.existsById(proyectoId)) {
      throw new ProyectoNotFoundException(proyectoId);
    }

    // Comprobar si la entidad ya está asignada al proyecto
    if (datosOriginales == null) {
      Assert.isTrue(
          !repository.existsProyectoEntidadGestoraByProyectoIdAndEntidadRef(proyectoId,
              datosProyectoEntidadGestora.getEntidadRef()),
          "Ya existe una asociación activa para ese Proyecto y Entidad");
    } else {
      // Si se está modificando se excluye de la búsqueda el propio
      // ProyectoEntidadGestora
      Assert.isTrue(
          !repository.existsProyectoEntidadGestoraByIdNotAndProyectoIdAndEntidadRef(datosOriginales.getId(), proyectoId,
              datosProyectoEntidadGestora.getEntidadRef()),
          "Ya existe una asociación activa para ese Proyecto y Entidad");
    }

    log.debug(
        "validarProyectoEntidadGestora(ProyectoEntidadGestora datosProyectoEntidadGestora, ProyectoEntidadGestora datosOriginales) - end");
  }

  /**
   * Comprueba la presencia de los datos requeridos al crear o modificar
   * {@link ProyectoEntidadGestora}
   * 
   * @param datosProyectoEntidadGestora
   */
  private void validarRequeridosProyectoEntidadGestora(ProyectoEntidadGestora datosProyectoEntidadGestora) {
    log.debug("validarRequeridosProyectoEntidadGestora(ProyectoEntidadGestora datosProyectoEntidadGestora) - start");

    Assert.isTrue(datosProyectoEntidadGestora.getProyectoId() != null,
        "Id Proyecto no puede ser null para realizar la acción sobre ProyectoEntidadGestora");

    Assert.isTrue(StringUtils.isNotBlank(datosProyectoEntidadGestora.getEntidadRef()),
        "EntidadRef no puede ser null para realizar la acción sobre ProyectoEntidadGestora");

    log.debug("validarRequeridosProyectoEntidadGestora(ProyectoEntidadGestora datosProyectoEntidadGestora) - end");
  }

}
