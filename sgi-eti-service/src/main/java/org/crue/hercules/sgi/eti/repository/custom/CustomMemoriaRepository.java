package org.crue.hercules.sgi.eti.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Memoria}.
 */
@Component
public interface CustomMemoriaRepository {

  /**
   * 
   * Devuelve una lista paginada de {@link Memoria} asignables para una
   * convocatoria determinada
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param idConvocatoriaReunion Identificador del {@link ConvocatoriaReunion}
   * @return lista de memorias asignables a la convocatoria.
   */
  List<Memoria> findAllMemoriasAsignablesConvocatoria(Long idConvocatoriaReunion);

  /**
   * Devuelve las memorias de una petición evaluación con su fecha límite y de
   * evaluación.
   * 
   * @param idPeticionEvaluacion Identificador {@link PeticionEvaluacion}
   * @param personaRefConsulta   Referencia persona consulta
   * @return lista de memorias de {@link MemoriaPeticionEvaluacion}
   */
  List<MemoriaPeticionEvaluacion> findMemoriasEvaluacion(Long idPeticionEvaluacion, String personaRefConsulta);

  /**
   * Recupera todas las memorias de una evaluación.
   * 
   * @param specs              condiciones de búsqueda
   * @param pageable           datos paginación
   * @param personaRefConsulta Referencia persona consulta
   * @return Lista paginada de {@link MemoriaPeticionEvaluacion}
   */
  Page<MemoriaPeticionEvaluacion> findAllMemoriasEvaluaciones(Specification<Memoria> specs, Pageable pageable,
      String personaRefConsulta);

}
