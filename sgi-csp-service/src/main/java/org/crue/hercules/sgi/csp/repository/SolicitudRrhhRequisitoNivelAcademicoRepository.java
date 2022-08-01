package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoNivelAcademico;
import org.crue.hercules.sgi.csp.repository.custom.CustomGrupoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudRrhhRequisitoNivelAcademicoRepository
    extends JpaRepository<SolicitudRrhhRequisitoNivelAcademico, Long>,
    JpaSpecificationExecutor<SolicitudRrhhRequisitoNivelAcademico>, CustomGrupoRepository {

}
