package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoSeguimientoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoSeguimientoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoPeriodoSeguimientoDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoDocumentoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion
 * {@link ProyectoPeriodoSeguimientoDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoPeriodoSeguimientoDocumentoServiceImpl implements ProyectoPeriodoSeguimientoDocumentoService {

  private final ProyectoPeriodoSeguimientoDocumentoRepository repository;

  public ProyectoPeriodoSeguimientoDocumentoServiceImpl(ProyectoPeriodoSeguimientoDocumentoRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link ProyectoPeriodoSeguimientoDocumento}.
   * 
   * @param proyectoPeriodoSeguimientoDocumento la entidad
   *                                            {@link ProyectoPeriodoSeguimientoDocumento}
   *                                            a guardar.
   * @return ProyectoPeriodoSeguimientoDocumento la entidad
   *         {@link ProyectoPeriodoSeguimientoDocumento} persistida.
   */
  @Override
  @Transactional
  public ProyectoPeriodoSeguimientoDocumento create(
      ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento) {
    log.debug("create(ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento) - start");

    Assert.isNull(proyectoPeriodoSeguimientoDocumento.getId(),
        "Id tiene que ser null para crear la ProyectoPeriodoSeguimientoDocumento");

    Assert.notNull(proyectoPeriodoSeguimientoDocumento.getProyectoPeriodoSeguimientoId(),
        "ProyectoPeriodoSeguimiento id no puede ser null para crear una ProyectoPeriodoSeguimientoModalidad");

    Assert.notNull(proyectoPeriodoSeguimientoDocumento.getNombre(),
        "Nombre documento no puede ser null para crear la ProyectoPeriodoSeguimientoDocumento");
    Assert.notNull(proyectoPeriodoSeguimientoDocumento.getDocumentoRef(),
        "La referencia del documento no puede ser null para crear la ProyectoPeriodoSeguimientoDocumento");

    ProyectoPeriodoSeguimientoDocumento returnValue = repository.save(proyectoPeriodoSeguimientoDocumento);

    log.debug("create(ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link ProyectoPeriodoSeguimientoDocumento}.
   * 
   * @param proyectoPeriodoSeguimientoDocumento rolSocioActualizar
   *                                            {@link ProyectoPeriodoSeguimientoDocumento}
   *                                            con los datos actualizados.
   * @return {@link ProyectoPeriodoSeguimientoDocumento} actualizado.
   */
  @Override
  @Transactional
  public ProyectoPeriodoSeguimientoDocumento update(
      ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento) {
    log.debug("update(ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento) - start");

    Assert.notNull(proyectoPeriodoSeguimientoDocumento.getId(),
        "Id no puede ser null para actualizar ProyectoPeriodoSeguimientoDocumento");

    Assert.notNull(proyectoPeriodoSeguimientoDocumento.getProyectoPeriodoSeguimientoId(),
        "ProyectoPeriodoSeguimiento id no puede ser null para crear una ProyectoPeriodoSeguimientoModalidad");

    Assert.notNull(proyectoPeriodoSeguimientoDocumento.getNombre(),
        "Nombre documento no puede ser null para actualizar la ProyectoPeriodoSeguimientoDocumento");
    Assert.notNull(proyectoPeriodoSeguimientoDocumento.getDocumentoRef(),
        "La referencia del documento no puede ser null para actualizar la ProyectoPeriodoSeguimientoDocumento");

    return repository.findById(proyectoPeriodoSeguimientoDocumento.getId())
        .map((proyectoPeriodoSeguimientoDocumentoExistente) -> {

          proyectoPeriodoSeguimientoDocumentoExistente
              .setComentario(proyectoPeriodoSeguimientoDocumento.getComentario());
          proyectoPeriodoSeguimientoDocumentoExistente
              .setDocumentoRef(proyectoPeriodoSeguimientoDocumento.getDocumentoRef());
          proyectoPeriodoSeguimientoDocumentoExistente
              .setTipoDocumento(proyectoPeriodoSeguimientoDocumento.getTipoDocumento());
          proyectoPeriodoSeguimientoDocumentoExistente.setNombre(proyectoPeriodoSeguimientoDocumento.getNombre());

          ProyectoPeriodoSeguimientoDocumento returnValue = repository
              .save(proyectoPeriodoSeguimientoDocumentoExistente);

          log.debug("update(ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento) - end");
          return returnValue;
        }).orElseThrow(() -> new ProyectoPeriodoSeguimientoDocumentoNotFoundException(
            proyectoPeriodoSeguimientoDocumento.getId()));
  }

  /**
   * Obtiene una entidad {@link ProyectoPeriodoSeguimientoDocumento} por id.
   * 
   * @param id Identificador de la entidad
   *           {@link ProyectoPeriodoSeguimientoDocumento}.
   * @return ProyectoPeriodoSeguimientoDocumento la entidad
   *         {@link ProyectoPeriodoSeguimientoDocumento}.
   */
  @Override
  public ProyectoPeriodoSeguimientoDocumento findById(Long id) {
    log.debug("findById(Long id) - start");
    final ProyectoPeriodoSeguimientoDocumento returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoPeriodoSeguimientoDocumentoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link ProyectoPeriodoSeguimientoDocumento}.
   *
   * @param id Id del {@link ProyectoPeriodoSeguimientoDocumento}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "ProyectoPeriodoSeguimientoDocumento id no puede ser null para eliminar un ProyectoPeriodoSeguimientoDocumento");
    if (!repository.existsById(id)) {
      throw new ProyectoPeriodoSeguimientoDocumentoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene las {@link ProyectoPeriodoSeguimientoDocumento} para una
   * {@link ProyectoPeriodoSeguimiento}.
   *
   * @param proyectoPeriodoSeguimientoId el id de la
   *                                     {@link ProyectoPeriodoSeguimiento}.
   * @param query                        la información del filtro.
   * @param paging                       la información de la paginación.
   * @return la lista de entidades {@link ProyectoPeriodoSeguimientoDocumento} de
   *         la {@link ProyectoPeriodoSeguimiento} paginadas.
   */
  @Override
  public Page<ProyectoPeriodoSeguimientoDocumento> findAllByProyectoPeriodoSeguimiento(
      Long proyectoPeriodoSeguimientoId, String query, Pageable paging) {
    log.debug("findAllByProyectoPeriodoSeguimiento(Long solicitudId, String query, Pageable paging) - start");

    Specification<ProyectoPeriodoSeguimientoDocumento> specs = ProyectoPeriodoSeguimientoDocumentoSpecifications
        .byProyectoPeriodoSeguimientoId(proyectoPeriodoSeguimientoId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoPeriodoSeguimientoDocumento> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByProyectoPeriodoSeguimiento(Long solicitudId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link ProyectoPeriodoSeguimientoDocumento} por
   * id de {@link ProyectoPeriodoSeguimiento}
   *
   * @param id el id de la entidad {@link ProyectoPeriodoSeguimiento}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsByProyectoPeriodoSeguimiento(final Long id) {
    log.debug("existsByProyectoPeriodoSeguimiento(final Long id)  - start", id);
    final boolean existe = repository.existsByProyectoPeriodoSeguimientoId(id);
    log.debug("existsByProyectoPeriodoSeguimiento(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene las {@link ProyectoPeriodoSeguimientoDocumento} para una
   * {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @return la lista de entidades {@link ProyectoPeriodoSeguimientoDocumento} de
   *         la {@link Proyecto}.
   */
  @Override
  public List<ProyectoPeriodoSeguimientoDocumento> findAllByProyecto(Long proyectoId) {
    log.debug("findAllByProyecto(Long solicitudId) - start");

    Specification<ProyectoPeriodoSeguimientoDocumento> specByProyecto = ProyectoPeriodoSeguimientoDocumentoSpecifications
        .byProyectoId(proyectoId);
    Specification<ProyectoPeriodoSeguimientoDocumento> specs = Specification.where(specByProyecto);

    List<ProyectoPeriodoSeguimientoDocumento> returnValue = repository.findAll(specs);

    log.debug("findAllByProyecto(Long solicitudId) - end");
    return returnValue;
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoPeriodoSeguimientoDocumento}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsByProyecto(Long proyectoId) {
    return repository.existsByProyectoPeriodoSeguimientoProyectoId(proyectoId);
  }
}
