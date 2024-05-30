package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico_;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProyectoResponsableEconomicoSpecifications {

  /**
   * {@link ProyectoResponsableEconomico} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoResponsableEconomico}
   *         del {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoResponsableEconomico> byProyectoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoResponsableEconomico_.proyecto).get(Proyecto_.id), id);
  }

  /**
   * {@link ProyectoResponsableEconomico} cuya persona Ref sea la recibida.
   * 
   * @param personaRef persona ref de {@link ProyectoResponsableEconomico}
   * @return specification para obtener los {@link ProyectoResponsableEconomico}
   *         cuya persona Ref sea la recibida.
   */
  public static Specification<ProyectoResponsableEconomico> byPersonaRef(String personaRef) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoResponsableEconomico_.personaRef), personaRef);
  }

  /**
   * Se obtienen los {@link ProyectoResponsableEconomico} con valores para las
   * fechas de
   * inicio y/o fin
   * 
   * @return specification para obtener los {@link ProyectoSocio} con
   *         valor en alguna de sus fechas
   */
  public static Specification<ProyectoResponsableEconomico> withFechaInicioOrFechaFin() {
    return (root, query, cb) -> cb.or(cb.isNotNull(root.get(ProyectoResponsableEconomico_.fechaInicio)),
        cb.isNotNull(root.get(ProyectoResponsableEconomico_.fechaFin)));
  }

}