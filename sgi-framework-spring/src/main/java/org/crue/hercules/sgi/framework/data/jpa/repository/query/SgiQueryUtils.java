package org.crue.hercules.sgi.framework.data.jpa.repository.query;

import static javax.persistence.metamodel.Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.MANY_TO_MANY;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.MANY_TO_ONE;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.ONE_TO_MANY;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.ONE_TO_ONE;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Simple utility class to create JPA queries using the default implementation
 * of a custom parser.
 * 
 * Based on SpringData {@link QueryUtils} with a little changes to generate
 * joins of
 * i18n fields limited to current languages to prevent cartesian product when
 * sorting by that fields.
 *
 * @author Oliver Gierke
 * @author Kevin Raymond
 * @author Thomas Darimont
 * @author Komi Innocent
 * @author Christoph Strobl
 * @author Mark Paluch
 * @author Sébastien Péralta
 * @author Jens Schauder
 * @author Nils Borrmann
 * @author Reda.Housni-Alaoui
 * @author Florian Lüdiger
 * @author Grégoire Druant
 * @author Mohammad Hewedy
 * @author Andriy Redko
 * @author Peter Großmann
 * @author Greg Turnquist
 * @author Diego Krupitza
 * @author Jędrzej Biedrzycki
 * @author Darin Manica
 * @author Simon Paradies
 * @author Vladislav Yukharin
 * @author Chris Fraser
 */
public class SgiQueryUtils {

  private static final Map<PersistentAttributeType, Class<? extends Annotation>> ASSOCIATION_TYPES;

  static {
    Map<PersistentAttributeType, Class<? extends Annotation>> persistentAttributeTypes = new HashMap<>();
    persistentAttributeTypes.put(ONE_TO_ONE, OneToOne.class);
    persistentAttributeTypes.put(ONE_TO_MANY, null);
    persistentAttributeTypes.put(MANY_TO_ONE, ManyToOne.class);
    persistentAttributeTypes.put(MANY_TO_MANY, null);
    persistentAttributeTypes.put(ELEMENT_COLLECTION, null);

    ASSOCIATION_TYPES = Collections.unmodifiableMap(persistentAttributeTypes);
  }

  /**
   * Private constructor to prevent instantiation.
   */
  private SgiQueryUtils() {

  }

  /**
   * Turns the given {@link Sort} into {@link javax.persistence.criteria.Order}s.
   *
   * @param sort the {@link Sort} instance to be transformed into JPA
   *             {@link javax.persistence.criteria.Order}s.
   * @param from must not be {@literal null}.
   * @param cb   must not be {@literal null}.
   * @return a {@link List} of {@link javax.persistence.criteria.Order}s.
   */
  public static List<javax.persistence.criteria.Order> toOrders(Sort sort,
      From<?, ?> from, CriteriaBuilder cb) {

    if (sort.isUnsorted()) {
      return Collections.emptyList();
    }

    Assert.notNull(from, "From must not be null!");
    Assert.notNull(cb, "CriteriaBuilder must not be null!");

    List<javax.persistence.criteria.Order> orders = new ArrayList<>();

    for (org.springframework.data.domain.Sort.Order order : sort) {
      orders.add(toJpaOrder(order, from, cb));
    }

    return orders;
  }

  /**
   * Creates a criteria API {@link javax.persistence.criteria.Order} from the
   * given {@link Order}.
   *
   * @param order the order to transform into a JPA
   *              {@link javax.persistence.criteria.Order}
   * @param from  the {@link From} the {@link Order} expression is based on
   * @param cb    the {@link CriteriaBuilder} to build the
   *              {@link javax.persistence.criteria.Order} with
   * @return Guaranteed to be not {@literal null}.
   */
  @SuppressWarnings("unchecked")
  private static javax.persistence.criteria.Order toJpaOrder(Order order, From<?, ?> from, CriteriaBuilder cb) {

    PropertyPath property = PropertyPath.from(order.getProperty(), from.getJavaType());
    Expression<?> expression = toExpressionRecursively(from, cb, property);

    if (order.isIgnoreCase() && String.class.equals(expression.getJavaType())) {
      Expression<String> upper = cb.lower((Expression<String>) expression);
      return order.isAscending() ? cb.asc(upper) : cb.desc(upper);
    } else {
      return order.isAscending() ? cb.asc(expression) : cb.desc(expression);
    }
  }

  static <T> Path<T> toExpressionRecursively(From<?, ?> from, CriteriaBuilder cb, PropertyPath property) {
    return toExpressionRecursively(from, cb, property, false);
  }

  static <T> Path<T> toExpressionRecursively(From<?, ?> from, CriteriaBuilder cb, PropertyPath property,
      boolean isForSelection) {
    return toExpressionRecursively(from, cb, property, isForSelection, false);
  }

