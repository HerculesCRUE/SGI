package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoFase_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoFaseSpecifications {

  /**
   * {@link ProyectoFase} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoFase} del
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoFase> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoFase_.proyecto).get(Proyecto_.id), id);
    };
  }

  /**
   * {@link ProyectoFase} de la {@link Proyecto} con el id de {@link TipoFase}
   * indicado
   * 
   * @param id identificador de la {@link TipoFase}.
   * @return specification para obtener los {@link ProyectoFase} de la
   *         {@link TipoFase} con el id indicado.
   */
  public static Specification<ProyectoFase> byTipoFaseId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoFase_.tipoFase).get(TipoFase_.id), id);
    };
  }

  /**
   * {@link ProyectoFase} id diferente de {@link ProyectoFase} con el indicado.
   * 
   * @param id identificador de la {@link TipoFase}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link ProyectoFase} indicado.
   */
  public static Specification<ProyectoFase> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(ProyectoFase_.id), id).not();
    };
  }

  /**
   * {@link ProyectoFase} de la {@link Proyecto} con fechas y horas solapadas
   * 
   * @param fechaInicio fecha inicio de {@link ProyectoFase}
   * @param fechaFin    fecha fin de la {@link ProyectoFase}.
   * @return specification para obtener los {@link ProyectoFase} con rango de
   *         fechas solapadas
   */
  public static Specification<ProyectoFase> byRangoFechaSolapados(Instant fechaInicio, Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.and(cb.lessThanOrEqualTo(root.get(ProyectoFase_.fechaInicio), fechaFin),
          cb.greaterThanOrEqualTo(root.get(ProyectoFase_.fechaFin), fechaInicio));
    };
  }

}
