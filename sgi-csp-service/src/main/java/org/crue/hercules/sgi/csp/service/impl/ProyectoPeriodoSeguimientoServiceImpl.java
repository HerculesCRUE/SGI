package org.crue.hercules.sgi.csp.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoSeguimientoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoSeguimientoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoSeguimientoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoPeriodoSeguimientoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service Implementation para la gestión de {@link ProyectoPeriodoSeguimiento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoPeriodoSeguimientoServiceImpl implements ProyectoPeriodoSeguimientoService {

  private final ProyectoPeriodoSeguimientoRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final ProyectoPeriodoSeguimientoDocumentoRepository proyectoPeriodoSeguimientoDocumentoRepository;

  public ProyectoPeriodoSeguimientoServiceImpl(
      ProyectoPeriodoSeguimientoRepository proyectoPeriodoSeguimientoRepository, ProyectoRepository proyectoRepository,
      ProyectoPeriodoSeguimientoDocumentoRepository proyectoPeriodoSeguimientoDocumentoRepository) {
    this.repository = proyectoPeriodoSeguimientoRepository;
    this.proyectoRepository = proyectoRepository;
    this.proyectoPeriodoSeguimientoDocumentoRepository = proyectoPeriodoSeguimientoDocumentoRepository;
  }

  /**
   * Guarda la entidad {@link ProyectoPeriodoSeguimiento}.
   * 
   * @param proyectoPeriodoSeguimiento la entidad
   *                                   {@link ProyectoPeriodoSeguimiento} a
   *                                   guardar.
   * @return ProyectoPeriodoSeguimiento la entidad
   *         {@link ProyectoPeriodoSeguimiento} persistida.
   */
  @Override
  @Transactional
  public ProyectoPeriodoSeguimiento create(ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento) {
    log.debug("create(ProyectoPeriodoSeguimiento ProyectoPeriodoSeguimiento) - start");

    Assert.isNull(proyectoPeriodoSeguimiento.getId(),
        "ProyectoPeriodoSeguimiento id tiene que ser null para crear un nuevo ProyectoPeriodoSeguimiento");

    this.validarProyectoPeriodoSeguimiento(proyectoPeriodoSeguimiento, null);

    ProyectoPeriodoSeguimiento returnValue = repository.save(proyectoPeriodoSeguimiento);

    // Se recalcula el número de período en función de la ordenación de la fecha de
    // inicio
    this.recalcularNumPeriodos(returnValue.getProyectoId());

    log.debug("create(ProyectoPeriodoSeguimiento ProyectoPeriodoSeguimiento) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ProyectoPeriodoSeguimiento}.
   * 
   * @param proyectoPeriodoSeguimientoActualizar la entidad
   *                                             {@link ProyectoPeriodoSeguimiento}
   *                                             a guardar.
   * @return ProyectoPeriodoSeguimiento la entidad
   *         {@link ProyectoPeriodoSeguimiento} persistida.
   */
  @Override
  @Transactional
  public ProyectoPeriodoSeguimiento update(ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoActualizar) {
    log.debug("update(ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoActualizar) - start");

    Assert.notNull(proyectoPeriodoSeguimientoActualizar.getId(),
        "ProyectoPeriodoSeguimiento id no puede ser null para actualizar un proyectoPeriodoSeguimiento");

    return repository.findById(proyectoPeriodoSeguimientoActualizar.getId()).map(proyectoPeriodoSeguimiento -> {

      validarProyectoPeriodoSeguimiento(proyectoPeriodoSeguimientoActualizar, proyectoPeriodoSeguimiento);

      proyectoPeriodoSeguimiento.setTipoSeguimiento(proyectoPeriodoSeguimientoActualizar.getTipoSeguimiento());
      proyectoPeriodoSeguimiento.setFechaInicio(proyectoPeriodoSeguimientoActualizar.getFechaInicio());
      proyectoPeriodoSeguimiento.setFechaFin(proyectoPeriodoSeguimientoActualizar.getFechaFin());
      proyectoPeriodoSeguimiento
          .setFechaInicioPresentacion(proyectoPeriodoSeguimientoActualizar.getFechaInicioPresentacion());
      proyectoPeriodoSeguimiento
          .setFechaFinPresentacion(proyectoPeriodoSeguimientoActualizar.getFechaFinPresentacion());
      proyectoPeriodoSeguimiento.setObservaciones(proyectoPeriodoSeguimientoActualizar.getObservaciones());

      ProyectoPeriodoSeguimiento returnValue = repository.save(proyectoPeriodoSeguimiento);

      // Se recalcula el número de período en función de la ordenación de la fecha de
      // inicio
      this.recalcularNumPeriodos(returnValue.getProyectoId());

      log.debug("update(ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoPeriodoSeguimientoNotFoundException(proyectoPeriodoSeguimientoActualizar.getId()));

  }

  @Override
  @Transactional
  public ProyectoPeriodoSeguimiento updateFechaPresentacionDocumentacion(Long id,
      Instant fechaPresentacionDocumentacion) {
    log.debug("updateFechaPresentacionDocumentacion(Long id, Instant fechaPresentacionDocumentacion) - start");

    AssertHelper.idNotNull(id, ProyectoPeriodoSeguimiento.class);

    return repository.findById(id).map(proyectoPeriodoSeguimiento -> {

      proyectoPeriodoSeguimiento.setFechaPresentacionDocumentacion(fechaPresentacionDocumentacion);

      ProyectoPeriodoSeguimiento returnValue = repository.save(proyectoPeriodoSeguimiento);

      log.debug("updateFechaPresentacionDocumentacion(Long id, Instant fechaPresentacionDocumentacion) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoPeriodoSeguimientoNotFoundException(id));
  }

  /**
   * Actualiza el número de período en función de la fecha de inicio de los
   * períodos de seguimiento del {@link Proyecto} que haya en el sistema
   * 
   * @param proyectoId identificador del {@link Proyecto}
   */
  private void recalcularNumPeriodos(Long proyectoId) {
    List<ProyectoPeriodoSeguimiento> listadoProyectoPeriodoSeguimientoBD = repository
        .findByProyectoIdOrderByFechaInicio(proyectoId);

    AtomicInteger numPeriodo = new AtomicInteger(0);

    for (ProyectoPeriodoSeguimiento periodoSeguimiento : listadoProyectoPeriodoSeguimientoBD) {
      // Actualiza el numero de periodo
      periodoSeguimiento.setNumPeriodo(numPeriodo.incrementAndGet());
    }

    repository.saveAll(listadoProyectoPeriodoSeguimientoBD);
  }

  /**
   * Elimina la {@link ProyectoPeriodoSeguimiento}.
   *
   * @param id Id del {@link ProyectoPeriodoSeguimiento}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ProyectoPeriodoSeguimiento id no puede ser null para eliminar un ProyectoPeriodoSeguimiento");
    if (!repository.existsById(id)) {
      throw new ProyectoPeriodoSeguimientoNotFoundException(id);
    }

    if (proyectoPeriodoSeguimientoDocumentoRepository.existsByProyectoPeriodoSeguimientoId(id)) {
      proyectoPeriodoSeguimientoDocumentoRepository.deleteByProyectoPeriodoSeguimientoId(id);
    }

    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = this.findById(id);
    repository.deleteById(id);
    this.recalcularNumPeriodos(proyectoPeriodoSeguimiento.getProyectoId());
    log.debug("delete(Long id) - end");

  }

  /**
   * Comprueba la existencia del {@link ProyectoPeriodoSeguimiento} por id.
   *
   * @param id el id de la entidad {@link ProyectoPeriodoSeguimiento}.
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
   * Obtiene {@link ProyectoPeriodoSeguimiento} por su id.
   *
   * @param id el id de la entidad {@link ProyectoPeriodoSeguimiento}.
   * @return la entidad {@link ProyectoPeriodoSeguimiento}.
   */
  @Override
  public ProyectoPeriodoSeguimiento findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoPeriodoSeguimiento returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoPeriodoSeguimientoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene los {@link ProyectoPeriodoSeguimiento} para un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoPeriodoSeguimiento} del
   *         {@link Proyecto} paginadas.
   */
  @Override
  public Page<ProyectoPeriodoSeguimiento> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoPeriodoSeguimiento> specs = ProyectoPeriodoSeguimientoSpecifications.byProyecto(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoPeriodoSeguimiento> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  @Override
  public Page<ProyectoPeriodoSeguimiento> findAllByProyectoSgeRef(String proyectoSgeRef, String query,
      Pageable paging) {
    log.debug("findAllByProyectoSgeRef(String proyectoSgeRef, String query, Pageable paging) - start");

    Specification<ProyectoPeriodoSeguimiento> specs = ProyectoPeriodoSeguimientoSpecifications
        .byProyectoSgeRef(proyectoSgeRef).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoPeriodoSeguimiento> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByProyectoSgeRef(String proyectoSgeRef, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Realiza las validaciones necesarias para la creación y modificación de
   * {@link ProyectoPeriodoSeguimiento}
   * 
   * @param datosProyectoPeriodoSeguimiento
   * @param datosOriginales
   */
  private void validarProyectoPeriodoSeguimiento(ProyectoPeriodoSeguimiento datosProyectoPeriodoSeguimiento,
      ProyectoPeriodoSeguimiento datosOriginales) {
    log.debug(
        "validarProyectoPeriodoSeguimiento(ProyectoPeriodoSeguimiento datosProyectoPeriodoSeguimiento, ProyectoPeriodoSeguimiento datosOriginales) - start");

    this.validarRequeridosProyectoPeriodoSeguimiento(datosProyectoPeriodoSeguimiento, datosOriginales);

    if (datosProyectoPeriodoSeguimiento.getFechaInicio() != null
        && datosProyectoPeriodoSeguimiento.getFechaFin() != null) {

      Assert.isTrue(
          datosProyectoPeriodoSeguimiento.getFechaFin().isAfter(datosProyectoPeriodoSeguimiento.getFechaInicio()),
          "La fecha de fin debe ser posterior a la fecha de inicio");

      Proyecto proyecto = proyectoRepository.findById(datosProyectoPeriodoSeguimiento.getProyectoId())
          .orElseThrow(() -> new ProyectoNotFoundException(datosProyectoPeriodoSeguimiento.getProyectoId()));

      Assert.isTrue(
          proyecto.getFechaInicio().isBefore(datosProyectoPeriodoSeguimiento.getFechaInicio())
              || proyecto.getFechaInicio().equals(datosProyectoPeriodoSeguimiento.getFechaInicio()),
          "La fecha de inicio del proyecto debe ser anterior o igual a la fecha de inicio del periodo de seguimiento");

      Assert.isTrue(
          proyecto.getFechaFin().isAfter(datosProyectoPeriodoSeguimiento.getFechaFin())
              || proyecto.getFechaFin().equals(datosProyectoPeriodoSeguimiento.getFechaFin()),
          "La fecha de fin del proyecto debe ser posterior o igual a la fecha de fin del periodo de seguimiento");

      List<ProyectoPeriodoSeguimiento> listaPeriodosSeguimiento = repository
          .findByProyectoIdOrderByFechaInicio(proyecto.getId());

      if (!CollectionUtils.isEmpty(listaPeriodosSeguimiento)
          && datosProyectoPeriodoSeguimiento.getTipoSeguimiento() == TipoSeguimiento.FINAL) {

        if (listaPeriodosSeguimiento.stream().anyMatch(
            proyectoPeriodoSeguimiento -> proyectoPeriodoSeguimiento.getTipoSeguimiento().equals(TipoSeguimiento.FINAL)
                && !Objects.equals(proyectoPeriodoSeguimiento.getId(), datosProyectoPeriodoSeguimiento.getId()))) {
          throw new IllegalArgumentException("Solo puede haber un periodo 'final'");
        }
        if (listaPeriodosSeguimiento.get(listaPeriodosSeguimiento.size() - 1).getFechaInicio()
            .isAfter(datosProyectoPeriodoSeguimiento.getFechaInicio())) {
          throw new IllegalArgumentException("El periodo 'final' ha de ser el último");
        }

      }
    }

    if (datosProyectoPeriodoSeguimiento.getFechaInicioPresentacion() != null
        && datosProyectoPeriodoSeguimiento.getFechaFinPresentacion() != null) {

      Assert.isTrue(
          datosProyectoPeriodoSeguimiento.getFechaFinPresentacion()
              .isAfter(datosProyectoPeriodoSeguimiento.getFechaInicioPresentacion()),
          "La fecha de fin de presentación debe ser posterior a la fecha de inicio de presentación");
    }

    // Solapamiento de fechas
    Assert.isTrue(
        validarSolapamientoFechas(datosProyectoPeriodoSeguimiento.getProyectoId(),
            datosProyectoPeriodoSeguimiento.getFechaInicio(), datosProyectoPeriodoSeguimiento.getFechaFin(),
            Arrays.asList(datosProyectoPeriodoSeguimiento.getId())),
        "Ya existe un periodo de fechas en vigencia que se solapa con el indicado");

    log.debug(
        "validarProyectoPeriodoSeguimiento(ProyectoPeriodoSeguimiento datosProyectoPeriodoSeguimiento, ProyectoPeriodoSeguimiento datosOriginales) - end");
  }

  /**
   * Se valida que el periodo del ProyectoPeriodoSeguimiento no se solape con los
   * que ya hay para un {@link Proyecto}
   * 
   * @param proyectoId  id {@link Proyecto}
   * @param fechaInicio fecha inicial
   * @param fechaFin    fecha final
   * @param excluidos   identificadores a excluir de la busqueda
   * @return true validación correcta/ false validacion incorrecta
   */
  private boolean validarSolapamientoFechas(Long proyectoId, Instant fechaInicio, Instant fechaFin,
      List<Long> excluidos) {
    log.debug(
        "validarSolapamientoFechas(Long proyectoId, Instant fechaInicio, Instant fechaFin, List<Long> excluidos - start");

    Specification<ProyectoPeriodoSeguimiento> specByProyecto = ProyectoPeriodoSeguimientoSpecifications
        .byProyecto(proyectoId);
    Specification<ProyectoPeriodoSeguimiento> specExcluidos = ProyectoPeriodoSeguimientoSpecifications.notIn(excluidos);

    Specification<ProyectoPeriodoSeguimiento> specs = Specification.where(specByProyecto).and(specExcluidos);

    Page<ProyectoPeriodoSeguimiento> results = repository.findAll(specs, Pageable.unpaged());
    List<ProyectoPeriodoSeguimiento> listaPeriodosSeguimiento = (results == null)
        ? new ArrayList<>()
        : results.getContent();

    boolean returnValue = Boolean.TRUE;
    // Si fechaIni y fechaFin están vacíos siempre habrá solapamiento.
    if (fechaInicio == null && fechaFin == null) {
      returnValue = Boolean.FALSE;
    } else {

      Iterator<ProyectoPeriodoSeguimiento> it = listaPeriodosSeguimiento.iterator();
      ProyectoPeriodoSeguimiento item = null;
      while (it.hasNext() && returnValue) {
        item = it.next();
        // Si fechaIni y fechaFin están vacíos siempre habrá solapamiento.
        if (item.getFechaInicio() == null && item.getFechaFin() == null) {
          returnValue = Boolean.FALSE;
        } else {
          // Si la fecha de inicio o fin está vacía es que tiene vigencia y se solapará.
          if ((fechaInicio == null || item.getFechaInicio() == null)
              || (fechaFin == null || item.getFechaFin() == null)) {
            returnValue = Boolean.FALSE;
          } else {
            returnValue = !((item.getFechaInicio().equals(fechaInicio) && item.getFechaFin().equals(fechaFin))
                || (item.getFechaInicio().isBefore(fechaFin) && item.getFechaFin().isAfter(fechaInicio)));
          }
        }
      }
    }
    return returnValue;
  }

  /**
   * Comprueba la presencia de los datos requeridos al crear o modificar
   * {@link ProyectoPeriodoSeguimiento}
   * 
   * @param datosProyectoPeriodoSeguimiento
   * @param datosOriginales
   */
  private void validarRequeridosProyectoPeriodoSeguimiento(ProyectoPeriodoSeguimiento datosProyectoPeriodoSeguimiento,
      ProyectoPeriodoSeguimiento datosOriginales) {
    log.debug(
        "validarRequeridosProyectoPeriodoSeguimiento(ProyectoPeriodoSeguimiento datosProyectoPeriodoSeguimiento, ProyectoPeriodoSeguimiento datosOriginales) - start");

    Assert.isTrue(datosProyectoPeriodoSeguimiento.getProyectoId() != null, "Id Proyecto no puede ser null para "
        + ((datosOriginales == null) ? "crear" : "actualizar") + " ProyectoPeriodoSeguimiento");

    Assert.notNull(datosProyectoPeriodoSeguimiento.getFechaInicio(), "FechaInicio no puede ser null para "
        + ((datosOriginales == null) ? "crear" : "actualizar") + " ProyectoPeriodoSeguimiento");

    Assert.notNull(datosProyectoPeriodoSeguimiento.getFechaFin(), "FechaFin no puede ser null para "
        + ((datosOriginales == null) ? "crear" : "actualizar") + " ProyectoPeriodoSeguimiento");

    Assert.notNull(datosProyectoPeriodoSeguimiento.getTipoSeguimiento(), "TipoSeguimiento no puede ser null para "
        + ((datosOriginales == null) ? "crear" : "actualizar") + "ProyectoPeriodoSeguimiento");

    // Se comprueba la existencia del proyecto
    Proyecto proyecto = proyectoRepository.findById(datosProyectoPeriodoSeguimiento.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(datosProyectoPeriodoSeguimiento.getProyectoId()));

    if (proyecto.getEstado() != null && proyecto.getEstado().getEstado() == EstadoProyecto.Estado.CONCEDIDO) {
      Assert.notNull(datosProyectoPeriodoSeguimiento.getFechaInicioPresentacion(),
          "FechaInicioPresentacion no puede ser null para " + ((datosOriginales == null) ? "crear" : "actualizar")
              + " ProyectoPeriodoSeguimiento");

      Assert.notNull(datosProyectoPeriodoSeguimiento.getFechaFinPresentacion(),
          "FechaFinPresentacion no puede ser null para " + ((datosOriginales == null) ? "crear" : "actualizar")
              + " ProyectoPeriodoSeguimiento");
    }

    log.debug(
        "validarRequeridosProyectoPeriodoSeguimiento(ProyectoPeriodoSeguimiento datosProyectoPeriodoSeguimiento, ProyectoPeriodoSeguimiento datosOriginales) - end");

  }
}
