package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.crue.hercules.sgi.eti.model.Formly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FormlyRepository extends JpaRepository<Formly, Long>, JpaSpecificationExecutor<Formly> {

  /**
   * Recupera el Formly con mayor versión cuyo nombre coincide con el
   * especificado.
   * 
   * @param nombre Nombre del Formly a buscar.
   * @return el Formly con mayor versión cuyo nombre coincide con el especificado.
   */
  public Optional<Formly> findFirstByNombreOrderByVersionDesc(String nombre);
}