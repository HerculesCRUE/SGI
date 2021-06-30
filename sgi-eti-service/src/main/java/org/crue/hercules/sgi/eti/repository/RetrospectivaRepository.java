package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Retrospectiva}.
 */
@Repository
public interface RetrospectivaRepository
    extends JpaRepository<Retrospectiva, Long>, JpaSpecificationExecutor<Retrospectiva> {

}