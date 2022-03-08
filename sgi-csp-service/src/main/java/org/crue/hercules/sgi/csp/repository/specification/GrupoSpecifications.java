package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoSpecifications {

  private GrupoSpecifications() {
  }

  /**
   * {@link Grupo} activos.
   * 
   * @return specification para obtener los {@link Grupo} activos.
   */
  public static Specification<Grupo> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(Activable_.activo));
  }

  /**
   * {@link Grupo} con el codigo indicado.
   * 
   * @param codigo un codigo
   * @return specification para obtener los {@link Grupo} con el codigo indicado.
   */
  public static Specification<Grupo> byCodigo(String codigo) {
    return (root, query, cb) -> cb.equal(root.get(Grupo_.codigo), codigo);
  }

  /**
   * {@link Grupo} con el departamento origen indicado.
   * 
   * @param departamentoRef referencia del departamento
   * @return specification para obtener los {@link Grupo} con el departamento
   *         indicado.
   */
  public static Specification<Grupo> byDepartamentoOrigenRef(String departamentoRef) {
    return (root, query, cb) -> cb.equal(root.get(Grupo_.departamentoOrigenRef), departamentoRef);
  }

  /**
   * {@link Grupo} con un id distinto del indicado.
   * 
   * @param grupoId Identificador del {@link Grupo}
   * @return specification para obtener los {@link Grupo} con id distinto del
   *         indicado.
   */
  public static Specification<Grupo> byIdNotEqual(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(Grupo_.id), grupoId).not();
  }

}
