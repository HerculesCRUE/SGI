package org.crue.hercules.sgi.pii.repository;

import java.util.Optional;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.pii.model.ViaProteccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ViaProteccionRepository
    extends JpaRepository<ViaProteccion, Long>, JpaSpecificationExecutor<ViaProteccion> {

  /**
   * Obtiene la entidad {@link ViaProteccion} activa con el nombre e idioma
   * indicado
   *
   * @param lang   el idioma sobre el que buscar
   * @param nombre el nombre de {@link ViaProteccion}.
   * @return el {@link ViaProteccion} con el nombre e idioma indicado
   */
  Optional<ViaProteccion> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);

}
