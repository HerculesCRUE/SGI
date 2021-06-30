package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.exceptions.EvaluadorNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
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
  private final EvaluadorRepository evaluadorRepository;

  public EvaluadorServiceImpl(EvaluadorRepository evaluadorRepository) {
    this.evaluadorRepository = evaluadorRepository;
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
    if (evaluador.getCargoComite().getNombre().toLowerCase().equals("presidente")) {
      Assert.isTrue(isPresidenteInFechasOk(evaluador), "Existen presidentes entre las fechas seleccionadas");
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
  public Boolean isPresidenteInFechasOk(Evaluador evaluador) {
    Specification<Evaluador> specActivos = EvaluadorSpecifications.activos();
    Specification<Evaluador> specPresidentes = EvaluadorSpecifications.presidentes();
    Specification<Evaluador> specInFechas = EvaluadorSpecifications.inFechas(evaluador.getFechaAlta(),
        evaluador.getFechaBaja());
    Specification<Evaluador> specComite = EvaluadorSpecifications.byComite(evaluador.getComite().getComite());
    Specification<Evaluador> specs = Specification.where(specActivos).and(specPresidentes).and(specInFechas)
        .and(specComite);

    Specification<Evaluador> specFechaBajaNull = EvaluadorSpecifications.byFechaBajaNull();
    Specification<Evaluador> specsFechaBajaNull = Specification.where(specActivos).and(specPresidentes)
        .and(specFechaBajaNull).and(specComite);

    List<Evaluador> evaluadoresFechaBajaNull = evaluadorRepository.findAll(specsFechaBajaNull).stream()
        .filter(eval -> evaluador.getId() != null ? eval.getId() != evaluador.getId() : true)
        .filter(
            eval -> evaluador.getFechaBaja() != null ? eval.getFechaAlta().isBefore(evaluador.getFechaBaja()) : true)
        .collect(Collectors.toList());

    if (evaluadoresFechaBajaNull.size() == 0) {
      List<Evaluador> returnValue = evaluadorRepository.findAll(specs).stream()
          .filter(eval -> evaluador.getId() != null ? eval.getId() != evaluador.getId() : true)
          .collect(Collectors.toList());
      // Si existen registros en las fechas en las que opera el nuevo presidente, el
      // rango de fechas es incorrecto
      return !(returnValue.size() > 0);
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
        .filter(eval -> evaluador.getId() != null ? eval.getId() != evaluador.getId() : true)
        .filter(
            eval -> evaluador.getFechaBaja() != null ? eval.getFechaAlta().isBefore(evaluador.getFechaBaja()) : true)
        .collect(Collectors.toList());

    if (evaluadoresFechaBajaNull.size() == 0) {
      List<Evaluador> returnValue = evaluadorRepository.findAll(specs).stream()
          .filter(eval -> evaluador.getId() != null ? eval.getId() != evaluador.getId() : true)
          .collect(Collectors.toList());
      return !(returnValue.size() > 0);
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
   * @param idComite  Identificador del {@link Comite}
   * @param idMemoria Identificador de la {@link Memoria}
   * @return lista de evaluadores sin conflictos de intereses
   */
  @Override
  public List<Evaluador> findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria) {
    log.debug("findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria) - start");
    List<Evaluador> returnValue = evaluadorRepository.findAllByComiteSinconflictoInteresesMemoria(idComite, idMemoria);
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
    final Evaluador Evaluador = evaluadorRepository.findById(id).orElseThrow(() -> new EvaluadorNotFoundException(id));
    log.debug("Petición a get Evaluador : {}  - end", id);
    return Evaluador;

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
    if (evaluadorActualizar.getCargoComite().getNombre().toLowerCase().equals("presidente")) {
      Assert.isTrue(isPresidenteInFechasOk(evaluadorActualizar), "Existen presidentes entre las fechas seleccionadas");
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

}
