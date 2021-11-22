package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.SectorLicenciado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SectorLicenciadoRepository
    extends JpaRepository<SectorLicenciado, Long>, JpaSpecificationExecutor<SectorLicenciado> {
}
