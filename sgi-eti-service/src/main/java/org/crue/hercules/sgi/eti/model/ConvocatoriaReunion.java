package org.crue.hercules.sgi.eti.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "convocatoria_reunion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ConvocatoriaReunion extends BaseEntity {

  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_reunion_seq")
  @SequenceGenerator(name = "convocatoria_reunion_seq", sequenceName = "convocatoria_reunion_seq", allocationSize = 1)
  private Long id;

  /** Comite. */
  @ManyToOne
  @JoinColumn(name = "comite_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAREUNION_COMITE"))
  @NotNull
  private Comite comite;

  /** Fecha Evaluación. */
  @Column(name = "fecha_evaluacion", nullable = false)
  private Instant fechaEvaluacion;

  /** Fecha Límite. */
  @Column(name = "fecha_limite", nullable = false)
  private Instant fechaLimite;

  /** Lugar. */
  @Column(name = "lugar", length = 250, nullable = false)
  private String lugar;

  /** Orden del día. */
  @Column(name = "orden_dia", length = 2000, nullable = false)
  private String ordenDia;

  /** Anio */
  @Column(name = "anio", nullable = false)
  private Integer anio;

  /** Numero acta */
  @Column(name = "numero_acta", nullable = false)
  private Long numeroActa;

  /** Tipo Convocatoria Reunión. */
  @OneToOne
  @JoinColumn(name = "tipo_convocatoria_reunion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAREUNION_TIPOCONVOCATORIAREUNION"))
  private TipoConvocatoriaReunion tipoConvocatoriaReunion;

  /** Hora Inicio. */
  @Min(0)
  @Max(23)
  @Column(name = "hora_inicio", nullable = false)
  private Integer horaInicio;

  /** Minuto Inicio. */
  @Column(name = "minuto_inicio", nullable = false)
  @Min(0)
  @Max(59)
  private Integer minutoInicio;

  /** Hora Inicio Segunda. */
  @Min(0)
  @Max(23)
  @Column(name = "hora_inicio_segunda", nullable = true)
  private Integer horaInicioSegunda;

  /** Minuto Inicio Segunda. */
  @Column(name = "minuto_inicio_segunda", nullable = true)
  @Min(0)
  @Max(59)
  private Integer minutoInicioSegunda;

  /** Fecha Envío. */
  @Column(name = "fecha_envio")
  private Instant fechaEnvio;

  /** Control de borrado lógico */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

  public ConvocatoriaReunion(Long id, Instant fechaEvaluacion, Integer anio, Long numeroActa) {
    this.id = id;
    this.fechaEvaluacion = fechaEvaluacion;
    this.anio = anio;
    this.numeroActa = numeroActa;
  }

}
