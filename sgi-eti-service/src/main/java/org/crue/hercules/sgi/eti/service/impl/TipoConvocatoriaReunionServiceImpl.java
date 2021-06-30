package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.TipoConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.repository.TipoConvocatoriaReunionRepository;
import org.crue.hercules.sgi.eti.repository.specification.TipoConvocatoriaReunionSpecifications;
import org.crue.hercules.sgi.eti.service.TipoConvocatoriaReunionService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoConvocatoriaReunion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoConvocatoriaReunionServiceImpl implements TipoConvocatoriaReunionService {
  private final TipoConvocatoriaReunionRepository tipoConvocatoriaReunionRepository;

  public TipoConvocatoriaReunionServiceImpl(TipoConvocatoriaReunionRepository tipoConvocatoriaReunionRepository) {
    this.tipoConvocatoriaReunionRepository = tipoConvocatoriaReunionRepository;
  }

  /**
   * Guarda la entidad {@link TipoConvocatoriaReunion}.
   *
   * @param tipoConvocatoriaReunion la entidad {@link TipoConvocatoriaReunion} a
   *                                guardar.
   * @return la entidad {@link TipoConvocatoriaReunion} persistida.
   */
  @Transactional
  public TipoConvocatoriaReunion create(TipoConvocatoriaReunion tipoConvocatoriaReunion) {
    log.debug("Petición a create TipoConvocatoriaReunion : {} - start", tipoConvocatoriaReunion);
    Assert.notNull(tipoConvocatoriaReunion.getId(),
        "TipoConvocatoriaReunion id no puede ser null para crear un nuevo tipoConvocatoriaReunion");

    return tipoConvocatoriaReunionRepository.save(tipoConvocatoriaReunion);
  }

  /**
   * Obtiene todas las entidades {@link TipoConvocatoriaReunion} paginadas y
   * filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link TipoConvocatoriaReunion} paginadas y
   *         filtradas.
   */
  public Page<TipoConvocatoriaReunion> findAll(String query, Pageable paging) {
    log.debug("findAllTipoConvocatoriaReunion(String query,Pageable paging) - start");
    Specification<TipoConvocatoriaReunion> specs = TipoConvocatoriaReunionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoConvocatoriaReunion> returnValue = tipoConvocatoriaReunionRepository.findAll(specs, paging);
    log.debug("findAllTipoConvocatoriaReunion(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoConvocatoriaReunion} por id.
   *
   * @param id el id de la entidad {@link TipoConvocatoriaReunion}.
   * @return la entidad {@link TipoConvocatoriaReunion}.
   * @throws TipoConvocatoriaReunionNotFoundException Si no existe ningún
   *                                                  {@link TipoConvocatoriaReunion}e
   *                                                  con ese id.
   */
  public TipoConvocatoriaReunion findById(final Long id) throws TipoConvocatoriaReunionNotFoundException {
    log.debug("Petición a get TipoConvocatoriaReunion : {}  - start", id);
    final TipoConvocatoriaReunion tipoConvocatoriaReunion = tipoConvocatoriaReunionRepository.findById(id)
        .orElseThrow(() -> new TipoConvocatoriaReunionNotFoundException(id));
    log.debug("Petición a get TipoConvocatoriaReunion : {}  - end", id);
    return tipoConvocatoriaReunion;

  }

  /**
   * Elimina una entidad {@link TipoConvocatoriaReunion} por id.
   *
   * @param id el id de la entidad {@link TipoConvocatoriaReunion}.
   */
  @Transactional
  public void delete(Long id) throws TipoConvocatoriaReunionNotFoundException {
    log.debug("Petición a delete TipoConvocatoriaReunion : {}  - start", id);
    Assert.notNull(id, "El id de TipoConvocatoriaReunion no puede ser null.");
    if (!tipoConvocatoriaReunionRepository.existsById(id)) {
      throw new TipoConvocatoriaReunionNotFoundException(id);
    }
    tipoConvocatoriaReunionRepository.deleteById(id);
    log.debug("Petición a delete TipoConvocatoriaReunion : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link TipoConvocatoriaReunion}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de TipoConvocatoriaReunion: {} - start");
    tipoConvocatoriaReunionRepository.deleteAll();
    log.debug("Petición a deleteAll de TipoConvocatoriaReunion: {} - end");

  }

  /**
   * Actualiza los datos del {@link TipoConvocatoriaReunion}.
   * 
   * @param tipoConvocatoriaReunionActualizar {@link TipoConvocatoriaReunion} con
   *                                          los datos actualizados.
   * @return El {@link TipoConvocatoriaReunion} actualizado.
   * @throws TipoConvocatoriaReunionNotFoundException Si no existe ningún
   *                                                  {@link TipoConvocatoriaReunion}
   *                                                  con ese id.
   * @throws IllegalArgumentException                 Si el
   *                                                  {@link TipoConvocatoriaReunion}
   *                                                  no tiene id.
   */

  @Transactional
  public TipoConvocatoriaReunion update(final TipoConvocatoriaReunion tipoConvocatoriaReunionActualizar) {
    log.debug("update(TipoConvocatoriaReunion tipoConvocatoriaReunionActualizar) - start");

    Assert.notNull(tipoConvocatoriaReunionActualizar.getId(),
        "TipoConvocatoriaReunion id no puede ser null para actualizar un tipo comentario");

    return tipoConvocatoriaReunionRepository.findById(tipoConvocatoriaReunionActualizar.getId())
        .map(tipoConvocatoriaReunion -> {
          tipoConvocatoriaReunion.setNombre(tipoConvocatoriaReunionActualizar.getNombre());
          tipoConvocatoriaReunion.setActivo(tipoConvocatoriaReunionActualizar.getActivo());

          TipoConvocatoriaReunion returnValue = tipoConvocatoriaReunionRepository.save(tipoConvocatoriaReunion);
          log.debug("update(TipoConvocatoriaReunion tipoConvocatoriaReunionActualizar) - end");
          return returnValue;
        }).orElseThrow(() -> new TipoConvocatoriaReunionNotFoundException(tipoConvocatoriaReunionActualizar.getId()));
  }

}
