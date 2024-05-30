package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.InvencionGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InvencionGastoRepository
    extends JpaRepository<InvencionGasto, Long>, JpaSpecificationExecutor<InvencionGasto> {

  boolean existsBySolicitudProteccionId(Long solicitudProteccionId);

}
