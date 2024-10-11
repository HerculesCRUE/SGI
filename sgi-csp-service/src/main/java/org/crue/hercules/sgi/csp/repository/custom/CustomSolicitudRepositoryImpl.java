package org.crue.hercules.sgi.csp.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA custom repository para {@link Solicitud}.
 */
@Slf4j
@Component
public class CustomSolicitudRepositoryImpl implements CustomSolicitudRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtiene los ids de {@link Solicitud} que cumplen con la specification
   * recibida.
   * 
   * @param specification condiciones que deben cumplir.
   * @return lista de ids de {@link Solicitud}.
   */
  @Override
  public List<Long> findIds(Specification<Solicitud> specification) {
    log.debug("findIds(Specification<Solicitud> specification) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Solicitud> root = cq.from(Solicitud.class);

    cq.select(root.get(Solicitud_.id)).distinct(true).where(specification.toPredicate(root, cq, cb));

    log.debug("findIds(Specification<Solicitud> specification) - end");

    return entityManager.createQuery(cq).getResultList();
  }

}
