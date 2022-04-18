package org.crue.hercules.sgi.prc.repository.specification;

import java.time.Instant;

import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.Autor_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.jpa.domain.Specification;

public class AutorSpecifications {

  private AutorSpecifications() {
  }

  /**
   * {@link Autor} de la {@link ProduccionCientifica} con el id indicado.
   * 
   * @param id identificador de la {@link ProduccionCientifica}.
   * @return specification para obtener los {@link Autor} de
   *         la {@link ProduccionCientifica} con el id indicado.
   */
  public static Specification<Autor> byProduccionCientificaId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(Autor_.produccionCientificaId), id);
  }

  /**
   * {@link Autor} con el personaRef informado.
   * 
   * @return specification para obtener los {@link Autor} con el personaRef
   *         informado.
   */
  public static Specification<Autor> byPersonaRefIsNotNull() {
    return (root, query, cb) -> cb.isNotNull(root.get(Autor_.personaRef));
  }

  /**
   * {@link Autor} con fechas entre la fecha de baremación
   * 
   * @param fechaBaremacion fecha de baremacion
   * @return specification para obtener los {@link Autor} con rango de fechas
   *         entre la fecha de baremación
   */
  public static Specification<Autor> byRangoFechaInFechaBaremacion(Instant fechaBaremacion) {
    return (root, query, cb) -> cb.and(
        cb.or(cb.isNull(root.get(Autor_.fechaInicio)),
            cb.lessThanOrEqualTo(root.get(Autor_.fechaInicio), fechaBaremacion)),
        cb.or(cb.isNull(root.get(Autor_.fechaFin)),
            cb.greaterThanOrEqualTo(root.get(Autor_.fechaFin), fechaBaremacion)));
  }
}
