package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.InformeDocumento;
import org.crue.hercules.sgi.eti.model.InformeDocumentoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link InformeDocumento}.
 */
@Repository
public interface InformeDocumentoRepository
    extends JpaRepository<InformeDocumento, InformeDocumentoId>, JpaSpecificationExecutor<InformeDocumento> {

}