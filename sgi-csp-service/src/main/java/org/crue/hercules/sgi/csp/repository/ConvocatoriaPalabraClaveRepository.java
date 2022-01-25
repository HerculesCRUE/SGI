package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ConvocatoriaPalabraClave;
import org.crue.hercules.sgi.csp.repository.custom.CustomConvocatoriaPalabraClaveRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ConvocatoriaPalabraClaveRepository
    extends JpaRepository<ConvocatoriaPalabraClave, Long>, JpaSpecificationExecutor<ConvocatoriaPalabraClave>,
    CustomConvocatoriaPalabraClaveRepository {

}
