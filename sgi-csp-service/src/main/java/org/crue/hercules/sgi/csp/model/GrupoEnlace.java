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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = GrupoEnlace.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoEnlace extends BaseEntity {

  protected static final String TABLE_NAME = "grupo_enlace";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int ENLACE_LENGTH = 100;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GrupoEnlace.SEQUENCE_NAME)
  @SequenceGenerator(name = GrupoEnlace.SEQUENCE_NAME, sequenceName = GrupoEnlace.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Número Registro */
  @Column(name = "enlace", nullable = false)
  @Size(max = ENLACE_LENGTH)
  @NotNull
  private String enlace;

  /** Grupo */
  @Column(name = "grupo_id", nullable = false)
  @NotNull
  private Long grupoId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPOENLACE_GRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Grupo grupo = null;

}
