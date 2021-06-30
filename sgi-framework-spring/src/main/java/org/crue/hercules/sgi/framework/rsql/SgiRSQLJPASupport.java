package org.crue.hercules.sgi.framework.rsql;

import java.util.Set;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import io.github.perplexhub.rsql.RSQLCommonSupport;
import io.github.perplexhub.rsql.RSQLOperators;
import lombok.extern.slf4j.Slf4j;

/**
 * Specification builder from RSQL query with support for custom properties
 * resolver
 */
@Slf4j
public class SgiRSQLJPASupport extends RSQLCommonSupport {

  /**
   * Build a {@link Specification} from an RSQL query
   * 
   * @param <T>       the target entity type
   * @param rsqlQuery RSQL query
   * @return the Specifiation to apply
   */
  public static <T> Specification<T> toSpecification(final String rsqlQuery) {
    log.debug("toSpecification(String rsqlQuery) - start");
    Specification<T> returnValue = toSpecification(rsqlQuery, null);
    log.debug("toSpecification(String rsqlQuery) - end");
    return returnValue;
  }

  /**
   * Build a {@link Specification} from an RSQL query
   * 
   * @param <T>               the target entity type
   * @param rsqlQuery         RSQL query
   * @param predicateResolver predicate resolver for custom properties
   * @return the Specification to apply
   */
  public static <T> Specification<T> toSpecification(final String rsqlQuery,
      SgiRSQLPredicateResolver<T> predicateResolver) {
    log.debug("toSpecification(String rsqlQuery,SgiRSQLPredicateResolver<T> predicateResolver) - start");
    Specification<T> returnValue = (root, query, cb) -> {
      if (StringUtils.hasText(rsqlQuery)) {
        Set<ComparisonOperator> supportedOperators = RSQLOperators.supportedOperators();
        Node rsql = new RSQLParser(supportedOperators).parse(rsqlQuery);
        return rsql.accept(new SgiRSQLJPAPredicateConverter(cb, query, predicateResolver), root);
      } else {
        return null;
      }
    };
    log.debug("toSpecification(String rsqlQuery,SgiRSQLPredicateResolver<T> predicateResolver) - end");
    return returnValue;
  }
}
