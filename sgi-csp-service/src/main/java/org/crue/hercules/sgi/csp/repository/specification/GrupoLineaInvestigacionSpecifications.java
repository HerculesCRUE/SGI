package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion_;
import org.crue.hercules.sgi.csp.model.LineaInvestigacion_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoLineaInvestigacionSpecifications {

  private GrupoLineaInvestigacionSpecifications() {
  }

  /**
   * {@link GrupoLineaInvestigacion} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoLineaInvestigacion} del
   *         {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoLineaInvestigacion> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(GrupoLineaInvestigacion_.grupoId), grupoId);
  }

  /**
   * {@link GrupoLineaInvestigacion} con fechas solapadas
   * 
   * @param fechaInicio fecha inicio de {@link GrupoLineaInvestigacion}
   * @param fechaFin    fecha fin de la {@link GrupoLineaInvestigacion}.
   * @return specification para obtener los {@link GrupoLineaInvestigacion} con
   *         rango de
   *         fechas solapadas
   */
  public static Specification<GrupoLineaInvestigacion> byRangoFechaSolapados(Instant fechaInicio, Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.and(
          cb.or(cb.isNull(root.get(GrupoLineaInvestigacion_.fechaInicio)),
              cb.lessThanOrEqualTo(root.get(GrupoLineaInvestigacion_.fechaInicio),
                  fechaFin != null ? fechaFin : Instant.parse("2500-01-01T23:59:59Z"))),
          cb.or(cb.isNull(root.get(GrupoLineaInvestigacion_.fechaFin)),
              cb.greaterThanOrEqualTo(root.get(GrupoLineaInvestigacion_.fechaFin),
                  fechaInicio != null ? fechaInicio : Instant.parse("1900-01-01T00:00:00Z"))));
    };
  }

  /**
   * {@link GrupoLineaInvestigacion} cuya persona Ref sea la recibida.
   * 
   * @param lineaInvestiacion linea de investigación de
   *                          {@link GrupoLineaInvestigacion}
   * @return specification para obtener los {@link GrupoLineaInvestigacion} cuya
   *         línea de investigación sea la recibida.
   */
  public static Specification<GrupoLineaInvestigacion> byLineaInvestigacion(String lineaInvestiacion) {
    return (root, query, cb) -> {
      return cb.equal(root.get(GrupoLineaInvestigacion_.lineaInvestigacion).get(LineaInvestigacion_.nombre),
          lineaInvestiacion);
    };
  }

  /**
   * {@link GrupoLineaInvestigacion} id diferente de
   * {@link GrupoLineaInvestigacion} con el
   * indicado.
   * 
   * @param id identificador de la {@link GrupoLineaInvestigacion}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link GrupoLineaInvestigacion} indicado.
   */
  public static Specification<GrupoLineaInvestigacion> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(GrupoLineaInvestigacion_.id), id).not();
    };

  }

}
