package org.crue.hercules.sgi.csp.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.CollectionUtils;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoProrrogaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoProrrogaWithRelatedProyectoFacturacionException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.GrupoTipo.Tipo;
import org.crue.hercules.sgi.csp.repository.ProrrogaDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFacturacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProrrogaRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoProrrogaSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoProrrogaService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProyectoProrroga}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProyectoProrrogaServiceImpl implements ProyectoProrrogaService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_FIELD_NUM_PRORROGA = "numProrroga";
  private static final String MSG_FIELD_FECHA_INICIO_PROYECTO = "fechaInicioProyecto";
  private static final String MSG_FIELD_FECHA_FIN_PROYECTO = "fechaFinProyecto";
  private static final String MSG_FIELD_FECHA_ULTIMA_PRORROGA = "fechaUltimaProrroga";
  private static final String MSG_FIELD_FECHA_CONCESION = "fechaConcesion";
  private static final String MSG_FIELD_IMPORTE = "importe";
  private static final String MSG_MODEL_PROYECTO_PRORROGA = "org.crue.hercules.sgi.csp.model.ProyectoProrroga.message";
  private static final String MSG_PERMISO_ELIMINAR_ULTIMA_PRORROGA = "proyectoProrroga.permiso.eliminar.ultimaProrroga";
  private static final String MSG_PERMISO_MODIFICAR_ULTIMA_PRORROGA = "proyectoProrroga.permiso.modificar.ultimaProrroga";
  private static final String MSG_PROBLEM_NOT_EMPTY = "org.springframework.util.Assert.notEmpty.message";

  private final ProyectoProrrogaRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final ProrrogaDocumentoRepository prorrogaDocumentoRepository;
  private final ProyectoEquipoRepository proyectoEquipoRepository;
  private final ProyectoHelper proyectoHelper;
  private final ProyectoFacturacionRepository proyectoFacturacionRepository;

  /**
   * Guarda la entidad {@link ProyectoProrroga}.
   * 
   * @param proyectoProrroga la entidad {@link ProyectoProrroga} a guardar.
   * @return ProyectoProrroga la entidad {@link ProyectoProrroga} persistida.
   */
  @Override
  @Transactional
  public ProyectoProrroga create(ProyectoProrroga proyectoProrroga) {
    log.debug("create(ProyectoProrroga ProyectoProrroga) - start");

    AssertHelper.idIsNull(proyectoProrroga.getId(), ProyectoProrroga.class);

    this.validarRequeridosProyectoProrroga(proyectoProrroga);
    this.validarProyectoProrroga(proyectoProrroga, null);

    ProyectoProrroga returnValue = repository.save(proyectoProrroga);

    // Actualizar nueva fecha de fin
    if (proyectoProrroga.getFechaFin() != null) {
      this.actualizarFechaFin(returnValue.getProyectoId(), returnValue.getFechaFin());
    }

    // Se recalcula el número de prórroga en función de la ordenación de la fecha de
    // concesión
    this.recalcularNumProrroga(returnValue.getProyectoId());

    log.debug("create(ProyectoProrroga ProyectoProrroga) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ProyectoProrroga}.
   * 
   * @param proyectoProrrogaActualizar la entidad {@link ProyectoProrroga} a
   *                                   guardar.
   * @return ProyectoProrroga la entidad {@link ProyectoProrroga} persistida.
   */
  @Override
  @Transactional
  public ProyectoProrroga update(ProyectoProrroga proyectoProrrogaActualizar) {
    log.debug("update(ProyectoProrroga ProyectoProrrogaActualizar) - start");

    AssertHelper.idNotNull(proyectoProrrogaActualizar.getId(), ProyectoProrroga.class);

    this.validarRequeridosProyectoProrroga(proyectoProrrogaActualizar);

    return repository.findById(proyectoProrrogaActualizar.getId()).map(proyectoProrroga -> {

      // Si se modifica fecha de fin hay que actualizar
      boolean actualizarFechaFin = proyectoProrrogaActualizar.getFechaFin() != null
          && ((proyectoProrroga.getFechaFin() != null
              && proyectoProrrogaActualizar.getFechaFin().compareTo(proyectoProrroga.getFechaFin()) != 0)
              || proyectoProrroga.getFechaFin() == null);

      validarProyectoProrroga(proyectoProrrogaActualizar, proyectoProrroga);

      proyectoProrroga.setNumProrroga(proyectoProrrogaActualizar.getNumProrroga());
      proyectoProrroga.setFechaConcesion(proyectoProrrogaActualizar.getFechaConcesion());
      proyectoProrroga.setTipo(proyectoProrrogaActualizar.getTipo());
      proyectoProrroga.setFechaFin(proyectoProrrogaActualizar.getFechaFin());
      proyectoProrroga.setImporte(proyectoProrrogaActualizar.getImporte());
      proyectoProrroga.setObservaciones(proyectoProrrogaActualizar.getObservaciones());

      ProyectoProrroga returnValue = repository.save(proyectoProrroga);

      // Actualizar nueva fecha de fin
      if (actualizarFechaFin) {
        this.actualizarFechaFin(returnValue.getProyectoId(), returnValue.getFechaFin());
      }

      log.debug("update(ProyectoProrroga ProyectoProrrogaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoProrrogaNotFoundException(proyectoProrrogaActualizar.getId()));

  }

  /**
   * Elimina la {@link ProyectoProrroga}.
   *
   * @param id Id del {@link ProyectoProrroga}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, ProyectoProrroga.class);

    ProyectoProrroga proyectoProrrogaToDelete = repository.findById(id).map(proyectoProrroga -> {

      // Se recupera el ProyectoProrroga con la última fecha de concesión
      Optional<ProyectoProrroga> ultimoProyectoProrroga = repository
          .findFirstByProyectoIdOrderByFechaConcesionDesc(proyectoProrroga.getProyectoId());

      // Solamente se puede modificar la última prórroga
      if (ultimoProyectoProrroga.isPresent()
          && !ultimoProyectoProrroga.get().getId().equals(proyectoProrroga.getId())) {
        Assert.isTrue(Objects.equals(proyectoProrroga.getId(), ultimoProyectoProrroga.get().getId()),
            ApplicationContextSupport.getMessage(MSG_PERMISO_ELIMINAR_ULTIMA_PRORROGA));
      }
      return proyectoProrroga;
    }).orElseThrow(() -> new ProyectoProrrogaNotFoundException(id));

    if (proyectoFacturacionRepository.existsByProyectoProrrogaId(id)) {
      throw new ProyectoProrrogaWithRelatedProyectoFacturacionException();
    }

    // Borrado de los documentos asociados a la prórroga
    prorrogaDocumentoRepository.deleteByProyectoProrrogaId(id);

    repository.deleteById(id);

    // Se recalcula el número de prórroga en función de la ordenación de la fecha de
    // concesión
    this.recalcularNumProrroga(proyectoProrrogaToDelete.getProyectoId());

    if (proyectoProrrogaToDelete.getFechaFin() != null) {
      Instant fechaFinNew = repository
          .findAllByProyectoIdOrderByFechaConcesion(proyectoProrrogaToDelete.getProyectoId()).stream()
          .map(ProyectoProrroga::getFechaFin)
          .filter(fechaFin -> fechaFin != null)
          .max(Comparator.naturalOrder())
          .orElse(null);

      this.actualizarFechaFin(proyectoProrrogaToDelete.getProyectoId(), fechaFinNew);
    }

    log.debug("delete(Long id) - end");
  }

  /**
   * Comprueba la existencia del {@link ProyectoProrroga} por id.
   *
   * @param id el id de la entidad {@link ProyectoProrroga}.
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
   * Obtiene {@link ProyectoProrroga} por su id.
   *
   * @param id el id de la entidad {@link ProyectoProrroga}.
   * @return la entidad {@link ProyectoProrroga}.
   */
  @Override
  public ProyectoProrroga findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoProrroga returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoProrrogaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene los {@link ProyectoProrroga} para un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoProrroga} del {@link Proyecto}
   *         paginadas.
   */
  @Override
  public Page<ProyectoProrroga> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");

    proyectoHelper.checkCanAccessProyecto(proyectoId);

    Specification<ProyectoProrroga> specs = ProyectoProrrogaSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoProrroga> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Si una nueva fecha de fin que ha sido concedida, con el valor indicado en
   * este campo se actualizará la fecha de fin del apartado de "Datos generales"
   * del proyecto. Además, sobre los miembros del equipo cuya fecha de fin de
   * participación coincida con la fecha de fin de proyecto anterior, se producirá
   * automáticamente la actualización de la fecha de fin al nuevo valor de fecha
   * de fin del proyecto.
   * 
   * @param proyectoId  identificador del {@link Proyecto}
   * @param fechaFinNew nueva fecha de fin
   */
  private void actualizarFechaFin(Long proyectoId, Instant fechaFinNew) {
    log.debug("actualizarFechaFin(Long proyectoId, Instant fechaFinNew) - start");

    Optional<Proyecto> proyecto = proyectoRepository.findById(proyectoId);

    AssertHelper.fieldBefore(
        proyecto.isPresent() && ((fechaFinNew != null
            && fechaFinNew.compareTo(proyecto.get().getFechaInicio()) >= 0) || fechaFinNew == null),
        MSG_FIELD_FECHA_INICIO_PROYECTO, AssertHelper.MESSAGE_KEY_DATE_END);

    // Se actualizan los miembros de equipo cuya fecha de fin coincida con la fecha
    // de fin del proyecto o sea mayor a la nueva fecha de fin del proyecto
    List<ProyectoEquipo> miembros = new ArrayList<>();
    Instant fechaFin = proyecto.get().getFechaFinDefinitiva() != null ? proyecto.get().getFechaFinDefinitiva()
        : proyecto.get().getFechaFin();
    List<ProyectoEquipo> miembrosFechaFinEqual = proyectoEquipoRepository
        .findAllByProyectoIdAndFechaFin(proyecto.get().getId(), fechaFin);
    List<ProyectoEquipo> miembrosFechaFinGreater = proyectoEquipoRepository
        .findAllByProyectoIdAndFechaFinGreaterThan(proyecto.get().getId(), fechaFin);

    if (CollectionUtils.isNotEmpty(miembrosFechaFinEqual)) {
      miembros.addAll(miembrosFechaFinEqual);
    }

    if (CollectionUtils.isNotEmpty(miembrosFechaFinGreater)) {
      miembros.addAll(miembrosFechaFinGreater);
    }

    if (CollectionUtils.isNotEmpty(miembros)) {
      Instant fechaFinMiembrosNew = fechaFinNew != null ? fechaFinNew : fechaFin;
      miembros.stream().forEach(miembro -> {
        miembro.setFechaFin(fechaFinMiembrosNew);
        proyectoEquipoRepository.save(miembro);
      });
    }

    proyecto.get().setFechaFinDefinitiva(fechaFinNew);

    proyectoRepository.save(proyecto.get());
    log.debug("actualizarFechaFin(Long proyectoId, Instant fechaFinNew) - end");
  }

  /**
   * Realiza las validaciones necesarias para la creación y modificación de
   * {@link ProyectoProrroga}
   * 
   * @param datosProyectoProrroga
   * @param datosOriginales
   */
  private void validarProyectoProrroga(ProyectoProrroga datosProyectoProrroga, ProyectoProrroga datosOriginales) {
    log.debug(
        "validarProyectoProrroga(ProyectoProrroga datosProyectoProrroga, ProyectoProrroga datosOriginales) - start");

    // Si TipoProrroga "Importe" FechaFin irá vacío
    if (datosProyectoProrroga.getTipo() == ProyectoProrroga.Tipo.IMPORTE) {
      datosProyectoProrroga.setFechaFin(null);
    }

    // Si TipoProrroga "Tiempo" Importe irá vacío
    if (datosProyectoProrroga.getTipo() == ProyectoProrroga.Tipo.TIEMPO) {
      datosProyectoProrroga.setImporte(null);
    }

    // Se comprueba la existencia del proyecto
    Long proyectoId = datosProyectoProrroga.getProyectoId();
    Proyecto proyecto = proyectoRepository.findById(proyectoId)
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoId));

    Instant fechaFinProyecto = proyecto.getFechaFinDefinitiva() != null ? proyecto.getFechaFinDefinitiva()
        : proyecto.getFechaFin();

    // Si es una nueva prorroga con fecha o ha cambiado la fecha de la prorroga se
    // valida que sea posterior a la fecha de fin del proyecto
    if (datosProyectoProrroga.getFechaFin() != null
        && (datosOriginales == null || !datosProyectoProrroga.getFechaFin().equals(datosOriginales.getFechaFin()))) {
      AssertHelper.fieldBefore(datosProyectoProrroga.getFechaFin().isAfter(fechaFinProyecto),
          MSG_FIELD_FECHA_FIN_PROYECTO, AssertHelper.MESSAGE_KEY_DATE_END);
    }

    // Se recupera el ProyectoProrroga con la última fecha de concesión
    Optional<ProyectoProrroga> ultimoProyectoProrroga = repository
        .findFirstByProyectoIdOrderByFechaConcesionDesc(proyectoId);

    // Solamente se puede modificar la última prórroga
    if (ultimoProyectoProrroga.isPresent()) {

      // Si se trata de una creación, incrementa el número de prórroga
      if (datosOriginales == null) {
        datosProyectoProrroga.setNumProrroga(ultimoProyectoProrroga.get().getNumProrroga() + 1);
        // fecha de concesión debe ser posterior a la de la última prórroga
        AssertHelper.fieldBefore(
            datosProyectoProrroga.getFechaConcesion().compareTo(ultimoProyectoProrroga.get().getFechaConcesion()) > 0,
            MSG_FIELD_FECHA_ULTIMA_PRORROGA, MSG_FIELD_FECHA_CONCESION);
      } else {
        // Se trata de una modificación
        Assert.isTrue(Objects.equals(datosProyectoProrroga.getId(), ultimoProyectoProrroga.get().getId()),
            ApplicationContextSupport.getMessage(MSG_PERMISO_MODIFICAR_ULTIMA_PRORROGA));

        // Se recupera el ProyectoProrroga inmediatamente anterior
        Optional<ProyectoProrroga> anteriorProyectoProrroga = repository
            .findFirstByIdNotAndProyectoIdOrderByFechaConcesionDesc(datosProyectoProrroga.getId(), proyectoId);
        if (anteriorProyectoProrroga.isPresent()) {
          // fecha de concesión debe ser posterior a la de la última prórroga
          AssertHelper.fieldBefore(
              datosProyectoProrroga.getFechaConcesion()
                  .compareTo(anteriorProyectoProrroga.get().getFechaConcesion()) > 0,
              MSG_FIELD_FECHA_ULTIMA_PRORROGA, MSG_FIELD_FECHA_CONCESION);
        }
      }

    } else {
      // No existen otros ProyectoProrroga
      // El número de prorroga será 1
      datosProyectoProrroga.setNumProrroga(1);
    }

    log.debug(
        "validarProyectoProrroga(ProyectoProrroga datosProyectoProrroga, ProyectoProrroga datosOriginales) - end");
  }

  /**
   * Comprueba la presencia de los datos requeridos al crear o modificar
   * {@link ProyectoProrroga}
   * 
   * @param datosProyectoProrroga
   */
  private void validarRequeridosProyectoProrroga(ProyectoProrroga datosProyectoProrroga) {
    log.debug("validarRequeridosProyectoProrroga(ProyectoProrroga datosProyectoProrroga) - start");

    AssertHelper.idNotNull(datosProyectoProrroga.getProyectoId(), Proyecto.class);

    AssertHelper.fieldNotNull(datosProyectoProrroga.getNumProrroga(), ProyectoProrroga.class, MSG_FIELD_NUM_PRORROGA);

    AssertHelper.entityNotNull(datosProyectoProrroga.getTipo(), ProyectoProrroga.class, Tipo.class);

    AssertHelper.fieldNotNull(datosProyectoProrroga.getFechaConcesion(), ProyectoProrroga.class,
        AssertHelper.MESSAGE_KEY_DATE);

    // Será obligatorio si se ha seleccionado en el campo TipoProrroga "Tiempo" o
    // "Tiempo e importe"
    if (datosProyectoProrroga.getTipo() != ProyectoProrroga.Tipo.IMPORTE) {
      AssertHelper.fieldNotNull(datosProyectoProrroga.getFechaFin(), ProyectoProrroga.class,
          AssertHelper.MESSAGE_KEY_DATE_END);
    }

    // Será obligatorio si se ha seleccionado en el campo TipoProrroga "Importe" o
    // "Tiempo e importe"
    if (datosProyectoProrroga.getTipo() != ProyectoProrroga.Tipo.TIEMPO) {
      Assert.isTrue(
          datosProyectoProrroga.getImporte() != null
              && datosProyectoProrroga.getImporte().compareTo(BigDecimal.ZERO) >= 0,
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_NOT_EMPTY)
              .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(
                  MSG_FIELD_IMPORTE))
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                  MSG_MODEL_PROYECTO_PRORROGA))
              .build());
    }

    log.debug("validarRequeridosProyectoProrroga(ProyectoProrroga datosProyectoProrroga) - end");
  }

  /**
   * Actualiza el número de prórroga en función de la fecha de concesión de las
   * prórrogas del {@link Proyecto} que haya en el sistema
   * 
   * @param proyectoId identificador del {@link Proyecto}
   */
  private void recalcularNumProrroga(Long proyectoId) {
    List<ProyectoProrroga> listadoProyectoProrrogaBD = repository.findAllByProyectoIdOrderByFechaConcesion(proyectoId);

    AtomicInteger numProrroga = new AtomicInteger(0);

    for (ProyectoProrroga prorroga : listadoProyectoProrrogaBD) {
      // Actualiza el numero de periodo
      prorroga.setNumProrroga(numProrroga.incrementAndGet());
    }

    repository.saveAll(listadoProyectoProrrogaBD);
  }

  /**
   * Indica si existen {@link ProyectoProrroga} de un {@link Proyecto}
   * 
   * @param proyectoId identificador de la {@link Proyecto}
   * @return si existe la entidad {@link ProyectoProrroga}
   */
  @Override
  public boolean existsByProyecto(Long proyectoId) {
    log.debug("existsByProyecto(Long proyectoId) - start");
    boolean returnValue = repository.existsByProyectoId(proyectoId);

    proyectoHelper.checkCanAccessProyecto(proyectoId);

    log.debug("existsByProyecto(Long proyectoId) - end");
    return returnValue;
  }

}
