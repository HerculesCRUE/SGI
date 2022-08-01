package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoNivelAcademico;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoNivelAcademico_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SolicitudRrhhRequisitoNivelAcademicoSpecifications {

  /**
   * {@link SolicitudRrhhRequisitoNivelAcademico} del {@link SolicitudRrhh} con el
   * id indicado.
   * 
   * @param id identificador de la {@link SolicitudRrhh}.
   * @return specification para obtener los
   *         {@link SolicitudRrhhRequisitoNivelAcademico} de la
   *         {@link SolicitudRrhh} con el id indicado.
   */
  public static Specification<SolicitudRrhhRequisitoNivelAcademico> bySolicitudRrhhId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(SolicitudRrhhRequisitoNivelAcademico_.solicitudRrhhId), id);
  }

}
