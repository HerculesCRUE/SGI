package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TipoOrigenFuenteFinanciacionRepository
    extends JpaRepository<TipoOrigenFuenteFinanciacion, Long>, JpaSpecificationExecutor<TipoOrigenFuenteFinanciacion> {

}
