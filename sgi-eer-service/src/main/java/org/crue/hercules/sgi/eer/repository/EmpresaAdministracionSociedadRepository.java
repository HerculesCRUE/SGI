package org.crue.hercules.sgi.eer.repository;

import java.util.List;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmpresaAdministracionSociedadRepository
    extends JpaRepository<EmpresaAdministracionSociedad, Long>,
    JpaSpecificationExecutor<EmpresaAdministracionSociedad> {

  /**
   * Devuelve un listado de {@link EmpresaAdministracionSociedad} asociados a un
   * {@link Empresa}.
   * 
   * @param empresaId Identificador de {@link Empresa}.
   * @return listado de {@link EmpresaAdministracionSociedad}.
   */
  List<EmpresaAdministracionSociedad> findAllByEmpresaId(Long empresaId);

}
