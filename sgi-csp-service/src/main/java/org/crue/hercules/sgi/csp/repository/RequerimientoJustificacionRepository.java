package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequerimientoJustificacionRepository
    extends JpaRepository<RequerimientoJustificacion, Long>, JpaSpecificationExecutor<RequerimientoJustificacion> {
}
