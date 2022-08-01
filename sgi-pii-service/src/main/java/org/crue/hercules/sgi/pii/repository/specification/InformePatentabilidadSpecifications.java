package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.InformePatentabilidad;
import org.crue.hercules.sgi.pii.model.InformePatentabilidad_;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InformePatentabilidadSpecifications {

  /**
   * {@link InformePatentabilidad} de la {@link Invencion} con el id indicado.
   * 
   * @param id identificador de la {@link Invencion}.
   * @return specification para obtener los {@link InformePatentabilidad} de la
   *         {@link Invencion} con el id indicado.
   */
  public static Specification<InformePatentabilidad> byInvencionId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(InformePatentabilidad_.invencionId), id);
  }
}
