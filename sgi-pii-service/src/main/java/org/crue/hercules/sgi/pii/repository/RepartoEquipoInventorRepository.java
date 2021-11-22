package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.RepartoEquipoInventor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RepartoEquipoInventorRepository
    extends JpaRepository<RepartoEquipoInventor, Long>, JpaSpecificationExecutor<RepartoEquipoInventor> {
}
