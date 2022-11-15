package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Programa_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProyectoEntidadConvocanteSpecifications {

  /**
   * {@link ProyectoEntidadConvocante} de la {@link Proyecto} con el id indicado.
   * 
   * @param id identificador de la {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoEntidadConvocante} de
   *         la {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoEntidadConvocante> byProyectoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoEntidadConvocante_.proyectoId), id);
  }

  /**
   * {@link ProyectoEntidadConvocante} con el entidadRef indicado.
   * 
   * @param entidadRef identificador de la entidad.
   * @return specification para obtener los {@link ProyectoEntidadConvocante} con
   *         el entidadRef indicado.
   */
  public static Specification<ProyectoEntidadConvocante> byEntidadRef(String entidadRef) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoEntidadConvocante_.entidadRef), entidadRef);
  }

  /**
   * {@link ProyectoEntidadConvocante} con el
   * {@link ProyectoEntidadConvocante#programaConvocatoria}
   * con el id indicado.
   * 
   * @param id identificador del {@link Programa}.
   * @return specification para obtener los {@link ProyectoEntidadConvocante} con
   *         el {@link ProyectoEntidadConvocante#programaConvocatoria}
   *         con el id indicado.
   */
  public static Specification<ProyectoEntidadConvocante> byProgramaConvocatoriaId(Long id) {
    return (root, query,
        cb) -> id == null ? cb.isNull(root.get(ProyectoEntidadConvocante_.programaConvocatoria))
            : cb.equal(root.get(ProyectoEntidadConvocante_.programaConvocatoria).get(Programa_.id), id);
  }

}