package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.SolicitudHitoAviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudHitoAvisoRepository
    extends JpaRepository<SolicitudHitoAviso, Long>, JpaSpecificationExecutor<SolicitudHitoAviso> {

  /**
   * Obtiene un {@link SolicitudHitoAviso} a partir del identificador de
   * {@link SolicitudHito}
   * 
   * @param id Identificador de {@link SolicitudHito}
   * @return SolicitudHitoAviso
   */
  Optional<SolicitudHitoAviso> findBySolicitudHitoId(Long id);
}
