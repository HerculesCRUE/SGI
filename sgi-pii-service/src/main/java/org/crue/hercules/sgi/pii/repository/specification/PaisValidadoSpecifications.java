package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.PaisValidado;
import org.crue.hercules.sgi.pii.model.PaisValidado_;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaisValidadoSpecifications {

  /**
   * {@link PaisValidado} de la entidad {@link SolicitudProteccion} con el
   * id indicado.
   * 
   * @param id identificador de la entidad {@link SolicitudProteccion}.
   * @return specification para obtener los {@link PaisValidado} de la
   *         entidad {@link SolicitudProteccion} con el id indicado.
   */
  public static Specification<PaisValidado> bySolicitudProteccionId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(PaisValidado_.solicitudProteccionId), id);
  }

}
