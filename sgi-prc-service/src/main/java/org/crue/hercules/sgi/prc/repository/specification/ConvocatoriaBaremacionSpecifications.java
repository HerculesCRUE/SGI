package org.crue.hercules.sgi.prc.repository.specification;

import java.time.Instant;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaBaremacionSpecifications {

  private ConvocatoriaBaremacionSpecifications() {
  }

  /**
   * {@link ConvocatoriaBaremacion} activos.
   * 
   * @return specification para obtener los {@link ConvocatoriaBaremacion}
   *         activos.
   */
  public static Specification<ConvocatoriaBaremacion> activos() {
    return (root, query, cb) -> cb.equal(root.get(Activable_.activo), Boolean.TRUE);
  }

  /**
   * {@link ConvocatoriaBaremacion} necesita resetear porque no ha finalizado la
   * baremación en el tiempo establecido.
   * 
   * @param fechaLimiteFinBaremacion fecha limite
   * @return specification para obtener los {@link ConvocatoriaBaremacion} que
   *         necesitan resetearse
   */
  public static Specification<ConvocatoriaBaremacion> isResettable(Instant fechaLimiteFinBaremacion) {
    return (root, query, cb) -> cb.and(cb.isNotNull(root.get(ConvocatoriaBaremacion_.fechaInicioEjecucion)),
        cb.isNull(root.get(ConvocatoriaBaremacion_.fechaFinEjecucion)),
        cb.lessThan(root.get(ConvocatoriaBaremacion_.fechaInicioEjecucion), fechaLimiteFinBaremacion));
  }
}
