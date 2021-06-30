package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.EstadoMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.service.EstadoMemoriaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link EstadoMemoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EstadoMemoriaServiceImpl implements EstadoMemoriaService {
  private final EstadoMemoriaRepository estadoMemoriaRepository;

  public EstadoMemoriaServiceImpl(EstadoMemoriaRepository estadoMemoriaRepository) {
    this.estadoMemoriaRepository = estadoMemoriaRepository;
  }

  /**
   * Guarda la entidad {@link EstadoMemoria}.
   *
   * @param estadoMemoria la entidad {@link EstadoMemoria} a guardar.
   * @return la entidad {@link EstadoMemoria} persistida.
   */
  @Transactional
  public EstadoMemoria create(EstadoMemoria estadoMemoria) {
    log.debug("Petición a create EstadoMemoria : {} - start", estadoMemoria);
    Assert.isNull(estadoMemoria.getId(), "EstadoMemoria id tiene que ser null para crear un nuevo estadoMemoria");

    return estadoMemoriaRepository.save(estadoMemoria);
  }

  /**
   * Obtiene todas las entidades {@link EstadoMemoria} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link EstadoMemoria} paginadas y filtradas.
   */
  public Page<EstadoMemoria> findAll(String query, Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Specification<EstadoMemoria> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<EstadoMemoria> returnValue = estadoMemoriaRepository.findAll(specs, paging);
    log.debug("findAll(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link EstadoMemoria} por id.
   *
   * @param id el id de la entidad {@link EstadoMemoria}.
   * @return la entidad {@link EstadoMemoria}.
   * @throws EstadoMemoriaNotFoundException Si no existe ningún
   *                                        {@link EstadoMemoria} con ese id.
   */
  public EstadoMemoria findById(final Long id) throws EstadoMemoriaNotFoundException {
    log.debug("Petición a get EstadoMemoria : {}  - start", id);
    final EstadoMemoria EstadoMemoria = estadoMemoriaRepository.findById(id)
        .orElseThrow(() -> new EstadoMemoriaNotFoundException(id));
    log.debug("Petición a get EstadoMemoria : {}  - end", id);
    return EstadoMemoria;

  }

  /**
   * Elimina una entidad {@link EstadoMemoria} por id.
   *
   * @param id el id de la entidad {@link EstadoMemoria}.
   */
  @Transactional
  public void delete(Long id) throws EstadoMemoriaNotFoundException {
    log.debug("Petición a delete EstadoMemoria : {}  - start", id);
    Assert.notNull(id, "El id de EstadoMemoria no puede ser null.");
    if (!estadoMemoriaRepository.existsById(id)) {
      throw new EstadoMemoriaNotFoundException(id);
    }
    estadoMemoriaRepository.deleteById(id);
    log.debug("Petición a delete EstadoMemoria : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link EstadoMemoria}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de EstadoMemoria: {} - start");
    estadoMemoriaRepository.deleteAll();
    log.debug("Petición a deleteAll de EstadoMemoria: {} - end");

  }

  /**
   * Actualiza los datos del {@link EstadoMemoria}.
   * 
   * @param estadoMemoriaActualizar {@link EstadoMemoria} con los datos
   *                                actualizados.
   * @return El {@link EstadoMemoria} actualizado.
   * @throws EstadoMemoriaNotFoundException Si no existe ningún
   *                                        {@link EstadoMemoria} con ese id.
   * @throws IllegalArgumentException       Si el {@link EstadoMemoria} no tiene
   *                                        id.
   */

  @Transactional
  public EstadoMemoria update(final EstadoMemoria estadoMemoriaActualizar) {
    log.debug("update(EstadoMemoria EstadoMemoriaActualizar) - start");

    Assert.notNull(estadoMemoriaActualizar.getId(),
        "EstadoMemoria id no puede ser null para actualizar un estado memoria");

    return estadoMemoriaRepository.findById(estadoMemoriaActualizar.getId()).map(estadoMemoria -> {
      estadoMemoria.setMemoria(estadoMemoriaActualizar.getMemoria());
      estadoMemoria.setTipoEstadoMemoria(estadoMemoriaActualizar.getTipoEstadoMemoria());

      EstadoMemoria returnValue = estadoMemoriaRepository.save(estadoMemoria);
      log.debug("update(EstadoMemoria estadoMemoriaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new EstadoMemoriaNotFoundException(estadoMemoriaActualizar.getId()));
  }

}
