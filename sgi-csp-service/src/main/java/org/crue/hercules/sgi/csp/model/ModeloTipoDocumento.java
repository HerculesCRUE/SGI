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
@Table(name = "modelo_tipo_documento", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "modelo_ejecucion_id", "tipo_documento_id",
        "modelo_tipo_fase_id" }, name = "UK_MODELOTIPODOCUMENTO_MODELO_TIPODOCUMENTO_MODELOTIPOFASE") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModeloTipoDocumento extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "modelo_tipo_documento_seq")
  @SequenceGenerator(name = "modelo_tipo_documento_seq", sequenceName = "modelo_tipo_documento_seq", allocationSize = 1)
  private Long id;

  /** Tipo documento. */
  @ManyToOne
  @JoinColumn(name = "tipo_documento_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MODELOTIPODOCUMENTO_TIPODOCUMENTO"))
  @NotNull
  private TipoDocumento tipoDocumento;

  /** Modelo ejecución. */
  @ManyToOne
  @JoinColumn(name = "modelo_ejecucion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MODELOTIPODOCUMENTO_MODELOEJECUCION"))
  @NotNull
  private ModeloEjecucion modeloEjecucion;

  /** Modelo tipo fase. */
  @ManyToOne
  @JoinColumn(name = "modelo_tipo_fase_id", nullable = true, foreignKey = @ForeignKey(name = "FK_MODELOTIPODOCUMENTO_MODELOTIPOFASE"))
  private ModeloTipoFase modeloTipoFase;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @NotNull(groups = { Update.class })
  private Boolean activo;

}
