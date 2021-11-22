package org.crue.hercules.sgi.eti.repository.custom;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.eti.dto.ConvocatoriaReunionDatosGenerales;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.springframework.stereotype.Component;

/**
 * CustomConvocatoriaReunionRepository
 */
@Component
public interface CustomConvocatoriaReunionRepository {

  /**
   * Obteniene la entidad {@link ConvocatoriaReunionDatosGenerales} que contiene
   * la convocatoria con el identificador proporcionado y un campo que nos indica
   * el número de evaluaciones activas que no son revisión mínima.
   *
   * @param idConvocatoria id de la convocatoria.
   * 
   * @return la {@link ConvocatoriaReunionDatosGenerales}
   */
  Optional<ConvocatoriaReunionDatosGenerales> findByIdWithDatosGenerales(Long idConvocatoria);

  /**
   * Devuelve una lista de convocatorias de reunión que no tengan acta**
   * 
   * @return lista paginada de convocatoria reunión
   */
  List<ConvocatoriaReunion> findConvocatoriasReunionSinActa();

  /**
   * Devuelve una lista de convocatorias de reunión que no tengan acta en estado
   * finalizada
   *
   * @param idComite identificador del {@link Comite}
   * @return la lista de convocatorias de reunión
   */
  Optional<ConvocatoriaReunion> findFirstConvocatoriaReunionSinActaFinalizadaByComiteOrderByFechaEvaluacionAsc(
      Long idComite);

  /**
   * Retorna la fecha convocatoria y acta (codigo convocatoria) de la última
   * evaluación de tipo memoria de la memoria original (y que no sea revisión
   * mínima)
   * 
   * @param idEvaluacion Id de la {@link Evaluacion}
   * @param idDictamen   Id del {@link Dictamen}
   * @return ConvocatoriaReunion
   */
  ConvocatoriaReunion findConvocatoriaUltimaEvaluacionTipoMemoria(Long idEvaluacion, Long idDictamen);

}
