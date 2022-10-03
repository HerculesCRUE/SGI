package org.crue.hercules.sgi.prc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CriteriaQueryUtils {

  public static List<Order> toOrders(Sort sort, From<?, ?> from, CriteriaBuilder cb, CriteriaQuery<?> cq,
      String[] selectionNames) {
    Optional<Integer> selectionIndex = getIndexOrderBySelectionName(selectionNames, sort, cq);

    if (selectionIndex.isPresent()) {
      return toOrdersByPosition(selectionIndex.get() + 1, sort, cb);
    } else {
      return QueryUtils.toOrders(sort, from, cb);
    }
  }

  private static List<Order> toOrdersByPosition(int position, Sort sort, CriteriaBuilder cb) {
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

  private static Optional<Integer> getIndexOrderBySelectionName(String[] selectionNames, Sort sort,
      CriteriaQuery<?> cq) {
    return Stream.of(selectionNames)
        .filter(
            selectionName -> !sort.filter(order -> selectionName.equals(order.getProperty())).isEmpty())
        .findFirst()
        .map(
            selectionName -> IntStream.range(0, cq.getSelection().getCompoundSelectionItems().size())
                .filter(
                    index -> selectionName.equals(cq.getSelection().getCompoundSelectionItems().get(index).getAlias()))
                .findFirst()
                .getAsInt());
  }

}
