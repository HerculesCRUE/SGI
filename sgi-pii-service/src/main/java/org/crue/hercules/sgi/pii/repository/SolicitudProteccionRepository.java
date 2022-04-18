package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.repository.custom.CustomSolicitudProteccionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProteccionRepository
    extends JpaRepository<SolicitudProteccion, Long>, JpaSpecificationExecutor<SolicitudProteccion>,
    CustomSolicitudProteccionRepository {

  Page<SolicitudProteccion> findByInvencionId(Long invencionId, Pageable paging);

}
