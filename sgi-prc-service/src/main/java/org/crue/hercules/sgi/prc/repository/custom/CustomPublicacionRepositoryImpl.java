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

import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
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
import org.crue.hercules.sgi.prc.repository.predicate.PublicacionPredicateResolver;
import org.crue.hercules.sgi.prc.util.CriteriaQueryUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link PublicacionResumen}.
 */
@Slf4j
@Component
public class CustomPublicacionRepositoryImpl implements CustomPublicacionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<PublicacionResumen> findAllPublicaciones(Specification<ProduccionCientifica> specs, Pageable pageable) {
    log.debug(
        "findAllPublicaciones(Specification<ProduccionCientifica> specs, Pageable pageable) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<PublicacionResumen> cq = cb.createQuery(PublicacionResumen.class);
    Root<ProduccionCientifica> root = cq.from(ProduccionCientifica.class);

    Join<ProduccionCientifica, EstadoProduccionCientifica> joinEstado = root.join(
        ProduccionCientifica_.estado);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTituloPublicacion = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresTituloPublicacion = joinCamposTituloPublicacion.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTipoProduccion = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresTipoProduccion = joinCamposTipoProduccion.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposFechaPublicacion = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresFechaPublicacion = joinCamposFechaPublicacion.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ProduccionCientifica> rootCount = countQuery.from(ProduccionCientifica.class);

    countQuery.select(cb.count(rootCount));

    rootCount.join(ProduccionCientifica_.estado);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTituloPublicacionCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposTituloPublicacionCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTipoProduccionCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposTipoProduccionCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposFechaPublicacionCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposFechaPublicacionCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    List<Predicate> listPredicates = new ArrayList<>();
    List<Predicate> listPredicatesCount = new ArrayList<>();

    listPredicates.add(cb.isNull(root.get(ProduccionCientifica_.convocatoriaBaremacionId)));
    listPredicates.add(cb.equal(root.get(ProduccionCientifica_.epigrafeCVN), EpigrafeCVN.E060_010_010_000));

    listPredicates.add(cb.and(
        cb.equal(joinCamposTituloPublicacion.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_010_010_030)));
    listPredicates.add(cb.and(
        cb.equal(joinCamposTipoProduccion.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_010_010_010)));
    listPredicates.add(cb.and(
        cb.equal(joinCamposFechaPublicacion.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_010_010_140)));

    listPredicatesCount.add(cb.isNull(rootCount.get(ProduccionCientifica_.convocatoriaBaremacionId)));
    listPredicatesCount.add(cb.equal(rootCount.get(ProduccionCientifica_.epigrafeCVN), EpigrafeCVN.E060_010_010_000));

    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposTituloPublicacionCount.get(CampoProduccionCientifica_.codigoCVN),
            CodigoCVN.E060_010_010_030)));
    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposTipoProduccionCount.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_010_010_010)));
    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposFechaPublicacionCount.get(CampoProduccionCientifica_.codigoCVN),
            CodigoCVN.E060_010_010_140)));

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
        joinValoresTituloPublicacion.get(ValorCampo_.valor)
            .alias(PublicacionPredicateResolver.Property.TITULO_PUBLICACION.getCode()),
        joinValoresTipoProduccion.get(ValorCampo_.valor)
            .alias(PublicacionPredicateResolver.Property.TIPO_PRODUCCION.getCode()),
        joinValoresFechaPublicacion.get(ValorCampo_.valor)
            .alias(PublicacionPredicateResolver.Property.FECHA_PUBLICACION.getCode()));

    String[] selectionNames = new String[] {
        PublicacionPredicateResolver.Property.TITULO_PUBLICACION.getCode(),
        PublicacionPredicateResolver.Property.TIPO_PRODUCCION.getCode(),
        PublicacionPredicateResolver.Property.FECHA_PUBLICACION.getCode() };

    cq.orderBy(CriteriaQueryUtils.toOrders(pageable.getSort(), root, cb, cq, selectionNames));

    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<PublicacionResumen> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<PublicacionResumen> result = typedQuery.getResultList();
    Page<PublicacionResumen> returnValue = new PageImpl<>(result, pageable, count);

    log.debug(
        "findAllPublicaciones(Specification<ProduccionCientifica> specs, Pageable pageable) - end");

    return returnValue;
  }

}
