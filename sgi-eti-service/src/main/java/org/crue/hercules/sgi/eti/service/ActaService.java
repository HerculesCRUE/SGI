package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.ActaWithNumEvaluaciones;
import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.dto.MemoriaEvaluada;
import org.crue.hercules.sgi.eti.exceptions.ActaNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Comentario.TipoEstadoComentario;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Acta}.
 */
public interface ActaService {

  /**
   * Guardar {@link Acta}.
   *
   * @param acta la entidad {@link Acta} a guardar.
   * @return la entidad {@link Acta} persistida.
   */
  Acta create(Acta acta);

  /**
   * Actualizar {@link Acta}.
   *
   * @param acta la entidad {@link Acta} a actualizar.
   * @return la entidad {@link Acta} persistida.
   */
  Acta update(Acta acta);

  /**
   * Devuelve una lista paginada y filtrada {@link ActaWithNumEvaluaciones}.
   *
   * @param pageable   la información de la paginación.
   * @param query      la información del filtro.
   * @param personaRef referencia de la persona.
   * @return la lista de {@link ActaWithNumEvaluaciones} paginadas y/o filtradas.
   */
  Page<ActaWithNumEvaluaciones> findAllActaWithNumEvaluaciones(String query, Pageable pageable, String personaRef);

  /**
   * Obtiene {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   * @return la entidad {@link Acta}.
   */
  Acta findById(Long id);

  /**
   * Comprueba la existencia del {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Elimina el {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   */
  void delete(Long id) throws ActaNotFoundException;

  /**
   * Finaliza el {@link Acta} con el id recibido como parámetro.
   * 
   * @param id identificador del {@link Acta} a finalizar.
   */
  void finishActa(Long id);

  /**
   * Devuelve el {@link Acta} asociada a una {@link ConvocatoriaReunion}
   *
   * @param convocatoriaReunionId Id de {@link ConvocatoriaReunion}.
   * @return si hay acta asociado a la convocatoria de reunión
   */
  Acta findByConvocatoriaReunionId(Long convocatoriaReunionId);

  /**
   * Devuelve el número de evaluaciones nuevas asociadas a un {@link Acta}
   *
   * @param idActa Id de {@link Acta}.
   * @return número de evaluaciones nuevas
   */
  Long countEvaluacionesNuevas(Long idActa);

  /**
   * Devuelve el número de evaluaciones de revisión sin las de revisión mínima
   *
   * @param idActa Id de {@link Acta}.
   * @return número de evaluaciones
   */
  Long countEvaluacionesRevisionSinMinima(Long idActa);

  /**
   * Devuelve una lista de {@link MemoriaEvaluada} sin las de revisión mínima para
   * una determinada {@link Acta}
   * 
   * @param idActa Id de {@link Acta}.
   * @return lista de memorias evaluadas
   */
  List<MemoriaEvaluada> findAllMemoriasEvaluadasSinRevMinimaByActaId(Long idActa);

  /**
   * Obtiene el informe de un {@link Acta}
   * 
   * @param idActa id {@link Acta}
   * @return El documento del informe del acta
   */
  DocumentoOutput generarDocumentoActa(Long idActa);

  /**
   * Devuelve si el usuario es miembro activo del comité del {@link Acta}
   * 
   * @param personaRef usuario
   * @param idActa     identificador del {@link Acta}
   * @return las entidades {@link Acta}
   */
  Boolean isMiembroComiteActa(String personaRef, Long idActa);

  /**
   * Identifica si el usuario es {@link Evaluador} en algun {@link Acta}
   * 
   * @param personaRef El usuario de la petición
   * @return true/false
   */
  Boolean hasAssignedActasByEvaluador(String personaRef);

  /**
   * Confirma si el hash del blockchain sellado es igual o ha sido alterado
   * 
   * @param id identificador del {@link Acta} a confirmar.
   * @return true/false
   */
  Boolean confirmarRegistroBlockchain(Long id);

  /**
   * Identifica si los {@link Comentario} en el {@link Acta} han sido
   * enviados
   * 
   * @param idActa     identificador de la {@link Acta}
   * @param personaRef El usuario de la petición
   * @return true/false
   */
  boolean isComentariosActaEnviados(Long idActa, String personaRef);

  /**
   * Obtiene el número total de {@link Comentario} de otros usuarios distintos
   * al que realiza la petición para un determinado {@link Acta} y un
   * {@link TipoEstadoComentario} Abierto
   * 
   * @param id         Id de {@link Acta}.
   * @param personaRef Persona creadora del comentario
   * @return true/false
   */
  boolean isPosibleEnviarComentariosActa(Long id, String personaRef);

  /**
   * Permite enviar los comentarios de las {@link Evaluacion} del {@link Acta} y
   * persona
   *
   * @param id         Id del {@link Acta}.
   * @param personaRef referencia de la persona de los {@link Comentario}
   * @return true si puede ser enviado / false si no puede ser enviado
   */
  boolean enviarComentariosEvaluacion(Long id, String personaRef);
}