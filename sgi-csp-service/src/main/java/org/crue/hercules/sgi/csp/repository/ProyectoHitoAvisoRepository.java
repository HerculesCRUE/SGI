package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoHitoAviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoHitoAvisoRepository
    extends JpaRepository<ProyectoHitoAviso, Long>, JpaSpecificationExecutor<ProyectoHitoAviso> {

  /**
   * Obtiene un {@link ProyectoHitoAviso} a partir del identificador de
   * {@link ProyectoHito}
   * 
   * @param id Identificador de {@link ProyectoHito}
   * @return ProyectoHitoAviso
   */
  Optional<ProyectoHitoAviso> findByProyectoHitoId(Long id);

}
