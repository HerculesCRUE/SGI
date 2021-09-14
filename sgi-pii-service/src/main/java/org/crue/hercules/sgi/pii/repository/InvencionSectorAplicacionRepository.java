package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.InvencionSectorAplicacion;
import org.crue.hercules.sgi.pii.repository.custom.CustomInvencionSectorAplicacionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvencionSectorAplicacionRepository extends JpaRepository<InvencionSectorAplicacion, Long>,
    JpaSpecificationExecutor<InvencionSectorAplicacion>, CustomInvencionSectorAplicacionRepository {
}
