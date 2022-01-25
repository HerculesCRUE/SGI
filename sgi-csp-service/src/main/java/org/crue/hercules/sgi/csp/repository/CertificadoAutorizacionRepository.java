package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CertificadoAutorizacionRepository
    extends JpaRepository<CertificadoAutorizacion, Long>, JpaSpecificationExecutor<CertificadoAutorizacion> {

  /**
   * Obtiene los {@link CertificadoAutorizacion} para una {@link Autorizacion}.
   *
   * @param autorizacionId el id de la {@link Autorizacion}.
   * @param paging         la información de la paginación.
   * @return la lista de entidades {@link CertificadoAutorizacion} de la
   *         {@link Autorizacion}
   *         paginadas.
   */
  Page<CertificadoAutorizacion> findAllByAutorizacionId(Long autorizacionId, Pageable paging);

}
