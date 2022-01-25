package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion;
import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion_;
import org.springframework.data.jpa.domain.Specification;

public class CertificadoAutorizacionSpecifications {

  /**
   * Devuelve el {@link CertificadoAutorizacion} con el id indicado
   * 
   * @param certificadoId id del {@link CertificadoAutorizacion}
   * @return specification para obtener el {@link CertificadoAutorizacion} con el
   *         id indicado
   */
  public static Specification<CertificadoAutorizacion> byId(Long certificadoId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(CertificadoAutorizacion_.id), certificadoId);
    };
  }

  /**
   * Devuelve todos los {@link CertificadoAutorizacion} con el asociados a un
   * {@link Autorizacion}
   * 
   * @param autorizacionId id de la {@link Autorizacion}
   * @return specification para obtener los {@link CertificadoAutorizacion}
   *         asociados a un {@link Autorizacion}
   */
  public static Specification<CertificadoAutorizacion> byAutorizacionId(Long autorizacionId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(CertificadoAutorizacion_.autorizacionId), autorizacionId);
    };
  }

  /**
   * {@link CertificadoAutorizacion} visibles.
   * 
   * @return specification para obtener los {@link CertificadoAutorizacion}
   *         visibles.
   */
  public static Specification<CertificadoAutorizacion> visibles() {
    return (root, query, cb) -> {
      return cb.equal(root.get(CertificadoAutorizacion_.visible), Boolean.TRUE);
    };
  }
}
