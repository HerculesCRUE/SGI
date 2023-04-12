package org.crue.hercules.sgi.eti.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Bloque_;
import org.crue.hercules.sgi.eti.model.Formulario_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BloqueSpecifications {

  public static Specification<Bloque> formularioIdsIn(List<Long> formulariosIds) {
    return (root, query, cb) -> (root.get(Bloque_.formulario).get(Formulario_.id)).in(formulariosIds);
  }

  /**
   * Bloque de comentarios generales (sin formulario y con orden 0)
   * 
   * @return el {@link Bloque} de comentarios generales
   */
  public static Specification<Bloque> bloqueComentarioGenerales() {
    return (root, query, cb) -> cb.and(
        root.get(Bloque_.FORMULARIO).isNull(),
        cb.equal(root.get(Bloque_.ORDEN), "0"));
  }

}
