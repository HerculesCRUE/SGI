package org.crue.hercules.sgi.csp.service.impl;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPartidaNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPartidaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaPartidaSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPartidaService;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ConvocatoriaPartida}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ConvocatoriaPartidaServiceImpl implements ConvocatoriaPartidaService {

  private final ConvocatoriaPartidaRepository repository;
  private final ConfiguracionRepository configuracionRepository;
  private final ConvocatoriaAuthorityHelper authorityHelper;

  public ConvocatoriaPartidaServiceImpl(ConvocatoriaPartidaRepository repository,
      ConfiguracionRepository configuracionRepository, ConvocatoriaAuthorityHelper authorityHelper) {
    this.repository = repository;
    this.configuracionRepository = configuracionRepository;
    this.authorityHelper = authorityHelper;
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
  @Validated({ BaseEntity.Create.class })
  public ConvocatoriaPartida create(@Valid ConvocatoriaPartida convocatoriaPartida) {
    log.debug("create(ConvocatoriaPartida convocatoriaPartida) - start");

    Assert.isNull(convocatoriaPartida.getId(), "Id tiene que ser null para crear ConvocatoriaPartida");

    Assert.notNull(convocatoriaPartida.getConvocatoriaId(),
        "Id Convocatoria no puede ser null para crear ConvocatoriaPartida");
    this.validate(convocatoriaPartida);

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
  @Validated({ BaseEntity.Update.class })
  public ConvocatoriaPartida update(@Valid ConvocatoriaPartida convocatoriaPartidaActualizar) {
    log.debug("update(ConvocatoriaPartida convocatoriaPartidaActualizar) - start");

    Assert.notNull(convocatoriaPartidaActualizar.getId(),
        "ConvocatoriaPartida id no puede ser null para actualizar un ConvocatoriaPartida");
    this.validate(convocatoriaPartidaActualizar);

    // Restricción de convocatorias asociadas a proyectos con presupuesto

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

    if (!repository.existsById(id)) {
      throw new ConvocatoriaPartidaNotFoundException(id);
    }

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
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaPartida} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaPartida> findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    Specification<ConvocatoriaPartida> specs = ConvocatoriaPartidaSpecifications.byConvocatoriaId(
        convocatoriaId)
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

    configuracionRepository.findFirstByOrderByIdAsc()
        .ifPresent(configuracion -> Assert.isTrue(
            Boolean.TRUE.equals(configuracion.getPartidasPresupuestariasSGE()) ||
                convocatoriaPartida.getCodigo().matches(configuracion.getFormatoPartidaPresupuestaria()),
            "Formato de codigo no valido"));

    Assert.isTrue(this.modificable(convocatoriaPartida.getId(), ConvocatoriaAuthorityHelper.CSP_CON_E),
        "No se puede modificar ConvocatoriaPartida. No tiene los permisos necesarios o el proyecto de la convocatoria tiene presupuestos anuales asignados.");

    log.debug("validate(ConvocatoriaPartida convocatoriaPartida) - end");
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ConvocatoriaPartida} puede ser modificada. También se utilizará para
   * permitir la creación, modificación o eliminación de ciertas entidades
   * relacionadas con la propia {@link ConvocatoriaPartida}.
   *
   * @param id        Id de la {@link ConvocatoriaPartida}.
   * @param authority Authority a validar
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  @Override
  public boolean modificable(Long id, String authority) {
    log.debug("modificable(Long id, String authority) - start");

    if (SgiSecurityContextHolder.hasAuthorityForAnyUO(authority)) {
      // Será modificable si no tiene solicitudes o proyectos asociados
      if (id != null) {
        return (repository.isPosibleEditar(id));
      }
      return true;
    }

    log.debug("modificable(Long id, String authority) - end");
    return false;
  }

}