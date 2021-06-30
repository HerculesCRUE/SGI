package org.crue.hercules.sgi.eti.model;

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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "acta")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Acta extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acta_seq")
  @SequenceGenerator(name = "acta_seq", sequenceName = "acta_seq", allocationSize = 1)
  private Long id;

  @OneToOne
  @JoinColumn(name = "convocatoria_reunion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ACTA_CONVOCATORIAREUNION"))
  @NotNull
  private ConvocatoriaReunion convocatoriaReunion;

  /** Hora inicio */
  @Column(name = "hora_inicio", nullable = false)
  @Min(0)
  @Max(23)
  @NotNull
  private Integer horaInicio;

  /** Minuto inicio */
  @Column(name = "minuto_inicio", nullable = false)
  @Min(0)
  @Max(59)
  @NotNull
  private Integer minutoInicio;

  /** Hora fin */
  @Column(name = "hora_fin", nullable = false)
  @Min(0)
  @Max(23)
  @NotNull
  private Integer horaFin;

  /** Minuto fin */
  @Column(name = "minuto_fin", nullable = false)
  @Min(0)
  @Max(59)
  @NotNull
  private Integer minutoFin;

  /** Resumen */
  @Column(name = "resumen", length = 8000, nullable = false)
  @NotNull
  private String resumen;

  /** Numero */
  @Column(name = "numero", nullable = false)
  private Integer numero;

  /** Tipo Estado Acta */
  @OneToOne
  @JoinColumn(name = "estado_actual_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ACTA_ESTADOACTUAL"))
  @NotNull(groups = { Update.class })
  private TipoEstadoActa estadoActual;

  /** Inactiva */
  @Column(name = "inactiva", columnDefinition = "boolean default true", nullable = false)
  @NotNull
  private Boolean inactiva;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @NotNull
  private Boolean activo;

}