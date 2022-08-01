package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoCategoria;
import org.crue.hercules.sgi.csp.repository.custom.CustomGrupoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudRrhhRequisitoCategoriaRepository
    extends JpaRepository<SolicitudRrhhRequisitoCategoria, Long>,
    JpaSpecificationExecutor<SolicitudRrhhRequisitoCategoria>, CustomGrupoRepository {

}
