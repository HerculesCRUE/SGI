package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.TipoEstadoMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.repository.TipoEstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.specification.TipoEstadoMemoriaSpecifications;
import org.crue.hercules.sgi.eti.service.TipoEstadoMemoriaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoEstadoMemoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoEstadoMemoriaServiceImpl implements TipoEstadoMemoriaService {
  private final TipoEstadoMemoriaRepository tipoEstadoMemoriaRepository;

  public TipoEstadoMemoriaServiceImpl(TipoEstadoMemoriaRepository tipoEstadoMemoriaRepository) {
    this.tipoEstadoMemoriaRepository = tipoEstadoMemoriaRepository;
  }

  /**
   * Guarda la entidad {@link TipoEstadoMemoria}.
   *
   * @param tipoEstadoMemoria la entidad {@link TipoEstadoMemoria} a guardar.
   * @return la entidad {@link TipoEstadoMemoria} persistida.
   */
  @Transactional
  public TipoEstadoMemoria create(TipoEstadoMemoria tipoEstadoMemoria) {
    log.debug("Petición a create TipoEstadoMemoria : {} - start", tipoEstadoMemoria);
    Assert.notNull(tipoEstadoMemoria.getId(),
        "TipoEstadoMemoria id no puede ser null para crear un nuevo tipoEstadoMemoria");

    return tipoEstadoMemoriaRepository.save(tipoEstadoMemoria);
  }

  /**
   * Obtiene todas las entidades {@link TipoEstadoMemoria} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link TipoEstadoMemoria} paginadas y
   *         filtradas.
   */
  public Page<TipoEstadoMemoria> findAll(String query, Pageable paging) {
    log.debug("findAllTipoEstadoMemoria(String query,Pageable paging) - start");
    Specification<TipoEstadoMemoria> specs = TipoEstadoMemoriaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoEstadoMemoria> returnValue = tipoEstadoMemoriaRepository.findAll(specs, paging);
    log.debug("findAllTipoEstadoMemoria(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoEstadoMemoria} por id.
   *
   * @param id el id de la entidad {@link TipoEstadoMemoria}.
   * @return la entidad {@link TipoEstadoMemoria}.
   * @throws TipoEstadoMemoriaNotFoundException Si no existe ningún
   *                                            {@link TipoEstadoMemoria}e con ese
   *                                            id.
   */
  public TipoEstadoMemoria findById(final Long id) throws TipoEstadoMemoriaNotFoundException {
    log.debug("Petición a get TipoEstadoMemoria : {}  - start", id);
    final TipoEstadoMemoria TipoEstadoMemoria = tipoEstadoMemoriaRepository.findById(id)
        .orElseThrow(() -> new TipoEstadoMemoriaNotFoundException(id));
    log.debug("Petición a get TipoEstadoMemoria : {}  - end", id);
    return TipoEstadoMemoria;

  }

  /**
   * Elimina una entidad {@link TipoEstadoMemoria} por id.
   *
   * @param id el id de la entidad {@link TipoEstadoMemoria}.
   */
  @Transactional
  public void delete(Long id) throws TipoEstadoMemoriaNotFoundException {
    log.debug("Petición a delete TipoEstadoMemoria : {}  - start", id);
    Assert.notNull(id, "El id de TipoEstadoMemoria no puede ser null.");
    if (!tipoEstadoMemoriaRepository.existsById(id)) {
      throw new TipoEstadoMemoriaNotFoundException(id);
    }
    tipoEstadoMemoriaRepository.deleteById(id);
    log.debug("Petición a delete TipoEstadoMemoria : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link TipoEstadoMemoria}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de TipoEstadoMemoria: {} - start");
    tipoEstadoMemoriaRepository.deleteAll();
    log.debug("Petición a deleteAll de TipoEstadoMemoria: {} - end");

  }

  /**
   * Actualiza los datos del {@link TipoEstadoMemoria}.
   * 
   * @param tipoEstadoMemoriaActualizar {@link TipoEstadoMemoria} con los datos
   *                                    actualizados.
   * @return El {@link TipoEstadoMemoria} actualizado.
   * @throws TipoEstadoMemoriaNotFoundException Si no existe ningún
   *                                            {@link TipoEstadoMemoria} con ese
   *                                            id.
   * @throws IllegalArgumentException           Si el {@link TipoEstadoMemoria} no
   *                                            tiene id.
   */

  @Transactional
  public TipoEstadoMemoria update(final TipoEstadoMemoria tipoEstadoMemoriaActualizar) {
    log.debug("update(TipoEstadoMemoria TipoEstadoMemoriaActualizar) - start");

    Assert.notNull(tipoEstadoMemoriaActualizar.getId(),
        "TipoEstadoMemoria id no puede ser null para actualizar un tipo estado memoria");

    return tipoEstadoMemoriaRepository.findById(tipoEstadoMemoriaActualizar.getId()).map(tipoEstadoMemoria -> {
      tipoEstadoMemoria.setNombre(tipoEstadoMemoriaActualizar.getNombre());
      tipoEstadoMemoria.setActivo(tipoEstadoMemoriaActualizar.getActivo());

      TipoEstadoMemoria returnValue = tipoEstadoMemoriaRepository.save(tipoEstadoMemoria);
      log.debug("update(TipoEstadoMemoria tipoEstadoMemoriaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoEstadoMemoriaNotFoundException(tipoEstadoMemoriaActualizar.getId()));
  }

}
