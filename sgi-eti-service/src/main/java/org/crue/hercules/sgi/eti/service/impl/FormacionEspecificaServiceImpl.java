package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.FormacionEspecificaNotFoundException;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.repository.FormacionEspecificaRepository;
import org.crue.hercules.sgi.eti.repository.specification.FormacionEspecificaSpecifications;
import org.crue.hercules.sgi.eti.service.FormacionEspecificaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link FormacionEspecifica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class FormacionEspecificaServiceImpl implements FormacionEspecificaService {
  private final FormacionEspecificaRepository formacionEspecificaRepository;

  public FormacionEspecificaServiceImpl(FormacionEspecificaRepository formacionEspecificaRepository) {
    this.formacionEspecificaRepository = formacionEspecificaRepository;
  }

  /**
   * Obtiene todas las entidades {@link FormacionEspecifica} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link FormacionEspecifica} paginadas y
   *         filtradas.
   */
  public Page<FormacionEspecifica> findAll(String query, Pageable paging) {
    log.debug("findAllFormacionEspecifica(String query,Pageable paging) - start");
    Specification<FormacionEspecifica> specs = FormacionEspecificaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<FormacionEspecifica> returnValue = formacionEspecificaRepository.findAll(specs, paging);
    log.debug("findAllFormacionEspecifica(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link FormacionEspecifica} por id.
   *
   * @param id el id de la entidad {@link FormacionEspecifica}.
   * @return la entidad {@link FormacionEspecifica}.
   * @throws FormacionEspecificaNotFoundException Si no existe ninguna
   *                                              {@link FormacionEspecifica}e con
   *                                              ese id.
   */
  public FormacionEspecifica findById(final Long id) throws FormacionEspecificaNotFoundException {
    log.debug("Petición a get FormacionEspecifica : {}  - start", id);
    final FormacionEspecifica FormacionEspecifica = formacionEspecificaRepository.findById(id)
        .orElseThrow(() -> new FormacionEspecificaNotFoundException(id));
    log.debug("Petición a get FormacionEspecifica : {}  - end", id);
    return FormacionEspecifica;

  }

}
