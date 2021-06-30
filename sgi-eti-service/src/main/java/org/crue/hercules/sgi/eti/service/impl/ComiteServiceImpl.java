package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.repository.specification.ComiteSpecifications;
import org.crue.hercules.sgi.eti.service.ComiteService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Comite}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ComiteServiceImpl implements ComiteService {

  private final ComiteRepository comiteRepository;

  public ComiteServiceImpl(ComiteRepository comiteRepository) {
    this.comiteRepository = comiteRepository;
  }

  /**
   * Guarda la entidad {@link Comite}.
   *
   * @param comite la entidad {@link Comite} a guardar.
   * @return la entidad {@link Comite} persistida.
   */
  @Transactional
  public Comite create(Comite comite) {
    log.debug("Petición a create Comite : {} - start", comite);
    Assert.isNull(comite.getId(), "Comite id tiene que ser null para crear un nuevo comité");

    return comiteRepository.save(comite);
  }

  /**
   * Obtiene todas las entidades {@link Comite} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Comite} paginadas y filtradas.
   */
  public Page<Comite> findAll(String query, Pageable paging) {
    log.debug("findAllComite(String query,Pageable paging) - start");
    Specification<Comite> specs = ComiteSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<Comite> returnValue = comiteRepository.findAll(specs, paging);
    log.debug("findAllComite(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Optiene una entidad {@link Comite} por id.
   *
   * @param id el id de la entidad {@link Comite}.
   * @return la entidad {@link Comite}.
   * @throws ComiteNotFoundException excepción.
   */
  public Comite findById(final Long id) throws ComiteNotFoundException {
    log.debug("Petición a get Comite : {}  - start", id);
    Comite comite = comiteRepository.findById(id).orElseThrow(() -> new ComiteNotFoundException(id));
    log.debug("Petición a get Comite : {}  - end", id);
    return comite;

  }

  /**
   * Elimina una entidad {@link Comite} por id.
   *
   * @param id el id de la entidad {@link Comite}.
   */
  @Transactional
  public void deleteById(Long id) throws ComiteNotFoundException {
    log.debug("Petición a delete Comite : {}  - start", id);
    Assert.notNull(id, "El id de Comite no puede ser null.");
    if (!comiteRepository.existsById(id)) {
      throw new ComiteNotFoundException(id);
    }
    comiteRepository.deleteById(id);
    log.debug("Petición a delete Comite : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Comite}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Comite: {} - start");
    comiteRepository.deleteAll();
    log.debug("Petición a deleteAll de Comite: {} - end");

  }

  /**
   * Actualiza los datos del {@link Comite}.
   * 
   * @param comiteActualizar {@link Comite} con los datos actualizados.
   * @return El {@link Comite} actualizado.
   * @throws ComiteNotFoundException  Si no existe ningún {@link Comite} con ese
   *                                  id.
   * @throws IllegalArgumentException Si el {@link Comite} no tiene id.
   */

  @Transactional
  public Comite update(final Comite comiteActualizar) {
    log.debug("update(Comite comiteActualizar) - start");

    Assert.notNull(comiteActualizar.getId(), "Comite id no puede ser null para actualizar un comité");

    return comiteRepository.findById(comiteActualizar.getId()).map(comite -> {
      comite.setComite(comiteActualizar.getComite());

      Comite returnValue = comiteRepository.save(comite);
      log.debug("update(Comite comiteActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ComiteNotFoundException(comiteActualizar.getId()));
  }

}