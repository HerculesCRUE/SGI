package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.AsistentesNotFoundException;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.repository.AsistentesRepository;
import org.crue.hercules.sgi.eti.service.AsistentesService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Asistentes}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class AsistentesServiceImpl implements AsistentesService {
  private final AsistentesRepository asistentesRepository;

  public AsistentesServiceImpl(AsistentesRepository asistentesRepository) {
    this.asistentesRepository = asistentesRepository;
  }

  /**
   * Guarda la entidad {@link Asistentes}.
   *
   * @param asistentes la entidad {@link Asistentes} a guardar.
   * @return la entidad {@link Asistentes} persistida.
   */
  @Transactional
  public Asistentes create(Asistentes asistentes) {
    log.debug("Petición a create Asistentes : {} - start", asistentes);
    Assert.isNull(asistentes.getId(), "Asistentes id tiene que ser null para crear un nuevo asistente");

    return asistentesRepository.save(asistentes);
  }

  /**
   * Obtiene todas las entidades {@link Asistentes} paginadas y/o filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Asistentes} paginadas y filtradas.
   */
  public Page<Asistentes> findAll(String query, Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Specification<Asistentes> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Asistentes> returnValue = asistentesRepository.findAll(specs, paging);
    log.debug("findAll(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades paginadas {@link Asistentes} activas para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Asistentes} paginadas.
   */
  public Page<Asistentes> findAllByConvocatoriaReunionId(Long id, Pageable pageable) {
    log.debug("findAllByConvocatoriaReunionId(Long id, Pageable pageable) - start");
    Page<Asistentes> returnValue = asistentesRepository.findAllByConvocatoriaReunionId(id, pageable);
    log.debug("findAllByConvocatoriaReunionId(Long id, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Asistentes} por id.
   *
   * @param id el id de la entidad {@link Asistentes}.
   * @return la entidad {@link Asistentes}.
   * @throws AsistentesNotFoundException Si no existe ningún {@link Asistentes}e
   *                                     con ese id.
   */
  public Asistentes findById(final Long id) throws AsistentesNotFoundException {
    log.debug("Petición a get Asistentes : {}  - start", id);
    final Asistentes Asistentes = asistentesRepository.findById(id)
        .orElseThrow(() -> new AsistentesNotFoundException(id));
    log.debug("Petición a get Asistentes : {}  - end", id);
    return Asistentes;

  }

  /**
   * Elimina una entidad {@link Asistentes} por id.
   *
   * @param id el id de la entidad {@link Asistentes}.
   */
  @Transactional
  public void delete(Long id) throws AsistentesNotFoundException {
    log.debug("Petición a delete Asistentes : {}  - start", id);
    Assert.notNull(id, "El id de Asistentes no puede ser null.");
    if (!asistentesRepository.existsById(id)) {
      throw new AsistentesNotFoundException(id);
    }
    asistentesRepository.deleteById(id);
    log.debug("Petición a delete Asistentes : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Asistentes}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Asistentes: {} - start");
    asistentesRepository.deleteAll();
    log.debug("Petición a deleteAll de Asistentes: {} - end");

  }

  /**
   * Actualiza los datos del {@link Asistentes}.
   * 
   * @param asistentesActualizar {@link Asistentes} con los datos actualizados.
   * @return El {@link Asistentes} actualizado.
   * @throws AsistentesNotFoundException Si no existe ningún {@link Asistentes}
   *                                     con ese id.
   * @throws IllegalArgumentException    Si el {@link Asistentes} no tiene id.
   */

  @Transactional
  public Asistentes update(final Asistentes asistentesActualizar) {
    log.debug("update(Asistentes AsistentesActualizar) - start");

    Assert.notNull(asistentesActualizar.getId(), "Asistentes id no puede ser null para actualizar un asistente");

    return asistentesRepository.findById(asistentesActualizar.getId()).map(asistentes -> {
      asistentes.setEvaluador(asistentesActualizar.getEvaluador());
      asistentes.setConvocatoriaReunion(asistentesActualizar.getConvocatoriaReunion());
      asistentes.setAsistencia(asistentesActualizar.getAsistencia());
      asistentes.setMotivo(asistentesActualizar.getMotivo());

      Asistentes returnValue = asistentesRepository.save(asistentes);
      log.debug("update(Asistentes asistentesActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new AsistentesNotFoundException(asistentesActualizar.getId()));
  }

}
