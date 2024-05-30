package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria.Tipo;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
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
   * @throws MemoriaNotFoundException Si no existe ningún {@link Memoria} con ese
   *                                  id.
   */
  Memoria findById(Long id) throws MemoriaNotFoundException;

  /**
   * Devuelve las memorias de una petición evaluación con su fecha límite y de
   * evaluación.
   * 
   * @param idPeticionEvaluacion Identificador {@link PeticionEvaluacion}
   * @return lista de memorias de {@link PeticionEvaluacion}
   */
  List<MemoriaPeticionEvaluacion> findMemoriaByPeticionEvaluacionMaxVersion(Long idPeticionEvaluacion);

  /**
   * Se crea el nuevo estado para la memoria recibida y se actualiza el estado
   * actual de esta.
   * 
   * @param memoriaId           Identificador de la {@link Memoria}.
   * @param tipoEstadoMemoriaId Identificador del estado nuevo de la memoria.
   */
  void updateEstadoMemoria(Long memoriaId, Long tipoEstadoMemoriaId);

  /**
   * Actualiza el estado de la {@link Memoria}.
   * 
   * @param memoria             a actualizar.
   * @param tipoEstadoMemoriaId del estado de la memoria nuevo.
   */
  void updateEstadoMemoria(Memoria memoria, long tipoEstadoMemoriaId);

  /**
   * Se crea el nuevo estado para la memoria recibida y se actualiza el estado
   * actual de esta.
   * 
   * @param id                    Identificador de la {@link Memoria}
   * @param requiereRetrospectiva flag para identificar si la {@link Memoria}
   *                              requiere retrospectiva
   * @param retrospectiva         la {@link Retrospectiva}
   */
  void updateDatosRetrospectiva(Long id, boolean requiereRetrospectiva, Retrospectiva retrospectiva);

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
   * Actualiza el estado de la memoria a su estado anterior, baja la version de la
   * memoria y elimina la evaluacion si la memoria se encuentra en el estado
   * EN_EVALUACION
   * 
   * @param id identificador del objeto {@link Memoria}
   * @return la {@link Memoria} si se ha podido actualizar el estado
   */
  Memoria updateEstadoAnteriorMemoria(Long id);

  /**
   * Recupera la memoria con su estado anterior seteado ya sea memoria o
   * retrospectiva, devuelve la memoria a la version anterior y elimina el estado
   * actual de la memoria
   * 
   * @param memoria el objeto {@link Memoria}
   * @return la memoria o retrospectiva con su estado anterior
   */
  Memoria getMemoriaWithEstadoAnterior(Memoria memoria);

  /**
   * Actualiza el estado de la {@link Memoria} al estado en secretaria
   * correspondiente al {@link TipoEvaluacion} y {@link TipoEstadoMemoria}
   * actuales de la {@link Memoria}.
   * 
   * Se crea el informe asociado a la version actual de la memoria y si esta en un
   * estado de revision minima se crea tambien la evaluacion de revision minima.
   * 
   * @param id         identificador de la {@link Memoria}.
   * @param personaRef Identificador de la persona que realiza la accion
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
   * @param idComite             Identificador {@link Comite}.
   * @param idPeticionEvaluacion Identificador {@link PeticionEvaluacion}.
   * @param pageable             Datos de la paginación.
   * @return lista paginada de memorias
   */
  Page<Memoria> findAllMemoriasPeticionEvaluacionModificables(Long idComite, Long idPeticionEvaluacion,
      Pageable pageable);

  /**
   * 
   * Enviar comunicados de aviso para memorias con evaluación retrospectiva
   * pendiente
   * 
   */
  void sendComunicadoInformeRetrospectivaCeeaPendiente();

  /**
   * Crea una memoria del tipo modificada a partir de la recibida por parámetro.
   * 
   * @param nuevaMemoria Nueva {@link Memoria} a crear.
   * @param id           Identificador de {@link Memoria} de la que se parte.
   * @return {@link Memoria} creada.
   */
  Memoria createModificada(Memoria nuevaMemoria, Long id);

  /**
   * Comprobación de si están o no los documentos obligatorios aportados para
   * pasar la memoria al estado en secretaría
   * 
   * @param idMemoria Id de {@link Memoria}
   * @param paging    pageable
   * @return true si existen documentos adjuntos obligatorios / false Si no se
   *         existen documentos adjuntos obligatorios
   */
  Boolean checkDatosAdjuntosExists(Long idMemoria, Pageable paging);

  /**
   * Se actualiza el estado de la memoria a "Archivado" de {@link Memoria} que han
   * pasado "mesesArchivadaPendienteCorrecciones" días desde la fecha de estado de
   * una memoria cuyo estado es "Pendiente Correcciones"
   * 
   * @return Los ids de memorias que pasan al estado "Archivado"
   */
  List<Long> archivarNoPresentados();

  /**
   * Se actualiza el estado de la memoria a "Archivado" de las {@link Memoria}
   * para las que han pasado "diasArchivadaInactivo" dias desde la fecha desde el
   * ultimo cambio de estado si esta en alguno de los siguientes estados:
   * FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS, NO_PROCEDE_EVALUAR,
   * SOLICITUD_MODIFICACION, EN_ACLARACION_SEGUIMIENTO_FINAL, DESFAVORABLE y
   * PENDIENTE_CORRECCIONES
   * 
   * @return Los ids de las memorias que pasan al estado "Archivado"
   */
  List<Long> archivarInactivos();

  /**
   * Devuelve si la {@link Memoria} existe para la persona responsable de memorias
   * o creador de la petición de evaluación
   * 
   * @param personaRef usuario
   * @param idMemoria  identificador de la {@link Memoria}
   * @return la entidad {@link Memoria}
   */
  Boolean isMemoriaWithPersonaRefCreadorPeticionEvaluacionOrResponsableMemoria(String personaRef, Long idMemoria);

  /**
   * 
   * Envia comunicados de aviso para informes con seguimiento final
   * pendiente
   * 
   */
  void sendComunicadoInformeSeguimientoFinalPendiente();

  /**
   * Devuelve un listado de {@link Memoria} para una determinada petición de
   * evaluación en dos posibles estados
   * 
   * @param idPeticionEvaluacion Identificador {@link PeticionEvaluacion}.
   * @param tipoEstadoMemoria    identificador del {@link TipoEstadoMemoria}
   * @return listado de memorias
   */
  List<Memoria> findAllByPeticionEvaluacionIdAndEstadoActualId(
      Long idPeticionEvaluacion, Long tipoEstadoMemoria);

  /**
   * Desactiva la {@link Memoria}.
   *
   * @param id Id de la {@link Memoria}.
   * @return Entidad {@link Memoria} persistida desactivada.
   */
  Memoria desactivar(Long id);

  /**
   * Cambia el estado de la memoria a {@link Tipo#SUBSANACION} con el comentario
   * 
   * @param id         Id de la {@link Memoria}.
   * @param comentario comentario subsanacion
   */
  void indicarSubsanacion(Long id, String comentario);

  /**
   * Devuelve el estado actual de la memoria
   * 
   * @param id Id de la {@link Memoria}.
   * @return el estado de la memoria
   */
  EstadoMemoria getEstadoActualMemoria(Long id);

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
   * @param idPeticionEvaluacion Identificador del {@link PeticionEvaluacion}
   * @return lista de memorias asignables a la petición de evaluación.
   */
  List<Memoria> findAllMemoriasAsignablesPeticionEvaluacion(Long idPeticionEvaluacion);

  /**
   * Procesa la notificacion de revision minima y actualiza el estado de la
   * {@link Memoria} a EN_EVALUACION_REVISION_MINIMA, crea la evaluacion de
   * revision minima y envia un comunicado para notificar el cambio
   * 
   * @param memoriaId Identificador de la memoria
   */
  void notificarRevisionMinima(Long memoriaId);

}