package org.crue.hercules.sgi.pii.repository.specification;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.SingularAttribute;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.Invencion_;
import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.model.TipoProteccion_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvencionSpecifications {

  /**
   * {@link Invencion} activos.
   * 
   * @return specification para obtener los {@link Invencion} activos.
   */
  public static Specification<Invencion> activos() {
    return (root, query, cb) -> cb.equal(root.get(Activable_.activo), Boolean.TRUE);
  }

  /**
   * Solo {@link Invencion} distintas.
   * 
   * @return specification para obtener las {@link Invencion} distintas solamente.
   */
  public static Specification<Invencion> distinct() {
    return (root, query, cb) -> {
      Join<Invencion, TipoProteccion> join = root.join(Invencion_.tipoProteccion, JoinType.LEFT);

      List<Expression<?>> expressions = getAllClassFields(Invencion_.class, root);
      expressions.addAll(getAllClassFields(TipoProteccion_.class, join));

      query.groupBy(expressions);
      return cb.isTrue(cb.literal(true));
    };
  }

  @SuppressWarnings("unchecked")
  private static <T> List<Expression<?>> getAllClassFields(Class<?> metamodelClass, From<?, T> from) {
    List<Expression<?>> expressions = new ArrayList<>();
    Field[] fields = metamodelClass.getFields();
    for (Field field : fields) {
      if (Modifier.isPublic(field.getModifiers())) {
        try {
          Object obj = field.get(null);
          if (obj instanceof SingularAttribute) {
            expressions.add(from.get((SingularAttribute<T, ?>) obj));
          }
        } catch (IllegalAccessException e) {
          log.error(e.getMessage(), e);
        }
      }
    }

    return expressions;
  }

}
