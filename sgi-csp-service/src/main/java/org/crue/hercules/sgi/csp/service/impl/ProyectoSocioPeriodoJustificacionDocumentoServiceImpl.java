package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoJustificacionDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoSocioPeriodoJustificacionDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionDocumentoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link ProyectoSocioPeriodoJustificacionDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoSocioPeriodoJustificacionDocumentoServiceImpl
    implements ProyectoSocioPeriodoJustificacionDocumentoService {

  private final ProyectoSocioPeriodoJustificacionDocumentoRepository repository;
  private final ProyectoSocioPeriodoJustificacionRepository proyectoSocioRepository;

  /**
   * {@link ProyectoSocioPeriodoJustificacionDocumentoServiceImpl}.
   * 
   * @param proyectoSocioPeriodoJustificacionRepository {@link ProyectoSocioPeriodoJustificacionDocumentoRepository}.
   * @param proyectoSocioRepository                     {@link ProyectoSocioPeriodoJustificacionRepository}.
   */
  public ProyectoSocioPeriodoJustificacionDocumentoServiceImpl(
      ProyectoSocioPeriodoJustificacionDocumentoRepository proyectoSocioPeriodoJustificacionRepository,
      ProyectoSocioPeriodoJustificacionRepository proyectoSocioRepository) {
    this.repository = proyectoSocioPeriodoJustificacionRepository;
    this.proyectoSocioRepository = proyectoSocioRepository;
  }

  /**
   * Actualiza el listado de {@link ProyectoSocioPeriodoJustificacionDocumento} de
   * la {@link ProyectoSocioPeriodoJustificacion} con el listado
   * proyectoSocioPeriodoJustificaciones añadiendo, editando o eliminando los
   * elementos segun proceda.
   *
   * @param proyectoSocioId                     Id de la
   *                                            {@link ProyectoSocioPeriodoJustificacion}.
   * @param proyectoSocioPeriodoJustificaciones lista con los nuevos
   *                                            {@link ProyectoSocioPeriodoJustificacionDocumento}
   *                                            a guardar.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacionDocumento}
   *         persistida.
   */
  @Override
  @Transactional
  public List<ProyectoSocioPeriodoJustificacionDocumento> update(Long proyectoSocioId,
      List<ProyectoSocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificaciones) {
    log.debug(
        "update(Long proyectoSocioId, List<ProyectoSocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificaciones) - start");

    proyectoSocioRepository.findById(proyectoSocioId)
        .orElseThrow(() -> new ProyectoSocioPeriodoJustificacionNotFoundException(proyectoSocioId));

    List<ProyectoSocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificacionesBD = repository
        .findAllByProyectoSocioPeriodoJustificacionId(proyectoSocioId);

    // Periodos eliminados
    List<ProyectoSocioPeriodoJustificacionDocumento> periodoJustificacionesEliminar = proyectoSocioPeriodoJustificacionesBD
        .stream()
        .filter(periodo -> !proyectoSocioPeriodoJustificaciones.stream()
            .map(ProyectoSocioPeriodoJustificacionDocumento::getId).anyMatch(id -> id == periodo.getId()))
        .collect(Collectors.toList());

    if (!periodoJustificacionesEliminar.isEmpty()) {
      repository.deleteAll(periodoJustificacionesEliminar);
    }

    for (ProyectoSocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacionDocumento : proyectoSocioPeriodoJustificaciones) {
      // Si tiene id se valida que exista y que tenga el proyecto socio de la que se
      // estan actualizando los periodos
      if (proyectoSocioPeriodoJustificacionDocumento.getId() != null) {
        ProyectoSocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacionDocumentoBD = proyectoSocioPeriodoJustificacionesBD
            .stream().filter(periodo -> periodo.getId() == proyectoSocioPeriodoJustificacionDocumento.getId())
            .findFirst().orElseThrow(() -> new ProyectoSocioPeriodoJustificacionDocumentoNotFoundException(
                proyectoSocioPeriodoJustificacionDocumento.getId()));

        Assert.isTrue(
            proyectoSocioPeriodoJustificacionDocumentoBD
                .getProyectoSocioPeriodoJustificacionId() == proyectoSocioPeriodoJustificacionDocumento
                    .getProyectoSocioPeriodoJustificacionId(),
            "No se puede modificar el proyecto socio del ProyectoSocioPeriodoJustificacionDocumento");
      }
    }

    List<ProyectoSocioPeriodoJustificacionDocumento> returnValue = repository
        .saveAll(proyectoSocioPeriodoJustificaciones);
    log.debug(
        "update(Long proyectoSocioId, List<ProyectoSocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificaciones) - end");

    return returnValue;
  }

  /**
   * Obtiene {@link ProyectoSocioPeriodoJustificacionDocumento} por su id.
   *
   * @param id el id de la entidad
   *           {@link ProyectoSocioPeriodoJustificacionDocumento}.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacionDocumento}.
   */
  @Override
  public ProyectoSocioPeriodoJustificacionDocumento findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoSocioPeriodoJustificacionDocumento returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoSocioPeriodoJustificacionDocumentoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ProyectoSocioPeriodoJustificacionDocumento} para una
   * {@link ProyectoSocioPeriodoJustificacion}.
   *
   * @param proyectoSocioId el id de la {@link ProyectoSocioPeriodoJustificacion}.
   * @param query           la información del filtro.
   * @param pageable        la información de la paginación.
   * @return la lista de entidades
   *         {@link ProyectoSocioPeriodoJustificacionDocumento} de la
   *         {@link ProyectoSocioPeriodoJustificacion} paginadas.
   */
  public Page<ProyectoSocioPeriodoJustificacionDocumento> findAllByProyectoSocioPeriodoJustificacion(
      Long proyectoSocioId, String query, Pageable pageable) {
    log.debug(
        "findAllByProyectoSocioPeriodoJustificacion(Long proyectoSocioId, String query, Pageable pageable) - start");
    Specification<ProyectoSocioPeriodoJustificacionDocumento> specs = ProyectoSocioPeriodoJustificacionDocumentoSpecifications
        .byProyectoSocioPeriodoJustificacionId(proyectoSocioId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoSocioPeriodoJustificacionDocumento> returnValue = repository.findAll(specs, pageable);
    log.debug(
        "findAllByProyectoSocioPeriodoJustificacion(Long proyectoSocioId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ProyectoSocioPeriodoJustificacionDocumento} para una
   * {@link Proyecto}.
   *
   * @param idProyecto el id de la {@link Proyecto}.
   * @return la lista de entidades
   *         {@link ProyectoSocioPeriodoJustificacionDocumento} de la
   *         {@link Proyecto} paginadas.
   */
  @Override
  public List<ProyectoSocioPeriodoJustificacionDocumento> findAllByProyecto(Long idProyecto) {
    log.debug("findAllByProyecto(Long idProyecto) - start");

    Specification<ProyectoSocioPeriodoJustificacionDocumento> specByProyecto = ProyectoSocioPeriodoJustificacionDocumentoSpecifications
        .byProyectoId(idProyecto);

    Specification<ProyectoSocioPeriodoJustificacionDocumento> specs = Specification.where(specByProyecto);

    List<ProyectoSocioPeriodoJustificacionDocumento> returnValue = repository.findAll(specs);
    log.debug("findAllByProyecto(Long idProyecto) - end");
    return returnValue;
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoSocioPeriodoJustificacionDocumento}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsByProyecto(Long proyectoId) {
    return repository.existsByProyectoSocioPeriodoJustificacionProyectoSocioProyectoId(proyectoId);
  }

}
