package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPeriodoSeguimientoCientificoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.csp.exceptions.PeriodoLongerThanConvocatoriaException;
import org.crue.hercules.sgi.csp.exceptions.PeriodoWrongOrderException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinalException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaPeriodoSeguimientoCientificoSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoSeguimientoCientificoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion
 * {@link ConvocatoriaPeriodoSeguimientoCientifico}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaPeriodoSeguimientoCientificoServiceImpl
    implements ConvocatoriaPeriodoSeguimientoCientificoService {

  private final Validator validator;
  private final ConvocatoriaPeriodoSeguimientoCientificoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;

  public ConvocatoriaPeriodoSeguimientoCientificoServiceImpl(Validator validator,
      ConvocatoriaPeriodoSeguimientoCientificoRepository repository, ConvocatoriaRepository convocatoriaRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository) {
    this.validator = validator;
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
  }

  /**
   * Actualiza el listado de {@link ConvocatoriaPeriodoSeguimientoCientifico} de
   * la {@link Convocatoria} con el listado
   * convocatoriaPeriodoSeguimientoCientificos añadiendo, editando o eliminando
   * los elementos segun proceda.
   *
   * @param convocatoriaId Id de la {@link Convocatoria}.
   * @param periodos       lista con los nuevos
   *                       {@link ConvocatoriaPeriodoSeguimientoCientifico} a
   *                       guardar.
   * @return la entidad {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *         persistida.
   */
  @Override
  @Transactional
  public List<ConvocatoriaPeriodoSeguimientoCientifico> updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(
      Long convocatoriaId, List<ConvocatoriaPeriodoSeguimientoCientifico> periodos) {
    log.debug(
        "updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(Long convocatoriaId, List<ConvocatoriaPeriodoSeguimientoCientifico> periodos) - start");

    if (periodos.isEmpty()) {
      // Fast check
      repository.deleteInBulkByConvocatoriaId(convocatoriaId);
      return new ArrayList<>();
    }

    // Recuperamos la convocatoria
    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));

    // Comprobamos la consistencia y preparamos para la actualización los
    // ConvocatoriaPeriodoSeguimientoCientifico recibidos
    checkAndSetupPeriodos(convocatoria, periodos);

    // Recuperamos los ConvocatoriaPeriodoSeguimientoCientifico asociados a la
    // convocatoria existentes en base de datos
    List<ConvocatoriaPeriodoSeguimientoCientifico> periodosBD = repository
        .findAllByConvocatoriaIdOrderByMesInicial(convocatoriaId);

    // Id's de periodos a modificar (tienen id)
    List<Long> idsPeriodosModificados = periodos.stream().map(ConvocatoriaPeriodoSeguimientoCientifico::getId)
        .filter(Objects::nonNull).collect(Collectors.toList());

    // Id's de periodos existentes en base de datos
    List<Long> idsPeriodosExistentes = periodosBD.stream().map(ConvocatoriaPeriodoSeguimientoCientifico::getId)
        .collect(Collectors.toList());

    // Se valida que los periodos a modificar existan en base de datos
    for (Long id : idsPeriodosModificados) {
      if (!idsPeriodosExistentes.contains(id)) {
        throw new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(id);
      }
    }

    // Periodos a eliminar (existen en base de datos pero no están entre los que se
    // van a modificar)
    List<ConvocatoriaPeriodoSeguimientoCientifico> periodosEliminar = periodosBD.stream()
        .filter(periodo -> !idsPeriodosModificados.contains(periodo.getId())).collect(Collectors.toList());

    // Eliminamos los periodos no existentes en la nueva lista
    if (!periodosEliminar.isEmpty()) {
      repository.deleteAll(periodosEliminar);
    }

    // Actualizamos los registros modificados (tienen id) y creamos los no
    // existentes (no tienen id)
    List<ConvocatoriaPeriodoSeguimientoCientifico> returnValue = repository.saveAll(periodos);
    log.debug(
        "updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(Long convocatoriaId, List<ConvocatoriaPeriodoSeguimientoCientifico> periodos) - end");

    return returnValue;
  }

  /**
   * Obtiene una entidad {@link ConvocatoriaPeriodoSeguimientoCientifico} por id.
   * 
   * @param id Identificador de la entidad
   *           {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * @return ConvocatoriaPeriodoSeguimientoCientifico la entidad
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   */
  @Override
  public ConvocatoriaPeriodoSeguimientoCientifico findById(Long id) {
    log.debug("findById(Long id) - start");
    final ConvocatoriaPeriodoSeguimientoCientifico returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaPeriodoSeguimientoCientifico} para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaPeriodoSeguimientoCientifico> findAllByConvocatoria(Long convocatoriaId, String query,
      Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));
    if (hasAuthorityViewInvestigador()) {
      ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
          .findByConvocatoriaId(convocatoriaId)
          .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(convocatoriaId));
      if (!convocatoria.getEstado().equals(Estado.REGISTRADA)
          || Boolean.FALSE.equals(configuracionSolicitud.getTramitacionSGI())) {
        throw new UserNotAuthorizedToAccessConvocatoriaException();
      }
    }

    Specification<ConvocatoriaPeriodoSeguimientoCientifico> specs = ConvocatoriaPeriodoSeguimientoCientificoSpecifications
        .byConvocatoriaId(convocatoriaId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaPeriodoSeguimientoCientifico> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Valida la consistencia de la lista de
   * ConvocatoriaPeriodoSeguimientoCientifico a asignar a una Convocatoria y la
   * prepara para la actualización según las siguientes reglas:
   * <ul>
   * <li>El primer periodo siempre comenzará en el mes 1</li>
   * <li>No pueden existir saltos de meses entre periodos</li>
   * <li>Mes fin debe ser mayor o igual que mes inicio</li>
   * <li>El mes final no puede ser superior a la duración en meses indicada en la
   * Convocatoria</li>
   * <li>La fecha de fin de presentación debe ser mayor o igual que la de inicio
   * de presentación</li>
   * <li>El ConvocatoriaPeriodoSeguimientoCientifico de tipo final tiene que ser
   * el último</li>
   * <li>Si incluye identificador de convocatoria, debe ser el correcto. Si no lo
   * incluye, se lo establecemos.</li>
   * <li>Se actualiza el numero de periodo para que sea consecutivo por fecha de
   * inicio</li>
   * </ul>
   * 
   * @param Convocatoria convocatoria
   * @param periodos     ConvocatoriaPeriodoSeguimientoCientificoes
   */
  private void checkAndSetupPeriodos(Convocatoria convocatoria,
      List<ConvocatoriaPeriodoSeguimientoCientifico> periodos) {
    if (periodos.isEmpty()) {
      // Fast check
      return;
    }
    // Ordena los periodos por mesInicial
    periodos.sort(Comparator.comparing(ConvocatoriaPeriodoSeguimientoCientifico::getMesInicial));

    Integer mesFinal = 0;
    TipoSeguimiento tipo = null;
    for (int i = 0; i < periodos.size(); i++) {
      ConvocatoriaPeriodoSeguimientoCientifico periodo = periodos.get(i);

      // Invocar validaciones anotadas en ConvocatoriaPeriodoSeguimientoCientifico
      Set<ConstraintViolation<ConvocatoriaPeriodoSeguimientoCientifico>> result = validator.validate(periodo);
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
      if (tipo != null && tipo == TipoSeguimiento.FINAL) {
        // El ConvocatoriaPeriodoSeguimientoCientifico de tipo final tiene que ser el
        // último
        throw new TipoFinalException();
      }

      if (periodo.getConvocatoriaId() == null) {
        // Si no incluye identificador de convocatoria, se lo establecemos
        periodo.setConvocatoriaId(convocatoria.getId());
      } else if (!periodo.getConvocatoriaId().equals(convocatoria.getId())) {
        // Si incluye identificador de convocatoria, debe ser el correcto
        throw new NoRelatedEntitiesException(ConvocatoriaPeriodoSeguimientoCientifico.class, Convocatoria.class);
      }
      // Se actualiza el numero de periodo para que sea consecutivo por fecha de
      // inicio
      periodo.setNumPeriodo(i + 1);

      // Guardamos el mesFinal del periodo actual para comparar con los siguientes
      mesFinal = periodo.getMesFinal();
      // Guardamos el tipo del periodo actual para comparar con los siguientes
      tipo = periodo.getTipoSeguimiento();
    }
  }

  private boolean hasAuthorityViewInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-CON-INV-V");
  }

}
