package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPartidaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPartidaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaPartidaSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPartidaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ConvocatoriaPartida}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaPartidaServiceImpl implements ConvocatoriaPartidaService {

  private final ConvocatoriaPartidaRepository repository;
  private final ConfiguracionRepository configuracionRepository;

  public ConvocatoriaPartidaServiceImpl(ConvocatoriaPartidaRepository repository,
      ConfiguracionRepository configuracionRepository) {
    this.repository = repository;
    this.configuracionRepository = configuracionRepository;
  }

  /**
   * Guarda la entidad {@link ConvocatoriaPartida}.
   * 
   * @param convocatoriaPartida la entidad {@link ConvocatoriaPartida} a guardar.
   * @return ConvocatoriaPartida la entidad {@link ConvocatoriaPartida}
   *         persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaPartida create(ConvocatoriaPartida convocatoriaPartida) {
    log.debug("create(ConvocatoriaPartida convocatoriaPartida) - start");

    Assert.isNull(convocatoriaPartida.getId(), "Id tiene que ser null para crear ConvocatoriaPartida");

    Assert.notNull(convocatoriaPartida.getConvocatoriaId(),
        "Id Convocatoria no puede ser null para crear ConvocatoriaPartida");
    this.validate(convocatoriaPartida);

    // TODO Incluir restricción de convocatorias asociadas a proyectos con
    // presupuesto

    ConvocatoriaPartida returnValue = repository.save(convocatoriaPartida);

    log.debug("create(ConvocatoriaPartida convocatoriaPartida) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConvocatoriaPartida}.
   *
   * @param convocatoriaPartidaActualizar la entidad {@link ConvocatoriaPartida} a
   *                                      actualizar.
   * @return la entidad {@link ConvocatoriaPartida} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaPartida update(ConvocatoriaPartida convocatoriaPartidaActualizar) {
    log.debug("update(ConvocatoriaPartida convocatoriaPartidaActualizar) - start");

    Assert.notNull(convocatoriaPartidaActualizar.getId(),
        "ConvocatoriaPartida id no puede ser null para actualizar un ConvocatoriaPartida");
    this.validate(convocatoriaPartidaActualizar);

    // TODO Incluir restricción de convocatorias asociadas a proyectos con
    // presupuesto

    return repository.findById(convocatoriaPartidaActualizar.getId()).map(convocatoriaPartida -> {

      ConvocatoriaPartida returnValue = repository.save(convocatoriaPartidaActualizar);
      log.debug("update(ConvocatoriaPartida convocatoriaPartidaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaPartidaNotFoundException(convocatoriaPartidaActualizar.getId()));
  }

  /**
   * Elimina la {@link ConvocatoriaPartida}.
   *
   * @param id Id del {@link ConvocatoriaPartida}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ConvocatoriaPartida id no puede ser null para eliminar un ConvocatoriaPartida");

    repository.findById(id).map(convocatoriaPartida -> {

      // TODO Incluir restricción de convocatorias asociadas a proyectos con
      // presupuesto
      return convocatoriaPartida;
    }).orElseThrow(() -> new ConvocatoriaPartidaNotFoundException(id));

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaPartida} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaPartida}.
   * @return la entidad {@link ConvocatoriaPartida}.
   */
  @Override
  public ConvocatoriaPartida findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaPartida returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaPartidaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene las {@link ConvocatoriaPartida} para una {@link Convocatoria}.
   *
   * @param idConvocatoria el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaPartida} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaPartida> findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");
    Specification<ConvocatoriaPartida> specs = ConvocatoriaPartidaSpecifications.byConvocatoriaId(idConvocatoria)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaPartida> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Se comprueba que los datos a guardar cumplan las validaciones oportunas
   * 
   * @param convocatoriaPartida datos del {@link ConvocatoriaPartida}
   */
  private void validate(ConvocatoriaPartida convocatoriaPartida) {
    log.debug("validate(ConvocatoriaPartida convocatoriaPartida) - start");

    Assert.isTrue(convocatoriaPartida.getConvocatoriaId() != null,
        "Id Convocatoria no puede ser null para realizar la acción sobre ConvocatoriaPartida");

    Assert.isTrue(convocatoriaPartida.getCodigo() != null,
        "Codigo no puede ser null para realizar la acción sobre ConvocatoriaPartida");

    configuracionRepository.findFirstByOrderByIdAsc().ifPresent(configuracion -> {
      Assert.isTrue(convocatoriaPartida.getCodigo().matches(configuracion.getFormatoPartidaPresupuestaria()),
          "Formato de codigo no valido");
    });

    log.debug("validate(ConvocatoriaPartida convocatoriaPartida) - end");
  }

}