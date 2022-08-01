package org.crue.hercules.sgi.csp.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProyectoProyectoSgeSpecifications {

  /**
   * {@link ProyectoProyectoSge} del {@link Proyecto} con el id indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoProyectoSge} del
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoProyectoSge> byProyectoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.id), id);

  }

  /**
   * {@link ProyectoProyectoSge} con un { @link Proyecto} con un unidadGestionRef
   * incluido en la lista.
   * 
   * @param unidadGestionRefs lista de unidadGestionRefs
   * @return specification para obtener los {@link ProyectoProyectoSge} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<ProyectoProyectoSge> unidadGestionRefIn(List<String> unidadGestionRefs) {
    return (root, query, cb) -> root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.unidadGestionRef)
        .in(unidadGestionRefs);
  }

  /**
   * {@link ProyectoProyectoSge} asociados a {@link Proyecto} activos.
   * 
   * @return specification para obtener los ProyectoProyectoSge} asociados a
   * 
   *         {@link Proyecto} activos
   */
  public static Specification<ProyectoProyectoSge> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.activo));
  }

  /**
   * {@link ProyectoProyectoSge} del ProyectoSGE con el id indicado.
   * 
   * @param proyectoSgeRef identificador del ProyectoSGE.
   * @return specification para obtener los {@link ProyectoProyectoSge} del
   *         ProyectoSGE con el id indicado.
   */
  public static Specification<ProyectoProyectoSge> byProyectoSgeRef(String proyectoSgeRef) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoProyectoSge_.proyectoSgeRef), proyectoSgeRef);

  }
}