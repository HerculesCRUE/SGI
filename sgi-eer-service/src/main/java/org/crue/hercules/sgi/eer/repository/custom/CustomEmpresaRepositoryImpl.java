package org.crue.hercules.sgi.eer.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.Empresa_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA custom repository para {@link Empresa}.
 */
@Slf4j
@Component
public class CustomEmpresaRepositoryImpl implements CustomEmpresaRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtiene los ids de {@link Empresa} que cumplen con la specification recibida.
   * 
   * @param specification condiciones que deben cumplir.
   * @return lista de ids de {@link Empresa}.
   */
  @Override
  public List<Long> findIds(Specification<Empresa> specification) {
    log.debug("findIds(Specification<Empresa> specification) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Empresa> root = cq.from(Empresa.class);

    cq.select(root.get(Empresa_.id)).distinct(true).where(specification.toPredicate(root, cq, cb));

    log.debug("findIds(Specification<Empresa> specification) - end");

    return entityManager.createQuery(cq).getResultList();
  }

}
