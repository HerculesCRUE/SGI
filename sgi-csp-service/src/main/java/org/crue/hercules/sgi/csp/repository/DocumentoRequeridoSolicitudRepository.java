package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentoRequeridoSolicitudRepository
    extends JpaRepository<DocumentoRequeridoSolicitud, Long>, JpaSpecificationExecutor<DocumentoRequeridoSolicitud> {

}
