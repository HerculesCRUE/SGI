package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.BloqueNotFoundException;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.repository.BloqueRepository;
import org.crue.hercules.sgi.eti.repository.specification.BloqueSpecifications;
import org.crue.hercules.sgi.eti.service.BloqueService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  @Override
  public Page<Bloque> findAll(String query, Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Specification<Bloque> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Bloque> returnValue = bloqueRepository.findAll(specs, paging);
    log.debug("findAll(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Bloque} por id e idioma.
   *
   * @param id el id de la entidad {@link Bloque}.
   * @return la entidad {@link Bloque}.
   * @throws BloqueNotFoundException Si no existe ningún {@link Bloque} con ese
   *                                 id.
   */
  @Override
  public Bloque findById(final Long id) throws BloqueNotFoundException {
    log.debug("Petición a get Bloque : {} - start", id);
    final Bloque bloque = bloqueRepository.findById(id).orElseThrow(() -> new BloqueNotFoundException(id));
    log.debug("Petición a get Bloque : {} - end", id);
    return bloque;
  }

  /**
   * Obtener todas las entidades {@link Bloque} paginadas de una
   * {@link Formulario} en el idioma solicitado.
   * 
   * @param id       Id del formulario
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Bloque} paginadas y/o filtradas.
   */
  @Override
  public Page<Bloque> findByFormularioId(Long id, Pageable pageable) {
    log.debug("update(Bloque bloqueActualizar) - start");
    AssertHelper.idNotNull(id, Formulario.class);
    Page<Bloque> bloque = bloqueRepository.findByFormularioId(id, pageable);
    log.debug("update(Bloque bloqueActualizar) - start");
    return bloque;
  }

  /**
   * Obtiene el {@link Bloque} de comentarios generales en el idioma solicitado.
   *
   * @return la entidad {@link Bloque}.
   */
  @Override
  public Bloque getBloqueComentariosGenerales() {
    log.debug("getBloqueComentariosGenerales(String lang) - start");
    final Bloque bloque = bloqueRepository.findOne(BloqueSpecifications.bloqueComentarioGenerales())
        .orElseThrow(() -> new BloqueNotFoundException(null));
    log.debug("getBloqueComentariosGenerales(String lang) - end");
    return bloque;
  }

}
