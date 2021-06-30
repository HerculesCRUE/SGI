package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TipoRegimenConcurrenciaRepository
    extends JpaRepository<TipoRegimenConcurrencia, Long>, JpaSpecificationExecutor<TipoRegimenConcurrencia> {
  /**
   * Obtiene la entidad {@link TipoRegimenConcurrencia} con el nombre indicado
   *
   * @param nombre el nombre de {@link TipoRegimenConcurrencia}.
   * @return el {@link TipoRegimenConcurrencia} con el nombre indicado
   */
  Optional<TipoRegimenConcurrencia> findByNombre(String nombre);
}
