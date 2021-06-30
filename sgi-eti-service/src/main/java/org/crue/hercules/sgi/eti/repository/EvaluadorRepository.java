package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.repository.custom.CustomEvaluadorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Evaluador}.
 */
@Repository
public interface EvaluadorRepository
    extends JpaRepository<Evaluador, Long>, JpaSpecificationExecutor<Evaluador>, CustomEvaluadorRepository {

}