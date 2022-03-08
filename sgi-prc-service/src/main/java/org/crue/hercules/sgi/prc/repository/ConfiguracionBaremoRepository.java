package org.crue.hercules.sgi.prc.repository;

import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConfiguracionBaremo}.
 */

@Repository
public interface ConfiguracionBaremoRepository
    extends JpaRepository<ConfiguracionBaremo, Long>, JpaSpecificationExecutor<ConfiguracionBaremo> {

}
