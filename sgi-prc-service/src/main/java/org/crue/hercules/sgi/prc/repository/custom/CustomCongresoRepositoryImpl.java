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
import org.crue.hercules.sgi.prc.dto.CongresoResumen;
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
import org.crue.hercules.sgi.prc.repository.predicate.CongresoPredicateResolver;
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
 * Spring Data JPA repository para {@link CongresoResumen}.
 */
@Slf4j
@Component
public class CustomCongresoRepositoryImpl implements CustomCongresoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<CongresoResumen> findAllCongresos(Specification<ProduccionCientifica> specIsInvestigador, String query,
      Pageable pageable) {
    log.debug(
        "findAllCongresos(Specification<ProduccionCientifica> specIsInvestigador, String query, Pageable pageable) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<CongresoResumen> cq = cb.createQuery(CongresoResumen.class);
    Root<ProduccionCientifica> root = cq.from(ProduccionCientifica.class);

    Join<ProduccionCientifica, EstadoProduccionCientifica> joinEstado = root.join(
        ProduccionCientifica_.estado);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTipoEvento = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresTipoEvento = joinCamposTipoEvento.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTituloTrabajo = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresTituloTrabajo = joinCamposTituloTrabajo.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposFechaCelebracion = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresFechaCelebracion = joinCamposFechaCelebracion.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ProduccionCientifica> rootCount = countQuery.from(ProduccionCientifica.class);

    countQuery.select(cb.count(rootCount));

    rootCount.join(ProduccionCientifica_.estado);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTipoEventoCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposTipoEventoCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTituloTrabajoCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposTituloTrabajoCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposFechaCelebracionCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposFechaCelebracionCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    List<Predicate> listPredicates = new ArrayList<>();
    List<Predicate> listPredicatesCount = new ArrayList<>();

    listPredicates.add(cb.isNull(root.get(ProduccionCientifica_.convocatoriaBaremacionId)));
    listPredicates.add(cb.equal(root.get(ProduccionCientifica_.epigrafeCVN), EpigrafeCVN.E060_010_020_000));

    listPredicates.add(cb.and(
        cb.equal(joinCamposTipoEvento.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_010_020_010)));
    listPredicates.add(cb.and(
        cb.equal(joinCamposTituloTrabajo.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_010_020_030)));
    listPredicates.add(cb.and(
        cb.equal(joinCamposFechaCelebracion.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_010_020_190)));

    listPredicatesCount.add(cb.isNull(rootCount.get(ProduccionCientifica_.convocatoriaBaremacionId)));
    listPredicatesCount.add(cb.equal(rootCount.get(ProduccionCientifica_.epigrafeCVN), EpigrafeCVN.E060_010_020_000));

    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposTipoEventoCount.get(CampoProduccionCientifica_.codigoCVN),
            CodigoCVN.E060_010_020_010)));
    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposTituloTrabajoCount.get(CampoProduccionCientifica_.codigoCVN),
            CodigoCVN.E060_010_020_030)));
    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposFechaCelebracionCount.get(CampoProduccionCientifica_.codigoCVN),
            CodigoCVN.E060_010_020_190)));

    if (StringUtils.hasText(query)) {
      Specification<ProduccionCientifica> spec = SgiRSQLJPASupport.toSpecification(query,
          CongresoPredicateResolver.getInstance());
      listPredicates.add(spec.toPredicate(root, cq, cb));
      listPredicatesCount.add(spec.toPredicate(rootCount, countQuery, cb));
    }

    if (specIsInvestigador != null) {
      listPredicates.add(specIsInvestigador.toPredicate(root, cq, cb));
      listPredicatesCount.add(specIsInvestigador.toPredicate(rootCount, countQuery, cb));
    }

    Path<Long> pathProduccionCientificaId = root.get(ProduccionCientifica_.id);
    cq.where(listPredicates.toArray(new Predicate[] {}));

    cq.multiselect(pathProduccionCientificaId.alias("id"),
        root.get(ProduccionCientifica_.produccionCientificaRef).alias("produccionCientificaRef"),
        joinEstado.get(EstadoProduccionCientifica_.estado).alias("estado"),
        root.get(ProduccionCientifica_.epigrafeCVN).alias("epigrafe"),
        joinValoresTipoEvento.get(ValorCampo_.valor)
            .alias(CongresoPredicateResolver.Property.TIPO_EVENTO.getCode()),
        joinValoresTituloTrabajo.get(ValorCampo_.valor)
            .alias(CongresoPredicateResolver.Property.TITULO_TRABAJO.getCode()),
        joinValoresFechaCelebracion.get(ValorCampo_.valor)
            .alias(CongresoPredicateResolver.Property.FECHA_CELEBRACION.getCode()));

    String[] selectionNames = new String[] {
        CongresoPredicateResolver.Property.TIPO_EVENTO.getCode(),
        CongresoPredicateResolver.Property.TITULO_TRABAJO.getCode(),
        CongresoPredicateResolver.Property.FECHA_CELEBRACION.getCode() };

    Optional<Integer> selectionIndex = getIndexOrderBySelectionName(selectionNames, pageable.getSort(), cq);
    if (selectionIndex.isPresent()) {
      cq.orderBy(toOrdersByPosition(selectionIndex.get() + 1, pageable.getSort(), cb));
    } else {
      List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
      cq.orderBy(orders);
    }

    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<CongresoResumen> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<CongresoResumen> result = typedQuery.getResultList();
    Page<CongresoResumen> returnValue = new PageImpl<>(result, pageable, count);

    log.debug(
        "findAllCongresos(Specification<ProduccionCientifica> specIsInvestigador, String query, Pageable pageable) - end");

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
