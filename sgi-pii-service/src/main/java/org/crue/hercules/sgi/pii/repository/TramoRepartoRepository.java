package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.TramoReparto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TramoRepartoRepository
    extends ActivableRepository, JpaRepository<TramoReparto, Long>, JpaSpecificationExecutor<TramoReparto> {

}
