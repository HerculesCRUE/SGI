package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion;
import org.crue.hercules.sgi.csp.repository.CertificadoAutorizacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.CertificadoAutorizacionSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link CertificadoAutorizacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class CertificadoAutorizacionService {

  private final CertificadoAutorizacionRepository repository;

  public CertificadoAutorizacionService(CertificadoAutorizacionRepository repository) {
    this.repository = repository;
  }

  /**
   * Obtiene los {@link CertificadoAutorizacion} para una {@link Autorizacion}.
   *
   * @param autorizacionId el id de la {@link Autorizacion}.
   * @param paging         la información de la paginación.
   * @return la lista de entidades {@link CertificadoAutorizacion} de la
   *         {@link Autorizacion}
   *         paginadas.
   */
  public Page<CertificadoAutorizacion> findAllByAutorizacion(Long autorizacionId, Pageable paging) {
    log.debug("findAllByAutorizacion(Long autorizacionId, Pageable paging) - start");

    Page<CertificadoAutorizacion> returnValue = repository.findAllByAutorizacionId(autorizacionId, paging);
    log.debug("findAllByAutorizacion(Long autorizacionId, Pageable paging) - end");
    return returnValue;
  }

  public boolean hasCertificadoAutorizacionVisible(Long autorizacionId) {
    log.debug(
        "hasCertificadoAutorizacionVisible(Long autorizacionId)- start");

    Specification<CertificadoAutorizacion> specs = CertificadoAutorizacionSpecifications
        .byAutorizacionId(
            autorizacionId)
        .and(CertificadoAutorizacionSpecifications.visibles());

    boolean returnValue = repository.count(specs) > 0 ? true : false;
    log.debug(
        "hasCertificadoAutorizacionVisible(Long autorizacionId) - end");
    return returnValue;
  }

}
