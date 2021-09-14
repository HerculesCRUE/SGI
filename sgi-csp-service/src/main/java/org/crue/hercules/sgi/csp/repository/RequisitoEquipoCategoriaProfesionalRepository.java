package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoCategoriaProfesional;
import org.crue.hercules.sgi.csp.repository.custom.CustomRequisitoEquipoCategoriaProfesionalRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequisitoEquipoCategoriaProfesionalRepository
    extends JpaRepository<RequisitoEquipoCategoriaProfesional, Long>,
    JpaSpecificationExecutor<RequisitoEquipoCategoriaProfesional>, CustomRequisitoEquipoCategoriaProfesionalRepository {

  /**
   * Devuelve una lista de objetos de tipo
   * {@link RequisitoEquipoCategoriaProfesional} asociados al objeto de tipo
   * {@link RequisitoEquipo} cuyo id se pasa por parámetro
   * 
   * @param requisitoEquipoId id del {@link RequisitoEquipo}
   * @return lista de {@link RequisitoEquipoCategoriaProfesional}
   */
  List<RequisitoEquipoCategoriaProfesional> findByRequisitoEquipoId(Long requisitoEquipoId);
}