  /**
   * Creates an expression with proper inner and left joins by recursively
   * navigating the path
   *
   * @param from                 the {@link From}
   * @param cb                   the {@link CriteriaBuilder}
   * @param property             the property path
   * @param isForSelection       is the property navigated for the selection or
   *                             ordering part of the query?
   * @param hasRequiredOuterJoin has a parent already required an outer join?
   * @param <T>                  the type of the expression
   * @return the expression
   */
  @SuppressWarnings("unchecked")
  static <T> Path<T> toExpressionRecursively(From<?, ?> from, CriteriaBuilder cb, PropertyPath property,
      boolean isForSelection,
      boolean hasRequiredOuterJoin) {

    String segment = property.getSegment();

    boolean isLeafProperty = !property.hasNext();

    boolean requiresOuterJoin = requiresOuterJoin(from, property, isForSelection, hasRequiredOuterJoin);

    boolean isI18nProperty = isI18nProperty(from, property);

    // if it does not require an outer join and is a leaf, simply get the segment
    if (!requiresOuterJoin && isLeafProperty && !isI18nProperty) {
      return from.get(segment);
    }

    // get or create the join
    JoinType joinType = requiresOuterJoin ? JoinType.LEFT : JoinType.INNER;
    Join<?, ?> join = getOrCreateJoin(from, cb, segment, joinType, isI18nProperty);

    // if it's a leaf, return the join
    if (isLeafProperty) {
      return (Path<T>) join;
    }

    PropertyPath nextProperty = Objects.requireNonNull(property.next(), "An element of the property path is null!");

    // recurse with the next property
    return toExpressionRecursively(join, cb, nextProperty, isForSelection, requiresOuterJoin);
  }

  /**
   * Returns an existing join for the given attribute if one already exists or
   * creates a new one if not. If is an I18n attribute join is limited to the
   * current language request.
   *
   * @param from            the {@link From} to get the current joins from.
   * @param cb              the {@link CriteriaBuilder} to use
   * @param attribute       the {@link Attribute} to look for in the current
   *                        joins.
   * @param joinType        the join type to create if none was found
   * @param isI18nAttribute the flag that indicates if the attribute is a i18n
   *                        field
   * @return will never be {@literal null}.
   */
  private static Join<?, ?> getOrCreateJoin(From<?, ?> from, CriteriaBuilder cb, String attribute, JoinType joinType,
      boolean isI18nAttribute) {

    for (Join<?, ?> join : from.getJoins()) {

      if (join.getAttribute().getName().equals(attribute)) {
        if (join.getOn() == null && isI18nAttribute) {
          join.on(cb.equal(join.get("lang"), SgiLocaleContextHolder.getLanguage()));
        }
        return join;
      }
    }
    if (isI18nAttribute) {
      Join<?, ?> join = from.join(attribute, JoinType.LEFT);
      join.on(cb.equal(join.get("lang"), SgiLocaleContextHolder.getLanguage()));
      return join;
    }
    return from.join(attribute, joinType);
  }

  /**
   * Checks if this attribute requires an outer join. This is the case e.g. if it
   * hadn't already been fetched with an
   * inner join and if it's an optional association, and if previous paths has
   * already required outer joins. It also
   * ensures outer joins are used even when Hibernate defaults to inner joins
   * (HHH-12712 and HHH-12999).
   *
   * @param from                 the {@link From} to check for fetches.
   * @param property             the property path
   * @param isForSelection       is the property navigated for the selection or
   *                             ordering part of the query? if true, we need
   *                             to generate an explicit outer join in order to
   *                             prevent Hibernate to use an inner join instead.
   *                             see
   *                             https://hibernate.atlassian.net/browse/HHH-12999
   * @param hasRequiredOuterJoin has a parent already required an outer join?
   * @return whether an outer join is to be used for integrating this attribute in
   *         a query.
   */
  private static boolean requiresOuterJoin(From<?, ?> from, PropertyPath property, boolean isForSelection,
      boolean hasRequiredOuterJoin) {

    String segment = property.getSegment();

    // already inner joined so outer join is useless
    if (isAlreadyInnerJoined(from, segment))
      return false;

    Bindable<?> propertyPathModel;
    Bindable<?> model = from.getModel();

    // required for EclipseLink: we try to avoid using from.get as EclipseLink
    // produces an inner join
    // regardless of which join operation is specified next
    // see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=413892
    // still occurs as of 2.7
    ManagedType<?> managedType = null;
    if (model instanceof ManagedType) {
      managedType = (ManagedType<?>) model;
    } else if (model instanceof SingularAttribute
        && ((SingularAttribute<?, ?>) model).getType() instanceof ManagedType) {
      managedType = (ManagedType<?>) ((SingularAttribute<?, ?>) model).getType();
    }
    if (managedType != null) {
      propertyPathModel = (Bindable<?>) managedType.getAttribute(segment);
    } else {
      propertyPathModel = from.get(segment).getModel();
    }

    // is the attribute of Collection type?
    boolean isPluralAttribute = model instanceof PluralAttribute;

    boolean isLeafProperty = !property.hasNext();

    if (propertyPathModel == null && isPluralAttribute) {
      return true;
    }

    if (!(propertyPathModel instanceof Attribute)) {
      return false;
    }

    Attribute<?, ?> attribute = (Attribute<?, ?>) propertyPathModel;

    // not a persistent attribute type association (@OneToOne, @ManyToOne)
    if (!ASSOCIATION_TYPES.containsKey(attribute.getPersistentAttributeType())) {
      return false;
    }

    boolean isCollection = attribute.isCollection();
    // if this path is an optional one to one attribute navigated from the not
    // owning side we also need an
    // explicit outer join to avoid https://hibernate.atlassian.net/browse/HHH-12712
    // and https://github.com/eclipse-ee4j/jpa-api/issues/170
    boolean isInverseOptionalOneToOne = PersistentAttributeType.ONE_TO_ONE == attribute.getPersistentAttributeType()
        && StringUtils.hasText(getAnnotationProperty(attribute, "mappedBy", ""));

    if (isLeafProperty && !isForSelection && !isCollection && !isInverseOptionalOneToOne && !hasRequiredOuterJoin) {
      return false;
    }

    return hasRequiredOuterJoin || getAnnotationProperty(attribute, "optional", true);
  }

