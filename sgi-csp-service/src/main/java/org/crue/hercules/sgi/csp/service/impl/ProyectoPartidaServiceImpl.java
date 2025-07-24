package org.crue.hercules.sgi.csp.service.impl;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.ProyectoPartidaNotFoundException;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.crue.hercules.sgi.csp.repository.AnualidadGastoRepository;
import org.crue.hercules.sgi.csp.repository.AnualidadIngresoRepository;
import org.crue.hercules.sgi.csp.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoPartidaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoPartidaSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoPartidaService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProyectoPartida}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ProyectoPartidaServiceImpl implements ProyectoPartidaService {

  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_MSG = "msg";
  private static final String MSG_MODEL_PROYECTO_PARTIDA = "org.crue.hercules.sgi.csp.model.ProyectoPartida.message";
  private static final String MSG_ENTITY_MODIFICABLE = "org.springframework.util.Assert.entity.modificable.message";
  private static final String MSG_MODIFICABLE_DESCRIPCION = "proyectoPartida.modificable.descripcion";

  private final ProyectoPartidaRepository repository;
  private final ConfiguracionRepository configuracionRepository;
  private final AnualidadGastoRepository anualidadGastoRepository;
  private final AnualidadIngresoRepository anualidadIngresoRepository;

  public ProyectoPartidaServiceImpl(ProyectoPartidaRepository proyectoPartidaRepository,
      ConfiguracionRepository configuracionRepository, AnualidadGastoRepository anualidadGastoRepository,
      AnualidadIngresoRepository anualidadIngresoRepository) {
    this.repository = proyectoPartidaRepository;
    this.configuracionRepository = configuracionRepository;
    this.anualidadGastoRepository = anualidadGastoRepository;
    this.anualidadIngresoRepository = anualidadIngresoRepository;
  }

  /**
   * Guarda la entidad {@link ProyectoPartida}.
   * 
   * @param proyectoPartida la entidad {@link ProyectoPartida} a guardar.
   * @return la entidad {@link ProyectoPartida} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public ProyectoPartida create(@Valid ProyectoPartida proyectoPartida) {
    log.debug("create(ProyectoPartida proyectoPartida) - start");

    AssertHelper.idIsNull(proyectoPartida.getId(), ProyectoPartida.class);
    this.validate(proyectoPartida);

    ProyectoPartida returnValue = repository.save(proyectoPartida);

    log.debug("create(ProyectoPartida proyectoPartida) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ProyectoPartida}.
   * 
   * @param proyectoPartidaActualizar la entidad {@link ProyectoPartida} a
   *                                  guardar.
   * @return la entidad {@link ProyectoPartida} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public ProyectoPartida update(@Valid ProyectoPartida proyectoPartidaActualizar) {
    log.debug("update(ProyectoPartida proyectoPartidaActualizar) - start");

    AssertHelper.idNotNull(proyectoPartidaActualizar.getId(), ProyectoPartida.class);
    this.validate(proyectoPartidaActualizar);

    return repository.findById(proyectoPartidaActualizar.getId()).map(proyectoPartida -> {

      proyectoPartida.setCodigo(proyectoPartidaActualizar.getCodigo());
      proyectoPartida.setDescripcion(proyectoPartidaActualizar.getDescripcion());
      proyectoPartida.setTipoPartida(proyectoPartidaActualizar.getTipoPartida());
      proyectoPartida.setConvocatoriaPartidaId(proyectoPartidaActualizar.getConvocatoriaPartidaId());

      ProyectoPartida returnValue = repository.save(proyectoPartida);

      log.debug("update(ProyectoPartida proyectoPartidaProrrogaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoPartidaNotFoundException(proyectoPartidaActualizar.getId()));

  }

  /**
   * Elimina la {@link ProyectoPartida}.
   *
   * @param id Id del {@link ProyectoPartida}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, ProyectoPartida.class);

    if (!repository.existsById(id)) {
      throw new ProyectoPartidaNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Comprueba la existencia del {@link ProyectoPartida} por id.
   *
   * @param id el id de la entidad {@link ProyectoPartida}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene {@link ProyectoPartida} por su id.
   *
   * @param id el id de la entidad {@link ProyectoPartida}.
   * @return la entidad {@link ProyectoPartida}.
   */
  @Override
  public ProyectoPartida findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoPartida returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoPartidaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene los {@link ProyectoPartida} para un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoPartida} del {@link Proyecto}
   *         paginadas.
   */
  @Override
  public Page<ProyectoPartida> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoPartida> specs = ProyectoPartidaSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoPartida> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Se comprueba que los datos a guardar cumplan las validaciones oportunas
   * 
   * @param proyectoPartida datos del {@link ProyectoPartida}
   */
  private void validate(ProyectoPartida proyectoPartida) {
    log.debug("validate(ProyectoPartida proyectoPartida) - start");

    AssertHelper.idNotNull(proyectoPartida.getProyectoId(), Proyecto.class);

    configuracionRepository.findFirstByOrderByIdAsc()
        .ifPresent(configuracion -> Assert.isTrue(
            Boolean.TRUE.equals(configuracion.getPartidasPresupuestariasSgeEnabled())
                || proyectoPartida.getCodigo().matches(configuracion.getFormatoPartidaPresupuestaria()),
            ApplicationContextSupport.getMessage("formato.codigo.invalido")));

    Assert.isTrue(this.modificable(proyectoPartida
        .getId(), "CSP-PRO-E"),
        () -> ProblemMessage.builder()
            .key(MSG_ENTITY_MODIFICABLE)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_PARTIDA))
            .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_MODIFICABLE_DESCRIPCION))
            .build());

    log.debug("validate(ProyectoPartida proyectoPartida) - end");
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ProyectoPartida} puede ser modificada. También se utilizará para
   * permitir la creación, modificación o eliminación de ciertas entidades
   * relacionadas con la propia {@link ProyectoPartida}.
   *
   * @param id        Id de la {@link ProyectoPartida}.
   * @param authority Authority a validar
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  @Override
  public boolean modificable(Long id, String authority) {
    log.debug("modificable(Long id, String unidadConvocatoria) - start");

    if (SgiSecurityContextHolder.hasAuthorityForAnyUO(authority)) {
      // Será modificable si no tiene anualidades asociadas
      if (id != null && (anualidadGastoRepository.existsByProyectoPartidaIdAndProyectoAnualidadEnviadoSgeIsTrue(id)
          || anualidadIngresoRepository.existsByProyectoPartidaIdAndProyectoAnualidadEnviadoSgeIsTrue(id))) {
        return false;
      }
      return true;
    }

    log.debug("modificable(Long id, String unidadConvocatoria) - end");
    return false;
  }

  /**
   * Devuelve un boolean indicando si la paritda indicada por el parámetro
   * proyectoPartidaId, tiene asociada alguna {@link AnualidadIngreso} o alguna
   * {@link AnualidadGasto}
   * 
   * @param proyectoPartidaId Id de la {@link ProyectoPartida}
   * @return boolean respuesta en función de si hay o no objetos de tipo
   *         {@link AnualidadIngreso} o alguna {@link AnualidadGasto} asociadas
   */
  @Override
  public boolean existsAnyAnualidad(Long proyectoPartidaId) {

    return this.anualidadIngresoRepository.findByProyectoPartidaId(proyectoPartidaId)
        .map(anualidades -> !anualidades.isEmpty()).orElse(false)
        || this.anualidadGastoRepository.findByProyectoPartidaId(proyectoPartidaId)
            .map(anualidades -> !anualidades.isEmpty()).orElse(false);

  }

}
