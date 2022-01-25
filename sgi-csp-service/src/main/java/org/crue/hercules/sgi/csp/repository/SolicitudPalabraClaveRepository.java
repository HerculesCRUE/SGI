package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudPalabraClave;
import org.crue.hercules.sgi.csp.repository.custom.CustomSolicitudPalabraClaveRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudPalabraClaveRepository
    extends JpaRepository<SolicitudPalabraClave, Long>, JpaSpecificationExecutor<SolicitudPalabraClave>,
    CustomSolicitudPalabraClaveRepository {

}
