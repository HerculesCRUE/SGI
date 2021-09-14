package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.csp.exceptions.PeriodoLongerThanSolicitudProyectoException;
import org.crue.hercules.sgi.csp.exceptions.PeriodoWrongOrderException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoSocioPeriodoJustificacionSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
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
 * {@link SolicitudProyectoSocioPeriodoJustificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoSocioPeriodoJustificacionServiceImpl
    implements SolicitudProyectoSocioPeriodoJustificacionService {

  private final Validator validator;
  private final SolicitudProyectoSocioPeriodoJustificacionRepository repository;
  private final SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;
  private final SolicitudService solicitudService;
  private final SolicitudProyectoRepository solicitudProyectoRepository;

  public SolicitudProyectoSocioPeriodoJustificacionServiceImpl(Validator validator,
      SolicitudProyectoSocioPeriodoJustificacionRepository repository,
      SolicitudProyectoSocioRepository solicitudProyectoSocioRepository, SolicitudService solicitudService,
      SolicitudProyectoRepository solicitudProyectoRepository) {
    this.validator = validator;
    this.repository = repository;
    this.solicitudProyectoSocioRepository = solicitudProyectoSocioRepository;
    this.solicitudService = solicitudService;
    this.solicitudProyectoRepository = solicitudProyectoRepository;
  }

  /**
   * Actualiza los datos del {@link SolicitudProyectoSocioPeriodoJustificacion}.
   * 
   * @param solicitudProyectoSocioId Id de la {@link SolicitudProyectoSocio}.
   * @param periodos                 lista con los nuevos
   *                                 {@link SolicitudProyectoSocioPeriodoJustificacion}
   *                                 a guardar.
   * @return {@link SolicitudProyectoSocioPeriodoJustificacion} actualizado.
   */
  @Override
  @Transactional
  public List<SolicitudProyectoSocioPeriodoJustificacion> update(Long solicitudProyectoSocioId,
      List<SolicitudProyectoSocioPeriodoJustificacion> periodos) {

    log.debug(
        "update(Long solicitudProyectoSocioId, List<SolicitudProyectoSocioPeriodoJustificacion> periodos) - start");

    if (periodos.isEmpty()) {
      // Fast check
      repository.deleteInBulkBySolicitudProyectoSocioId(solicitudProyectoSocioId);
      return new ArrayList<>();
    }

    // Recuperamos la solicitud
    SolicitudProyectoSocio solicitud = solicitudProyectoSocioRepository.findById(solicitudProyectoSocioId)
        .orElseThrow(() -> new SolicitudProyectoSocioNotFoundException(solicitudProyectoSocioId));

    // Comprobar si la solicitud es modificable
    SolicitudProyecto solicitudProyecto = solicitudProyectoRepository.findById(solicitud.getSolicitudProyectoId())
        .orElseThrow(() -> new SolicitudProyectoNotFoundException(solicitud.getSolicitudProyectoId()));
    Assert.isTrue(solicitudService.modificable(solicitudProyecto.getId()),
        "No se puede modificar SolicitudProyectoSocioPeriodoJustificacion");

    // Comprobamos la consistencia y preparamos para la actualización los
    // SolicitudProyectoSocioPeriodoJustificacion recibidos
    checkAndSetupPeriodos(solicitud, solicitudProyecto, periodos);

    // Recuperamos los SolicitudProyectoSocioPeriodoJustificacion asociados a la
    // solicitud existentes en base de datos
    List<SolicitudProyectoSocioPeriodoJustificacion> periodosBD = repository
        .findAllBySolicitudProyectoSocioId(solicitudProyectoSocioId);

    // Id's de periodos a modificar (tienen id)
    List<Long> idsPeriodosModificados = periodos.stream().map(SolicitudProyectoSocioPeriodoJustificacion::getId)
        .filter(id -> id != null).collect(Collectors.toList());

    // Id's de periodos existentes en base de datos
    List<Long> idsPeriodosExistentes = periodosBD.stream().map(SolicitudProyectoSocioPeriodoJustificacion::getId)
        .collect(Collectors.toList());

    // Se valida que los periodos a modificar existan en base de datos
    for (Long id : idsPeriodosModificados) {
      if (!idsPeriodosExistentes.contains(id)) {
        throw new SolicitudProyectoSocioPeriodoJustificacionNotFoundException(id);
      }
    }

    // Periodos a eliminar (existen en base de datos pero no están entre los que se
    // van a modificar)
    List<SolicitudProyectoSocioPeriodoJustificacion> periodosEliminar = periodosBD.stream()
        .filter(periodo -> !idsPeriodosModificados.contains(periodo.getId())).collect(Collectors.toList());

    // Eliminamos los periodos no existentes en la nueva lista
    if (!periodosEliminar.isEmpty()) {
      repository.deleteAll(periodosEliminar);
    }

    // Actualizamos los registros modificados (tienen id) y creamos los no
    // existentes (no tienen id)
    List<SolicitudProyectoSocioPeriodoJustificacion> returnValue = repository.saveAll(periodos);

    log.debug(
        "update(Long solicitudProyectoSocioId,  List<SolicitudProyectoSocioPeriodoJustificacion> periodos) - end");

    return returnValue;

  }

  /**
   * Comprueba la existencia del
   * {@link SolicitudProyectoSocioPeriodoJustificacion} por id.
   *
   * @param id el id de la entidad
   *           {@link SolicitudProyectoSocioPeriodoJustificacion}.
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
   * Obtiene una entidad {@link SolicitudProyectoSocioPeriodoJustificacion} por
   * id.
   * 
   * @param id Identificador de la entidad
   *           {@link SolicitudProyectoSocioPeriodoJustificacion}.
   * @return SolicitudProyectoSocioPeriodoJustificacion la entidad
   *         {@link SolicitudProyectoSocioPeriodoJustificacion}.
   */
  @Override
  public SolicitudProyectoSocioPeriodoJustificacion findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoSocioPeriodoJustificacion returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoSocioPeriodoJustificacionNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudProyectoSocioPeriodoJustificacion}.
   *
   * @param id Id del {@link SolicitudProyectoSocioPeriodoJustificacion}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "SolicitudProyectoSocioPeriodoJustificacion id no puede ser null para eliminar un SolicitudProyectoSocioPeriodoJustificacion");
    if (!repository.existsById(id)) {
      throw new SolicitudProyectoSocioPeriodoJustificacionNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene las {@link SolicitudProyectoSocioPeriodoJustificacion} para un
   * {@link Solicitud}.
   *
   * @param solicitudProyectoSocioId el id del {@link SolicitudProyectoSocio}.
   * @param query                    la información del filtro.
   * @param paging                   la información de la paginación.
   * @return la lista de entidades
   *         {@link SolicitudProyectoSocioPeriodoJustificacion} del
   *         {@link SolicitudProyectoSocio} paginadas.
   */
  @Override
  public Page<SolicitudProyectoSocioPeriodoJustificacion> findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId,
      String query, Pageable paging) {
    log.debug("findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query, Pageable paging) - start");

    Specification<SolicitudProyectoSocioPeriodoJustificacion> specs = SolicitudProyectoSocioPeriodoJustificacionSpecifications
        .bySolicitudId(solicitudProyectoSocioId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoSocioPeriodoJustificacion> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Valida la consistencia de la lista de
   * SolicitudProyectoSocioPeriodoJustificacion a asignar a una
   * SolicitudProyectoSocio y la prepara para la actualización según las
   * siguientes reglas:
   * <ul>
   * <li>El primer periodo siempre comenzará en el mes 1</li>
   * <li>No pueden existir saltos de meses entre periodos</li>
   * <li>Mes fin debe ser mayor o igual que mes inicio</li>
   * <li>El mes final no puede ser superior a la duración en meses indicada en la
   * SolicitudProyectoSocio</li>
   * <li>La fecha de fin de presentación debe ser mayor o igual que la de inicio
   * de presentación</li>
   * <li>Si incluye identificador de SolicitudProyectoSocio, debe ser el correcto.
   * Si no lo incluye, se lo establecemos.</li>
   * <li>Se actualiza el numero de periodo para que sea consecutivo por fecha de
   * inicio</li>
   * </ul>
   * 
   * @param solicitud         SolicitudProyectoSocio
   * @param solicitudProyecto SolicitudProyecto
   * @param periodos          SolicitudProyectoSocioPeriodoJustificaciones
   */
  private void checkAndSetupPeriodos(SolicitudProyectoSocio solicitud, SolicitudProyecto solicitudProyecto,
      List<SolicitudProyectoSocioPeriodoJustificacion> periodos) {
    if (periodos.size() == 0) {
      // Fast check
      return;
    }
    // Ordena los periodos por mesInicial
    periodos.sort(Comparator.comparing(SolicitudProyectoSocioPeriodoJustificacion::getMesInicial));

    Integer mesFinal = 0;
    for (int i = 0; i < periodos.size(); i++) {
      SolicitudProyectoSocioPeriodoJustificacion periodo = periodos.get(i);
      // Validado por anotaciones en la entidad
      /*
       * if (periodo.getMesInicial() .compareTo(periodo.getMesFinal()) > 0) { // Mes
       * fin debe ser mayor o igual que mes inicio }
       */
      // Validado por anotaciones en la entidad
      /*
       * if (periodo.getFechaInicioPresentacion() != null &&
       * periodo.getFechaFinPresentacion() != null && periodo
       * .getFechaInicioPresentacion().compareTo(periodo.getFechaFinPresentacion()) >
       * 0) { // La fecha de fin de presentación debe ser mayor o igual que la de
       * inicio de // presentación }
       */
      // Invocar validaciones anotadas en SolicitudProyectoSocioPeriodoJustificacion
      Set<ConstraintViolation<SolicitudProyectoSocioPeriodoJustificacion>> result = validator.validate(periodo);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }
      if (i == 0 && !periodo.getMesInicial().equals(1)) {
        // El primer periodo siempre comenzará en el mes 1
        throw new PeriodoWrongOrderException();
      }
      if (!mesFinal.equals(periodo.getMesInicial() - 1)) {
        // No pueden existir saltos de meses entre periodos
        throw new PeriodoWrongOrderException();
      }
      if (solicitudProyecto.getDuracion() != null && periodo.getMesFinal() > solicitudProyecto.getDuracion()) {
        // El mes final no puede ser superior a la duración en meses indicada en la
        // solicitud
        throw new PeriodoLongerThanSolicitudProyectoException();
      }

      if (periodo.getSolicitudProyectoSocioId() == null) {
        // Si no incluye identificador de solicitud, se lo establecemos
        periodo.setSolicitudProyectoSocioId(solicitud.getId());
      } else if (!periodo.getSolicitudProyectoSocioId().equals(solicitud.getId())) {
        // Si incluye identificador de solicitud, debe ser el correcto
        throw new NoRelatedEntitiesException(SolicitudProyectoSocioPeriodoJustificacion.class,
            SolicitudProyectoSocio.class);
      }
      // Se actualiza el numero de periodo para que sea consecutivo por fecha de
      // inicio
      periodo.setNumPeriodo(i + 1);

      // Guardamos el mesFinal del periodo actual para comparar con los siguientes
      mesFinal = periodo.getMesFinal();
    }
  }
}