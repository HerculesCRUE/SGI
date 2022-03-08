package org.crue.hercules.sgi.prc.repository;

import org.crue.hercules.sgi.prc.model.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Config}.
 */
@Repository
public interface ConfigRepository extends JpaRepository<Config, String>, JpaSpecificationExecutor<Config> {
}