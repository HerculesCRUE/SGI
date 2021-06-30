package org.crue.hercules.sgi.eti.service.impl;

import java.util.Optional;

import org.crue.hercules.sgi.eti.exceptions.ConflictoInteresNotFoundException;
import org.crue.hercules.sgi.eti.model.ConflictoInteres;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.repository.ConflictoInteresRepository;
import org.crue.hercules.sgi.eti.repository.EvaluadorRepository;
import org.crue.hercules.sgi.eti.service.ConflictoInteresService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ConflictoInteres}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConflictoInteresServiceImpl implements ConflictoInteresService {
  private final ConflictoInteresRepository conflictoInteresRepository;
  private final EvaluadorRepository evaluadorRepository;

  public ConflictoInteresServiceImpl(ConflictoInteresRepository conflictoInteresRepository,
      EvaluadorRepository evaluadorRepository) {
    this.conflictoInteresRepository = conflictoInteresRepository;
    this.evaluadorRepository = evaluadorRepository;
  }

  /**
   * Guarda la entidad {@link ConflictoInteres}.
   *
   * @param conflictoInteres la entidad {@link ConflictoInteres} a guardar.
   * @return la entidad {@link ConflictoInteres} persistida.
   */
  @Transactional
  public ConflictoInteres create(ConflictoInteres conflictoInteres) {
    log.debug("Petición a create ConflictoInteres : {} - start", conflictoInteres);
    Assert.notNull(conflictoInteres.getEvaluador().getId(),
        "ConflictoInteres el id de evaluador no puede ser null para crear un nuevo conflictoInteres");
    Optional<Evaluador> evaluador = evaluadorRepository.findById(conflictoInteres.getEvaluador().getId());
    if (!evaluador.isPresent()) {
      throw new ConflictoInteresNotFoundException(conflictoInteres.getId());
    }
    conflictoInteres.setEvaluador(evaluador.get());
    return conflictoInteresRepository.save(conflictoInteres);
  }

  /**
   * Obtiene todas las entidades {@link ConflictoInteres} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link ConflictoInteres} paginadas y
   *         filtradas.
   */
  public Page<ConflictoInteres> findAll(String query, Pageable paging) {
    log.debug("findAllConflictoInteres(String query,Pageable paging) - start");
    Specification<ConflictoInteres> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<ConflictoInteres> returnValue = conflictoInteresRepository.findAll(specs, paging);

    log.debug("findAllConflictoInteres(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link ConflictoInteres} por id.
   *
   * @param id el id de la entidad {@link ConflictoInteres}.
   * @return la entidad {@link ConflictoInteres}.
   * @throws ConflictoInteresNotFoundException Si no existe ningún
   *                                           {@link ConflictoInteres}e con ese
   *                                           id.
   */
  public ConflictoInteres findById(final Long id) throws ConflictoInteresNotFoundException {
    log.debug("Petición a get ConflictoInteres : {}  - start", id);
    final ConflictoInteres ConflictoInteres = conflictoInteresRepository.findById(id)
        .orElseThrow(() -> new ConflictoInteresNotFoundException(id));
    log.debug("Petición a get ConflictoInteres : {}  - end", id);
    return ConflictoInteres;

  }

  /**
   * Elimina una entidad {@link ConflictoInteres} por id.
   *
   * @param id el id de la entidad {@link ConflictoInteres}.
   */
  @Transactional
  public void delete(Long id) throws ConflictoInteresNotFoundException {
    log.debug("Petición a delete ConflictoInteres : {}  - start", id);
    Assert.notNull(id, "El id de ConflictoInteres no puede ser null.");
    if (!conflictoInteresRepository.existsById(id)) {
      throw new ConflictoInteresNotFoundException(id);
    }
    conflictoInteresRepository.deleteById(id);
    log.debug("Petición a delete ConflictoInteres : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link ConflictoInteres}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de ConflictoInteres: {} - start");
    conflictoInteresRepository.deleteAll();
    log.debug("Petición a deleteAll de ConflictoInteres: {} - end");

  }

  /**
   * Actualiza los datos del {@link ConflictoInteres}.
   * 
   * @param conflictoInteresActualizar {@link ConflictoInteres} con los datos
   *                                   actualizados.
   * @return El {@link ConflictoInteres} actualizado.
   * @throws ConflictoInteresNotFoundException Si no existe ningún
   *                                           {@link ConflictoInteres} con ese
   *                                           id.
   * @throws IllegalArgumentException          Si el {@link ConflictoInteres} no
   *                                           tiene id.
   */

  @Transactional
  public ConflictoInteres update(final ConflictoInteres conflictoInteresActualizar) {
    log.debug("update(ConflictoInteres ConflictoInteresActualizar) - start");

    Assert.notNull(conflictoInteresActualizar.getId(),
        "ConflictoInteres id no puede ser null para actualizar un conflicto de interés");

    return conflictoInteresRepository.findById(conflictoInteresActualizar.getId()).map(conflictoInteres -> {
      conflictoInteres.setEvaluador(conflictoInteresActualizar.getEvaluador());
      conflictoInteres.setPersonaConflictoRef(conflictoInteresActualizar.getPersonaConflictoRef());

      ConflictoInteres returnValue = conflictoInteresRepository.save(conflictoInteres);
      log.debug("update(ConflictoInteres conflictoInteresActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConflictoInteresNotFoundException(conflictoInteresActualizar.getId()));
  }

  /**
   * Obtiene todas las entidades paginadas {@link ConflictoInteres} para un
   * determinado {@link Evaluador}.
   *
   * @param id       Id de {@link Evaluador}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link ConflictoInteres} paginadas.
   */
  @Override
  public Page<ConflictoInteres> findAllByEvaluadorId(Long id, Pageable pageable) {
    log.debug("findAllByEvaluadorId(Long id, Pageable pageable) - start");
    Page<ConflictoInteres> returnValue = conflictoInteresRepository.findAllByEvaluadorId(id, pageable);
    log.debug("findAllByEvaluadorId(Long id, Pageable pageable) - end");
    return returnValue;
  }

}
