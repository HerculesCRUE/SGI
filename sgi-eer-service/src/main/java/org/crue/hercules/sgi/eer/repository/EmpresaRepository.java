package org.crue.hercules.sgi.eer.repository;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.repository.custom.CustomEmpresaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmpresaRepository
    extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa>, CustomEmpresaRepository {

}
