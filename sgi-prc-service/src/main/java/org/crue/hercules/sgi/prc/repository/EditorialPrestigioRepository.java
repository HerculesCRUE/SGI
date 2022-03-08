package org.crue.hercules.sgi.prc.repository;

import org.crue.hercules.sgi.prc.model.EditorialPrestigio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link EditorialPrestigio}.
 */

@Repository
public interface EditorialPrestigioRepository
    extends JpaRepository<EditorialPrestigio, Long>, JpaSpecificationExecutor<EditorialPrestigio> {

}
