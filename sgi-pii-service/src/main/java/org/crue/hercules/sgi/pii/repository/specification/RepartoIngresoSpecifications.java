package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.Reparto;
import org.crue.hercules.sgi.pii.model.RepartoIngreso;
import org.crue.hercules.sgi.pii.model.RepartoIngreso_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RepartoIngresoSpecifications {
  /**
   * {@link RepartoIngreso} de la entidad {@link Reparto} con el id indicado.
   * 
   * @param id identificador de la entidad {@link Reparto}.
   * @return specification para obtener los {@link RepartoIngreso} de la entidad
   *         {@link Reparto} con el id indicado.
   */
  public static Specification<RepartoIngreso> byRepartoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(RepartoIngreso_.repartoId), id);
  }
}
