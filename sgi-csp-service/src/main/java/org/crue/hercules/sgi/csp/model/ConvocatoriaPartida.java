package org.crue.hercules.sgi.csp.model;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.enums.TipoPartida;
import org.crue.hercules.sgi.csp.validation.ConvocatoriaPartidaCodigoOrPartidaRefRequired;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "convocatoria_partida")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ConvocatoriaPartidaCodigoOrPartidaRefRequired(groups = { BaseEntity.Create.class, BaseEntity.Update.class })
public class ConvocatoriaPartida extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_partida_seq")
  @SequenceGenerator(name = "convocatoria_partida_seq", sequenceName = "convocatoria_partida_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria Id */
  @Column(name = "convocatoria_id", nullable = false)
  @NotNull
  private Long convocatoriaId;

  /** Partida sge ref. */
  @Column(name = "partida_ref", length = 50, nullable = true)
  @Size(max = 50)
  private String partidaRef;

  /** Código. */
  @Column(name = "codigo", length = 50, nullable = true)
  @Size(max = 50)
  private String codigo;

  /** descripcion. */
  @Column(name = "descripcion", length = 50, nullable = true)
  @Size(max = 50)
  private String descripcion;

  /** Tipo partida */
  @Column(name = "tipo_partida", length = 10)
  @Enumerated(EnumType.STRING)
  private TipoPartida tipoPartida;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAPARTIDA_CONVOCATORIA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Convocatoria convocatoria = null;
}
