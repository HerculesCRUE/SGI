package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InvencionRepository extends JpaRepository<Invencion, Long>, JpaSpecificationExecutor<Invencion> {
}
