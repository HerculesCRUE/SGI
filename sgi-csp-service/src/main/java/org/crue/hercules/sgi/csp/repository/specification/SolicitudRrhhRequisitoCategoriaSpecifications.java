package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoCategoria;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoCategoria_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SolicitudRrhhRequisitoCategoriaSpecifications {

  /**
   * {@link SolicitudRrhhRequisitoCategoria} del {@link SolicitudRrhh} con el id
   * indicado.
   * 
   * @param id identificador de la {@link SolicitudRrhh}.
   * @return specification para obtener los
   *         {@link SolicitudRrhhRequisitoCategoria} de la {@link SolicitudRrhh}
   *         con el id indicado.
   */
  public static Specification<SolicitudRrhhRequisitoCategoria> bySolicitudRrhhId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(SolicitudRrhhRequisitoCategoria_.solicitudRrhhId), id);
  }

}
