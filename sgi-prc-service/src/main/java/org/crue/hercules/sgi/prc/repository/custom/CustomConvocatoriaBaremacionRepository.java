package org.crue.hercules.sgi.prc.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;

public interface CustomConvocatoriaBaremacionRepository {
  /**
   * Obtiene la suma de puntos de cada tipo de una {@link ConvocatoriaBaremacion}
   * cuyo id
   * coincide con el indicado.
   * 
   * @param id el identificador de la {@link ConvocatoriaBaremacion}
   * @return suma de puntos de cada tipo
   */
  ConvocatoriaBaremacion findSumPuntosById(Long id);

  /**
   * Retorna el Id de {@link ConvocatoriaBaremacion} del último año
   * 
   * @return Id de {@link ConvocatoriaBaremacion}
   */
  Long findIdByMaxAnio();

  /**
   * Obtiene los años en los que hay alguna {@link ConvocatoriaBaremacion}
   * 
   * @return lista de años en los hay alguna {@link ConvocatoriaBaremacion}
   */
  List<Integer> findAniosWithConvocatoriasBaremacion();

}
