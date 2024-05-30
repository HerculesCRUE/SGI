package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo_;
import org.crue.hercules.sgi.csp.model.ProyectoSocio_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProyectoSocioEquipoSpecifications {

  /**
   * {@link ProyectoSocioEquipo} de la {@link ProyectoSocio} con el id indicado.
   * 
   * @param id identificador de la {@link ProyectoSocio}.
   * @return specification para obtener los {@link ProyectoSocioEquipo} de la
   *         {@link ProyectoSocio} con el id indicado.
   */
  public static Specification<ProyectoSocioEquipo> byProyectoSocioId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoSocioEquipo_.proyectoSocio).get(ProyectoSocio_.id), id);
  }

  /**
   * {@link ProyectoSocioEquipo} del proyecto
   * 
   * @param proyectoId el id del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoSocioEquipo} del
   *         proyecto.
   */
  public static Specification<ProyectoSocioEquipo> byProyectoId(Long proyectoId) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoSocioEquipo_.proyectoSocio).get(ProyectoSocio_.proyectoId),
        proyectoId);
  }

  /**
   * Se obtienen los {@link ProyectoSocioEquipo} con valores para las fechas de
   * inicio y/o fin
   * 
   * @return specification para obtener los {@link ProyectoSocio} con
   *         valor en alguna de sus fechas
   */
  public static Specification<ProyectoSocioEquipo> withFechaInicioOrFechaFin() {
    return (root, query, cb) -> cb.or(cb.isNotNull(root.get(ProyectoSocioEquipo_.fechaInicio)),
        cb.isNotNull(root.get(ProyectoSocioEquipo_.fechaFin)));
  }

}
