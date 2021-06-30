package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Memoria}.
 */
public interface MemoriaService {
  /**
   * Guardar {@link Memoria}.
   *
   * @param memoria la entidad {@link Memoria} a guardar.
   * @return la entidad {@link Memoria} persistida.
   */
  Memoria create(Memoria memoria);

  /**
   * Actualizar {@link Memoria}.
   *
   * @param memoria la entidad {@link Memoria} a actualizar.
   * @return la entidad {@link Memoria} persistida.
   */
  Memoria update(Memoria memoria);

  /**
   * Obtener todas las entidades {@link Memoria} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Memoria} paginadas y/o filtradas.
   */
  Page<MemoriaPeticionEvaluacion> findAll(String query, Pageable pageable);

  /**
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
   * Devuelve una lista paginada y filtrada con las entidades {@link Memoria}
   * asignables a una Convocatoria de tipo "Ordinaria" o "Extraordinaria".
   * 
   * Para determinar si es asignable es necesario especificar en el filtro el
   * Comité Fecha Límite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param query    filtro de búsqueda.
   * @param pageable pageable
   * @return lista de memorias asignables
   */
  Page<Memoria> findAllAsignablesTipoConvocatoriaOrdExt(String query, Pageable pageable);

  /**
   * Devuelve una lista paginada y filtrada con las entidades {@link Memoria}
   * asignables a una Convocatoria de tipo "Seguimiento".
   * 
   * Para determinar si es asignable es necesario especificar en el filtro el
   * Comité y Fecha Límite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * @param query    filtro de búsqueda.
   * @param pageable pageable
   * @return lista de memorias asignables
   */
  Page<Memoria> findAllAsignablesTipoConvocatoriaSeguimiento(String query, Pageable pageable);

  /**
   * Obtiene {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   * @return la entidad {@link Memoria}.
   */
  Memoria findById(Long id);

  /**
   * Elimina el {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   */
  void delete(Long id) throws MemoriaNotFoundException;

  /**
   * Devuelve las memorias de una petición evaluación con su fecha límite y de
   * evaluación.
   * 
   * @param idPeticionEvaluacion Identificador {@link PeticionEvaluacion}
   * @return lista de memorias de {@link PeticionEvaluacion}
   */
  List<MemoriaPeticionEvaluacion> findMemoriaByPeticionEvaluacionMaxVersion(Long idPeticionEvaluacion);

  /**
   * 
   * Actualiza el estado de la {@link Memoria}.**
   * 
   * @param memoria a actualizar.*
   * @param id      del estado de la memoria nuevo.
   */

  void updateEstadoMemoria(Memoria memoria, long id);

  /**
   * Obtener todas las entidades {@link MemoriaPeticionEvaluacion} paginadas y/o
   * filtradas por referencia de la persona
   *
   * @param pageable   la información de la paginación.
   * @param query      la información del filtro.
   * @param personaRef Referencia de la persona
   * @return la lista de entidades {@link Memoria} paginadas y/o filtradas.
   */
  Page<MemoriaPeticionEvaluacion> findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
      String query, Pageable pageable, String personaRef);

  /**
   * Actualiza la memoria a su estado anterior
   * 
   * @param id identificador del objeto {@link Memoria}
   * @return la {@link Memoria} si se ha podido actualizar el estado
   */
  Memoria updateEstadoAnteriorMemoria(Long id);

  /**
   * Recupera la memoria con su estado anterior seteado ya sea memoria o
   * retrospectiva
   * 
   * @param memoria el objeto {@link Memoria}
   * @return la memoria o retrospectiva con su estado anterior
   */
  Memoria getEstadoAnteriorMemoria(Memoria memoria);

  /**
   * 
   * Actualiza el estado de la {@link Memoria} a 'En Secretaria' o 'En Secretaría
   * Revisión Mínima'
   * 
   * @param id         del estado de la memoria nuevo.
   * @param personaRef Usuario logueado.
   */
  void enviarSecretaria(Long id, String personaRef);

  /**
   * 
   * Actualiza el estado de la Retrospectiva de {@link Memoria} a 'En Secretaria'
   * 
   * @param id         Identificador de la {@link Memoria}.
   * @param personaRef Referencia de la persona logueada.
   */
  void enviarSecretariaRetrospectiva(Long id, String personaRef);

  /**
   * Recupera una lista paginada de memorias asociadas al comité recibido.
   * 
   * @param idComite Identificador {@link Comite}.
   * @param pageable Datos de la paginación.
   * @return lista paginada de memorias
   */
  Page<Memoria> findByComite(Long idComite, Pageable pageable);

  /**
   * Crea una memoria del tipo modificada a partir de la recibida por parámetro.
   * 
   * @param nuevaMemoria Nueva {@link Memoria} a crear.
   * @param id           Identificador de {@link Memoria} de la que se parte.
   * @return {@link Memoria} creada.
   */
  Memoria createModificada(Memoria nuevaMemoria, Long id);
}