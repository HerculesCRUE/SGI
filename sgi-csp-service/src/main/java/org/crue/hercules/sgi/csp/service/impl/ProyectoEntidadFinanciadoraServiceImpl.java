package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.FuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoEntidadFinanciadoraNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.repository.FuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.TipoFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEntidadFinanciadoraSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadFinanciadoraService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link ProyectoEntidadFinanciadora}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoEntidadFinanciadoraServiceImpl implements ProyectoEntidadFinanciadoraService {

  private final ProyectoEntidadFinanciadoraRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final FuenteFinanciacionRepository fuenteFinanciacionRepository;
  private final TipoFinanciacionRepository tipoFinanciacionRepository;

  public ProyectoEntidadFinanciadoraServiceImpl(
      ProyectoEntidadFinanciadoraRepository proyectoEntidadFinanciadoraRepository,
      ProyectoRepository proyectoRepository, FuenteFinanciacionRepository fuenteFinanciacionRepository,
      TipoFinanciacionRepository tipoFinanciacionRepository) {
    this.repository = proyectoEntidadFinanciadoraRepository;
    this.proyectoRepository = proyectoRepository;
    this.fuenteFinanciacionRepository = fuenteFinanciacionRepository;
    this.tipoFinanciacionRepository = tipoFinanciacionRepository;
  }

  /**
   * Guardar la entidad {@link ProyectoEntidadFinanciadora}.
   *
   * @param proyectoEntidadFinanciadora la entidad
   *                                    {@link ProyectoEntidadFinanciadora} a
   *                                    guardar.
   * @return la entidad {@link ProyectoEntidadFinanciadora} persistida.
   */
  @Override
  @Transactional
  public ProyectoEntidadFinanciadora create(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora) {
    log.debug("create(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora) - start");
    Assert.notNull(proyectoEntidadFinanciadora, "proyectoEntidadFinanciadora must not be null");

    Assert.isNull(proyectoEntidadFinanciadora.getId(),
        "ProyectoEntidadFinanciadora id tiene que ser null para crear un nuevo ProyectoEntidadFinanciadora");

    validateEditable(proyectoEntidadFinanciadora);
    validateData(proyectoEntidadFinanciadora);

    ProyectoEntidadFinanciadora returnValue = repository.save(proyectoEntidadFinanciadora);
    log.debug("create(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ProyectoEntidadFinanciadora}.
   *
   * @param proyectoEntidadFinanciadora la entidad
   *                                    {@link ProyectoEntidadFinanciadora} a
   *                                    actualizar.
   * @return la entidad {@link ProyectoEntidadFinanciadora} persistida.
   * @throws ProyectoEntidadFinanciadoraNotFoundException si no existe una entidad
   *                                                      con el id recibido
   */
  @Override
  @Transactional
  public ProyectoEntidadFinanciadora update(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora) {
    log.debug("update(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora) - start");
    Assert.notNull(proyectoEntidadFinanciadora, "proyectoEntidadFinanciadora must not be null");
    ProyectoEntidadFinanciadora old = repository.findById(proyectoEntidadFinanciadora.getId())
        .orElseThrow(() -> new ProyectoEntidadFinanciadoraNotFoundException(proyectoEntidadFinanciadora.getId()));

    validateEditable(proyectoEntidadFinanciadora);
    compareEditableProperties(old, proyectoEntidadFinanciadora);
    validateData(proyectoEntidadFinanciadora);

    // Copy only editable properties
    old.setFuenteFinanciacion(proyectoEntidadFinanciadora.getFuenteFinanciacion());
    old.setPorcentajeFinanciacion(proyectoEntidadFinanciadora.getPorcentajeFinanciacion());
    old.setTipoFinanciacion(proyectoEntidadFinanciadora.getTipoFinanciacion());
    old.setImporteFinanciacion(proyectoEntidadFinanciadora.getImporteFinanciacion());

    ProyectoEntidadFinanciadora returnValue = repository.save(old);
    log.debug("update(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora) - end");
    return returnValue;
  }

  /**
   * Desactiva la entidad {@link ProyectoEntidadFinanciadora}.
   *
   * @param id Id de {@link ProyectoEntidadFinanciadora}.
   * @throws ProyectoEntidadFinanciadoraNotFoundException si no existe una entidad
   *                                                      con el id recibido
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "id must not be null");

    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = repository.findById(id)
        .orElseThrow(() -> new ProyectoEntidadFinanciadoraNotFoundException(id));

    validateEditable(proyectoEntidadFinanciadora);

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ProyectoEntidadFinanciadora} por su id.
   *
   * @param id el id de la entidad {@link ProyectoEntidadFinanciadora}.
   * @return la entidad {@link ProyectoEntidadFinanciadora}.
   * @throws ProyectoEntidadFinanciadoraNotFoundException si no existe una entidad
   *                                                      con el id recibido
   */
  @Override
  public ProyectoEntidadFinanciadora findById(Long id) {
    log.debug("findById(Long id)  - start");
    Assert.notNull(id, "id must not be null");
    ProyectoEntidadFinanciadora returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoEntidadFinanciadoraNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ProyectoEntidadFinanciadora} para un {@link Proyecto}.
   *
   * @param idProyecto el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoEntidadFinanciadora} del
   *         {@link Proyecto} paginadas.
   */
  public Page<ProyectoEntidadFinanciadora> findAllByProyecto(Long idProyecto, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long idProyecto, String query, Pageable pageable) - start");
    Assert.notNull(idProyecto, "idProyecto must not be null");

    Specification<ProyectoEntidadFinanciadora> specs = ProyectoEntidadFinanciadoraSpecifications
        .byProyectoId(idProyecto).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoEntidadFinanciadora> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long idProyecto, String query, Pageable pageable) - end");
    return returnValue;
  }

  private void validateEditable(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora) {
    Assert.notNull(proyectoEntidadFinanciadora.getProyectoId(), "El id de proyecto no puede ser nulo");

    Proyecto proyecto = proyectoRepository.findById(proyectoEntidadFinanciadora.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoEntidadFinanciadora.getProyectoId()));

    Assert.isTrue(SgiSecurityContextHolder.hasAnyAuthorityForUO(new String[] { "CSP-PRO-C", "CSP-PRO-E" },
        proyecto.getUnidadGestionRef()), "La Unidad de Gestión no es gestionable por el usuario");
  }

  private void validateData(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora) {
    Assert.isTrue(
        proyectoEntidadFinanciadora.getPorcentajeFinanciacion() == null
            || proyectoEntidadFinanciadora.getPorcentajeFinanciacion().floatValue() >= 0,
        "PorcentajeFinanciacion no puede ser negativo");

    if (proyectoEntidadFinanciadora.getFuenteFinanciacion() != null) {
      if (proyectoEntidadFinanciadora.getFuenteFinanciacion().getId() == null) {
        proyectoEntidadFinanciadora.setFuenteFinanciacion(null);
      } else {
        proyectoEntidadFinanciadora.setFuenteFinanciacion(
            fuenteFinanciacionRepository.findById(proyectoEntidadFinanciadora.getFuenteFinanciacion().getId())
                .orElseThrow(() -> new FuenteFinanciacionNotFoundException(
                    proyectoEntidadFinanciadora.getFuenteFinanciacion().getId())));
        Assert.isTrue(proyectoEntidadFinanciadora.getFuenteFinanciacion().getActivo(),
            "La FuenteFinanciacion debe estar Activo");
      }
    }

    if (proyectoEntidadFinanciadora.getTipoFinanciacion() != null) {
      if (proyectoEntidadFinanciadora.getTipoFinanciacion().getId() == null) {
        proyectoEntidadFinanciadora.setTipoFinanciacion(null);
      } else {
        proyectoEntidadFinanciadora.setTipoFinanciacion(
            tipoFinanciacionRepository.findById(proyectoEntidadFinanciadora.getTipoFinanciacion().getId())
                .orElseThrow(() -> new TipoFinanciacionNotFoundException(
                    proyectoEntidadFinanciadora.getTipoFinanciacion().getId())));
        Assert.isTrue(proyectoEntidadFinanciadora.getTipoFinanciacion().getActivo(),
            "El TipoFinanciacion debe estar Activo");
      }
    }
  }

  private void compareEditableProperties(ProyectoEntidadFinanciadora current, ProyectoEntidadFinanciadora update) {
    Assert.isTrue(current.getProyectoId().equals(update.getProyectoId()),
        "El atributo proyectoId no se puede modificar");
    Assert.isTrue(current.getAjena().equals(update.getAjena()), "El atributo ajena no se puede modificar");
    Assert.isTrue(current.getEntidadRef().equals(update.getEntidadRef()),
        "El atributo entidadRef no se puede modificar");

  }

}
