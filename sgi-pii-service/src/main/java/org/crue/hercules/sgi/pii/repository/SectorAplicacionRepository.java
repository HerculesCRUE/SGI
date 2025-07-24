package org.crue.hercules.sgi.pii.repository;

import java.util.Optional;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.pii.model.SectorAplicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorAplicacionRepository
    extends JpaRepository<SectorAplicacion, Long>, JpaSpecificationExecutor<SectorAplicacion> {

  /**
   * Obtiene la entidad {@link SectorAplicacion} activa con el nombre e idioma
   * indicado
   *
   * @param lang   el idioma sobre el que buscar
   * @param nombre el nombre de {@link SectorAplicacion}.
   * @return el {@link SectorAplicacion} con el nombre e idioma indicado
   */
  Optional<SectorAplicacion> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);
}
