package org.crue.hercules.sgi.framework.rsql;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import cz.jirutka.rsql.parser.ast.ComparisonNode;

/**
 * Build {@link Predicate} for custom properties. Used in
 * {@link SgiRSQLJPASupport}
 */
public interface SgiRSQLPredicateResolver<T> {

  /**
   * Indicate if a RSQL node is managed by this resolver
   * 
   * @param node the {@link ComparisonNode} to evaluate
   * @return <code>true</code> when is managed, <code>false</code> otherwise
   */
  boolean isManaged(ComparisonNode node);

  /**
   * Generate a {@link Predicate} for a {@link ComparisonNode}. Only accept
   * managed properties, if not managed returns <code>null</code>
   * 
   * @param node            the {@link ComparisonNode} to convert
   * @param root            the {@link Root} entity to use in conversion
   * @param query           the {@link CriteriaQuery} to use in conversion
   * @param criteriaBuilder the {@link CriteriaBuilder} to use in conversion
   * @return the {@link Predicate} to apply
   */
  Predicate toPredicate(ComparisonNode node, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder);
}
