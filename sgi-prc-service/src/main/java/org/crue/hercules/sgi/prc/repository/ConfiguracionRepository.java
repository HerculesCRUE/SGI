package org.crue.hercules.sgi.prc.repository;

import org.crue.hercules.sgi.prc.model.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Configuracion}.
 */

@Repository
public interface ConfiguracionRepository
    extends JpaRepository<Configuracion, Long>, JpaSpecificationExecutor<Configuracion> {

}
