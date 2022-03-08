package org.crue.hercules.sgi.prc.repository;

import org.crue.hercules.sgi.prc.model.TablaEditorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TablaEditorial}.
 */

@Repository
public interface TablaEditorialRepository
    extends JpaRepository<TablaEditorial, Long>, JpaSpecificationExecutor<TablaEditorial> {

}
