package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoAgrupacionGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository para {@link ProyectoAgrupacionGastoRepository}.
 */
public interface ProyectoAgrupacionGastoRepository
    extends JpaRepository<ProyectoAgrupacionGasto, Long>, JpaSpecificationExecutor<ProyectoAgrupacionGasto> {

}
