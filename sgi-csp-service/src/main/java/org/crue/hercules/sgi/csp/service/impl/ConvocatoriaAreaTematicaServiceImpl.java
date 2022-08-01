package org.crue.hercules.sgi.csp.service.impl;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.AreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaAreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.repository.AreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaAreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaAreaTematicaSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ConvocatoriaAreaTematica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaAreaTematicaServiceImpl implements ConvocatoriaAreaTematicaService {

  private final ConvocatoriaAreaTematicaRepository repository;
  private final AreaTematicaRepository areaTematicaRepository;
  private final ConvocatoriaService convocatoriaService;

  public ConvocatoriaAreaTematicaServiceImpl(ConvocatoriaAreaTematicaRepository repository,
      AreaTematicaRepository areaTematicaRepository, ConvocatoriaService convocatoriaService) {
    this.repository = repository;
    this.areaTematicaRepository = areaTematicaRepository;
    this.convocatoriaService = convocatoriaService;
  }

  /**
   * Guarda la entidad {@link ConvocatoriaAreaTematica}.
   * 
   * @param convocatoriaAreaTematica la entidad {@link ConvocatoriaAreaTematica} a
   *                                 guardar.
   * @return ConvocatoriaAreaTematica la entidad {@link ConvocatoriaAreaTematica}
   *         persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaAreaTematica create(ConvocatoriaAreaTematica convocatoriaAreaTematica) {
    log.debug("create(ConvocatoriaAreaTematica convocatoriaAreaTematica) - start");

    Assert.isNull(convocatoriaAreaTematica.getId(), "Id tiene que ser null para crear ConvocatoriaAreaTematica");

    Assert.notNull(convocatoriaAreaTematica.getConvocatoriaId(),
        "Id Convocatoria no puede ser null para crear ConvocatoriaAreaTematica");

    Assert.notNull(convocatoriaAreaTematica.getAreaTematica().getId(),
        "Id AreaTematica no puede ser null para crear ConvocatoriaAreaTematica");

    // comprobar si convocatoria es modificable
    Assert.isTrue(
        convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaAreaTematica.getConvocatoriaId(), null,
            new String[] { "CSP-CON-E", "CSP-CON-C" }),
        "No se puede crear ConvocatoriaAreaTematica. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

    convocatoriaAreaTematica
        .setAreaTematica(areaTematicaRepository.findById(convocatoriaAreaTematica.getAreaTematica().getId())
            .orElseThrow(() -> new AreaTematicaNotFoundException(convocatoriaAreaTematica.getAreaTematica().getId())));

    Assert.isTrue(
        !repository.findByConvocatoriaIdAndAreaTematicaId(convocatoriaAreaTematica.getConvocatoriaId(),
            convocatoriaAreaTematica.getAreaTematica().getId()).isPresent(),
        "Ya existe una asociación activa para esa Convocatoria y AreaTematica");

    ConvocatoriaAreaTematica returnValue = repository.save(convocatoriaAreaTematica);

    log.debug("create(ConvocatoriaAreaTematica convocatoriaAreaTematica) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConvocatoriaAreaTematica}.
   *
   * @param convocatoriaAreaTematicaActualizar la entidad
   *                                           {@link ConvocatoriaAreaTematica} a
   *                                           actualizar.
   * @return la entidad {@link ConvocatoriaAreaTematica} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaAreaTematica update(ConvocatoriaAreaTematica convocatoriaAreaTematicaActualizar) {
    log.debug("update(ConvocatoriaAreaTematica convocatoriaAreaTematicaActualizar) - start");

    Assert.notNull(convocatoriaAreaTematicaActualizar.getId(),
        "ConvocatoriaAreaTematica id no puede ser null para actualizar un ConvocatoriaAreaTematica");

    return repository.findById(convocatoriaAreaTematicaActualizar.getId()).map(convocatoriaAreaTematica -> {

      convocatoriaAreaTematica.setObservaciones(convocatoriaAreaTematicaActualizar.getObservaciones());
      ConvocatoriaAreaTematica returnValue = repository.save(convocatoriaAreaTematicaActualizar);
      log.debug("update(ConvocatoriaAreaTematica convocatoriaAreaTematicaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaAreaTematicaNotFoundException(convocatoriaAreaTematicaActualizar.getId()));
  }

  /**
   * Elimina la {@link ConvocatoriaAreaTematica}.
   *
   * @param id Id del {@link ConvocatoriaAreaTematica}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ConvocatoriaAreaTematica id no puede ser null para eliminar un ConvocatoriaAreaTematica");

    Optional<ConvocatoriaAreaTematica> areaTematica = repository.findById(id);
    if (areaTematica.isPresent()) {
      // comprobar si convocatoria es modificable
      Assert.isTrue(
          convocatoriaService.isRegistradaConSolicitudesOProyectos(areaTematica.get().getConvocatoriaId(), null,
              new String[] { "CSP-CON-E" }),
          "No se puede eliminar ConvocatoriaAreaTematica. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
    } else {
      throw new ConvocatoriaAreaTematicaNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaAreaTematica} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaAreaTematica}.
   * @return la entidad {@link ConvocatoriaAreaTematica}.
   */
  @Override
  public ConvocatoriaAreaTematica findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaAreaTematica returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaAreaTematicaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene las {@link ConvocatoriaAreaTematica} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaAreaTematica} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaAreaTematica> findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");
    Specification<ConvocatoriaAreaTematica> specs = ConvocatoriaAreaTematicaSpecifications
        .byConvocatoriaId(convocatoriaId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaAreaTematica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
