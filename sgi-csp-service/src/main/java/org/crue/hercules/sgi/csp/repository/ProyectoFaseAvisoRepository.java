package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoFaseAviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoFaseAvisoRepository
    extends JpaRepository<ProyectoFaseAviso, Long>, JpaSpecificationExecutor<ProyectoFaseAviso> {

  /**
   * Obtiene un {@link ProyectoFaseAviso} a partir del identificador de
   * {@link ProyectoFase}
   * 
   * @param id Identificador de {@link ProyectoFase}
   * @return ProyectoFaseAviso
   */
  Optional<ProyectoFaseAviso> findByProyectoFaseId(Long id);
}
