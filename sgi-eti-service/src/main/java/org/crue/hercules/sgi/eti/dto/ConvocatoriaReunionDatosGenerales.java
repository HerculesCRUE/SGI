package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvocatoriaReunionDatosGenerales implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  private Long id;

  /** Comite. */
  private Comite comite;

  /** Fecha Evaluación. */
  private Instant fechaEvaluacion;

  /** Fecha Límite. */
  private Instant fechaLimite;

  /** Lugar. */
  private String lugar;

  /** Orden del día. */
  private String ordenDia;

  /** Anio */
  private Integer anio;

  /** Numero acta */
  private Long numeroActa;

  /** Tipo Convocatoria Reunión. */
  private TipoConvocatoriaReunion tipoConvocatoriaReunion;

  /** Hora Inicio. */
  private Integer horaInicio;

  /** Minuto Inicio. */
  private Integer minutoInicio;

  /** Hora Inicio Segunda. */
  private Integer horaInicioSegunda;

  /** Minuto Inicio Segunda. */
  private Integer minutoInicioSegunda;

  /** Fecha Envío. */
  private Instant fechaEnvio;

  /** Control de borrado lógico */
  private Boolean activo;

  /** Número evaluaciones activas que no son revisión mínima */
  private Integer numEvaluaciones;

  /** Si la convocatoria tiene Acta */
  private Long idActa;

  public ConvocatoriaReunionDatosGenerales(ConvocatoriaReunion convocatoriaReunion, Long numEvaluaciones, Long idActa) {
    this.id = convocatoriaReunion.getId();
    this.comite = convocatoriaReunion.getComite();
    this.fechaEvaluacion = convocatoriaReunion.getFechaEvaluacion();
    this.fechaLimite = convocatoriaReunion.getFechaLimite();
    this.lugar = convocatoriaReunion.getLugar();
    this.ordenDia = convocatoriaReunion.getOrdenDia();
    this.anio = convocatoriaReunion.getAnio();
    this.numeroActa = convocatoriaReunion.getNumeroActa();
    this.tipoConvocatoriaReunion = convocatoriaReunion.getTipoConvocatoriaReunion();
    this.horaInicio = convocatoriaReunion.getHoraInicio();
    this.minutoInicio = convocatoriaReunion.getMinutoInicio();
    this.horaInicioSegunda = convocatoriaReunion.getHoraInicioSegunda();
    this.minutoInicioSegunda = convocatoriaReunion.getMinutoInicioSegunda();
    this.fechaEnvio = convocatoriaReunion.getFechaEnvio();
    this.activo = convocatoriaReunion.getActivo();
    this.numEvaluaciones = numEvaluaciones.intValue();
    this.idActa = idActa;
  }

}
