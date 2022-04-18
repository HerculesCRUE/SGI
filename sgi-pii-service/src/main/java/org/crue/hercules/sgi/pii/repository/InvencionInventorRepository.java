package org.crue.hercules.sgi.pii.repository;

import java.util.List;

import org.crue.hercules.sgi.pii.model.InvencionInventor;
import org.crue.hercules.sgi.pii.repository.custom.CustomInvencionInventorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InvencionInventorRepository extends JpaRepository<InvencionInventor, Long>,
    JpaSpecificationExecutor<InvencionInventor>, CustomInvencionInventorRepository {

  List<InvencionInventor> findByInvencionId(Long invencionId);
}
