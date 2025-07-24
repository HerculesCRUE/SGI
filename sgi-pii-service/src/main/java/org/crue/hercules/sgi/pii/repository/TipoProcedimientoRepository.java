package org.crue.hercules.sgi.pii.repository;

import java.util.Optional;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TipoProcedimientoRepository
    extends JpaRepository<TipoProcedimiento, Long>, JpaSpecificationExecutor<TipoProcedimiento> {

  /**
   * Obtiene la entidad {@link TipoProcedimiento} activa con el nombre e idioma
   * indicado
   *
   * @param lang   el idioma sobre el que buscar
   * @param nombre el nombre de {@link TipoProcedimiento}.
   * @return el {@link TipoProcedimiento} con el nombre e idioma indicado
   */
  Optional<TipoProcedimiento> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);
}
