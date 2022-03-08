package org.crue.hercules.sgi.prc.repository;

import org.crue.hercules.sgi.prc.model.Baremo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Baremo}.
 */

@Repository
public interface BaremoRepository
    extends JpaRepository<Baremo, Long>, JpaSpecificationExecutor<Baremo> {

}
