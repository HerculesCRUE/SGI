package org.crue.hercules.sgi.prc.repository.specification;

import java.math.BigDecimal;

import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.Rango;
import org.crue.hercules.sgi.prc.model.Rango.TipoRango;
import org.crue.hercules.sgi.prc.model.Rango_;
import org.springframework.data.jpa.domain.Specification;

public class RangoSpecifications {

  private RangoSpecifications() {
  }

  /**
   * {@link Rango} con el id de {@link ConvocatoriaBaremacion} indicado.
   * 
   * @param convocatoriaBaremacionId id {@link ConvocatoriaBaremacion}.
   * @return specification para obtener los {@link Rango} con el id de
   *         {@link ConvocatoriaBaremacion} indicado.
   */
  public static Specification<Rango> byConvocatoriaBaremacionId(Long convocatoriaBaremacionId) {
    return (root, query, cb) -> cb.equal(root.get(Rango_.convocatoriaBaremacionId), convocatoriaBaremacionId);
  }

  /**
   * {@link Rango} con el {@link TipoRango} indicado.
   * 
   * @param tipoRango {@link TipoRango}.
   * @return specification para obtener los {@link Rango} con el {@link TipoRango}
   *         indicado.
   */
  public static Specification<Rango> byTipoRango(TipoRango tipoRango) {
    return (root, query, cb) -> cb.equal(root.get(Rango_.tipoRango), tipoRango);
  }

  /**
   * {@link Rango} con cuantia entre el rango indicado.
   * 
   * @param cuantia number.
   * @return specification para obtener los {@link Rango} con cuantia entre el
   *         rango indicado
   */
  public static Specification<Rango> inRange(BigDecimal cuantia) {
    return (root, query, cb) -> cb.and(
        cb.or(cb.isNull(root.get(Rango_.hasta)), cb.greaterThanOrEqualTo(root.get(Rango_.hasta), cuantia)),
        cb.lessThanOrEqualTo(root.get(Rango_.desde), cuantia));
  }

}
