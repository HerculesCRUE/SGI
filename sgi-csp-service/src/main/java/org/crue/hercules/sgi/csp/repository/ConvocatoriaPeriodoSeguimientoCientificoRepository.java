package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaPeriodoSeguimientoCientificoRepository
    extends JpaRepository<ConvocatoriaPeriodoSeguimientoCientifico, Long>,
    JpaSpecificationExecutor<ConvocatoriaPeriodoSeguimientoCientifico> {

  /**
   * Obtiene las {@link ConvocatoriaPeriodoSeguimientoCientifico} para una
   * {@link Convocatoria} ordenadas por Mes Inicial
   * 
   * @param convocatoriaId Id de la {@link Convocatoria}
   * @return lista con las {@link ConvocatoriaEntidadGestora} ordenadas
   */
  List<ConvocatoriaPeriodoSeguimientoCientifico> findAllByConvocatoriaIdOrderByMesInicial(Long convocatoriaId);
}
