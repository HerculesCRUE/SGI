package org.crue.hercules.sgi.prc.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador_;
import org.springframework.data.jpa.domain.Specification;

public class PuntuacionItemInvestigadorSpecifications {

  private PuntuacionItemInvestigadorSpecifications() {
  }

  /**
   * {@link PuntuacionItemInvestigador} con el personaRef indicado.
   * 
   * @param personaRef personaRef de {@link PuntuacionItemInvestigador}
   * @return specification para obtener los {@link PuntuacionItemInvestigador} con
   *         el personaRef indicado.
   */
  public static Specification<PuntuacionItemInvestigador> byPersonaRef(String personaRef) {
    return (root, query, cb) -> cb.equal(root.get(PuntuacionItemInvestigador_.personaRef), personaRef);
  }

  /**
   * {@link PuntuacionItemInvestigador} de la {@link ConvocatoriaBaremacion} con
   * el id indicado.
   * 
   * @param id identificador del {@link ConvocatoriaBaremacion}.
   * @return specification para obtener los {@link PuntuacionItemInvestigador} de
   *         la {@link ConvocatoriaBaremacion} con el id indicado.
   */
  public static Specification<PuntuacionItemInvestigador> byConvocatoriaBaremacionId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(PuntuacionItemInvestigador_.produccionCientifica)
        .get(ProduccionCientifica_.convocatoriaBaremacion).get(ConvocatoriaBaremacion_.id), id);
  }

  /**
   * {@link PuntuacionItemInvestigador} de la {@link ConvocatoriaBaremacion} con
   * el anio indicado.
   * 
   * @param anio anio del {@link ConvocatoriaBaremacion}.
   * @return specification para obtener los {@link PuntuacionItemInvestigador} de
   *         la {@link ConvocatoriaBaremacion} con el anio indicado.
   */
  public static Specification<PuntuacionItemInvestigador> byConvocatoriaBaremacionAnio(Integer anio) {
    return (root, query, cb) -> cb.equal(root.get(PuntuacionItemInvestigador_.produccionCientifica)
        .get(ProduccionCientifica_.convocatoriaBaremacion).get(ConvocatoriaBaremacion_.anio), anio);
  }

  /**
   * {@link PuntuacionItemInvestigador} con el {@link TipoPuntuacion} indicado.
   * 
   * @param tipoPuntuacion {@link TipoPuntuacion}.
   * @return specification para obtener los {@link PuntuacionItemInvestigador} con
   *         el {@link TipoPuntuacion} indicado.
   */
  public static Specification<PuntuacionItemInvestigador> byTipoPuntuacion(TipoPuntuacion tipoPuntuacion) {
    return (root, query, cb) -> cb.equal(root.get(PuntuacionItemInvestigador_.tipoPuntuacion), tipoPuntuacion);
  }

  /**
   * Lista de {@link PuntuacionItemInvestigador} que no contenga los
   * {@link TipoPuntuacion} indicados.
   * 
   * @param tiposPuntuacion Lista de {@link TipoPuntuacion}.
   * @return specification para obtener los {@link PuntuacionItemInvestigador} que
   *         no contenga los {@link TipoPuntuacion} indicados.
   */
  public static Specification<PuntuacionItemInvestigador> byTipoPuntuacionNotIn(List<TipoPuntuacion> tiposPuntuacion) {
    return (root, query, cb) -> root.get(PuntuacionItemInvestigador_.tipoPuntuacion).in(tiposPuntuacion).not();
  }

}
