package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoHitoAviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoHitoAvisoRepository
    extends JpaRepository<ProyectoHitoAviso, Long>, JpaSpecificationExecutor<ProyectoHitoAviso> {

}
