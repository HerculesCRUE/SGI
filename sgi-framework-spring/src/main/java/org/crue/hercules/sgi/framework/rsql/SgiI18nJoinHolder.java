package org.crue.hercules.sgi.framework.rsql;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Subquery;

import lombok.Value;

/**
 * Join holder that store the Path and the Subquery
 */
@Value(staticConstructor = "of")
public class SgiI18nJoinHolder<Z, X> {
  Path<X> path;
  Subquery<X> subquery;
  List<Predicate> subqueryPredicates;
}
