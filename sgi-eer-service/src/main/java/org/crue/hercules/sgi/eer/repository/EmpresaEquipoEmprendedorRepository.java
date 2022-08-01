package org.crue.hercules.sgi.eer.repository;

import java.util.List;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmpresaEquipoEmprendedorRepository
    extends JpaRepository<EmpresaEquipoEmprendedor, Long>, JpaSpecificationExecutor<EmpresaEquipoEmprendedor> {

  /**
   * Devuelve un listado de {@link EmpresaEquipoEmprendedor} asociados a un
   * {@link Empresa}.
   * 
   * @param empresaId Identificador de {@link Empresa}.
   * @return listado de {@link EmpresaEquipoEmprendedor}.
   */
  List<EmpresaEquipoEmprendedor> findAllByEmpresaId(Long empresaId);

}
