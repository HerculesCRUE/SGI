package org.crue.hercules.sgi.prc.repository;

import org.crue.hercules.sgi.prc.model.TablaIndice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TablaIndice}.
 */

@Repository
public interface TablaIndiceRepository
    extends JpaRepository<TablaIndice, Long>, JpaSpecificationExecutor<TablaIndice> {

}
