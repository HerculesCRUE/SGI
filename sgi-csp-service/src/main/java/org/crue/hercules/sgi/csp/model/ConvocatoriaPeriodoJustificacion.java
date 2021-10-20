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

import org.crue.hercules.sgi.csp.enums.TipoJustificacion;
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
@Table(name = "convocatoria_periodo_justificacion")
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
public class ConvocatoriaPeriodoJustificacion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_periodo_justificacion_seq")
  @SequenceGenerator(name = "convocatoria_periodo_justificacion_seq", sequenceName = "convocatoria_periodo_justificacion_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria Id */
  @Column(name = "convocatoria_id", nullable = false)
  @NotNull
  private Long convocatoriaId;

  /** Num periodo */
  @Column(name = "num_periodo", nullable = false)
  private Integer numPeriodo;

  /** Mes inicial */
  @Column(name = "mes_inicial", nullable = false)
  @NotNull
  @Min(1)
  private Integer mesInicial;

  /** Mes final */
  @Column(name = "mes_final", nullable = false)
  @NotNull
  @Min(1)
  private Integer mesFinal;

  /** Fecha inicio presentacion */
  @Column(name = "fecha_inicio_presentacion", nullable = true)
  private Instant fechaInicioPresentacion;

  /** Fecha fin presentacion */
  @Column(name = "fecha_fin_presentacion", nullable = true)
  private Instant fechaFinPresentacion;

  /** Obervaciones */
  @Column(name = "observaciones", nullable = true)
  @Size(max = 2000)
  private String observaciones;

  /** Tipo justificacion */
  @Column(name = "tipo", length = 10)
  @Enumerated(EnumType.STRING)
  private TipoJustificacion tipo;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAPERIODOJUSTIFICACION_CONVOCATORIA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Convocatoria convocatoria = null;
}
