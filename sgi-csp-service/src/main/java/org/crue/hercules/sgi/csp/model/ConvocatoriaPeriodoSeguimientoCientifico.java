package org.crue.hercules.sgi.csp.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.hibernate.validator.constraints.ScriptAssert;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "convocatoria_periodo_seguimiento_cientifico")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ScriptAssert.List({
    // Validacion de meses
    @ScriptAssert(lang = "spel", alias = "_this", script = "#_this.getMesInicial() == null || #_this.getMesFinal() == null || #_this.getMesFinal().compareTo(#_this.getMesInicial()) >= 0", reportOn = "mesFinal", message = "{org.crue.hercules.sgi.csp.validation.MesInicialMayorMesFinal.message}"),
    // Validacion de fechas de presentación
    @ScriptAssert(lang = "spel", alias = "_this", script = "#_this.getFechaInicioPresentacion() == null || #_this.getFechaFinPresentacion() == null || #_this.getFechaFinPresentacion().compareTo(#_this.getFechaInicioPresentacion()) >= 0", reportOn = "fechaFinPresentacion", message = "{org.crue.hercules.sgi.csp.validation.FechaInicialMayorFechaFinal.message}") })
public class ConvocatoriaPeriodoSeguimientoCientifico extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_periodo_seguimiento_cientifico_seq")
  @SequenceGenerator(name = "convocatoria_periodo_seguimiento_cientifico_seq", sequenceName = "convocatoria_periodo_seguimiento_cientifico_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria Id */
  @Column(name = "convocatoria_id", nullable = false)
  @NotNull
  private Long convocatoriaId;

  /** Numero Periodo */
  @Column(name = "num_periodo", nullable = false)
  @Min(1)
  private Integer numPeriodo;

  /** Mes Inicial */
  @Column(name = "mes_inicial", nullable = false)
  @NotNull
  @Min(1)
  private Integer mesInicial;

  /** Mes Final */
  @Column(name = "mes_final", nullable = false)
  @NotNull
  @Min(1)
  private Integer mesFinal;

  /** Fecha Inicio Presentacion */
  @Column(name = "fecha_inicio_presentacion", nullable = true)
  private Instant fechaInicioPresentacion;

  /** Fecha Fin Presentacion */
  @Column(name = "fecha_fin_presentacion", nullable = true)
  private Instant fechaFinPresentacion;

  /** Tipo Seguimiento */
  @Column(name = "tipo_seguimiento", length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoSeguimiento tipoSeguimiento;

  /** Observaciones */
  @Column(name = "observaciones", length = 2000, nullable = true)
  @Size(max = 2000)
  private String observaciones;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAPERIODOSEGUIMIENTOCIENTIFICO_CONVOCATORIA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Convocatoria convocatoria = null;
}