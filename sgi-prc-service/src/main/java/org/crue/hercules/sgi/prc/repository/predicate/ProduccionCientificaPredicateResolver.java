package org.crue.hercules.sgi.prc.repository.predicate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.AutorGrupo_;
import org.crue.hercules.sgi.prc.model.Autor_;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.model.ValorCampo_;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

public abstract class ProduccionCientificaPredicateResolver implements SgiRSQLPredicateResolver<ProduccionCientifica> {
  protected static final String BAD_NUMBER_OF_ARGUMENTS_FOR = "Bad number of arguments for ";
  protected static final String FOR = " for ";
  protected static final String UNSUPPORTED_OPERATOR = "Unsupported operator: ";

  protected enum ComparisonOperatorsAllow {
    EQUAL(new ComparisonOperator("==")),
    IGNORE_CASE_LIKE(new ComparisonOperator("=ik=", "=icase=")),
    GREATER_THAN_OR_EQUAL(new ComparisonOperator("=ge=", ">")),
    LESS_THAN_OR_EQUAL(new ComparisonOperator("=le=", "<="));

    private ComparisonOperator comparisonOperator;

    private ComparisonOperatorsAllow(ComparisonOperator comparisonOperator) {
      this.comparisonOperator = comparisonOperator;
    }

    public ComparisonOperator getComparisonOperator() {
      return comparisonOperator;
    }

    public static ComparisonOperatorsAllow fromComparisonOperator(ComparisonOperator comparisonOperatorSearch) {
      for (ComparisonOperatorsAllow comparisonOperatorAllow : ComparisonOperatorsAllow.values()) {
        if (comparisonOperatorAllow.comparisonOperator.equals(comparisonOperatorSearch)) {
          return comparisonOperatorAllow;
        }
      }
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + comparisonOperatorSearch);
    }
  }

  protected Subquery<Long> getSubqueryAutor(CriteriaBuilder cb, CriteriaQuery<?> cq,
      Path<Long> rootProduccionCientificaId, String personaRef) {
    List<Predicate> predicatesSubquery = new ArrayList<>();

    Subquery<Long> queryAutor = cq.subquery(Long.class);
    Root<Autor> subqRoot = queryAutor.from(Autor.class);

    predicatesSubquery
        .add(cb.and(cb.equal(subqRoot.get(Autor_.produccionCientificaId), rootProduccionCientificaId)));
    predicatesSubquery
        .add(cb.and(cb.like(subqRoot.get(Autor_.personaRef), personaRef)));

    queryAutor.select(subqRoot.get(Autor_.id))
        .where(predicatesSubquery.toArray(new Predicate[] {}));

    return queryAutor;
  }

  protected Subquery<Long> getSubqueryAutorGrupo(CriteriaBuilder cb, CriteriaQuery<?> cq,
      Path<Long> rootProduccionCientificaId, String grupoRef) {
    List<Predicate> predicatesSubquery = new ArrayList<>();

    Subquery<Long> queryAutorGrupo = cq.subquery(Long.class);
    Root<AutorGrupo> subqRoot = queryAutorGrupo.from(AutorGrupo.class);

    Join<AutorGrupo, Autor> joinAutor = subqRoot.join(AutorGrupo_.autor);

    predicatesSubquery
        .add(cb.and(cb.equal(joinAutor.get(Autor_.produccionCientificaId), rootProduccionCientificaId)));
    predicatesSubquery
        .add(cb.and(cb.equal(subqRoot.get(AutorGrupo_.grupoRef), grupoRef)));

    queryAutorGrupo.select(subqRoot.get(AutorGrupo_.id))
        .where(predicatesSubquery.toArray(new Predicate[] {}));

    return queryAutorGrupo;
  }

  protected Subquery<String> buildSubqueryValorCampoProduccionCientifica(CriteriaBuilder cb, CriteriaQuery<?> cq,
      Path<Long> rootProduccionCientificaId, CodigoCVN codigoCVN, ComparisonOperator operator, String valor) {
    List<Predicate> predicatesSubquery = new ArrayList<>();

    ComparisonOperatorsAllow operatorAllow = ComparisonOperatorsAllow.fromComparisonOperator(operator);

    Subquery<String> queryValorCampoProduccionCientifica = cq.subquery(String.class);

    Root<ValorCampo> subqRoot = queryValorCampoProduccionCientifica.from(ValorCampo.class);
    Join<ValorCampo, CampoProduccionCientifica> joinCampoProduccionCientifica = subqRoot.join(
        ValorCampo_.campoProduccionCientifica, JoinType.INNER);
    Join<CampoProduccionCientifica, ProduccionCientifica> joinProduccionCientifica = joinCampoProduccionCientifica.join(
        CampoProduccionCientifica_.produccionCientifica, JoinType.INNER);

    predicatesSubquery
        .add(cb.equal(joinProduccionCientifica.get(ProduccionCientifica_.id), rootProduccionCientificaId));
    predicatesSubquery.add(cb.equal(joinCampoProduccionCientifica.get(CampoProduccionCientifica_.codigoCVN),
        codigoCVN));
    predicatesSubquery.add(cb.equal(joinCampoProduccionCientifica.get(CampoProduccionCientifica_.codigoCVN),
        codigoCVN));

    switch (operatorAllow) {
      case IGNORE_CASE_LIKE:
        predicatesSubquery.add(cb.and(cb.like(subqRoot.get(ValorCampo_.valor), valor)));
        break;
      case GREATER_THAN_OR_EQUAL:
        predicatesSubquery.add(cb.and(cb.greaterThanOrEqualTo(subqRoot.get(ValorCampo_.valor), valor)));
        break;
      case LESS_THAN_OR_EQUAL:
        predicatesSubquery.add(cb.and(cb.lessThanOrEqualTo(subqRoot.get(ValorCampo_.valor), valor)));
        break;
      case EQUAL:
        predicatesSubquery.add(cb.and(cb.equal(subqRoot.get(ValorCampo_.valor), valor)));
        break;
      default:
    }

    queryValorCampoProduccionCientifica.select(subqRoot.get(ValorCampo_.valor))
        .where(predicatesSubquery.toArray(new Predicate[] {}));

    return queryValorCampoProduccionCientifica;
  }
}
