package org.crue.hercules.sgi.framework.rsql;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 * Join utils used by RSQL Predicate converter. Based on RSQL JPA
 * {@link io.github.perplexhub.rsql.JoinUtils}.
 * 
 * Adds creation of joins for i18n fields that be a subquery instead of classic
 * join to prevent cartesian product when filtering over i18n field
 */
public class SgiJoinUtils {
  private SgiJoinUtils() {
  }

  public static <X, Z> Join<X, ?> getOrCreateJoin(
      final From<Z, X> root, final String attribute, final JoinType joinType) {
    final Join<X, ?> join = getJoin(root, attribute, joinType);
    return join == null ? createJoin(root, attribute, joinType) : join;
  }

  public static <X, Z> SgiI18nJoinHolder<X, ?> createI18nJoin(final From<Z, X> root, CriteriaQuery<?> query,
      final CriteriaBuilder cb, final String attribute, final Class<Z> attributeClass) {
    Subquery<Z> subquery = query.subquery(attributeClass);
    Root<X> rootSubquery = subquery.from((Class<X>) root.getJavaType());
    Join<X, Z> join = rootSubquery.join(attribute);
    subquery.select(join);
    List<Predicate> predicates = new ArrayList<>();
    predicates.add(cb.equal(root, rootSubquery));
    subquery.where(predicates.getFirst());

    return SgiI18nJoinHolder.of(join, subquery, predicates);
  }

  private static <X, Z> Join<X, ?> createJoin(final From<Z, X> root, final String attribute, final JoinType joinType) {
    return joinType == null ? root.join(attribute) : root.join(attribute, joinType);
  }

  private static <X, Z> Join<X, ?> getJoin(
      final From<Z, X> root, final String attribute, final JoinType joinType) {
    final Join<X, ?> fetchJoin = getJoinFromFetches(root, attribute, joinType);
    if (fetchJoin != null) {
      return fetchJoin;
    }
    return getJoinFromJoins(root, attribute, joinType);
  }

  private static <X, Z> Join<X, ?> getJoinFromFetches(
      final From<Z, X> root, final String attribute, final JoinType joinType) {
    for (final Fetch<X, ?> fetch : root.getFetches()) {
      if (Join.class.isAssignableFrom(fetch.getClass()) &&
          fetch.getAttribute().getName().equals(attribute) &&
          (joinType == null || fetch.getJoinType().equals(joinType))) {
        return (Join<X, ?>) fetch;
      }
    }
    return null;
  }

  private static <X, Z> Join<X, ?> getJoinFromJoins(
      final From<Z, X> root, final String attribute, final JoinType joinType) {
    for (final Join<X, ?> join : root.getJoins()) {
      if (join.getAttribute().getName().equals(attribute) &&
          (joinType == null || join.getJoinType().equals(joinType))) {
        return join;
      }
    }
    return null;
  }
}
