package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.PaisValidado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaisValidadoRepository
    extends JpaRepository<PaisValidado, Long>, JpaSpecificationExecutor<PaisValidado> {

  Page<PaisValidado> findBySolicitudProteccionId(Long solicitudProteccionId, Pageable paging);

  boolean existsBySolicitudProteccionId(Long solicitudProteccionId);

}
