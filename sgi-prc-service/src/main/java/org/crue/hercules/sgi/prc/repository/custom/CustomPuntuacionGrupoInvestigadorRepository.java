package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomPuntuacionGrupoInvestigadorRepository {
  /**
   * Elimina todos los {@link PuntuacionGrupoInvestigador} cuyo
   * produccionCientificaId coincide con
   * el indicado.
   * 
   * @param puntuacionGrupoId el identificador del {@link PuntuacionGrupo}
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByPuntuacionGrupoId(Long puntuacionGrupoId);

  @Modifying
  int updatePuntuacionItemInvestigadorNull(Long produccionCientificaId);
}
