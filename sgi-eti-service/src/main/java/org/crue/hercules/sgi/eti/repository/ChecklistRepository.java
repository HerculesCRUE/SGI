package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Checklist;
import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Persistencia de la entidad Checklist
 */
@Repository
public interface ChecklistRepository extends SgiRepository<Checklist, Long>, JpaSpecificationExecutor<Checklist> {

}