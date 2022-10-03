package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.crue.hercules.sgi.csp.repository.custom.CustomGastoRequerimientoJustificacionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GastoRequerimientoJustificacionRepository
    extends JpaRepository<GastoRequerimientoJustificacion, Long>,
    JpaSpecificationExecutor<GastoRequerimientoJustificacion>,
    CustomGastoRequerimientoJustificacionRepository {

}
