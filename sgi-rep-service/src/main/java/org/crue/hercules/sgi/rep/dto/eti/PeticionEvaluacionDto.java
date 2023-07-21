package org.crue.hercules.sgi.rep.dto.eti;

import java.math.BigDecimal;
import java.time.Instant;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PeticionEvaluacionDto extends BaseRestDto {

  /** Estados de la Financiacion */
  public enum EstadoFinanciacion {
    /** Solicitado */
    SOLICITADO,
    /** Concedido */
    CONCEDIDO,
    /** Denegado */
    DENEGADO;
  }

  /** Tipo valor social */
  public enum TipoValorSocial {
    /** INVESTIGACION_FUNDAMENTAL */
    INVESTIGACION_FUNDAMENTAL,
    /** INVESTIGACION_PREVENCION */
    INVESTIGACION_PREVENCION,
    /** INVESTIGACION_EVALUACIÓN */
    INVESTIGACION_EVALUACION,
    /** INVESTIGACION_DESARROLLO */
    INVESTIGACION_DESARROLLO,
    /** INVESTIGACION_PROTECCION */
    INVESTIGACION_PROTECCION,
    /** INVESTIGACION_BIENESTAR */
    INVESTIGACION_BIENESTAR,
    /** INVESTIGACION_CONSERVACION */
    INVESTIGACION_CONSERVACION,
    /** ENSEÑANZA_SUPERIOR */
    ENSENIANZA_SUPERIOR,
    /** INVESTIGACION_JURIDICA */
    INVESTIGACION_JURIDICA,
    /** OTRA FINALIDAD */
    OTRA_FINALIDAD
  }

  private String solicitudConvocatoriaRef;
  private String codigo;
  private String titulo;
  private transient TipoActividadDto tipoActividad;
  private transient TipoInvestigacionTuteladaDto tipoInvestigacionTutelada;
  private Boolean existeFinanciacion;
  private String fuenteFinanciacion;
  private EstadoFinanciacion estadoFinanciacion;
  private BigDecimal importeFinanciacion;
  private Instant fechaInicio;
  private Instant fechaFin;
  private String resumen;
  private TipoValorSocial valorSocial;
  private String otroValorSocial;
  private String objetivos;
  private String disMetodologico;
  private Boolean externo;
  private Boolean tieneFondosPropios;
  private String personaRef;
  private Long checklistId;
  private String tutorRef;
  private Boolean activo;
}