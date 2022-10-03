package org.crue.hercules.sgi.prc.repository.custom;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.dto.ActividadResumen;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.model.ValorCampo_;
import org.crue.hercules.sgi.prc.repository.predicate.ActividadPredicateResolver;
import org.crue.hercules.sgi.prc.util.CriteriaQueryUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ActividadResumen}.
 */
@Slf4j
@Component
public class CustomActividadRepositoryImpl implements CustomActividadRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<ActividadResumen> findAllActividades(Specification<ProduccionCientifica> specs, Pageable pageable) {
    log.debug(
        "findAllActividades(Specification<ProduccionCientifica> specs, Pageable pageable) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ActividadResumen> cq = cb.createQuery(ActividadResumen.class);
    Root<ProduccionCientifica> root = cq.from(ProduccionCientifica.class);

    Join<ProduccionCientifica, EstadoProduccionCientifica> joinEstado = root.join(
        ProduccionCientifica_.estado);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTituloActividad = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresTituloActividad = joinCamposTituloActividad.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposFechaInicio = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresFechaInicio = joinCamposFechaInicio.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ProduccionCientifica> rootCount = countQuery.from(ProduccionCientifica.class);

    countQuery.select(cb.count(rootCount));

    rootCount.join(ProduccionCientifica_.estado);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTituloActividadCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposTituloActividadCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposFechaInicioCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposFechaInicioCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    List<Predicate> listPredicates = new ArrayList<>();
    List<Predicate> listPredicatesCount = new ArrayList<>();

    listPredicates.add(cb.isNull(root.get(ProduccionCientifica_.convocatoriaBaremacionId)));
    listPredicates.add(cb.equal(root.get(ProduccionCientifica_.epigrafeCVN), EpigrafeCVN.E060_020_030_000));

    listPredicates.add(cb.and(
        cb.equal(joinCamposTituloActividad.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_020_030_010)));
    listPredicates.add(cb.and(
        cb.equal(joinCamposFechaInicio.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_020_030_160)));

    listPredicatesCount.add(cb.isNull(rootCount.get(ProduccionCientifica_.convocatoriaBaremacionId)));
    listPredicates.add(cb.equal(rootCount.get(ProduccionCientifica_.epigrafeCVN), EpigrafeCVN.E060_020_030_000));

    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposTituloActividadCount.get(CampoProduccionCientifica_.codigoCVN),
            CodigoCVN.E060_020_030_010)));
    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposFechaInicioCount.get(CampoProduccionCientifica_.codigoCVN),
            CodigoCVN.E060_020_030_160)));

    if (specs != null) {
      listPredicates.add(specs.toPredicate(root, cq, cb));
      listPredicatesCount.add(specs.toPredicate(rootCount, countQuery, cb));
    }

    Path<Long> pathProduccionCientificaId = root.get(ProduccionCientifica_.id);
    cq.where(listPredicates.toArray(new Predicate[] {}));

    cq.multiselect(pathProduccionCientificaId.alias("id"),
        root.get(ProduccionCientifica_.produccionCientificaRef).alias("produccionCientificaRef"),
        joinEstado.get(EstadoProduccionCientifica_.estado).alias("estado"),
        root.get(ProduccionCientifica_.epigrafeCVN).alias("epigrafe"),
        joinValoresTituloActividad.get(ValorCampo_.valor)
            .alias(ActividadPredicateResolver.Property.TITULO_ACTIVIDAD.getCode()),
        joinValoresFechaInicio.get(ValorCampo_.valor)
            .alias(ActividadPredicateResolver.Property.FECHA_INICIO.getCode()));

    String[] selectionNames = new String[] {
        ActividadPredicateResolver.Property.TITULO_ACTIVIDAD.getCode(),
        ActividadPredicateResolver.Property.FECHA_INICIO.getCode() };

    cq.orderBy(CriteriaQueryUtils.toOrders(pageable.getSort(), root, cb, cq, selectionNames));

    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<ActividadResumen> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<ActividadResumen> result = typedQuery.getResultList();
    Page<ActividadResumen> returnValue = new PageImpl<>(result, pageable, count);

    log.debug(
        "findAllActividades(Specification<ProduccionCientifica> specs, Pageable pageable) - end");

    return returnValue;
  }

}
