package org.crue.hercules.sgi.eti.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.ConvocatoriaReunionDatosGenerales;
import org.crue.hercules.sgi.eti.exceptions.ConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.repository.ActaRepository;
import org.crue.hercules.sgi.eti.repository.ConvocatoriaReunionRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.specification.ConvocatoriaReunionSpecifications;
import org.crue.hercules.sgi.eti.service.ComunicadosService;
import org.crue.hercules.sgi.eti.service.ConvocatoriaReunionService;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ConvocatoriaReunion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaReunionServiceImpl implements ConvocatoriaReunionService {

  private final SgiConfigProperties sgiConfigProperties;
  private final ConvocatoriaReunionRepository repository;
  private final ActaRepository actaRepository;
  private final EvaluacionRepository evaluacionRepository;
  private final ComunicadosService comunicadosService;

  public ConvocatoriaReunionServiceImpl(SgiConfigProperties sgiConfigProperties,
      ConvocatoriaReunionRepository repository, ActaRepository actaRepository,
      EvaluacionRepository evaluacionRepository, ComunicadosService comunicadosService) {
    this.sgiConfigProperties = sgiConfigProperties;
    this.repository = repository;
    this.actaRepository = actaRepository;
    this.evaluacionRepository = evaluacionRepository;
    this.comunicadosService = comunicadosService;
  }

  /**
   * Crea {@link ConvocatoriaReunion}.
   *
   * @param convocatoriaReunion La entidad {@link ConvocatoriaReunion} a crear.
   * @return La entidad {@link ConvocatoriaReunion} creada.
   * @throws IllegalArgumentException Si la entidad {@link ConvocatoriaReunion}
   *                                  tiene id.
   */
  @Override
  @Transactional
  public ConvocatoriaReunion create(ConvocatoriaReunion convocatoriaReunion) {
    log.debug("create(ConvocatoriaReunion convocatoriaReunion) - start");
    Assert.isNull(convocatoriaReunion.getId(),
        "ConvocatoriaReunion id debe ser null para crear una nueva ConvocatoriaReunion");

    ConvocatoriaReunion ultimaConvocatoriaReunionComite = repository
        .findFirstByComiteIdOrderByNumeroActaDesc(convocatoriaReunion.getComite().getId());
    Long numeroActa = ultimaConvocatoriaReunionComite != null ? ultimaConvocatoriaReunionComite.getNumeroActa() + 1
        : 1L;

    convocatoriaReunion.setNumeroActa(numeroActa);
    convocatoriaReunion
        .setAnio(Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).get(ChronoField.YEAR));

    ConvocatoriaReunion returnValue = repository.save(convocatoriaReunion);
    log.debug("create(ConvocatoriaReunion convocatoriaReunion) - end");
    return returnValue;
  }

  /**
   * Actualiza {@link ConvocatoriaReunion}.
   *
   * @param convocatoriaReunionActualizar La entidad {@link ConvocatoriaReunion} a
   *                                      actualizar.
   * @return La entidad {@link ConvocatoriaReunion} actualizada.
   * @throws ConvocatoriaReunionNotFoundException Si no existe ninguna entidad
   *                                              {@link ConvocatoriaReunion} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si la entidad
   *                                              {@link ConvocatoriaReunion}
   *                                              entidad no tiene id.
   */
  @Override
  @Transactional
  public ConvocatoriaReunion update(final ConvocatoriaReunion convocatoriaReunionActualizar) {
    log.debug("update(ConvocatoriaReunion convocatoriaReunionActualizar) - start");

    Assert.notNull(convocatoriaReunionActualizar.getId(),
        "ConvocatoriaReunion id id no puede ser null para actualizar una convocatoriaReunion");

    return repository.findById(convocatoriaReunionActualizar.getId()).map(convocatoriaReunion -> {
      convocatoriaReunion.setComite(convocatoriaReunionActualizar.getComite());
      convocatoriaReunion.setFechaEvaluacion(convocatoriaReunionActualizar.getFechaEvaluacion());
      convocatoriaReunion.setFechaLimite(convocatoriaReunionActualizar.getFechaLimite());
      convocatoriaReunion.setLugar(convocatoriaReunionActualizar.getLugar());
      convocatoriaReunion.setOrdenDia(convocatoriaReunionActualizar.getOrdenDia());
      convocatoriaReunion.setAnio(convocatoriaReunionActualizar.getAnio());
      convocatoriaReunion.setNumeroActa(convocatoriaReunionActualizar.getNumeroActa());
      convocatoriaReunion.setTipoConvocatoriaReunion(convocatoriaReunionActualizar.getTipoConvocatoriaReunion());
      convocatoriaReunion.setHoraInicio(convocatoriaReunionActualizar.getHoraInicio());
      convocatoriaReunion.setMinutoInicio(convocatoriaReunionActualizar.getMinutoInicio());
      convocatoriaReunion.setHoraInicioSegunda(convocatoriaReunionActualizar.getHoraInicioSegunda());
      convocatoriaReunion.setMinutoInicioSegunda(convocatoriaReunionActualizar.getMinutoInicioSegunda());
      convocatoriaReunion.setFechaEnvio(convocatoriaReunionActualizar.getFechaEnvio());
      convocatoriaReunion.setActivo(convocatoriaReunionActualizar.getActivo());

      ConvocatoriaReunion returnValue = repository.save(convocatoriaReunion);
      log.debug("update(ConvocatoriaReunion convocatoriaReunionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaReunionNotFoundException(convocatoriaReunionActualizar.getId()));
  }

  /**
   * Elimina todas las entidades {@link ConvocatoriaReunion}.
   *
   */
  @Override
  @Transactional
  public void deleteAll() {
    log.debug("deleteAll() - start");
    repository.deleteAll();
    log.debug("deleteAll() - end");
  }

  /**
   * Elimina {@link ConvocatoriaReunion} por id.
   *
   * @param id El id de la entidad {@link ConvocatoriaReunion}.
   * @throws ConvocatoriaReunionNotFoundException Si no existe ninguna entidad
   *                                              {@link ConvocatoriaReunion} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si no se informa Id.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id, "ConvocatoriaReunion id no puede ser null para eliminar una convocatoriaReunion");
    if (!repository.existsById(id)) {
      throw new ConvocatoriaReunionNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las entidades {@link ConvocatoriaReunion} filtradas y paginadas según
   * los criterios de búsqueda.
   *
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link ConvocatoriaReunion} paginadas y
   *         filtradas.
   */
  @Override
  public Page<ConvocatoriaReunion> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<ConvocatoriaReunion> specs = ConvocatoriaReunionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaReunion> returnValue = repository.findAll(specs, paging);

    log.debug("findAll(String query, Pageable paging) - end");

    return returnValue;
  }

  /**
   * Obtiene {@link ConvocatoriaReunion} por id.
   *
   * @param id El id de la entidad {@link ConvocatoriaReunion}.
   * @return La entidad {@link ConvocatoriaReunion}.
   * @throws ConvocatoriaReunionNotFoundException Si no existe ninguna entidad
   *                                              {@link ConvocatoriaReunion} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si no se informa Id.
   */
  @Override
  public ConvocatoriaReunion findById(final Long id) {
    log.debug("findById(final Long id) - start");
    Assert.notNull(id, "ConvocatoriaReunion id no puede ser null para buscar una convocatoriaReunion por Id");
    final ConvocatoriaReunion convocatoriaReunion = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaReunionNotFoundException(id));
    log.debug("findById(final Long id) - end");
    return convocatoriaReunion;
  }

  /**
   * Obtiene {@link ConvocatoriaReunionDatosGenerales} por id con el número de
   * evaluaciones activas que no son revisión mínima.
   *
   * @param id El id de la entidad {@link ConvocatoriaReunionDatosGenerales}.
   * @return La entidad {@link ConvocatoriaReunionDatosGenerales}.
   * @throws ConvocatoriaReunionNotFoundException Si no existe ninguna entidad
   *                                              {@link ConvocatoriaReunionDatosGenerales}
   *                                              con ese id.
   * @throws IllegalArgumentException             Si no se informa Id.
   */
  @Override
  public ConvocatoriaReunionDatosGenerales findByIdWithDatosGenerales(final Long id) {
    log.debug("findByIdWithDatosGenerales(final Long id) - start");
    Assert.notNull(id, "ConvocatoriaReunion id no puede ser null para buscar una ConvocatoriaReunion por Id");
    final ConvocatoriaReunionDatosGenerales convocatoriaReunion = repository.findByIdWithDatosGenerales(id)
        .orElseThrow(() -> new ConvocatoriaReunionNotFoundException(id));
    log.debug("findByIdWithDatosGenerales(final Long id) - end");
    return convocatoriaReunion;
  }

  /**
   * Devuelve una lista de convocatorias de reunión que no tengan acta
   *
   * @return la lista de convocatorias de reunión
   */

  @Override
  public List<ConvocatoriaReunion> findConvocatoriasSinActa() {
    log.debug("findConvocatoriasSinActa() - start");

    List<ConvocatoriaReunion> convocatoriaReunion = repository.findConvocatoriasReunionSinActa();

    log.debug("findConvocatoriasSinActa() - end");

    return convocatoriaReunion;

  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ConvocatoriaReunion} puede ser eliminada.
   *
   * @param id Id del {@link ConvocatoriaReunion}.
   * @return true si puede ser eliminada / false si no puede ser eliminada
   */
  @Override
  public Boolean eliminable(Long id) {
    return !actaRepository.existsByConvocatoriaReunionId(id) && !evaluacionRepository.existsByConvocatoriaReunionId(id)
        ? true
        : false;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ConvocatoriaReunion} puede ser modificada.
   *
   * @param id Id de la {@link ConvocatoriaReunion}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  @Override
  public Boolean modificable(Long id) {
    log.debug("modificable(Long id) - start - end");
    return !actaRepository.existsByConvocatoriaReunionIdAndEstadoActualId(id, Constantes.TIPO_ESTADO_ACTA_FINALIZADA);
  }

  /**
   * Retorna la fecha convocatoria y acta (codigo convocatoria) de la última
   * evaluación de tipo memoria de la memoria original (y que no sea revisión
   * mínima)
   * 
   * @param idEvaluacion Id de la {@link Evaluacion}
   * @param idDictamen   Id del {@link Dictamen}
   * @return ConvocatoriaReunion
   */
  @Override
  public ConvocatoriaReunion findConvocatoriaUltimaEvaluacionTipoMemoria(Long idEvaluacion, Long idDictamen) {
    log.debug("findConvocatoriaUltimaEvaluacionTipoMemoria(Long idEvaluacion, idDictamen) - start - end");
    return repository.findConvocatoriaUltimaEvaluacionTipoMemoria(idEvaluacion, idDictamen);
  }

  /**
   * Permite enviar el comunicado de {@link ConvocatoriaReunion}
   *
   * @param idConvocatoria Id del {@link ConvocatoriaReunion}.
   * @return true si puede ser enviado / false si no puede ser enviado
   */
  @Override
  @Transactional
  public Boolean enviarComunicado(Long idConvocatoria) {
    log.debug("enviarComunicado(Long idConvocatoria) - start");
    ConvocatoriaReunion convocatoriaReunion = this.findById(idConvocatoria);
    convocatoriaReunion.setFechaEnvio(Instant.now());
    this.update(convocatoriaReunion);
    try {
      this.comunicadosService.enviarComunicadoConvocatoriaReunionEti(convocatoriaReunion);
      log.debug("enviarComunicado(Long idConvocatoria) - end");
      return true;
    } catch (JsonProcessingException e) {
      log.debug("Error - enviarComunicado(Long idConvocatoria)", e);
      return false;
    }
  }
}
