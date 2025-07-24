package org.crue.hercules.sgi.eti.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.eti.dto.InformeOutput;
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.InformeDocumento;
import org.crue.hercules.sgi.eti.model.InformeDocumento_;
import org.crue.hercules.sgi.eti.model.Informe_;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion_;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link Informe}.
 */
@Slf4j
@Component
public class CustomInformeRepositoryImpl implements CustomInformeRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public InformeOutput findFirstByMemoriaIdAndLangOrderByVersionDesc(Long idMemoria, Language lang) {
    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<InformeOutput> cq = cb.createQuery(InformeOutput.class);

    // Define FROM clause
    Root<InformeDocumento> root = cq.from(InformeDocumento.class);

    Join<InformeDocumento, Informe> joinInforme = root.join(InformeDocumento_.informe, JoinType.LEFT);

    cq.multiselect(joinInforme.get(Informe_.id), joinInforme.get(Informe_.memoria),
        joinInforme.get(Informe_.tipoEvaluacion), joinInforme.get(Informe_.version),
        root.get(InformeDocumento_.documentoRef), root.get(InformeDocumento_.lang));

    // Where
    cq.where(cb.equal(root.get(InformeDocumento_.lang), lang),
        cb.equal(joinInforme.get(Informe_.memoria).get(Memoria_.id), idMemoria));

    List<Order> orders = QueryUtils.toOrders(Sort.by(Sort.Direction.DESC, Informe_.VERSION), joinInforme,
        cb);
    cq.orderBy(orders);

    TypedQuery<InformeOutput> typedQuery = entityManager.createQuery(cq);

    List<InformeOutput> result = typedQuery.setMaxResults(1).getResultList();

    log.debug("getInformeComentarioGenerales : {} - end");
    return result.get(0);
  }

  @Override
  public Page<InformeOutput> findByMemoriaIdAndLang(Long idMemoria, Language lang, Pageable pageable) {
    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<InformeOutput> cq = cb.createQuery(InformeOutput.class);

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<InformeDocumento> rootCount = countQuery.from(InformeDocumento.class);

    // Define FROM clause
    Root<InformeDocumento> root = cq.from(InformeDocumento.class);

    Join<InformeDocumento, Informe> joinInforme = root.join(InformeDocumento_.informe, JoinType.LEFT);
    Join<InformeDocumento, Informe> joinCountInforme = rootCount.join(InformeDocumento_.informe, JoinType.LEFT);
    countQuery.select(cb.count(joinCountInforme));

    cq.multiselect(joinInforme.get(Informe_.id), joinInforme.get(Informe_.memoria),
        joinInforme.get(Informe_.tipoEvaluacion), joinInforme.get(Informe_.version),
        root.get(InformeDocumento_.documentoRef), root.get(InformeDocumento_.lang));

    // Where
    cq.where(cb.equal(root.get(InformeDocumento_.lang), lang),
        cb.equal(joinInforme.get(Informe_.memoria).get(Memoria_.id), idMemoria));

    countQuery.where(cb.equal(rootCount.get(InformeDocumento_.lang), lang),
        cb.equal(joinCountInforme.get(Informe_.memoria).get(Memoria_.id), idMemoria));

    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<InformeOutput> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<InformeOutput> result = typedQuery.getResultList();

    Page<InformeOutput> returnValue = new PageImpl<>(result, pageable, count);
    log.debug("getInformeComentarioGenerales : {} - end");
    return returnValue;
  }

  @Override
  public InformeOutput findFirstByMemoriaIdAndTipoEvaluacionIdAndLangOrderByVersionDesc(Long idMemoria,
      Long idTipoEvaluacion, Language lang) {
    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<InformeOutput> cq = cb.createQuery(InformeOutput.class);

    // Define FROM clause
    Root<InformeDocumento> root = cq.from(InformeDocumento.class);

    Join<InformeDocumento, Informe> joinInforme = root.join(InformeDocumento_.informe, JoinType.LEFT);

    cq.multiselect(joinInforme.get(Informe_.id), joinInforme.get(Informe_.memoria),
        joinInforme.get(Informe_.tipoEvaluacion), joinInforme.get(Informe_.version),
        root.get(InformeDocumento_.documentoRef), root.get(InformeDocumento_.lang));

    // Where
    cq.where(cb.equal(root.get(InformeDocumento_.lang), lang),
        cb.equal(joinInforme.get(Informe_.memoria).get(Memoria_.id), idMemoria),
        cb.equal(joinInforme.get(Informe_.tipoEvaluacion).get(TipoEvaluacion_.id), idTipoEvaluacion));

    List<Order> orders = QueryUtils.toOrders(Sort.by(Sort.Direction.DESC, Informe_.VERSION), joinInforme,
        cb);
    cq.orderBy(orders);

    TypedQuery<InformeOutput> typedQuery = entityManager.createQuery(cq);

    List<InformeOutput> result = typedQuery.setMaxResults(1).getResultList();

    log.debug("getInformeComentarioGenerales : {} - end");
    return result.get(0);
  }

}