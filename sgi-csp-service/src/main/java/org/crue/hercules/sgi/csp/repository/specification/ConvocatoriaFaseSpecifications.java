package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaFaseSpecifications {

  /**
   * {@link ConvocatoriaFase} de la {@link Convocatoria} con el id indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los {@link ConvocatoriaFase} de la
   *         {@link Convocatoria} con el id indicado.
   */
  public static Specification<ConvocatoriaFase> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaFase_.convocatoria).get(Convocatoria_.id), id);
    };
  }

  /**
   * {@link ConvocatoriaFase} de la {@link Convocatoria} con el id de
   * {@link TipoFase} indicado
   * 
   * @param id identificador de la {@link TipoFase}.
   * @return specification para obtener los {@link ConvocatoriaFase} de la
   *         {@link TipoFase} con el id indicado.
   */
  public static Specification<ConvocatoriaFase> byTipoFaseId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaFase_.tipoFase).get(TipoFase_.id), id);
    };
  }

  /**
   * {@link ConvocatoriaFase} id diferente de {@link ConvocatoriaFase} con el
   * indicado.
   * 
   * @param id identificador de la {@link TipoFase}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link ConvocatoriaFase} indicado.
   */
  public static Specification<ConvocatoriaFase> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(ConvocatoriaFase_.id), id).not();
    };
  }

  /**
   * {@link ConvocatoriaFase} de la {@link Convocatoria} con fechas solapadas
   * 
   * @param fechaInicio fecha inicio de {@link ConvocatoriaFase}
   * @param fechaFin    fecha fin de la {@link ConvocatoriaFase}.
   * @return specification para obtener los {@link ConvocatoriaFase} con rango de
   *         fechas solapadas
   */
  public static Specification<ConvocatoriaFase> byRangoFechaSolapados(Instant fechaInicio, Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.and(cb.lessThanOrEqualTo(root.get(ConvocatoriaFase_.fechaInicio), fechaFin),
          cb.greaterThanOrEqualTo(root.get(ConvocatoriaFase_.fechaFin), fechaInicio));
    };
  }

}