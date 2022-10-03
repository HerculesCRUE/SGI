package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = ProyectoSeguimientoJustificacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoSeguimientoJustificacion extends BaseEntity {

  protected static final String TABLE_NAME = "proyecto_seguimiento_justificacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ProyectoSeguimientoJustificacion.SEQUENCE_NAME)
  @SequenceGenerator(name = ProyectoSeguimientoJustificacion.SEQUENCE_NAME, sequenceName = ProyectoSeguimientoJustificacion.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Proyecto proyecto SGE */
  @OneToOne
  @JoinColumn(name = "proyecto_proyecto_sge_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOSEGUIMIENTOJUSTIFICACION_PROYECTOPROYECTOSGE"))
  private ProyectoProyectoSge proyectoProyectoSge;

  /** Importe justificado */
  @Column(name = "importe_justificado", nullable = true)
  private BigDecimal importeJustificado;

  /** Importe justificado costes directos */
  @Column(name = "importe_justificado_cd", nullable = true)
  private BigDecimal importeJustificadoCD;

  /** Importe justificado costes indirectos */
  @Column(name = "importe_justificado_ci", nullable = true)
  private BigDecimal importeJustificadoCI;

  /** Importe aceptado */
  @Column(name = "importe_aceptado", nullable = true)
  private BigDecimal importeAceptado;

  /** Importe aceptado costes directos */
  @Column(name = "importe_aceptado_cd", nullable = true)
  private BigDecimal importeAceptadoCD;

  /** Importe aceptado costes indirectos */
  @Column(name = "importe_aceptado_ci", nullable = true)
  private BigDecimal importeAceptadoCI;

  /** Importe rechazado */
  @Column(name = "importe_rechazado", nullable = true)
  private BigDecimal importeRechazado;

  /** Importe rechazado costes directos */
  @Column(name = "importe_rechazado_cd", nullable = true)
  private BigDecimal importeRechazadoCD;

  /** Importe rechazado costes indirectos */
  @Column(name = "importe_rechazado_ci", nullable = true)
  private BigDecimal importeRechazadoCI;

  /** Importe alegado */
  @Column(name = "importe_alegado", nullable = true)
  private BigDecimal importeAlegado;

  /** Importe alegado costes directos */
  @Column(name = "importe_alegado_cd", nullable = true)
  private BigDecimal importeAlegadoCD;

  /** Importe alegado costes indirectos */
  @Column(name = "importe_alegado_ci", nullable = true)
  private BigDecimal importeAlegadoCI;

  /** Importe reintegrar */
  @Column(name = "importe_reintegrar", nullable = true)
  private BigDecimal importeReintegrar;

  /** Importe reintegrar costes directos */
  @Column(name = "importe_reintegrar_cd", nullable = true)
  private BigDecimal importeReintegrarCD;

  /** Importe reintegrar costes indirectos */
  @Column(name = "importe_reintegrar_ci", nullable = true)
  private BigDecimal importeReintegrarCI;

  /** Importe reintegrado */
  @Column(name = "importe_reintegrado", nullable = true)
  private BigDecimal importeReintegrado;

  /** Importe reintegrado costes directos */
  @Column(name = "importe_reintegrado_cd", nullable = true)
  private BigDecimal importeReintegradoCD;

  /** Importe reintegrado costes indirectos */
  @Column(name = "importe_reintegrado_ci", nullable = true)
  private BigDecimal importeReintegradoCI;

  /** Intereses reintegrados */
  @Column(name = "intereses_reintegrados", nullable = true)
  private BigDecimal interesesReintegrados;

  /** Intereses reintegrar */
  @Column(name = "intereses_reintegrar", nullable = true)
  private BigDecimal interesesReintegrar;

  /** Fecha reintegro */
  @Column(name = "fecha_reintegro", nullable = true)
  private Instant fechaReintegro;

  /** Numero justificante ultimo reintegro */
  @Column(name = "justificante_reintegro", nullable = true, length = DEFAULT_TEXT_LENGTH)
  private String justificanteReintegro;

  /** Importe no ejecutado */
  @Column(name = "importe_no_ejecutado", nullable = true)
  private BigDecimal importeNoEjecutado;

  /** Importe no ejecutado costes directos */
  @Column(name = "importe_no_ejecutado_cd", nullable = true)
  private BigDecimal importeNoEjecutadoCD;

  /** Importe no ejecutado costes indirectos */
  @Column(name = "importe_no_ejecutado_ci", nullable = true)
  private BigDecimal importeNoEjecutadoCI;
}
