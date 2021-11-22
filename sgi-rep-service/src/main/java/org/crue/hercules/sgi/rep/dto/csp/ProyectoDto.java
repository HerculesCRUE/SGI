package org.crue.hercules.sgi.rep.dto.csp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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
public class ProyectoDto extends BaseRestDto {

  /** Tipo de horas anuales. */
  public enum TipoHorasAnuales {
    /** Valor fijo */
    FIJO,
    /** Reales (TS) */
    REAL,
    /** Por categoría */
    CATEGORIA;
  }

  /** Causas de exención. */
  public enum CausaExencion {
    /** Sujeto y Exento */
    SUJETO_EXENTO,
    /** No sujeto a articulos 7, 14 y otros */
    NO_SUJETO,
    /** No sujeto por reglas de localización. Sin derecho a deducción */
    NO_SUJETO_SIN_DEDUCCION,
    /** No sujeto por reglas de localización. Con derecho a deducción */
    NO_SUJETO_CON_DEDUCCION;
  }

  /** Clasificación de la producción científica / CVN */
  public enum ClasificacionCVN {
    /** Ayudas y Becas */
    AYUDAS,
    /** Proyectos competitivos */
    COMPETITIVOS,
    /** Contratos, Convenios, Proyectos no competitivos */
    NO_COMPETITIVOS,
  }

  private Long convocatoriaId;
  private Long solicitudId;
  private EstadoProyectoDto estado;
  private String titulo;
  private String acronimo;
  private String codigoExterno;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Instant fechaFinDefinitiva;
  private String unidadGestionRef;
  private ModeloEjecucionDto modeloEjecucion;
  private TipoFinalidadDto finalidad;
  private String convocatoriaExterna;
  private TipoAmbitoGeograficoDto ambitoGeografico;
  private Boolean confidencial;
  private ClasificacionCVN clasificacionCVN;
  private Boolean coordinado;
  private Boolean colaborativo;
  private Boolean coordinadorExterno;
  private Boolean timesheet;
  private Boolean permitePaquetesTrabajo;
  private Boolean costeHora;
  private TipoHorasAnuales tipoHorasAnuales;
  private ProyectoIvaDto iva;
  private CausaExencion causaExencion;
  private String observaciones;
  private Boolean anualidades;
  private BigDecimal importePresupuesto;
  private BigDecimal importePresupuestoCostesIndirectos;
  private BigDecimal importeConcedido;
  private BigDecimal importeConcedidoCostesIndirectos;
  private BigDecimal importePresupuestoSocios;
  private BigDecimal importeConcedidoSocios;
  private BigDecimal totalImportePresupuesto;
  private BigDecimal totalImporteConcedido;
  private Boolean activo;
  private ContextoProyectoDto contexto;
  private List<ProyectoEntidadConvocanteDto> entidadesConvocantes;
  private List<ProyectoEntidadFinanciadoraDto> entidadesFinanciadoras;
  private List<EstadoProyectoDto> estados;
  private List<ProyectoEquipoDto> equipo;

}