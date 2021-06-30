package org.crue.hercules.sgi.eti.converter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.dto.EvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.springframework.stereotype.Service;

@Service
public class EvaluacionConverter {

  private final ComentarioRepository comentarioRepository;

  public EvaluacionConverter(ComentarioRepository comentarioRepository) {
    this.comentarioRepository = comentarioRepository;
  }

  /**
   * Transforma el objeto evaluación al dto EvaluacionWithIsEliminable
   * 
   * @param evaluacion el objeto Evaluacion
   * @return el dto EvaluacionWithIsEliminable
   */
  public EvaluacionWithIsEliminable evaluacionToEvaluacionWithIsEliminable(Evaluacion evaluacion) {
    if (evaluacion == null) {
      return null;
    } else {
      EvaluacionWithIsEliminable evaluacionWithIsEliminable = new EvaluacionWithIsEliminable();
      evaluacionWithIsEliminable.setActivo(evaluacion.getActivo());
      evaluacionWithIsEliminable.setConvocatoriaReunion(evaluacion.getConvocatoriaReunion());
      evaluacionWithIsEliminable.setDictamen(evaluacion.getDictamen());
      evaluacionWithIsEliminable.setEsRevMinima(evaluacion.getEsRevMinima());
      evaluacionWithIsEliminable.setEvaluador1(evaluacion.getEvaluador1());
      evaluacionWithIsEliminable.setEvaluador2(evaluacion.getEvaluador2());
      evaluacionWithIsEliminable.setFechaDictamen(evaluacion.getFechaDictamen());
      evaluacionWithIsEliminable.setId(evaluacion.getId());
      evaluacionWithIsEliminable.setMemoria(evaluacion.getMemoria());
      evaluacionWithIsEliminable.setTipoEvaluacion(evaluacion.getTipoEvaluacion());
      evaluacionWithIsEliminable.setVersion(evaluacion.getVersion());
      evaluacionWithIsEliminable.setEliminable(isEliminable(evaluacion));
      return evaluacionWithIsEliminable;
    }
  }

  /**
   * Transforma el listado de objetos evaluación al listado de dtos
   * EvaluacionWithIsEliminable
   * 
   * @param evaluaciones el listado de objetos Evaluacion
   * @return el listado de dtos EvaluacionWithIsEliminable
   */
  public List<EvaluacionWithIsEliminable> evaluacionesToEvaluacionesWithIsEliminable(List<Evaluacion> evaluaciones) {
    if (evaluaciones == null) {
      return new ArrayList<>();
    } else {
      return evaluaciones.stream().filter(Objects::nonNull).map(this::evaluacionToEvaluacionWithIsEliminable)
          .collect(Collectors.toList());
    }
  }

  /**
   * Comprueba si la memoria asignada a una convocatoria de reunión se puede
   * eliminar
   * 
   * @param evaluacion el objeto Evaluacion
   * @return true or false
   */
  public Boolean isEliminable(Evaluacion evaluacion) {
    // La fecha de la convocatoria es anterior a la actual
    if (evaluacion.getConvocatoriaReunion().getFechaEvaluacion().isBefore(Instant.now())) {
      return false;
    }

    // No se pueden eliminar memorias que ya contengan un dictamen
    if (evaluacion.getDictamen() != null) {
      return false;
    }

    // No se puede eliminar una memoria que tenga comentarios asociados
    if (comentarioRepository.countByEvaluacionId(evaluacion.getId()) > 0) {
      return false;
    }
    return true;
  }

}
