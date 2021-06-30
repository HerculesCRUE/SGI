package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.TipoEstadoActaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.repository.TipoEstadoActaRepository;
import org.crue.hercules.sgi.eti.repository.specification.TipoEstadoActaSpecifications;
import org.crue.hercules.sgi.eti.service.TipoEstadoActaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoEstadoActa}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoEstadoActaServiceImpl implements TipoEstadoActaService {
  private final TipoEstadoActaRepository tipoEstadoActaRepository;

  public TipoEstadoActaServiceImpl(TipoEstadoActaRepository tipoEstadoActaRepository) {
    this.tipoEstadoActaRepository = tipoEstadoActaRepository;
  }

  /**
   * Guarda la entidad {@link TipoEstadoActa}.
   *
   * @param tipoEstadoActa la entidad {@link TipoEstadoActa} a guardar.
   * @return la entidad {@link TipoEstadoActa} persistida.
   */
  @Transactional
  public TipoEstadoActa create(TipoEstadoActa tipoEstadoActa) {
    log.debug("Petición a create TipoEstadoActa : {} - start", tipoEstadoActa);
    Assert.notNull(tipoEstadoActa.getId(), "TipoEstadoActa id no puede ser null para crear un nuevo TipoEstadoActa");

    return tipoEstadoActaRepository.save(tipoEstadoActa);
  }

  /**
   * Obtiene todas las entidades {@link TipoEstadoActa} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link TipoEstadoActa} paginadas y filtradas.
   */
  public Page<TipoEstadoActa> findAll(String query, Pageable paging) {
    log.debug("findAllTipoEstadoActa(String query,Pageable paging) - start");
    Specification<TipoEstadoActa> specs = TipoEstadoActaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoEstadoActa> returnValue = tipoEstadoActaRepository.findAll(specs, paging);
    log.debug("findAllTipoEstadoActa(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoEstadoActa} por id.
   *
   * @param id el id de la entidad {@link TipoEstadoActa}.
   * @return la entidad {@link TipoEstadoActa}.
   * @throws TipoEstadoActaNotFoundException Si no existe ningún
   *                                         {@link TipoEstadoActa}e con ese id.
   */
  public TipoEstadoActa findById(final Long id) throws TipoEstadoActaNotFoundException {
    log.debug("Petición a get TipoEstadoActa : {}  - start", id);
    final TipoEstadoActa tipoEstadoActa = tipoEstadoActaRepository.findById(id)
        .orElseThrow(() -> new TipoEstadoActaNotFoundException(id));
    log.debug("Petición a get TipoEstadoActa : {}  - end", id);
    return tipoEstadoActa;

  }

  /**
   * Elimina una entidad {@link TipoEstadoActa} por id.
   *
   * @param id el id de la entidad {@link TipoEstadoActa}.
   */
  @Transactional
  public void delete(Long id) throws TipoEstadoActaNotFoundException {
    log.debug("Petición a delete TipoEstadoActa : {}  - start", id);
    Assert.notNull(id, "El id de TipoEstadoActa no puede ser null.");
    if (!tipoEstadoActaRepository.existsById(id)) {
      throw new TipoEstadoActaNotFoundException(id);
    }
    tipoEstadoActaRepository.deleteById(id);
    log.debug("Petición a delete TipoEstadoActa : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link TipoEstadoActa}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de TipoEstadoActa: {} - start");
    tipoEstadoActaRepository.deleteAll();
    log.debug("Petición a deleteAll de TipoEstadoActa: {} - end");

  }

  /**
   * Actualiza los datos del {@link TipoEstadoActa}.
   * 
   * @param tipoEstadoActaActualizar {@link TipoEstadoActa} con los datos
   *                                 actualizados.
   * @return El {@link TipoEstadoActa} actualizado.
   * @throws TipoEstadoActaNotFoundException Si no existe ningún
   *                                         {@link TipoEstadoActa} con ese id.
   * @throws IllegalArgumentException        Si el {@link TipoEstadoActa} no tiene
   *                                         id.
   */

  @Transactional
  public TipoEstadoActa update(final TipoEstadoActa tipoEstadoActaActualizar) {
    log.debug("update(TipoEstadoActa TipoEstadoActaActualizar) - start");

    Assert.notNull(tipoEstadoActaActualizar.getId(),
        "TipoEstadoActa id no puede ser null para actualizar un tipo estado acta");

    return tipoEstadoActaRepository.findById(tipoEstadoActaActualizar.getId()).map(tipoEstadoActa -> {
      tipoEstadoActa.setNombre(tipoEstadoActaActualizar.getNombre());
      tipoEstadoActa.setActivo(tipoEstadoActaActualizar.getActivo());

      TipoEstadoActa returnValue = tipoEstadoActaRepository.save(tipoEstadoActa);
      log.debug("update(TipoEstadoActa tipoEstadoActaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoEstadoActaNotFoundException(tipoEstadoActaActualizar.getId()));
  }

}