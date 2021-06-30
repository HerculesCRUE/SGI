package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "modelo_tipo_fase", uniqueConstraints = { @UniqueConstraint(columnNames = { "modelo_ejecucion_id",
    "tipo_fase_id" }, name = "UK_MODELOTIPOFASE_MODELO_TIPOFASE") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModeloTipoFase extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "modelo_tipo_fase_seq")
  @SequenceGenerator(name = "modelo_tipo_fase_seq", sequenceName = "modelo_tipo_fase_seq", allocationSize = 1)
  private Long id;

  /** Tipo fase. */
  @ManyToOne
  @JoinColumn(name = "tipo_fase_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MODELOTIPOFASE_TIPOFASE"))
  @NotNull
  private TipoFase tipoFase;

  /** Modelo ejecución. */
  @ManyToOne
  @JoinColumn(name = "modelo_ejecucion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MODELOTIPOFASE_MODELOEJECUCION"))
  @NotNull
  private ModeloEjecucion modeloEjecucion;

  /** Activo para solicitudes */
  @Column(name = "solicitud", columnDefinition = "boolean default false", nullable = false)
  @NotNull
  private Boolean solicitud;

  /** Activo para convocatorias */
  @Column(name = "convocatoria", columnDefinition = "boolean default false", nullable = false)
  @NotNull
  private Boolean convocatoria;

  /** Activo para proyectos */
  @Column(name = "proyecto", columnDefinition = "boolean default false", nullable = false)
  @NotNull
  private Boolean proyecto;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}
