package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPaqueteTrabajoNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.repository.ProyectoPaqueteTrabajoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoPaqueteTrabajoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoPaqueteTrabajoService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProyectoPaqueteTrabajo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoPaqueteTrabajoServiceImpl implements ProyectoPaqueteTrabajoService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_FIELD_PERSONA_MES = "proyectoPaqueteTrabajo.personaMes";
  private static final String MSG_MODEL_PROYECTO_PAQUETE_TRABAJO = "org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo.message";
  private static final String MSG_PAQUETES_TRABAJO_NO_CONFIGURADOS_PROYECTO = "paquetesTrabajo.proyecto.noConfigurados";
  private static final String MSG_PAQUETES_TRABAJO_PERIODO_PROYECTO_FUERA_RANGO = "paquetesTrabajo.proyecto.periodoFueraRango";
  private static final String MSG_PROBLEM_NOT_EMPTY = "org.springframework.util.Assert.notEmpty.message";

  private final ProyectoPaqueteTrabajoRepository repository;
  private final ProyectoRepository proyectoRepository;

  public ProyectoPaqueteTrabajoServiceImpl(ProyectoPaqueteTrabajoRepository proyectoPaqueteTrabajoRepository,
      ProyectoRepository proyectoRepository) {
    this.repository = proyectoPaqueteTrabajoRepository;
    this.proyectoRepository = proyectoRepository;
  }

  /**
   * Guarda la entidad {@link ProyectoPaqueteTrabajo}.
   * 
   * @param proyectoPaqueteTrabajo la entidad {@link ProyectoPaqueteTrabajo} a
   *                               guardar.
   * @return ProyectoPaqueteTrabajo la entidad {@link ProyectoPaqueteTrabajo}
   *         persistida.
   */
  @Override
  @Transactional
  public ProyectoPaqueteTrabajo create(ProyectoPaqueteTrabajo proyectoPaqueteTrabajo) {
    log.debug("create(ProyectoPaqueteTrabajo ProyectoPaqueteTrabajo) - start");

    AssertHelper.idIsNull(proyectoPaqueteTrabajo.getId(), ProyectoPaqueteTrabajo.class);

    this.validarRequeridosProyectoPaqueteTrabajo(proyectoPaqueteTrabajo);
    this.validarProyectoPaqueteTrabajo(proyectoPaqueteTrabajo, null);

    ProyectoPaqueteTrabajo returnValue = repository.save(proyectoPaqueteTrabajo);

    log.debug("create(ProyectoPaqueteTrabajo ProyectoPaqueteTrabajo) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ProyectoPaqueteTrabajo}.
   * 
   * @param proyectoPaqueteTrabajoActualizar la entidad
   *                                         {@link ProyectoPaqueteTrabajo} a
   *                                         guardar.
   * @return ProyectoPaqueteTrabajo la entidad {@link ProyectoPaqueteTrabajo}
   *         persistida.
   */
  @Override
  @Transactional
  public ProyectoPaqueteTrabajo update(ProyectoPaqueteTrabajo proyectoPaqueteTrabajoActualizar) {
    log.debug("update(ProyectoPaqueteTrabajo ProyectoPaqueteTrabajoActualizar) - start");

    AssertHelper.idNotNull(proyectoPaqueteTrabajoActualizar.getId(), ProyectoPaqueteTrabajo.class);

    this.validarRequeridosProyectoPaqueteTrabajo(proyectoPaqueteTrabajoActualizar);

    return repository.findById(proyectoPaqueteTrabajoActualizar.getId()).map(proyectoPaqueteTrabajo -> {

      validarProyectoPaqueteTrabajo(proyectoPaqueteTrabajoActualizar, proyectoPaqueteTrabajo);

      proyectoPaqueteTrabajo.setNombre(proyectoPaqueteTrabajoActualizar.getNombre());
      proyectoPaqueteTrabajo.setFechaInicio(proyectoPaqueteTrabajoActualizar.getFechaInicio());
      proyectoPaqueteTrabajo.setFechaFin(proyectoPaqueteTrabajoActualizar.getFechaFin());
      proyectoPaqueteTrabajo.setPersonaMes(proyectoPaqueteTrabajoActualizar.getPersonaMes());
      proyectoPaqueteTrabajo.setDescripcion(proyectoPaqueteTrabajoActualizar.getDescripcion());

      ProyectoPaqueteTrabajo returnValue = repository.save(proyectoPaqueteTrabajo);
      log.debug("update(ProyectoPaqueteTrabajo ProyectoPaqueteTrabajoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoPaqueteTrabajoNotFoundException(proyectoPaqueteTrabajoActualizar.getId()));

  }

  /**
   * Elimina la {@link ProyectoPaqueteTrabajo}.
   *
   * @param id Id del {@link ProyectoPaqueteTrabajo}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    // TODO: Implementar borrado en cascada cuando esté listo
    // PaqueteTrabajoInvestigadores

    AssertHelper.idNotNull(id, ProyectoPaqueteTrabajo.class);
    if (!repository.existsById(id)) {
      throw new ProyectoPaqueteTrabajoNotFoundException(id);
    }

    // Solo se podrán crean, modificar o eliminar si en la pantalla "Ficha general"
    // del proyecto, el campo "Paquetes de trabajo" tiene valor afirmativo
    Optional<Boolean> permiteProyectoPaqueteTrabajo = repository.getPermitePaquetesTrabajo(id);
    Assert.isTrue(permiteProyectoPaqueteTrabajo.isPresent() && permiteProyectoPaqueteTrabajo.get(),
        ApplicationContextSupport.getMessage(MSG_PAQUETES_TRABAJO_NO_CONFIGURADOS_PROYECTO));

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene {@link ProyectoPaqueteTrabajo} por su id.
   *
   * @param id el id de la entidad {@link ProyectoPaqueteTrabajo}.
   * @return la entidad {@link ProyectoPaqueteTrabajo}.
   */
  @Override
  public ProyectoPaqueteTrabajo findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoPaqueteTrabajo returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoPaqueteTrabajoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene los {@link ProyectoPaqueteTrabajo} para un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoPaqueteTrabajo} del
   *         {@link Proyecto} paginadas.
   */
  @Override
  public Page<ProyectoPaqueteTrabajo> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoPaqueteTrabajo> specs = ProyectoPaqueteTrabajoSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoPaqueteTrabajo> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Realiza las validaciones necesarias para la creación y modificación de
   * {@link ProyectoPaqueteTrabajo}
   * 
   * @param datosProyectoPaqueteTrabajo
   * @param datosOriginales
   */
  private void validarProyectoPaqueteTrabajo(ProyectoPaqueteTrabajo datosProyectoPaqueteTrabajo,
      ProyectoPaqueteTrabajo datosOriginales) {
    log.debug(
        "validarProyectoPaqueteTrabajo(ProyectoPaqueteTrabajo datosProyectoPaqueteTrabajo, ProyectoPaqueteTrabajo datosOriginales) - start");

    AssertHelper.isBefore(
        datosProyectoPaqueteTrabajo.getFechaFin().compareTo(datosProyectoPaqueteTrabajo.getFechaInicio()) >= 0);

    // Se comprueba la existencia del proyecto
    Long proyectoId = datosProyectoPaqueteTrabajo.getProyectoId();
    Proyecto proyecto = proyectoRepository.findById(proyectoId)
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoId));

    // Solo se podrán crean, modificar o eliminar si en la pantalla "Ficha general"
    // del proyecto, el campo "Paquetes de trabajo" tiene valor afirmativo
    Optional<Boolean> permiteProyectoPaqueteTrabajo = proyectoRepository.getPermitePaquetesTrabajo(proyectoId);
    Assert.isTrue(permiteProyectoPaqueteTrabajo.isPresent() && permiteProyectoPaqueteTrabajo.get(),
        ApplicationContextSupport.getMessage(MSG_PAQUETES_TRABAJO_NO_CONFIGURADOS_PROYECTO));

    // Nombre único en el proyecto
    if (datosOriginales == null) {
      AssertHelper.entityExists(
          !(repository.existsProyectoPaqueteTrabajoByProyectoIdAndNombre(proyectoId,
              datosProyectoPaqueteTrabajo.getNombre())),
          Proyecto.class, ProyectoPaqueteTrabajo.class);
    } else {
      // Si se está modificando se excluye de la búsqueda el propio
      // ProyectoPaqueteTrabajo
      AssertHelper.entityExists(
          !(repository.existsProyectoPaqueteTrabajoByIdNotAndProyectoIdAndNombre(datosOriginales.getId(), proyectoId,
              datosProyectoPaqueteTrabajo.getNombre())),
          Proyecto.class, ProyectoPaqueteTrabajo.class);
    }

    // Fechas dentro del rango del proyecto
    Instant proyectoFechaFin = proyecto.getFechaFinDefinitiva() != null ? proyecto.getFechaFinDefinitiva()
        : proyecto.getFechaFin();

    if (proyecto.getFechaInicio() != null) {
      Assert.isTrue(!datosProyectoPaqueteTrabajo.getFechaInicio().isBefore(proyecto.getFechaInicio()),
          ApplicationContextSupport.getMessage(MSG_PAQUETES_TRABAJO_PERIODO_PROYECTO_FUERA_RANGO));
    }

    if (proyectoFechaFin != null) {
      Assert.isTrue(!datosProyectoPaqueteTrabajo.getFechaFin().isAfter(proyectoFechaFin),
          ApplicationContextSupport.getMessage(MSG_PAQUETES_TRABAJO_PERIODO_PROYECTO_FUERA_RANGO));
    }

    log.debug(
        "validarProyectoPaqueteTrabajo(ProyectoPaqueteTrabajo datosProyectoPaqueteTrabajo, ProyectoPaqueteTrabajo datosOriginales) - end");
  }

  /**
   * Comprueba la presencia de los datos requeridos al crear o modificar
   * {@link ProyectoPaqueteTrabajo}
   * 
   * @param datosProyectoPaqueteTrabajo
   */
  private void validarRequeridosProyectoPaqueteTrabajo(ProyectoPaqueteTrabajo datosProyectoPaqueteTrabajo) {
    log.debug("validarRequeridosProyectoPaqueteTrabajo(ProyectoPaqueteTrabajo datosProyectoPaqueteTrabajo) - start");

    AssertHelper.idNotNull(datosProyectoPaqueteTrabajo.getProyectoId(), Proyecto.class);
    AssertHelper.fieldNotNull(datosProyectoPaqueteTrabajo.getNombre(), ProyectoPaqueteTrabajo.class,
        AssertHelper.MESSAGE_KEY_NAME);
    AssertHelper.fieldNotNull(datosProyectoPaqueteTrabajo.getFechaInicio(), ProyectoPaqueteTrabajo.class,
        AssertHelper.MESSAGE_KEY_DATE_START);
    AssertHelper.fieldNotNull(datosProyectoPaqueteTrabajo.getFechaFin(), ProyectoPaqueteTrabajo.class,
        AssertHelper.MESSAGE_KEY_DATE_END);

    Assert.isTrue(
        datosProyectoPaqueteTrabajo.getPersonaMes() != null && datosProyectoPaqueteTrabajo.getPersonaMes() >= 0,
        () -> ProblemMessage.builder()
            .key(MSG_PROBLEM_NOT_EMPTY)
            .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(
                MSG_FIELD_PERSONA_MES))
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                MSG_MODEL_PROYECTO_PAQUETE_TRABAJO))
            .build());

    log.debug("validarRequeridosProyectoPaqueteTrabajo(ProyectoPaqueteTrabajo datosProyectoPaqueteTrabajo) - end");

  }

}
