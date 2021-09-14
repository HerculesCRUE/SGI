package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.ViaProteccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ViaProteccionRepository
    extends ActivableRepository, JpaRepository<ViaProteccion, Long>, JpaSpecificationExecutor<ViaProteccion> {

}
