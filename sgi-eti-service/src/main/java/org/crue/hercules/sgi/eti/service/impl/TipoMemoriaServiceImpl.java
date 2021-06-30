package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.TipoMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.repository.TipoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.specification.TipoMemoriaSpecifications;
import org.crue.hercules.sgi.eti.service.TipoMemoriaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoMemoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoMemoriaServiceImpl implements TipoMemoriaService {
  private final TipoMemoriaRepository tipoMemoriaRepository;

  public TipoMemoriaServiceImpl(TipoMemoriaRepository tipoMemoriaRepository) {
    this.tipoMemoriaRepository = tipoMemoriaRepository;
  }

  /**
   * Guarda la entidad {@link TipoMemoria}.
   *
   * @param tipoMemoria la entidad {@link TipoMemoria} a guardar.
   * @return la entidad {@link TipoMemoria} persistida.
   */
  @Transactional
  public TipoMemoria create(TipoMemoria tipoMemoria) {
    log.debug("Petición a create TipoMemoria : {} - start", tipoMemoria);
    Assert.notNull(tipoMemoria.getId(), "TipoMemoria id no puede ser null para crear un nuevo tipoMemoria");

    return tipoMemoriaRepository.save(tipoMemoria);
  }

  /**
   * Obtiene todas las entidades {@link TipoMemoria} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link TipoMemoria} paginadas y filtradas.
   */
  public Page<TipoMemoria> findAll(String query, Pageable paging) {
    log.debug("findAllTipoMemoria(String query,Pageable paging) - start");
    Specification<TipoMemoria> specs = TipoMemoriaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoMemoria> returnValue = tipoMemoriaRepository.findAll(specs, paging);
    log.debug("findAllTipoMemoria(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoMemoria} por id.
   *
   * @param id el id de la entidad {@link TipoMemoria}.
   * @return la entidad {@link TipoMemoria}.
   * @throws TipoMemoriaNotFoundException Si no existe ningún {@link TipoMemoria}e
   *                                      con ese id.
   */
  public TipoMemoria findById(final Long id) throws TipoMemoriaNotFoundException {
    log.debug("Petición a get TipoMemoria : {}  - start", id);
    final TipoMemoria TipoMemoria = tipoMemoriaRepository.findById(id)
        .orElseThrow(() -> new TipoMemoriaNotFoundException(id));
    log.debug("Petición a get TipoMemoria : {}  - end", id);
    return TipoMemoria;

  }

  /**
   * Elimina una entidad {@link TipoMemoria} por id.
   *
   * @param id el id de la entidad {@link TipoMemoria}.
   */
  @Transactional
  public void delete(Long id) throws TipoMemoriaNotFoundException {
    log.debug("Petición a delete TipoMemoria : {}  - start", id);
    Assert.notNull(id, "El id de TipoMemoria no puede ser null.");
    if (!tipoMemoriaRepository.existsById(id)) {
      throw new TipoMemoriaNotFoundException(id);
    }
    tipoMemoriaRepository.deleteById(id);
    log.debug("Petición a delete TipoMemoria : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link TipoMemoria}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de TipoMemoria: {} - start");
    tipoMemoriaRepository.deleteAll();
    log.debug("Petición a deleteAll de TipoMemoria: {} - end");

  }

  /**
   * Actualiza los datos del {@link TipoMemoria}.
   * 
   * @param tipoMemoriaActualizar {@link TipoMemoria} con los datos actualizados.
   * @return El {@link TipoMemoria} actualizado.
   * @throws TipoMemoriaNotFoundException Si no existe ningún {@link TipoMemoria}
   *                                      con ese id.
   * @throws IllegalArgumentException     Si el {@link TipoMemoria} no tiene id.
   */

  @Transactional
  public TipoMemoria update(final TipoMemoria tipoMemoriaActualizar) {
    log.debug("update(TipoMemoria TipoMemoriaActualizar) - start");

    Assert.notNull(tipoMemoriaActualizar.getId(), "TipoMemoria id no puede ser null para actualizar un tipo memoria");

    return tipoMemoriaRepository.findById(tipoMemoriaActualizar.getId()).map(tipoMemoria -> {
      tipoMemoria.setNombre(tipoMemoriaActualizar.getNombre());
      tipoMemoria.setActivo(tipoMemoriaActualizar.getActivo());

      TipoMemoria returnValue = tipoMemoriaRepository.save(tipoMemoria);
      log.debug("update(TipoMemoria tipoMemoriaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoMemoriaNotFoundException(tipoMemoriaActualizar.getId()));
  }

}
