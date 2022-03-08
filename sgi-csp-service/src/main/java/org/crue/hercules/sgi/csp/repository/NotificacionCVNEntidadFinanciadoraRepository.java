package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.NotificacionCVNEntidadFinanciadora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificacionCVNEntidadFinanciadoraRepository
    extends JpaRepository<NotificacionCVNEntidadFinanciadora, Long>,
    JpaSpecificationExecutor<NotificacionCVNEntidadFinanciadora> {

  Page<NotificacionCVNEntidadFinanciadora> findByNotificacionProyectoExternoCvnId(
      Long notificacionProyectoExternoCvnId, Pageable paging);
}