package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoNivelAcademico;
import org.crue.hercules.sgi.csp.repository.custom.CustomRequisitoEquipoNivelAcademicoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequisitoEquipoNivelAcademicoRepository extends JpaRepository<RequisitoEquipoNivelAcademico, Long>,
    JpaSpecificationExecutor<RequisitoEquipoNivelAcademico>, CustomRequisitoEquipoNivelAcademicoRepository {

  /**
   * Obtiene todos los objetos de tipo {@link RequisitoEquipoNivelAcademico} que
   * estén asociados al objeto {@link RequisitoEquipo} cuyo id se pasa por
   * parámetro
   *
   * @param requisitoEquipoId id del {@link RequisitoEquipo}
   * @return lista de {@link RequisitoEquipoNivelAcademico}
   */
  List<RequisitoEquipoNivelAcademico> findByRequisitoEquipoId(Long requisitoEquipoId);
}
