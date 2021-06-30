package org.crue.hercules.sgi.csp.dto.eti;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * PeticionEvaluacion del API del módulo de Etica
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeticionEvaluacion {

  /** Id */
  private Long id;
  /** Referencia solicitud convocatoria */
  private String solicitudConvocatoriaRef;
  /** Código */
  private String codigo;
  /** Título */
  private String titulo;
  /** Tipo Actividad */
  private TipoActividad tipoActividad;
  /** Tipo Investigacion Tutelada */
  private TipoInvestigacionTutelada tipoInvestigacionTutelada;
  /** Existe financiacion */
  private Boolean existeFinanciacion;
  /** Fuente financiacion */
  private String fuenteFinanciacion;
  /** Estado Financiación */
  private EstadoFinanciacion estadoFinanciacion;
  /** Importe Financiación */
  private BigDecimal importeFinanciacion;
  /** Fecha Inicio. */
  private Instant fechaInicio;
  /** Fecha Fin. */
  private Instant fechaFin;
  /** Resumen */
  private String resumen;
  /** Valor social */
  private TipoValorSocial valorSocial;
  /** Otro valor social */
  private String otroValorSocial;
  /** Objetivos */
  private String objetivos;
  /** Diseño metodológico */
  private String disMetodologico;
  /** Externo */
  private Boolean externo;
  /** Tiene fondos propios */
  private Boolean tieneFondosPropios;
  /** Referencia usuario */
  private String personaRef;
  /** Referencia al Checklist asociado */
  private Long checklistId;
  /** Activo */
  private Boolean activo;

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

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoActividad {
    /** Id. */
    private Long id;
    /** Nombre. */
    private String nombre;
    /** Activo */
    private Boolean activo;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoInvestigacionTutelada {
    /** Id. */
    private Long id;
    /** Nombre. */
    private String nombre;
    /** Activo */
    private Boolean activo;
  }
}