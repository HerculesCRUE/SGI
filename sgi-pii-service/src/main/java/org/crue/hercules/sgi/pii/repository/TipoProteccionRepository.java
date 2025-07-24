package org.crue.hercules.sgi.pii.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoProteccion}.
 */
@Repository
public interface TipoProteccionRepository
    extends JpaRepository<TipoProteccion, Long>, JpaSpecificationExecutor<TipoProteccion> {

  /**
   * Obtiene la entidad {@link TipoProteccion} activo con el nombre e idioma
   * indicado
   *
   * @param lang   el idioma sobre el que buscar
   * @param nombre el nombre de {@link TipoProteccion}.
   * @return el {@link TipoProteccion} con el nombre e idioma indicado
   */
  Optional<TipoProteccion> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);

  /**
   * Recupera los {@link TipoProteccion} activos que tienen como padre alguno de
   * los {@link TipoProteccion} de la lista de ids.
   * 
   * @param ids Ids {@link TipoProteccion}.
   * @return lista de {@link TipoProteccion} que tienen como padre alguno de los
   *         {@link TipoProteccion} de la lista de ids.
   */
  List<TipoProteccion> findByPadreIdInAndActivoIsTrue(List<Long> ids);
}
