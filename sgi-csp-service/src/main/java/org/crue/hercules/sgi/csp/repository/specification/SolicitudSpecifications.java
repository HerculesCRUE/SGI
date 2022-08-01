package org.crue.hercules.sgi.csp.repository.specification;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.SingularAttribute;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud_;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SolicitudSpecifications {

  /**
   * {@link Solicitud} con Activo a True
   * 
   * @return specification para obtener las {@link Solicitud} activas
   */
  public static Specification<Solicitud> activos() {
    return (root, query, cb) -> cb.equal(root.get(Solicitud_.activo), Boolean.TRUE);
  }

  /**
   * {@link Solicitud} con un unidadGestionRef incluido en la lista.
   * 
   * @param unidadGestionRefs lista de unidadGestionRefs
   * @return specification para obtener los {@link Convocatoria} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<Solicitud> unidadGestionRefIn(List<String> unidadGestionRefs) {
    return (root, query, cb) -> root.get(Solicitud_.unidadGestionRef).in(unidadGestionRefs);
  }

  /**
   * {@link Solicitud} en las que la persona es el solicitante.
   * 
   * @param personaRef referencia de la persona
   * @return specification para obtener las {@link Solicitud} en las que la
   *         persona es el solicitante.
   */
  public static Specification<Solicitud> bySolicitante(String personaRef) {
    return (root, query, cb) -> cb.equal(root.get(Solicitud_.solicitanteRef), personaRef);
  }

  /**
   * {@link Solicitud} por id
   * 
   * @param id identificador de la {@link Solicitud}
   * @return specification para obtener las {@link Solicitud} por id.
   */
  public static Specification<Solicitud> byId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(Solicitud_.id), id);
  }

  /**
   * Solo {@link Solicitud} distintas.
   * 
   * @return specification para obtener las entidades {@link Solicitud} distintas
   *         solamente.
   */
  public static Specification<Solicitud> distinct() {
    return (root, query, cb) -> {
      Join<Solicitud, EstadoSolicitud> join = root.join(Solicitud_.estado, JoinType.LEFT);

      List<Expression<?>> expressions = getAllClassFields(Solicitud_.class, root);
      expressions.addAll(getAllClassFields(EstadoSolicitud_.class, join));

      query.groupBy(expressions);
      return cb.isTrue(cb.literal(true));
    };
  }

  /**
   * {@link Solicitud} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador de la {@link Grupo}.
   * @return specification para obtener las {@link Convocatoria} de
   *         la {@link Grupo} con el id indicado.
   */
  public static Specification<Solicitud> byGrupoId(Long grupoId) {
    return (root, query, cb) -> {
      Subquery<Long> queryGrupo = query.subquery(Long.class);
      Root<Grupo> queryGrupoRoot = queryGrupo.from(Grupo.class);
      queryGrupo.select(queryGrupoRoot.get(Grupo_.solicitud).get(Solicitud_.id))
          .where(cb.equal(queryGrupoRoot.get(Grupo_.id), grupoId));
      return root.get(Solicitud_.id).in(queryGrupo);
    };
  }

  /**
   * {@link Solicitud} en las que la persona es el tutor.
   * 
   * @param personaRef referencia de la persona
   * @return specification para obtener las {@link Solicitud} en las que la
   *         persona es el tutor.
   */
  public static Specification<Solicitud> byTutor(String personaRef) {
    return (root, query, cb) -> {
      Subquery<Long> queryTutor = query.subquery(Long.class);
      Root<SolicitudRrhh> queryTutorRoot = queryTutor.from(SolicitudRrhh.class);
      queryTutor.select(queryTutorRoot.get(SolicitudRrhh_.solicitud).get(Solicitud_.id))
          .where(cb.equal(queryTutorRoot.get(SolicitudRrhh_.tutorRef), personaRef));
      return root.get(Solicitud_.id).in(queryTutor);
    };
  }

  /**
   * {@link Solicitud} en las que la persona es el solicitante o el tutor.
   * 
   * @param personaRef referencia de la persona
   * @return specification para obtener las {@link Solicitud} en las que la
   *         persona es el solicitante o el tutor.
   */
  public static Specification<Solicitud> bySolicitanteOrTutor(String personaRef) {
    return bySolicitante(personaRef).or(byTutor(personaRef));
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
