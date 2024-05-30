package org.crue.hercules.sgi.eti.util;

import java.util.Arrays;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PredicateResolverUtil {

  public static void validateOperatorArgumentNumber(ComparisonNode node, Integer argumentNumber) {
    if (node.getArguments().size() != argumentNumber) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
  }

  public static void validateOperatorIsSupported(ComparisonNode node, ComparisonOperator... validOperators) {
    if (!Arrays.asList(validOperators).contains(node.getOperator())) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + node.getOperator() + " for " + node.getSelector());
    }
  }

}
