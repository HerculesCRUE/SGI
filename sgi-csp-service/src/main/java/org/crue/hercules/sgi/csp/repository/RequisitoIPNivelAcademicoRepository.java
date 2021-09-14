package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.repository.custom.CustomRequisitoIPNivelAcademicoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequisitoIPNivelAcademicoRepository extends JpaRepository<RequisitoIPNivelAcademico, Long>,
    JpaSpecificationExecutor<RequisitoIPNivelAcademico>, CustomRequisitoIPNivelAcademicoRepository {

  /**
   * Listado de {@link RequisitoIPNivelAcademico} asociadas a un
   * {@link RequisitoIP}
   * 
   * @param requisitoIPId id del {@link RequisitoIP}
   * @return lista de objetos de tipo {@link RequisitoIPNivelAcademico}
   */
  List<RequisitoIPNivelAcademico> findByRequisitoIPId(Long requisitoIPId);
}
