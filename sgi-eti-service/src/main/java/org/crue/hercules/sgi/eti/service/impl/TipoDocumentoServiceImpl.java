package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.eti.repository.specification.TipoDocumentoSpecifications;
import org.crue.hercules.sgi.eti.service.TipoDocumentoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoDocumentoServiceImpl implements TipoDocumentoService {
  private final TipoDocumentoRepository tipoDocumentoRepository;

  public TipoDocumentoServiceImpl(TipoDocumentoRepository tipoDocumentoRepository) {
    this.tipoDocumentoRepository = tipoDocumentoRepository;
  }

  /**
   * Obtiene todas las entidades {@link TipoDocumento} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link TipoDocumento} paginadas y filtradas.
   */
  @Override
  public Page<TipoDocumento> findAll(String query, Pageable paging) {
    log.debug("findAllTipoDocumento(String query,Pageable paging) - start");
    Specification<TipoDocumento> specs = TipoDocumentoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoDocumento> returnValue = tipoDocumentoRepository.findAll(specs, paging);
    log.debug("findAllTipoDocumento(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoDocumento} por id.
   *
   * @param id el id de la entidad {@link TipoDocumento}.
   * @return la entidad {@link TipoDocumento}.
   * @throws TipoDocumentoNotFoundException Si no existe ningún
   *                                        {@link TipoDocumento}e con ese id.
   */
  @Override
  public TipoDocumento findById(final Long id) throws TipoDocumentoNotFoundException {
    log.debug("Petición a get TipoDocumento : {}  - start", id);
    final TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(id)
        .orElseThrow(() -> new TipoDocumentoNotFoundException(id));
    log.debug("Petición a get TipoDocumento : {}  - end", id);
    return tipoDocumento;

  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoDocumento} inicial de una
   * memoria.
   * 
   * @param formularioId Id de {@link Formulario}
   * @return Listado de {@link TipoDocumento}
   */
  @Override
  public List<TipoDocumento> findByFormularioId(Long formularioId) {
    log.debug("findTipoDocumentacionInicial(String query,Pageable paging) - start");

    List<TipoDocumento> returnValue = tipoDocumentoRepository.findByFormularioIdAndActivoTrue(formularioId);
    log.debug("findTipoDocumentacionInicial(String query,Pageable paging) - end");
    return returnValue;
  }

}
