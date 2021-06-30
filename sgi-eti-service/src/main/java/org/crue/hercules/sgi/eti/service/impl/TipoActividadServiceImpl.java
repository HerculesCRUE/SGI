package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.TipoActividadNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.repository.TipoActividadRepository;
import org.crue.hercules.sgi.eti.repository.specification.TipoActividadSpecifications;
import org.crue.hercules.sgi.eti.service.TipoActividadService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoActividad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoActividadServiceImpl implements TipoActividadService {
  private final TipoActividadRepository tipoActividadRepository;

  public TipoActividadServiceImpl(TipoActividadRepository tipoActividadRepository) {
    this.tipoActividadRepository = tipoActividadRepository;
  }

  /**
   * Guarda la entidad {@link TipoActividad}.
   *
   * @param tipoActividad la entidad {@link TipoActividad} a guardar.
   * @return la entidad {@link TipoActividad} persistida.
   */
  @Transactional
  public TipoActividad create(TipoActividad tipoActividad) {
    log.debug("Petición a create TipoActividad : {} - start", tipoActividad);
    Assert.notNull(tipoActividad.getId(), "TipoActividad id no puede ser null para crear un nuevo tipoActividad");

    return tipoActividadRepository.save(tipoActividad);
  }

  /**
   * Obtiene todas las entidades {@link TipoActividad} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link TipoActividad} paginadas y filtradas.
   */
  public Page<TipoActividad> findAll(String query, Pageable paging) {
    log.debug("findAllTipoActividad(String query,Pageable paging) - start");
    Specification<TipoActividad> specs = TipoActividadSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoActividad> returnValue = tipoActividadRepository.findAll(specs, paging);
    log.debug("findAllTipoActividad(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoActividad} por id.
   *
   * @param id el id de la entidad {@link TipoActividad}.
   * @return la entidad {@link TipoActividad}.
   * @throws TipoActividadNotFoundException Si no existe ningún
   *                                        {@link TipoActividad}e con ese id.
   */
  public TipoActividad findById(final Long id) throws TipoActividadNotFoundException {
    log.debug("Petición a get TipoActividad : {}  - start", id);
    final TipoActividad TipoActividad = tipoActividadRepository.findById(id)
        .orElseThrow(() -> new TipoActividadNotFoundException(id));
    log.debug("Petición a get TipoActividad : {}  - end", id);
    return TipoActividad;

  }

  /**
   * Elimina una entidad {@link TipoActividad} por id.
   *
   * @param id el id de la entidad {@link TipoActividad}.
   */
  @Transactional
  public void delete(Long id) throws TipoActividadNotFoundException {
    log.debug("Petición a delete TipoActividad : {}  - start", id);
    Assert.notNull(id, "El id de TipoActividad no puede ser null.");
    if (!tipoActividadRepository.existsById(id)) {
      throw new TipoActividadNotFoundException(id);
    }
    tipoActividadRepository.deleteById(id);
    log.debug("Petición a delete TipoActividad : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link TipoActividad}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de TipoActividad: {} - start");
    tipoActividadRepository.deleteAll();
    log.debug("Petición a deleteAll de TipoActividad: {} - end");

  }

  /**
   * Actualiza los datos del {@link TipoActividad}.
   * 
   * @param tipoActividadActualizar {@link TipoActividad} con los datos
   *                                actualizados.
   * @return El {@link TipoActividad} actualizado.
   * @throws TipoActividadNotFoundException Si no existe ningún
   *                                        {@link TipoActividad} con ese id.
   * @throws IllegalArgumentException       Si el {@link TipoActividad} no tiene
   *                                        id.
   */

  @Transactional
  public TipoActividad update(final TipoActividad tipoActividadActualizar) {
    log.debug("update(TipoActividad tipoActividadActualizar) - start");

    Assert.notNull(tipoActividadActualizar.getId(),
        "TipoActividad id no puede ser null para actualizar un tipo actividad");

    return tipoActividadRepository.findById(tipoActividadActualizar.getId()).map(tipoActividad -> {
      tipoActividad.setNombre(tipoActividadActualizar.getNombre());
      tipoActividad.setActivo(tipoActividadActualizar.getActivo());

      TipoActividad returnValue = tipoActividadRepository.save(tipoActividad);
      log.debug("update(TipoActividad tipoActividadActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoActividadNotFoundException(tipoActividadActualizar.getId()));
  }

}
