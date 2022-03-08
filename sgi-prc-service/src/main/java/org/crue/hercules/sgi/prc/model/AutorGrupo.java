package org.crue.hercules.sgi.prc.model;

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

import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = AutorGrupo.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutorGrupo extends BaseEntity {

  protected static final String TABLE_NAME = "autor_grupo";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = AutorGrupo.SEQUENCE_NAME)
  @SequenceGenerator(name = AutorGrupo.SEQUENCE_NAME, sequenceName = AutorGrupo.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** TipoEstadoProduccion */
  @Column(name = "estado", nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoEstadoProduccion estado;

  /** grupoRef */
  @Column(name = "grupo_ref", length = GRUPO_REF_LENGTH, nullable = true)
  private String grupoRef;

  /** Autor Id */
  @Column(name = "autor_id", nullable = false)
  private Long autorId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "autor_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_AUTORGRUPO_AUTOR"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Autor autor = null;

}
