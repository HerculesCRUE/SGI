package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.FormularioReport;
import org.crue.hercules.sgi.eti.model.FormularioReportPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link FormularioReport}.
 */

@Repository
public interface FormularioReportRepository
    extends JpaRepository<FormularioReport, FormularioReportPK> {

}
