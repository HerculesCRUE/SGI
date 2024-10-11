package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.EstadoValidacionIP;
import org.crue.hercules.sgi.csp.model.EstadoValidacionIP_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProyectoFacturacionSpecifications {

  /**
   * {@link ProyectoFacturacion} de la {@link Proyecto} con el id indicado.
   * 
   * @param id identificador de la {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoFacturacion} del
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoFacturacion> byProyectoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoFacturacion_.proyectoId), id);
  }

  /**
   * {@link ProyectoFacturacion} cuya estado validacion ip es validada.
   *
   * @return specification para obtener los {@link ProyectoFacturacion} cuya
   *         estado validacion ip es validada.
   */
  public static Specification<ProyectoFacturacion> validado() {
    return (root, query, cb) -> cb.equal(
        root.get(ProyectoFacturacion_.estadoValidacionIP).get(EstadoValidacionIP_.estado),
        EstadoValidacionIP.TipoEstadoValidacion.VALIDADA);
  }

  /**
   * {@link ProyectoFacturacion} con fecha de conformidad.
   * 
   * @return specification para obtener los {@link ProyectoFacturacion} con fecha
   *         de conformidad.
   */
  public static Specification<ProyectoFacturacion> byFechaConformidadNotNull() {
    return (root, query, cb) -> cb.isNotNull(root.get(ProyectoFacturacion_.fechaConformidad));
  }
}