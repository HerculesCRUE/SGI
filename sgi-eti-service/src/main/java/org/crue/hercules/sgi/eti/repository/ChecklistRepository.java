package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.crue.hercules.sgi.eti.model.Checklist;
import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Persistencia de la entidad Checklist
 */
@Repository
public interface ChecklistRepository extends SgiRepository<Checklist, Long>, JpaSpecificationExecutor<Checklist> {
  /**
   * Recupera el Checklist de la personaRef
   * 
   * @param personaRef referencia de la persona
   * @return el Checklist de la personaRef
   */
  public Optional<Checklist> findByPersonaRef(String personaRef);
}