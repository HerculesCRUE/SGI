package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.repository.custom.CustomRequisitoIPCategoriaProfesionalRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequisitoIPCategoriaProfesionalRepository extends JpaRepository<RequisitoIPCategoriaProfesional, Long>,
    JpaSpecificationExecutor<RequisitoIPCategoriaProfesional>, CustomRequisitoIPCategoriaProfesionalRepository {

  /**
   * Listado de {@link RequisitoIPCategoriaProfesional} asociadas a un
   * {@link RequisitoIP}
   * 
   * @param requisitoIPId id del {@link RequisitoIP}
   * @return lista de objetos de tipo {@link RequisitoIPCategoriaProfesional}
   */
  List<RequisitoIPCategoriaProfesional> findByRequisitoIPId(Long requisitoIPId);
}
