package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AreaTematicaRepository
    extends JpaRepository<AreaTematica, Long>, JpaSpecificationExecutor<AreaTematica> {

  /**
   * Recupera los {@link AreaTematica} activos que tienen como padre alguno de los
   * {@link AreaTematica} de la lista de ids.
   * 
   * @param ids Ids {@link AreaTematica}.
   * @return lista de {@link AreaTematica} que tienen como padre alguno de los
   *         {@link AreaTematica} de la lista de ids.
   */
  List<AreaTematica> findByPadreIdInAndActivoIsTrue(List<Long> ids);
}
