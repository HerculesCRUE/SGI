package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PeriodoTitularidadSpecifications {

  /**
   * Devuelve la {@link Specification} necesaria para obtener los
   * {@link PeriodoTitularidad} asociados a la {@link Invencion} pasada por
   * parametros.
   * 
   * @param invencionId {@link Long} Id de la {@link Invencion}.
   * @return Specification
   */
  public static Specification<PeriodoTitularidad> periodoTitularidadByInvencionId(Long invencionId) {
    return (root, query, cb) -> cb.equal(root.get(PeriodoTitularidad_.invencionId), invencionId);
  }

}
