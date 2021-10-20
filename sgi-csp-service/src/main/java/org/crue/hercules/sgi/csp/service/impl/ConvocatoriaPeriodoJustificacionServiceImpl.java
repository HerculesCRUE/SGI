package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.enums.TipoJustificacion;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.csp.exceptions.PeriodoLongerThanConvocatoriaException;
import org.crue.hercules.sgi.csp.exceptions.PeriodoWrongOrderException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinalException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaPeriodoJustificacionSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link ConvocatoriaPeriodoJustificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaPeriodoJustificacionServiceImpl implements ConvocatoriaPeriodoJustificacionService {

  private final Validator validator;
  private final ConvocatoriaPeriodoJustificacionRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;

  public ConvocatoriaPeriodoJustificacionServiceImpl(Validator validator,
      ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository,
      ConvocatoriaRepository convocatoriaRepository, ConvocatoriaService convocatoriaService) {
    this.validator = validator;
    this.repository = convocatoriaPeriodoJustificacionRepository;
    this.convocatoriaRepository = convocatoriaRepository;
  }

  /**
   * Actualiza el listado de {@link ConvocatoriaPeriodoJustificacion} de la
   * {@link Convocatoria} con el listado convocatoriaPeriodoJustificaciones
   * añadiendo, editando o eliminando los elementos segun proceda.
   *
   * @param convocatoriaId Id de la {@link Convocatoria}.
   * @param periodos       lista con los nuevos
   *                       {@link ConvocatoriaPeriodoJustificacion} a guardar.
   * @return la entidad {@link ConvocatoriaPeriodoJustificacion} persistida.
   */
  @Override
  @Transactional
  public List<ConvocatoriaPeriodoJustificacion> updateConvocatoriaPeriodoJustificacionesConvocatoria(
      Long convocatoriaId, List<ConvocatoriaPeriodoJustificacion> periodos) {
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long convocatoriaId, List<ConvocatoriaPeriodoJustificacion> periodos) - start");

    if (periodos.isEmpty()) {
      // Fast check
      repository.deleteInBulkByConvocatoriaId(convocatoriaId);
      return new ArrayList<>();
    }

    // Recuperamos la convocatoria
    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));

    // Comprobamos la consistencia y preparamos para la actualización los
    // ConvocatoriaPeriodoJustificacion recibidos
    checkAndSetupPeriodos(convocatoria, periodos);

    // Recuperamos los ConvocatoriaPeriodoJustificacion asociados a la convocatoria
    // existentes en base de datos
    List<ConvocatoriaPeriodoJustificacion> periodosBD = repository.findAllByConvocatoriaId(convocatoriaId);

    // Id's de periodos a modificar (tienen id)
    List<Long> idsPeriodosModificados = periodos.stream().map(ConvocatoriaPeriodoJustificacion::getId)
        .filter(id -> id != null).collect(Collectors.toList());

    // Id's de periodos existentes en base de datos
    List<Long> idsPeriodosExistentes = periodosBD.stream().map(ConvocatoriaPeriodoJustificacion::getId)
        .collect(Collectors.toList());

    // Se valida que los periodos a modificar existan en base de datos
    for (Long id : idsPeriodosModificados) {
      if (!idsPeriodosExistentes.contains(id)) {
        throw new ConvocatoriaPeriodoJustificacionNotFoundException(id);
      }
    }

    // Periodos a eliminar (existen en base de datos pero no están entre los que se
    // van a modificar)
    List<ConvocatoriaPeriodoJustificacion> periodosEliminar = periodosBD.stream()
        .filter(periodo -> !idsPeriodosModificados.contains(periodo.getId())).collect(Collectors.toList());

    // Eliminamos los periodos no existentes en la nueva lista
    if (!periodosEliminar.isEmpty()) {
      repository.deleteAll(periodosEliminar);
    }

    // Actualizamos los registros modificados (tienen id) y creamos los no
    // existentes (no tienen id)
    List<ConvocatoriaPeriodoJustificacion> returnValue = repository.saveAll(periodos);
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long convocatoriaId, List<ConvocatoriaPeriodoJustificacion> periodos) - end");

    return returnValue;
  }

  /**
   * Obtiene {@link ConvocatoriaPeriodoJustificacion} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaPeriodoJustificacion}.
   * @return la entidad {@link ConvocatoriaPeriodoJustificacion}.
   */
  @Override
  public ConvocatoriaPeriodoJustificacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaPeriodoJustificacion returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaPeriodoJustificacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaPeriodoJustificacion} para una
   * {@link Convocatoria}.
   *
   * @param idConvocatoria el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaPeriodoJustificacion} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaPeriodoJustificacion> findAllByConvocatoria(Long idConvocatoria, String query,
      Pageable pageable) {
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - start");
    Specification<ConvocatoriaPeriodoJustificacion> specs = ConvocatoriaPeriodoJustificacionSpecifications
        .byConvocatoriaId(idConvocatoria).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaPeriodoJustificacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Valida la consistencia de la lista de ConvocatoriaPeriodoJustificacion a
   * asignar a una Convocatoria y la prepara para la actualización según las
   * siguientes reglas:
   * <ul>
   * <li>El primer periodo siempre comenzará en el mes 1</li>
   * <li>No pueden existir saltos de meses entre periodos</li>
   * <li>Mes fin debe ser mayor o igual que mes inicio</li>
   * <li>El mes final no puede ser superior a la duración en meses indicada en la
   * Convocatoria</li>
   * <li>La fecha de fin de presentación debe ser mayor o igual que la de inicio
   * de presentación</li>
   * <li>El ConvocatoriaPeriodoJustificacion de tipo final tiene que ser el
   * último</li>
   * <li>Si incluye identificador de convocatoria, debe ser el correcto. Si no lo
   * incluye, se lo establecemos.</li>
   * <li>Se actualiza el numero de periodo para que sea consecutivo por fecha de
   * inicio</li>
   * </ul>
   * 
   * @param Convocatoria convocatoria
   * @param periodos     convocatoriaPeriodoJustificaciones
   */
  private void checkAndSetupPeriodos(Convocatoria convocatoria, List<ConvocatoriaPeriodoJustificacion> periodos) {
    if (periodos.size() == 0) {
      // Fast check
      return;
    }
    // Ordena los periodos por mesInicial
    periodos.sort(Comparator.comparing(ConvocatoriaPeriodoJustificacion::getMesInicial));

    Integer mesFinal = 0;
    TipoJustificacion tipo = null;
    for (int i = 0; i < periodos.size(); i++) {
      ConvocatoriaPeriodoJustificacion periodo = periodos.get(i);
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
      // Invocar validaciones anotadas en ConvocatoriaPeriodoJustificacion
      Set<ConstraintViolation<ConvocatoriaPeriodoJustificacion>> result = validator.validate(periodo);
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
      if (convocatoria.getDuracion() != null && periodo.getMesFinal() > convocatoria.getDuracion()) {
        // El mes final no puede ser superior a la duración en meses indicada en la
        // Convocatoria
        throw new PeriodoLongerThanConvocatoriaException();
      }
      if (tipo != null && tipo == TipoJustificacion.FINAL) {
        // El ConvocatoriaPeriodoJustificacion de tipo final tiene que ser el último
        throw new TipoFinalException();
      }

      if (periodo.getConvocatoriaId() == null) {
        // Si no incluye identificador de convocatoria, se lo establecemos
        periodo.setConvocatoriaId(convocatoria.getId());
      } else if (!periodo.getConvocatoriaId().equals(convocatoria.getId())) {
        // Si incluye identificador de convocatoria, debe ser el correcto
        throw new NoRelatedEntitiesException(ConvocatoriaPeriodoJustificacion.class, Convocatoria.class);
      }
      // Se actualiza el numero de periodo para que sea consecutivo por fecha de
      // inicio
      periodo.setNumPeriodo(i + 1);

      // Guardamos el mesFinal del periodo actual para comparar con los siguientes
      mesFinal = periodo.getMesFinal();
      // Guardamos el tipo del periodo actual para comparar con los siguientes
      tipo = periodo.getTipo();
    }
  }
}
