package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.BloqueNotFoundException;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.repository.BloqueRepository;
import org.crue.hercules.sgi.eti.service.BloqueService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Bloque}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class BloqueServiceImpl implements BloqueService {

  /** Bloque repository. */
  private final BloqueRepository bloqueRepository;

  /**
   * Instancia un nuevo bloque.
   * 
   * @param bloqueRepository {@link BloqueRepository}.
   */
  public BloqueServiceImpl(BloqueRepository bloqueRepository) {
    this.bloqueRepository = bloqueRepository;
  }

  /**
   * Obtiene todas las entidades {@link Bloque} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Bloque} paginadas y filtradas.
   */
  public Page<Bloque> findAll(String query, Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Specification<Bloque> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Bloque> returnValue = bloqueRepository.findAll(specs, paging);
    log.debug("findAll(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Bloque} por id.
   *
   * @param id el id de la entidad {@link Bloque}.
   * @return la entidad {@link Bloque}.
   * @throws BloqueNotFoundException Si no existe ningún {@link Bloque} con ese
   *                                 id.
   */
  public Bloque findById(final Long id) throws BloqueNotFoundException {
    log.debug("Petición a get Bloque : {}  - start", id);
    final Bloque Bloque = bloqueRepository.findById(id).orElseThrow(() -> new BloqueNotFoundException(id));
    log.debug("Petición a get Bloque : {}  - end", id);
    return Bloque;

  }

  /**
   * Obtener todas las entidades {@link Bloque} paginadas de una
   * {@link Formulario}.
   * 
   * @param id       Id del formulario
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Bloque} paginadas y/o filtradas.
   */
  @Override
  public Page<Bloque> findByFormularioId(Long id, Pageable pageable) {
    log.debug("update(Bloque bloqueActualizar) - start");
    Assert.notNull(id, "El id del formulario no puede ser null para listar sus bloques");
    Page<Bloque> resultado = bloqueRepository.findByFormularioId(id, pageable);
    log.debug("update(Bloque bloqueActualizar) - start");
    return resultado;
  }

}
