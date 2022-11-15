package org.crue.hercules.sgi.csp.repository;

import java.util.UUID;

import org.crue.hercules.sgi.csp.model.SolicitudExterna;
import org.crue.hercules.sgi.csp.repository.custom.CustomSolicitudExternaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudExternaRepository
    extends JpaRepository<SolicitudExterna, UUID>, JpaSpecificationExecutor<SolicitudExterna>,
    CustomSolicitudExternaRepository {

}
