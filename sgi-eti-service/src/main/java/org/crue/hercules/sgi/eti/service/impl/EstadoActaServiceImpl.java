package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.EstadoActaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.TareaNotFoundException;
import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.crue.hercules.sgi.eti.repository.EstadoActaRepository;
import org.crue.hercules.sgi.eti.service.EstadoActaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link EstadoActa}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EstadoActaServiceImpl implements EstadoActaService {
  private final EstadoActaRepository estadoActaRepository;

  public EstadoActaServiceImpl(EstadoActaRepository estadoActaRepository) {
    this.estadoActaRepository = estadoActaRepository;
  }

  /**
   * Guarda la entidad {@link EstadoActa}.
   *
   * @param estadoActa la entidad {@link EstadoActa} a guardar.
   * @return la entidad {@link EstadoActa} persistida.
   */
  @Transactional
  public EstadoActa create(EstadoActa estadoActa) {
    log.debug("Petición a create EstadoActa : {} - start", estadoActa);
    Assert.isNull(estadoActa.getId(), "EstadoActa id tiene que ser null para crear un nuevo estadoActa");

    return estadoActaRepository.save(estadoActa);
  }

  /**
   * Obtiene todas las entidades {@link EstadoActa} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link EstadoActa} paginadas y filtradas.
   */
  public Page<EstadoActa> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<EstadoActa> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<EstadoActa> returnValue = estadoActaRepository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link EstadoActa} por id.
   *
   * @param id el id de la entidad {@link EstadoActa}.
   * @return la entidad {@link EstadoActa}.
   * @throws EstadoActaNotFoundException Si no existe ningún {@link EstadoActa}
   *                                     con ese id.
   */
  public EstadoActa findById(final Long id) throws TareaNotFoundException {
    log.debug("Petición a get EstadoActa : {}  - start", id);
    final EstadoActa estadoActa = estadoActaRepository.findById(id)
        .orElseThrow(() -> new EstadoActaNotFoundException(id));
    log.debug("Petición a get EstadoActa : {}  - end", id);
    return estadoActa;

  }

  /**
   * Elimina una entidad {@link EstadoActa} por id.
   *
   * @param id el id de la entidad {@link EstadoActa}.
   * @throws EstadoActaNotFoundException Si no existe ningún {@link EstadoActa}
   *                                     con ese id.
   */
  @Transactional
  public void delete(Long id) throws TareaNotFoundException {
    log.debug("Petición a delete EstadoActa : {}  - start", id);
    Assert.notNull(id, "El id de EstadoActa no puede ser null.");
    if (!estadoActaRepository.existsById(id)) {
      throw new EstadoActaNotFoundException(id);
    }
    estadoActaRepository.deleteById(id);
    log.debug("Petición a delete EstadoActa : {}  - end", id);
  }

  /**
   * Actualiza los datos del {@link EstadoActa}.
   * 
   * @param estadoActaActualizar {@link EstadoActa} con los datos actualizados.
   * @return El {@link EstadoActa} actualizado.
   * @throws EstadoActaNotFoundException Si no existe ningún {@link EstadoActa}
   *                                     con ese id.
   * @throws IllegalArgumentException    Si el {@link EstadoActa} no tiene id.
   */
  @Transactional
  public EstadoActa update(final EstadoActa estadoActaActualizar) {
    log.debug("update(EstadoActa estadoActaActualizar) - start");

    Assert.notNull(estadoActaActualizar.getId(), "EstadoActa id no puede ser null para actualizar un estado acta");

    return estadoActaRepository.findById(estadoActaActualizar.getId()).map(estadoActa -> {
      estadoActa.setActa(estadoActaActualizar.getActa());
      estadoActa.setTipoEstadoActa(estadoActaActualizar.getTipoEstadoActa());
      estadoActa.setFechaEstado(estadoActaActualizar.getFechaEstado());

      EstadoActa returnValue = estadoActaRepository.save(estadoActa);
      log.debug("update(EstadoActa estadoActaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new EstadoActaNotFoundException(estadoActaActualizar.getId()));
  }

}
