package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoTarea}.
 */
@Repository
public interface TipoTareaRepository extends JpaRepository<TipoTarea, Long>, JpaSpecificationExecutor<TipoTarea> {

}