package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.InvencionPalabraClave;
import org.crue.hercules.sgi.pii.repository.custom.CustomInvencionPalabraClaveRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InvencionPalabraClaveRepository
    extends JpaRepository<InvencionPalabraClave, Long>, JpaSpecificationExecutor<InvencionPalabraClave>,
    CustomInvencionPalabraClaveRepository {

}
