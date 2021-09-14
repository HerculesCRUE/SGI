package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProteccionRepository extends JpaRepository<SolicitudProteccion, Long>,
    JpaSpecificationExecutor<SolicitudProteccion>, ActivableRepository {

  Page<SolicitudProteccion> findByInvencionId(Long invencionId, Pageable paging);

}
