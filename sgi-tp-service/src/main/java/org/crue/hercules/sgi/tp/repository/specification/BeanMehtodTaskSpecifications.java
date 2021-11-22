package org.crue.hercules.sgi.tp.repository.specification;

import java.time.Instant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.tp.model.BeanMethodCronTask;
import org.crue.hercules.sgi.tp.model.BeanMethodInstantTask;
import org.crue.hercules.sgi.tp.model.BeanMethodInstantTask_;
import org.crue.hercules.sgi.tp.model.BeanMethodTask;
import org.crue.hercules.sgi.tp.model.BeanMethodTask_;
import org.springframework.data.jpa.domain.Specification;

public class BeanMehtodTaskSpecifications {

  /**
   * A private constructor to hide the implicit public one.
   */
  private BeanMehtodTaskSpecifications() {
  }

  /**
   * Enabled (not disabled) {@link BeanMethodTask}.
   * 
   * @return specification to get enabled {@link BeanMethodTask}.
   */
  public static Specification<BeanMethodTask> enabled() {
    return (root, query, cb) -> cb.equal(root.get(BeanMethodTask_.disabled), Boolean.FALSE);
  }

  /**
   * Disabled {@link BeanMethodTask}.
   * 
   * @return specification to get disabled {@link BeanMethodTask}.
   */
  public static Specification<BeanMethodTask> disabled() {
    return (root, query, cb) -> cb.equal(root.get(BeanMethodTask_.disabled), Boolean.TRUE);
  }

  /**
   * Future (instant greater than now) {@link BeanMethodInstantTask}.
   * 
   * @return specification to get only future {@link BeanMethodInstantTask}
   */
  public static Specification<BeanMethodTask> includeBeanMethodInstantTaskIfFuture() {
    return (root, query, cb) -> {
      Subquery<Long> beanMethodInstantTaskQuery = query.subquery(Long.class);
      Root<BeanMethodInstantTask> beanMethodInstantTaskRoot = beanMethodInstantTaskQuery
          .from(BeanMethodInstantTask.class);

      beanMethodInstantTaskQuery.select(beanMethodInstantTaskRoot.get(BeanMethodTask_.id));
      beanMethodInstantTaskQuery
          .where(cb.greaterThan(beanMethodInstantTaskRoot.get(BeanMethodInstantTask_.instant), Instant.now()));

      return cb.or(cb.notEqual(root.type(), BeanMethodInstantTask.class),
          cb.in(root.get(BeanMethodTask_.id)).value(beanMethodInstantTaskQuery));
    };
  }

  public static Specification<BeanMethodTask> isCronTasks() {
    return (root, query, cb) -> cb.equal(root.type(), BeanMethodCronTask.class);
  }

  public static Specification<BeanMethodTask> isInstantTasks() {
    return (root, query, cb) -> cb.equal(root.type(), BeanMethodInstantTask.class);
  }

  public static Specification<BeanMethodTask> pastInstantTasks() {
    return (Root<BeanMethodTask> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      Root<BeanMethodInstantTask> beanMethodInstantTaskRoot = cb.treat(root, BeanMethodInstantTask.class);
      return cb.greaterThan(beanMethodInstantTaskRoot.get(BeanMethodInstantTask_.instant), Instant.now());
    };
  }
}