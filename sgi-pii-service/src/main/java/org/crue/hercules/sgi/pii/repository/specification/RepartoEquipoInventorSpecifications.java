package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.Reparto;
import org.crue.hercules.sgi.pii.model.RepartoEquipoInventor;
import org.crue.hercules.sgi.pii.model.RepartoEquipoInventor_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RepartoEquipoInventorSpecifications {
  /**
   * {@link RepartoEquipoInventor} de la entidad {@link Reparto} con el id
   * indicado.
   * 
   * @param id identificador de la entidad {@link Reparto}.
   * @return specification para obtener los {@link RepartoEquipoInventor} de la
   *         entidad {@link Reparto} con el id indicado.
   */
  public static Specification<RepartoEquipoInventor> byRepartoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(RepartoEquipoInventor_.repartoId), id);
  }
}
