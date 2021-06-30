package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.TipoComentarioNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.repository.TipoComentarioRepository;
import org.crue.hercules.sgi.eti.repository.specification.TipoComentarioSpecifications;
import org.crue.hercules.sgi.eti.service.TipoComentarioService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoComentario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoComentarioServiceImpl implements TipoComentarioService {
  private final TipoComentarioRepository tipoComentarioRepository;

  public TipoComentarioServiceImpl(TipoComentarioRepository tipoComentarioRepository) {
    this.tipoComentarioRepository = tipoComentarioRepository;
  }

  /**
   * Guarda la entidad {@link TipoComentario}.
   *
   * @param tipoComentario la entidad {@link TipoComentario} a guardar.
   * @return la entidad {@link TipoComentario} persistida.
   */
  @Transactional
  public TipoComentario create(TipoComentario tipoComentario) {
    log.debug("Petición a create TipoComentario : {} - start", tipoComentario);
    Assert.notNull(tipoComentario.getId(), "TipoComentario id no puede ser null para crear un nuevo tipoComentario");

    return tipoComentarioRepository.save(tipoComentario);
  }

  /**
   * Obtiene todas las entidades {@link TipoComentario} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link TipoComentario} paginadas y filtradas.
   */
  public Page<TipoComentario> findAll(String query, Pageable paging) {
    log.debug("findAllTipoComentario(String query,Pageable paging) - start");
    Specification<TipoComentario> specs = TipoComentarioSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoComentario> returnValue = tipoComentarioRepository.findAll(specs, paging);
    log.debug("findAllTipoComentario(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoComentario} por id.
   *
   * @param id el id de la entidad {@link TipoComentario}.
   * @return la entidad {@link TipoComentario}.
   * @throws TipoComentarioNotFoundException Si no existe ningún
   *                                         {@link TipoComentario}e con ese id.
   */
  public TipoComentario findById(final Long id) throws TipoComentarioNotFoundException {
    log.debug("Petición a get TipoComentario : {}  - start", id);
    final TipoComentario TipoComentario = tipoComentarioRepository.findById(id)
        .orElseThrow(() -> new TipoComentarioNotFoundException(id));
    log.debug("Petición a get TipoComentario : {}  - end", id);
    return TipoComentario;

  }

  /**
   * Elimina una entidad {@link TipoComentario} por id.
   *
   * @param id el id de la entidad {@link TipoComentario}.
   */
  @Transactional
  public void delete(Long id) throws TipoComentarioNotFoundException {
    log.debug("Petición a delete TipoComentario : {}  - start", id);
    Assert.notNull(id, "El id de TipoComentario no puede ser null.");
    if (!tipoComentarioRepository.existsById(id)) {
      throw new TipoComentarioNotFoundException(id);
    }
    tipoComentarioRepository.deleteById(id);
    log.debug("Petición a delete TipoComentario : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link TipoComentario}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de TipoComentario: {} - start");
    tipoComentarioRepository.deleteAll();
    log.debug("Petición a deleteAll de TipoComentario: {} - end");

  }

  /**
   * Actualiza los datos del {@link TipoComentario}.
   * 
   * @param tipoComentarioActualizar {@link TipoComentario} con los datos
   *                                 actualizados.
   * @return El {@link TipoComentario} actualizado.
   * @throws TipoComentarioNotFoundException Si no existe ningún
   *                                         {@link TipoComentario} con ese id.
   * @throws IllegalArgumentException        Si el {@link TipoComentario} no tiene
   *                                         id.
   */

  @Transactional
  public TipoComentario update(final TipoComentario tipoComentarioActualizar) {
    log.debug("update(TipoComentario tipoComentarioActualizar) - start");

    Assert.notNull(tipoComentarioActualizar.getId(),
        "TipoComentario id no puede ser null para actualizar un tipo comentario");

    return tipoComentarioRepository.findById(tipoComentarioActualizar.getId()).map(tipoComentario -> {
      tipoComentario.setNombre(tipoComentarioActualizar.getNombre());
      tipoComentario.setActivo(tipoComentarioActualizar.getActivo());

      TipoComentario returnValue = tipoComentarioRepository.save(tipoComentario);
      log.debug("update(TipoComentario tipoComentarioActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoComentarioNotFoundException(tipoComentarioActualizar.getId()));
  }

}
