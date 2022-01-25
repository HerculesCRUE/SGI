package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoPalabraClave;
import org.crue.hercules.sgi.csp.repository.custom.CustomProyectoPalabraClaveRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProyectoPalabraClaveRepository
    extends JpaRepository<ProyectoPalabraClave, Long>, JpaSpecificationExecutor<ProyectoPalabraClave>,
    CustomProyectoPalabraClaveRepository {

}
