package org.crue.hercules.sgi.prc.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador_;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador_;
import org.springframework.data.jpa.domain.Specification;

public class PuntuacionGrupoInvestigadorSpecifications {

  private PuntuacionGrupoInvestigadorSpecifications() {
  }

  /**
   * {@link PuntuacionGrupoInvestigador} con el id de {@link PuntuacionGrupo}
   * 
   * @param puntuacionGrupoId id de {@link PuntuacionGrupo}
   * @return specification para obtener los {@link PuntuacionGrupo} con
   *         el puntuacionGrupoId indicado.
   */
  public static Specification<PuntuacionGrupoInvestigador> byPuntuacionGrupoId(Long puntuacionGrupoId) {
    return (root, query, cb) -> cb.equal(root.get(PuntuacionGrupoInvestigador_.puntuacionGrupoId), puntuacionGrupoId);
  }

  /**
   * {@link PuntuacionGrupoInvestigador} con el {@link TipoPuntuacion} indicado.
   * 
   * @param tipoPuntuacion {@link TipoPuntuacion}.
   * @return specification para obtener los {@link PuntuacionGrupoInvestigador}
   *         con el {@link TipoPuntuacion} indicado.
   */
  public static Specification<PuntuacionGrupoInvestigador> byTipoPuntuacion(TipoPuntuacion tipoPuntuacion) {
    return (root, query, cb) -> cb.equal(root.get(PuntuacionGrupoInvestigador_.puntuacionItemInvestigador)
        .get(PuntuacionItemInvestigador_.tipoPuntuacion), tipoPuntuacion);
  }

  /**
   * Lista de {@link PuntuacionGrupoInvestigador} que no contenga los
   * {@link TipoPuntuacion} indicados.
   * 
   * @param tiposPuntuacion Lista de {@link TipoPuntuacion}.
   * @return specification para obtener los {@link PuntuacionGrupoInvestigador}
   *         que no contenga los {@link TipoPuntuacion} indicados.
   */
  public static Specification<PuntuacionGrupoInvestigador> byTipoPuntuacionNotIn(List<TipoPuntuacion> tiposPuntuacion) {
    return (root, query, cb) -> root.get(PuntuacionGrupoInvestigador_.puntuacionItemInvestigador)
        .get(PuntuacionItemInvestigador_.tipoPuntuacion).in(tiposPuntuacion).not();
  }

}
