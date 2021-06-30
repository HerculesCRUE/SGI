package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.repository.custom.CustomProyectoSocioRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoSocioRepository
    extends JpaRepository<ProyectoSocio, Long>, JpaSpecificationExecutor<ProyectoSocio>, CustomProyectoSocioRepository {

}
