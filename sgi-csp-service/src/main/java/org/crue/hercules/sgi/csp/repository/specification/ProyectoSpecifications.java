package org.crue.hercules.sgi.csp.repository.specification;

import java.util.List;

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

  /**
   * {@link Proyecto} con Activo a True
   * 
   * @return specification para obtener las {@link Proyecto} activas
   */
  public static Specification<Proyecto> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Proyecto_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link Proyecto} con un unidadGestionRef incluido en la lista.
   * 
   * @param unidadGestionRefs lista de unidadGestionRefs
   * @return specification para obtener los {@link Convocatoria} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<Proyecto> unidadGestionRefIn(List<String> unidadGestionRefs) {
    return (root, query, cb) -> {
      return root.get(Proyecto_.unidadGestionRef).in(unidadGestionRefs);
    };
  }

  /**
   * Filtro de {@link Proyecto} por su identificador
   * 
   * @param id identificador del {@link Proyecto}
   * @return el {@link Proyecto}
   */
  public static Specification<Proyecto> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Proyecto_.id), id);
    };
  }

  /**
   * Filtro de {@link Proyecto} por el id de solicitud
   * 
   * @param solicitudId identificador de la {@link Solicitud}
   * @return lista de {@link Proyecto}
   */
  public static Specification<Proyecto> bySolicitudId(Long solicitudId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Proyecto_.solicitudId), solicitudId);
    };
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
    return (root, query, cb) -> {
      return cb.equal(root.get(Proyecto_.modeloEjecucion).get(ModeloEjecucion_.id), modeloEjecucionId);
    };
  }

  /**
   * Filtro de {@link Proyecto} con estado diferente a BORRADOR.
   * 
   * @return specification para obtener los {@link Proyecto} con estado diferente
   *         de borrador.
   */
  public static Specification<Proyecto> byEstadoNotBorrador() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Proyecto_.estado).get(EstadoProyecto_.ESTADO), EstadoProyecto.Estado.BORRADOR).not();
    };
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

}
