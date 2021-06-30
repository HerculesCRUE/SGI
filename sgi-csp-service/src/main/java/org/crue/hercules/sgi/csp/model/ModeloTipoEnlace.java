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
@Table(name = "modelo_tipo_enlace", uniqueConstraints = { @UniqueConstraint(columnNames = { "modelo_ejecucion_id",
    "tipo_enlace_id" }, name = "UK_MODELOTIPOENLACE_MODELO_TIPOENLACE") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModeloTipoEnlace extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "modelo_tipo_enlace_seq")
  @SequenceGenerator(name = "modelo_tipo_enlace_seq", sequenceName = "modelo_tipo_enlace_seq", allocationSize = 1)
  private Long id;

  /** Tipo enlace. */
  @ManyToOne
  @JoinColumn(name = "tipo_enlace_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MODELOTIPOENLACE_TIPODOENLACE"))
  @NotNull
  private TipoEnlace tipoEnlace;

  /** Modelo ejecución. */
  @ManyToOne
  @JoinColumn(name = "modelo_ejecucion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MODELOTIPOENLACE_MODELOEJECUCION"))
  @NotNull
  private ModeloEjecucion modeloEjecucion;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}
