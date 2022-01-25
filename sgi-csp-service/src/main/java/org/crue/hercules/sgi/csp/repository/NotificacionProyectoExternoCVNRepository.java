package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificacionProyectoExternoCVNRepository extends JpaRepository<NotificacionProyectoExternoCVN, Long>,
    JpaSpecificationExecutor<NotificacionProyectoExternoCVN> {

  /**
   * Comprueba si existe algun {@link NotificacionProyectoExternoCVN} asociado a
   * algún {@link Autorizacion}.
   * 
   * @param autorizacionId id del {@link Autorizacion}.
   * @return true si existe, false si no existe.
   */
  boolean existsByAutorizacionId(Long autorizacionId);
}
