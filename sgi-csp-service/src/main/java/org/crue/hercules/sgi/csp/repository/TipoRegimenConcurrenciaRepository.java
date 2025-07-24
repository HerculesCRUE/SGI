package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.framework.i18n.Language;
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
  // Optional<TipoRegimenConcurrencia> findByNombreValue(String nombre);

  /**
   * Obtiene la entidad {@link TipoRegimenConcurrencia} activo con el nombre
   * indicado
   *
   * @param nombre el nombre de {@link TipoRegimenConcurrencia}.
   * @param lang   el lang de {@link TipoRegimenConcurrencia}.
   * @return el {@link TipoRegimenConcurrencia} con el nombre indicado
   */
  Optional<TipoRegimenConcurrencia> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);

}
