package org.crue.hercules.sgi.prc.repository;

import java.util.List;

import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.repository.custom.CustomAutorGrupoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link AutorGrupo}.
 */

@Repository
public interface AutorGrupoRepository
    extends JpaRepository<AutorGrupo, Long>, JpaSpecificationExecutor<AutorGrupo>, CustomAutorGrupoRepository {

  List<AutorGrupo> findAllByAutorId(Long autorId);

}
