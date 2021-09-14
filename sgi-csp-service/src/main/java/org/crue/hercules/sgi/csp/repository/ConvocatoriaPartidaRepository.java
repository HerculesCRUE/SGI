package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.repository.custom.CustomConvocatoriaPartidaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConvocatoriaPartida}.
 */
@Repository
public interface ConvocatoriaPartidaRepository extends JpaRepository<ConvocatoriaPartida, Long>,
    JpaSpecificationExecutor<ConvocatoriaPartida>, CustomConvocatoriaPartidaRepository {

  /**
   * Comprueba si existe algún {@link ConvocatoriaPartida} relacionado con el Id
   * de Convocatoria recibido
   * 
   * @param convocatoriaId Id de Convocatoria
   * @return <code>true</code> Si existe algúna relación, <code>false</code> en
   *         cualquier otro caso.
   */
  boolean existsByConvocatoriaId(Long convocatoriaId);

  /**
   * Busca todos los objetos de tipo {@link ConvocatoriaPartida} que tengan
   * asociada la {@link Convocatoria} cuyo id es pasado por parámetro
   *
   * @param convocatoriaId id de la {@link Convocatoria}
   * @return lista de objetos de tipo {@link ConvocatoriaPartida}
   */
  List<ConvocatoriaPartida> findByConvocatoriaId(Long convocatoriaId);
}