package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.EstadoProyecto_;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoSpecifications {

  private ProyectoSpecifications() {
  }

  /**
   * {@link Proyecto} con Activo a True
   * 
   * @return specification para obtener las {@link Proyecto} activas
   */
  public static Specification<Proyecto> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(Proyecto_.activo));
  }

  /**
   * {@link Proyecto} con Activo a False
   * 
   * @return specification para obtener los {@link Proyecto} no activos
   */
  public static Specification<Proyecto> notActivos() {
    return (root, query, cb) -> cb.isFalse(root.get(Proyecto_.activo));
  }

  /**
   * {@link Proyecto} con un unidadGestionRef incluido en la lista.
   * 
   * @param unidadGestionRefs lista de unidadGestionRefs
   * @return specification para obtener los {@link Convocatoria} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<Proyecto> unidadGestionRefIn(List<String> unidadGestionRefs) {
    return (root, query, cb) -> root.get(Proyecto_.unidadGestionRef).in(unidadGestionRefs);
  }

  /**
   * Filtro de {@link Proyecto} por su identificador
   * 
   * @param id identificador del {@link Proyecto}
   * @return el {@link Proyecto}
   */
  public static Specification<Proyecto> byProyectoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(Proyecto_.id), id);
  }

  /**
   * Filtro de {@link Proyecto} por el id de solicitud
   * 
   * @param solicitudId identificador de la {@link Solicitud}
   * @return lista de {@link Proyecto}
   */
  public static Specification<Proyecto> bySolicitudId(Long solicitudId) {
    return (root, query, cb) -> cb.equal(root.get(Proyecto_.solicitudId), solicitudId);
  }

  /**
   * Solo {@link Proyecto} distintas.
   * 
   * @return specification para obtener las entidades {@link Proyecto} distintas
   *         solamente.
   */
  public static Specification<Proyecto> distinct() {
    return (root, query, cb) -> {
      query.distinct(true);
      return null;
    };
  }

  /**
   * Filtro de {@link Proyecto} por el id de modelo ejecucion
   * 
   * @param modeloEjecucionId identificador de la {@link ModeloEjecucion}
   * @return lista de {@link Proyecto}
   */
  public static Specification<Proyecto> byModeloEjecucionId(Long modeloEjecucionId) {
    return (root, query, cb) -> cb.equal(root.get(Proyecto_.modeloEjecucion).get(ModeloEjecucion_.id),
        modeloEjecucionId);
  }

  /**
   * Filtro de {@link Proyecto} con estado diferente a BORRADOR.
   * 
   * @return specification para obtener los {@link Proyecto} con estado diferente
   *         de borrador.
   */
  public static Specification<Proyecto> byEstadoNotBorrador() {
    return (root, query, cb) -> cb
        .equal(root.get(Proyecto_.estado).get(EstadoProyecto_.ESTADO), EstadoProyecto.Estado.BORRADOR).not();
  }

  /**
   * Filtro de {@link Proyecto} por id de investigador presente en el equipo
   * 
   * @param investigadorId identificador del investigador
   * @return lista de {@link Proyecto}
   */
  public static Specification<Proyecto> byInvestigadorId(String investigadorId) {
    return (root, query, cb) -> {
      Subquery<Long> queryEquipo = query.subquery(Long.class);
      Root<ProyectoEquipo> subqRoot = queryEquipo.from(ProyectoEquipo.class);
      queryEquipo.select(subqRoot.get(ProyectoEquipo_.proyecto).get(Proyecto_.id))
          .where(cb.equal(subqRoot.get(ProyectoEquipo_.personaRef), investigadorId));
      return root.get(Proyecto_.id).in(queryEquipo);
    };
  }

  /**
   * Filtro de {@link Proyecto} por id de investigador que sea responsable
   * económico
   * 
   * @param investigadorId identificador del investigador
   * @return lista de {@link Proyecto}
   */
  public static Specification<Proyecto> byResponsableEconomicoId(String investigadorId) {
    return (root, query, cb) -> {
      Subquery<Long> queryResponsableEconomico = query.subquery(Long.class);
      Root<ProyectoResponsableEconomico> subqRoot = queryResponsableEconomico.from(ProyectoResponsableEconomico.class);
      queryResponsableEconomico.select(subqRoot.get(ProyectoResponsableEconomico_.proyecto).get(Proyecto_.id))
          .where(cb.equal(subqRoot.get(ProyectoResponsableEconomico_.personaRef), investigadorId));
      return root.get(Proyecto_.id).in(queryResponsableEconomico);
    };
  }

  /**
   * {@link Proyecto} para los que alguna de las personas de la lista está entre
   * los {@link ProyectoEquipo} en la fecha indicada.
   *
   * @param personaRefs Lista de identificadores de las personas.
   * @param fecha       Fecha para la que se hace la comprobación.
   * @return Specification para obtener los {@link Proyecto} en los que alguna de
   *         las personas esté en el {@link ProyectoEquipo} en la fecha indicada.
   */
  public static Specification<Proyecto> byMiembrosEquipo(List<String> personaRefs, Instant fecha) {
    return (root, query, cb) -> {
      Join<Proyecto, ProyectoEquipo> joinEquipos = root.join(Proyecto_.equipo, JoinType.LEFT);

      List<Predicate> predicatesPorPersona = new ArrayList<>();
      for (String personaRef : personaRefs) {

        Predicate personaRefEquals = cb.equal(joinEquipos.get(ProyectoEquipo_.personaRef), personaRef);

        Predicate greaterThanFechaInicio = cb.or(
            cb.lessThanOrEqualTo(joinEquipos.get(ProyectoEquipo_.fechaInicio), fecha),
            cb.and(
                cb.isNull(joinEquipos.get(ProyectoEquipo_.fechaInicio)),
                cb.lessThanOrEqualTo(root.get(Proyecto_.fechaInicio), fecha)));

        // Si el miembro del equipo tiene fecha de fin se usa la suya, si no se usa la
        // fecha de fin definitiva del proyecto como fecha de fin y en caso de que no la
        // tenga entonces se usa la fecha de fin del proyecto
        Predicate lowerThanFechaFin = cb.or(

            cb.greaterThanOrEqualTo(joinEquipos.get(ProyectoEquipo_.fechaFin), fecha),
            cb.and(
                cb.isNull(joinEquipos.get(ProyectoEquipo_.fechaFin)),
                cb.or(
                    cb.and(
                        cb.isNotNull(root.get(Proyecto_.fechaFinDefinitiva)),
                        cb.greaterThanOrEqualTo(root.get(Proyecto_.fechaFinDefinitiva), fecha)),
                    cb.and(
                        cb.isNull(root.get(Proyecto_.fechaFinDefinitiva)),
                        cb.or(
                            cb.isNull(root.get(Proyecto_.fechaFin)),
                            cb.greaterThanOrEqualTo(root.get(Proyecto_.fechaFin), fecha))))));

        predicatesPorPersona.add(cb.and(
            personaRefEquals,
            greaterThanFechaInicio,
            lowerThanFechaFin));
      }

      return cb.or(predicatesPorPersona.toArray(new Predicate[0]));
    };
  }

}
