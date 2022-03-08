package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.GrupoPalabraClave;
import org.crue.hercules.sgi.csp.repository.custom.CustomGrupoPalabraClaveRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoPalabraClaveRepository extends JpaRepository<GrupoPalabraClave, Long>,
    JpaSpecificationExecutor<GrupoPalabraClave>, CustomGrupoPalabraClaveRepository {

}
