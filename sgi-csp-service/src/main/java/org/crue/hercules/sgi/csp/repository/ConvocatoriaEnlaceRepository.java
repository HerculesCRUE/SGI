package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaEnlaceRepository
    extends JpaRepository<ConvocatoriaEnlace, Long>, JpaSpecificationExecutor<ConvocatoriaEnlace> {

  /**
   * Busca un {@link ConvocatoriaEnlace} por su {@link Convocatoria} y url.
   * 
   * @param convocatoriaId Id de la {@link Convocatoria}
   * @param url            url
   * @return una {@link ConvocatoriaEnlace}
   */
  Optional<ConvocatoriaEnlace> findByConvocatoriaIdAndUrl(Long convocatoriaId, String url);

  /**
   * Comprueba si existe algún {@link ConvocatoriaEnlace} relacionado con el Id de
   * Convocatoria recibido
   * 
   * @param convocatoriaId Id de Convocatoria
   * @return <code>true</code> Si existe algúna relación, <code>false</code> en
   *         cualquier otro caso.
   */
  boolean existsByConvocatoriaId(Long convocatoriaId);
}
