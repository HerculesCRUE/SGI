package org.crue.hercules.sgi.prc.repository;

import java.util.List;

import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.repository.custom.CustomAutorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Autor}.
 */

@Repository
public interface AutorRepository
    extends JpaRepository<Autor, Long>, JpaSpecificationExecutor<Autor>, CustomAutorRepository {

  List<Autor> findAllByProduccionCientificaId(Long produccionCientificaId);

  List<Autor> findAllByProduccionCientificaIdAndPersonaRefIsNotNull(Long produccionCientificaId);

}
