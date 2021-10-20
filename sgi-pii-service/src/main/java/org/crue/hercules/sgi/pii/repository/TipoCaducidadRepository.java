package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.TipoCaducidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TipoCaducidadRepository
    extends JpaRepository<TipoCaducidad, Long>, JpaSpecificationExecutor<TipoCaducidad> {

}
