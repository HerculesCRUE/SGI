package org.crue.hercules.sgi.eti.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.exceptions.EvaluadorNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.repository.EvaluadorRepository;
import org.crue.hercules.sgi.eti.repository.specification.EvaluadorSpecifications;
import org.crue.hercules.sgi.eti.service.EvaluadorService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Evaluador}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EvaluadorServiceImpl implements EvaluadorService {
  private static final Long PRESIDENTE = 1L;
  private static final Long SECRETARIO = 3L;
  private final EvaluadorRepository evaluadorRepository;
  private final SgiConfigProperties sgiConfigProperties;

  public EvaluadorServiceImpl(EvaluadorRepository evaluadorRepository, SgiConfigProperties sgiConfigProperties) {
    this.evaluadorRepository = evaluadorRepository;
    this.sgiConfigProperties = sgiConfigProperties;
  }

  /**
   * Guarda la entidad {@link Evaluador}.
   *
   * @param evaluador la entidad {@link Evaluador} a guardar.
   * @return la entidad {@link Evaluador} persistida.
   */
  @Transactional
  @Override
  public Evaluador create(Evaluador evaluador) {
    log.debug("Petición a create Evaluador : {} - start", evaluador);
    Assert.isNull(evaluador.getId(), "Evaluador id tiene que ser null para crear un nuevo evaluador");

    // Si el evaluador a crear es presidente se ha de mirar que no coincida el
    // presidente en el rango de fechas de los presidentes existentes
    if (evaluador.getCargoComite().getId().equals(PRESIDENTE) || evaluador.getCargoComite().getId()
        .equals(SECRETARIO)) {
      Assert.isTrue(
          isPresidenteOrSecretarioInFechasOk(evaluador),
          evaluador.getCargoComite().getId().equals(PRESIDENTE)
              ? "Existen presidentes entre las fechas seleccionadas"
              : "Existen secretarios entre las fechas seleccionadas");
    } else {
      // Un evaluador no puede estar en el mismo comité en el mismo rango de fechas
      Assert.isTrue(isEvaluadorInFechasOk(evaluador),
          "Existe otro evaluador en el mismo comité entre las fechas seleccionadas");
    }

    return evaluadorRepository.save(evaluador);
  }

  /**
   * Evalua si existe otro presidente en el mismo rango de fechas en el mismo
   * comité
   * 
   * @param evaluador el objeto {@link Evaluador}
   * @return true or false si el presidente está en el rango de fechas correcto
   */
  public Boolean isPresidenteOrSecretarioInFechasOk(Evaluador evaluador) {
    Specification<Evaluador> specActivos = EvaluadorSpecifications.activos();
    Specification<Evaluador> specPresidentesOrSecretarios = null;
    if (evaluador.getCargoComite().getId().equals(PRESIDENTE)) {
      specPresidentesOrSecretarios = EvaluadorSpecifications.presidentes();
    } else {
      specPresidentesOrSecretarios = EvaluadorSpecifications.secretarios();
    }
    Specification<Evaluador> specInFechas = EvaluadorSpecifications.inFechas(evaluador.getFechaAlta(),
        evaluador.getFechaBaja());
    Specification<Evaluador> specComite = EvaluadorSpecifications.byComite(evaluador.getComite().getComite());
    Specification<Evaluador> specs = Specification.where(specActivos).and(
        specPresidentesOrSecretarios).and(specInFechas)
        .and(specComite);

    Specification<Evaluador> specFechaBajaNull = EvaluadorSpecifications.byFechaBajaNull();
    Specification<Evaluador> specsFechaBajaNull = Specification.where(specActivos).and(
        specPresidentesOrSecretarios)
        .and(specFechaBajaNull).and(specComite);

    List<Evaluador> evaluadoresFechaBajaNull = evaluadorRepository.findAll(specsFechaBajaNull).stream()
        .filter(eval -> evaluador.getId() != null ? !Objects.equals(eval.getId(), evaluador.getId()) : true)
        .filter(
            eval -> evaluador.getFechaBaja() != null ? eval.getFechaAlta().isBefore(evaluador.getFechaBaja()) : true)
        .collect(Collectors.toList());

    if (evaluadoresFechaBajaNull.isEmpty()) {
      List<Evaluador> returnValue = evaluadorRepository.findAll(specs).stream()
          .filter(eval -> evaluador.getId() != null ? !Objects.equals(eval.getId(), evaluador.getId()) : true)
          .collect(Collectors.toList());
      // Si existen registros en las fechas en las que opera el nuevo presidente, el
      // rango de fechas es incorrecto
      return returnValue.isEmpty();
    } else {
      return false;
    }

  }

  /**
   * Evalua si existe el mismo evaluador en el mismo rango de fechas en el mismo
   * comité
   * 
   * @param evaluador el objeto {@link Evaluador}
   * @return true or false si el evaluador cumple con las condiciones
   */
  public Boolean isEvaluadorInFechasOk(Evaluador evaluador) {
    Specification<Evaluador> specActivos = EvaluadorSpecifications.activos();
    Specification<Evaluador> specInFechas = EvaluadorSpecifications.inFechas(evaluador.getFechaAlta(),
        evaluador.getFechaBaja());
    Specification<Evaluador> specPersonaRef = EvaluadorSpecifications.byPersonaRef(evaluador.getPersonaRef());
    Specification<Evaluador> specComite = EvaluadorSpecifications.byComite(evaluador.getComite().getComite());
    Specification<Evaluador> specs = Specification.where(specActivos).and(specPersonaRef).and(specInFechas)
        .and(specComite);

    Specification<Evaluador> specFechaBajaNull = EvaluadorSpecifications.byFechaBajaNull();
    Specification<Evaluador> specsFechaBajaNull = Specification.where(specActivos).and(specPersonaRef)
        .and(specFechaBajaNull).and(specComite);

    List<Evaluador> evaluadoresFechaBajaNull = evaluadorRepository.findAll(specsFechaBajaNull).stream()
        .filter(eval -> evaluador.getId() != null ? !Objects.equals(eval.getId(), evaluador.getId()) : true)
        .filter(
            eval -> evaluador.getFechaBaja() != null ? eval.getFechaAlta().isBefore(evaluador.getFechaBaja()) : true)
        .collect(Collectors.toList());

    if (evaluadoresFechaBajaNull.isEmpty()) {
      List<Evaluador> returnValue = evaluadorRepository.findAll(specs).stream()
          .filter(eval -> evaluador.getId() != null ? !Objects.equals(eval.getId(), evaluador.getId()) : true)
          .collect(Collectors.toList());
      return returnValue.isEmpty();
    } else {
      return false;
    }
  }

  /**
   * Obtiene todas las entidades {@link Evaluador} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Evaluador} paginadas y filtradas.
   */
  @Override
  public Page<Evaluador> findAll(String query, Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Specification<Evaluador> specs = EvaluadorSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<Evaluador> returnValue = evaluadorRepository.findAll(specs, paging);
    log.debug("findAll(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Devuelve los evaluadores activos del comité indicado que no entre en
   * conflicto de intereses con ningún miembro del equipo investigador de la
   * memoria.
   * 
   * @param idComite        Identificador del {@link Comite}
   * @param idMemoria       Identificador de la {@link Memoria}
   * @param fechaEvaluacion la fecha de Evaluación de la
   *                        {@link ConvocatoriaReunion}
   * @return lista de evaluadores sin conflictos de intereses
   */
  @Override
  public List<Evaluador> findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria,
      Instant fechaEvaluacion) {
    log.debug("findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria) - start");
    List<Evaluador> returnValue = evaluadorRepository.findAllByComiteSinconflictoInteresesMemoria(idComite, idMemoria,
        fechaEvaluacion);
    log.debug("findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Evaluador} por id.
   *
   * @param id el id de la entidad {@link Evaluador}.
   * @return la entidad {@link Evaluador}.
   * @throws EvaluadorNotFoundException Si no existe ningún {@link Evaluador} con
   *                                    ese id.
   */
  @Override
  public Evaluador findById(final Long id) throws EvaluadorNotFoundException {
    log.debug("Petición a get Evaluador : {}  - start", id);
    final Evaluador evaluador = evaluadorRepository.findById(id).orElseThrow(() -> new EvaluadorNotFoundException(id));
    log.debug("Petición a get Evaluador : {}  - end", id);
    return evaluador;

  }

  /**
   * Elimina una entidad {@link Evaluador} por id.
   *
   * @param id el id de la entidad {@link Evaluador}.
   */
  @Transactional
  @Override
  public void delete(Long id) throws EvaluadorNotFoundException {
    log.debug("Petición a delete Evaluador : {}  - start", id);
    Assert.notNull(id, "El id de Evaluador no puede ser null.");
    if (!evaluadorRepository.existsById(id)) {
      throw new EvaluadorNotFoundException(id);
    }
    evaluadorRepository.deleteById(id);
    log.debug("Petición a delete Evaluador : {}  - end", id);
  }

  /**
   * 
   * @param evaluadorActualizar {@link Evaluador} con los datos actualizados.
   * @return El {@link Evaluador} actualizado.
   * @throws EvaluadorNotFoundException Si no existe ningún {@link Evaluador} con
   *                                    ese id.
   * @throws IllegalArgumentException   Si el {@link Evaluador} no tiene id.
   */

  @Transactional
  @Override
  public Evaluador update(final Evaluador evaluadorActualizar) {
    log.debug("update(Evaluador evaluadorActualizar) - start");

    Assert.notNull(evaluadorActualizar.getId(), "Evaluador id no puede ser null para actualizar un evaluador");

    // Si el evaluador a crear es presidente se ha de mirar que no coincida el
    // presidente en el rango de fechas de los presidentes existentes
    if (evaluadorActualizar.getCargoComite().getId().equals(PRESIDENTE)
        || evaluadorActualizar.getCargoComite().getId()
            .equals(SECRETARIO)) {
      Assert.isTrue(
          isPresidenteOrSecretarioInFechasOk(
              evaluadorActualizar),
          evaluadorActualizar.getCargoComite().getId().equals(PRESIDENTE)
              ? "Existen presidentes entre las fechas seleccionadas"
              : "Existen secretarios entre las fechas seleccionadas");
    } else {
      // Un evaluador no puede estar en el mismo comité en el mismo rango de fechas
      Assert.isTrue(isEvaluadorInFechasOk(evaluadorActualizar),
          "Existe otro evaluador en el mismo comité entre las fechas seleccionadas");
    }

    return evaluadorRepository.findById(evaluadorActualizar.getId()).map(evaluador -> {
      evaluador.setCargoComite(evaluadorActualizar.getCargoComite());
      evaluador.setComite(evaluadorActualizar.getComite());
      evaluador.setFechaAlta(evaluadorActualizar.getFechaAlta());
      evaluador.setFechaBaja(evaluadorActualizar.getFechaBaja());
      evaluador.setResumen(evaluadorActualizar.getResumen());
      evaluador.setPersonaRef(evaluadorActualizar.getPersonaRef());
      evaluador.setActivo(evaluadorActualizar.getActivo());

      Evaluador returnValue = evaluadorRepository.save(evaluador);
      log.debug("update(Evaluador evaluadorActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new EvaluadorNotFoundException(evaluadorActualizar.getId()));
  }

  /**
   * Devuelve los evaluadores activos del comité indicado
   * 
   * @param comite nombre del {@link Comite}
   * @return lista de evaluadores
   */
  @Override
  public List<Evaluador> findAllByComite(String comite) {
    log.debug("findAllByComite(String comite) - start");
    Instant fechaActual = Instant.now();
    Specification<Evaluador> specActivos = EvaluadorSpecifications.activos();
    Specification<Evaluador> specInFechas = EvaluadorSpecifications.inFechas(fechaActual, fechaActual);
    Specification<Evaluador> specFechaBajaNull = EvaluadorSpecifications.byFechaBajaNull();
    Specification<Evaluador> specComite = EvaluadorSpecifications.byComite(comite);

    Specification<Evaluador> specs = Specification.where(specActivos).and(
        specComite).and((specInFechas).or(specFechaBajaNull));

    List<Evaluador> returnValue = evaluadorRepository.findAll(specs);
    log.debug("findAllByComite(String comite) - end");
    return returnValue;
  }

  /**
   * Busca un secretario {@link Evaluador} activo dentro de la fecha indicada
   *
   * @param fecha  la fecha de actividad del secretario.
   * @param comite el nombre del {@link Comite} al que pertenece
   * @return el secretario {@link Evaluador}
   */
  @Override
  public Evaluador findSecretarioInFechaAndComite(Instant fecha, String comite) {
    log.debug("findSecretarioInFechaAndComite(Instant fecha, String comite) - start");
    Evaluador secretario = null;
    Specification<Evaluador> specActivos = EvaluadorSpecifications.activos();
    Specification<Evaluador> specSecretarios = EvaluadorSpecifications.secretarios();
    Specification<Evaluador> specFecha = EvaluadorSpecifications.between(fecha);
    Specification<Evaluador> specFechaBajaNull = EvaluadorSpecifications.byFechaBajaNull();
    Specification<Evaluador> specComite = EvaluadorSpecifications.byComite(comite);

    Specification<Evaluador> specsFechaBajaNull = Specification.where(specActivos).and(specSecretarios)
        .and(specFechaBajaNull).and(specComite);

    Specification<Evaluador> specs = Specification.where(specActivos).and(specSecretarios).and(specFecha)
        .and(specComite);

    Optional<Evaluador> secretarioFechaBajaNull = evaluadorRepository.findAll(specsFechaBajaNull).stream()
        .filter(
            eval -> fecha != null ? eval.getFechaAlta().isBefore(fecha) : true)
        .findFirst();

    if (!secretarioFechaBajaNull.isPresent()) {
      Optional<Evaluador> secretarioInFecha = evaluadorRepository.findAll(specs).stream().findFirst();
      secretario = secretarioInFecha.isPresent() ? secretarioInFecha.get() : null;
    } else {
      secretario = secretarioFechaBajaNull.get();
    }
    log.info("fecha: " + fecha + "comite: " + comite + (secretario != null ? secretario.getPersonaRef() : null));
    log.debug("findSecretarioInFechaAndComite(Instant fecha, String comite) - end");
    return secretario;
  }

  /**
   * Comprueba si la persona es evaluador activo en algun {@link Comite}
   * 
   * @param personaRef identificador de la persona
   * @return si es evaluador o no
   */
  @Override
  public boolean isEvaluador(String personaRef) {
    log.debug("isEvaluador({}) - start", personaRef);
    Specification<Evaluador> specActivos = EvaluadorSpecifications.activos();
    Specification<Evaluador> specByPersonaRef = EvaluadorSpecifications.byPersonaRef(personaRef);
    Specification<Evaluador> specs = Specification.where(specActivos).and(specByPersonaRef);

    boolean isEvaluador = evaluadorRepository.count(specs) > 0;

    log.debug("isEvaluador({}) - end", personaRef);
    return isEvaluador;
  }

  /**
   * Comprueba si la persona correspondiente al evaluador esta activa en el
   * {@link Comite}
   * 
   * @param evaluadorId identificador del {@link Evaluador}
   * @param comiteId    identificador del {@link Comite}
   * @return si esta activo o no
   */
  @Override
  public boolean isEvaluadorActivoComite(Long evaluadorId, Long comiteId) {
    log.debug("isEvaluadorActivoComite(Long evaluadorId, Long comiteId) - start");
    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    Specification<Evaluador> specActivos = EvaluadorSpecifications.activos();
    Specification<Evaluador> specByPersonaRef = EvaluadorSpecifications.byPersonaRefEvaluadorId(evaluadorId);
    Specification<Evaluador> specByComite = EvaluadorSpecifications.byComiteId(comiteId);
    Specification<Evaluador> specActivoByFecha = EvaluadorSpecifications.activoByFecha(fechaActual);
    Specification<Evaluador> specs = Specification.where(specActivos).and(specByPersonaRef).and(specByComite)
        .and(specActivoByFecha);

    boolean isEvaluador = evaluadorRepository.count(specs) > 0;

    log.debug("isEvaluadorActivoComite(Long evaluadorId, Long comiteId) - end");
    return isEvaluador;
  }

}
