package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoLineaInvestigadorRepository
    extends JpaRepository<GrupoLineaInvestigador, Long>, JpaSpecificationExecutor<GrupoLineaInvestigador> {

  /**
   * Devuelve un listado de {@link GrupoLineaInvestigador} asociados a una persona
   * y un {@link Grupo}.
   * 
   * @param personaRef Identificador de la persona
   * @param grupoId    Identificador de {@link Grupo}.
   * @return listado de {@link GrupoLineaInvestigador}.
   */
  List<GrupoLineaInvestigador> findAllByPersonaRefAndGrupoLineaInvestigacionGrupoId(String personaRef, Long grupoId);

  /**
   * Devuelve un listado de {@link GrupoLineaInvestigador} asociados a un
   * {@link GrupoLineaInvestigacion}.
   * 
   * @param grupoLineaInvestigacionId Identificador de
   *                                  {@link GrupoLineaInvestigacion}.
   * @return listado de {@link GrupoLineaInvestigador}.
   */
  List<GrupoLineaInvestigador> findAllByGrupoLineaInvestigacionId(Long grupoLineaInvestigacionId);

}