  /**
   * Determine if the property refers to a i18n field
   * 
   * @param from     the {@link From} to check for fetches.
   * @param property the property path
   * @return true if the attribute is a i18n field
   */
  private static boolean isI18nProperty(From<?, ?> from, PropertyPath property) {

    String segment = property.getSegment();

    // already inner joined so outer join is useless
    if (isAlreadyInnerJoined(from, segment)) {
      return false;
    }

    Bindable<?> propertyPathModel;
    Bindable<?> model = from.getModel();

    // required for EclipseLink: we try to avoid using from.get as EclipseLink
    // produces an inner join
    // regardless of which join operation is specified next
    // see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=413892
    // still occurs as of 2.7
    ManagedType<?> managedType = null;
    if (model instanceof ManagedType) {
      managedType = (ManagedType<?>) model;
    } else if (model instanceof SingularAttribute
        && ((SingularAttribute<?, ?>) model).getType() instanceof ManagedType) {
      managedType = (ManagedType<?>) ((SingularAttribute<?, ?>) model).getType();
    }
    if (managedType != null) {
      propertyPathModel = (Bindable<?>) managedType.getAttribute(segment);
    } else {
      propertyPathModel = from.get(segment).getModel();
    }

    if (propertyPathModel == null) {
      return false;
    }

    if (!(propertyPathModel instanceof Attribute)) {
      return false;
    }

    Attribute<?, ?> attribute = (Attribute<?, ?>) propertyPathModel;

    // Is a i18n field annotated with @CollectionElement
    if (attribute.getPersistentAttributeType() == ELEMENT_COLLECTION) {
      return true;
    }

    return false;
  }

  /**
   * Return whether the given {@link From} contains an inner join for the
   * attribute with the given name.
   *
   * @param from      the {@link From} to check for joins.
   * @param attribute the attribute name to check.
   * @return true if the attribute has already been inner joined
   */
  private static boolean isAlreadyInnerJoined(From<?, ?> from, String attribute) {

    for (Fetch<?, ?> fetch : from.getFetches()) {

      if (fetch.getAttribute().getName().equals(attribute) //
          && fetch.getJoinType().equals(JoinType.INNER)) {
        return true;
      }
    }

    for (Join<?, ?> join : from.getJoins()) {

      if (join.getAttribute().getName().equals(attribute) //
          && join.getJoinType().equals(JoinType.INNER)) {
        return true;
      }
    }

    return false;
  }

  @Nullable
  private static <T> T getAnnotationProperty(Attribute<?, ?> attribute, String propertyName, T defaultValue) {

    Class<? extends Annotation> associationAnnotation = ASSOCIATION_TYPES.get(attribute.getPersistentAttributeType());

    if (associationAnnotation == null) {
      return defaultValue;
    }

    Member member = attribute.getJavaMember();

    if (!(member instanceof AnnotatedElement)) {
      return defaultValue;
    }

    Annotation annotation = AnnotationUtils.getAnnotation((AnnotatedElement) member, associationAnnotation);
    return annotation == null ? defaultValue : (T) AnnotationUtils.getValue(annotation, propertyName);
  }
}
