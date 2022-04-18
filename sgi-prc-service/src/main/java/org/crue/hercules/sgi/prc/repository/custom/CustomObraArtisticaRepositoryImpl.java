package org.crue.hercules.sgi.prc.repository.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.prc.dto.ObraArtisticaResumen;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.model.ValorCampo_;
import org.crue.hercules.sgi.prc.repository.predicate.ObraArtisticaPredicateResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ObraArtisticaResumen}.
 */
@Slf4j
@Component
public class CustomObraArtisticaRepositoryImpl implements CustomObraArtisticaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<ObraArtisticaResumen> findAllObrasArtisticas(String query, Pageable pageable) {
    log.debug("findAllObrasArtisticas(String query, Pageable pageable) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ObraArtisticaResumen> cq = cb.createQuery(ObraArtisticaResumen.class);
    Root<ProduccionCientifica> root = cq.from(ProduccionCientifica.class);

    Join<ProduccionCientifica, EstadoProduccionCientifica> joinEstado = root.join(
        ProduccionCientifica_.estado);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposDescripcion = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresDescripcion = joinCamposDescripcion.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposFechaInicio = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresFechaInicio = joinCamposFechaInicio.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ProduccionCientifica> rootCount = countQuery.from(ProduccionCientifica.class);

    countQuery.select(cb.count(rootCount));

    rootCount.join(ProduccionCientifica_.estado);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposDescripcionCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposDescripcionCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposFechaInicioCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposFechaInicioCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    List<Predicate> listPredicates = new ArrayList<>();
    List<Predicate> listPredicatesCount = new ArrayList<>();

    listPredicates.add(cb.isNull(root.get(ProduccionCientifica_.convocatoriaBaremacionId)));
    listPredicates.add(cb.and(
        cb.equal(joinCamposDescripcion.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E050_020_030_010)));
    listPredicates.add(cb.and(
        cb.equal(joinCamposFechaInicio.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E050_020_030_120)));

    listPredicatesCount.add(cb.isNull(rootCount.get(ProduccionCientifica_.convocatoriaBaremacionId)));
    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposDescripcionCount.get(CampoProduccionCientifica_.codigoCVN),
            CodigoCVN.E050_020_030_010)));
    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposFechaInicioCount.get(CampoProduccionCientifica_.codigoCVN),
            CodigoCVN.E050_020_030_120)));

    if (StringUtils.hasText(query)) {
      Specification<ProduccionCientifica> spec = SgiRSQLJPASupport.toSpecification(query,
          ObraArtisticaPredicateResolver.getInstance());
      listPredicates.add(spec.toPredicate(root, cq, cb));
      listPredicatesCount.add(spec.toPredicate(rootCount, countQuery, cb));
    }

    Path<Long> pathProduccionCientificaId = root.get(ProduccionCientifica_.id);
    cq.where(listPredicates.toArray(new Predicate[] {}));

    cq.multiselect(pathProduccionCientificaId.alias("id"),
        root.get(ProduccionCientifica_.produccionCientificaRef).alias("produccionCientificaRef"),
        joinEstado.get(EstadoProduccionCientifica_.estado).alias("estado"),
        root.get(ProduccionCientifica_.epigrafeCVN).alias("epigrafe"),
        joinValoresDescripcion.get(ValorCampo_.valor)
            .alias(ObraArtisticaPredicateResolver.Property.DESCRIPCION.getCode()),
        joinValoresFechaInicio.get(ValorCampo_.valor)
            .alias(ObraArtisticaPredicateResolver.Property.FECHA_INICIO.getCode()));

    String[] selectionNames = new String[] {
        ObraArtisticaPredicateResolver.Property.DESCRIPCION.getCode(),
        ObraArtisticaPredicateResolver.Property.FECHA_INICIO.getCode() };

    Optional<Integer> selectionIndex = getIndexOrderBySelectionName(selectionNames, pageable.getSort(), cq);
    if (selectionIndex.isPresent()) {
      cq.orderBy(toOrdersByPosition(selectionIndex.get() + 1, pageable.getSort(), cb));
    } else {
      List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
      cq.orderBy(orders);
    }

    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<ObraArtisticaResumen> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<ObraArtisticaResumen> result = typedQuery.getResultList();
    Page<ObraArtisticaResumen> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findAllObrasArtisticas(String query, Pageable pageable) - end");

    return returnValue;
  }

  private List<Order> toOrdersByPosition(int position, Sort sort, CriteriaBuilder cb) {
    List<Order> orders = new ArrayList<>();
    if (sort.isUnsorted()) {
      return orders;
    }

    sort.forEach(order -> {
      Expression<Integer> orderByLiteral = cb.literal(position);
      Order orderByPosition = cb.desc(orderByLiteral);
      if (order.isAscending()) {
        orderByPosition = cb.asc(orderByLiteral);
      }
      orders.add(orderByPosition);
    });

    return orders;
  }

  private Optional<Integer> getIndexOrderBySelectionName(String[] selectionNames, Sort sort,
      CriteriaQuery<?> cq) {
    return Stream.of(selectionNames)
        .filter(
            selectionName -> !sort.filter(order -> order.getProperty().equals(selectionName)).isEmpty())
        .findFirst()
        .map(
            selectionName -> IntStream.range(0, cq.getSelection().getCompoundSelectionItems().size())
                .filter(
                    index -> cq.getSelection().getCompoundSelectionItems().get(index).getAlias().equals(selectionName))
                .findFirst()
                .getAsInt());
  }
}
