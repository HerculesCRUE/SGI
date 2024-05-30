package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionGasto;
import org.crue.hercules.sgi.pii.model.InvencionGasto_;
import org.crue.hercules.sgi.pii.model.Procedimiento;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvencionGastoSpecifications {

  /**
   * {@link InvencionGasto} de la {@link Invencion} con el id indicado.
   * 
   * @param id identificador de la {@link Invencion}.
   * @return specification para obtener las {@link InvencionGasto} de la
   *         {@link Invencion} con el id indicado.
   */
  public static Specification<InvencionGasto> byInvencionId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(InvencionGasto_.invencionId), id);
  }

  /**
   * {@link Procedimiento} de la entidad {@link InvencionGasto} con el
   * id indicado.
   * 
   * @param id identificador de la entidad {@link SolicitudProteccion}.
   * @return specification para obtener los {@link InvencionGasto} de la
   *         entidad {@link SolicitudProteccion} con el id indicado.
   */
  public static Specification<InvencionGasto> bySolicitudProteccionId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(InvencionGasto_.solicitudProteccionId), id);
  }

}
