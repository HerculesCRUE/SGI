package org.crue.hercules.sgi.prc.repository.specification;

import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion_;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo_;
import org.springframework.data.jpa.domain.Specification;

public class PuntuacionGrupoSpecifications {

  private PuntuacionGrupoSpecifications() {
  }

  /**
   * {@link PuntuacionGrupo} con el grupoRef indicado.
   * 
   * @param grupoRef grupoRef de {@link PuntuacionGrupo}
   * @return specification para obtener los {@link PuntuacionGrupo} con
   *         el GrupoRef indicado.
   */
  public static Specification<PuntuacionGrupo> byGrupoRef(Long grupoRef) {
    return (root, query, cb) -> cb.equal(root.get(PuntuacionGrupo_.grupoRef), grupoRef);
  }

  /**
   * {@link PuntuacionGrupo} de la {@link ConvocatoriaBaremacion} con
   * el anio indicado.
   * 
   * @param anio anio del {@link ConvocatoriaBaremacion}.
   * @return specification para obtener los {@link PuntuacionGrupo} de
   *         la {@link ConvocatoriaBaremacion} con el anio indicado.
   */
  public static Specification<PuntuacionGrupo> byConvocatoriaBaremacionAnio(Integer anio) {
    return (root, query, cb) -> cb
        .equal(root.get(PuntuacionGrupo_.convocatoriaBaremacion).get(ConvocatoriaBaremacion_.anio), anio);
  }

}
