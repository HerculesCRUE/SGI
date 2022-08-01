package org.crue.hercules.sgi.eer.repository;

import java.util.List;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmpresaComposicionSociedadRepository
    extends JpaRepository<EmpresaComposicionSociedad, Long>, JpaSpecificationExecutor<EmpresaComposicionSociedad> {

  /**
   * Devuelve un listado de {@link EmpresaComposicionSociedad} asociados a un
   * {@link Empresa}.
   * 
   * @param empresaId Identificador de {@link Empresa}.
   * @return listado de {@link EmpresaComposicionSociedad}.
   */
  List<EmpresaComposicionSociedad> findAllByEmpresaId(Long empresaId);

}
