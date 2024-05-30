package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.Procedimiento;
import org.crue.hercules.sgi.pii.model.Procedimiento_;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcedimientoSpecifications {

  /**
   * {@link Procedimiento} de la entidad {@link SolicitudProteccion} con el
   * id indicado.
   * 
   * @param id identificador de la entidad {@link SolicitudProteccion}.
   * @return specification para obtener los {@link Procedimiento} de la
   *         entidad {@link SolicitudProteccion} con el id indicado.
   */
  public static Specification<Procedimiento> bySolicitudProteccionId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(Procedimiento_.solicitudProteccionId), id);
  }

}
