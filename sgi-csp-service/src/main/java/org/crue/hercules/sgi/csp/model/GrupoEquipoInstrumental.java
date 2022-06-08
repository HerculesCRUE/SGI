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
@Table(name = GrupoEquipoInstrumental.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoEquipoInstrumental extends BaseEntity {

  protected static final String TABLE_NAME = "grupo_equipo_instrumental";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int NUM_REGISTRO_LENGTH = 50;
  public static final int NOMBRE_LENGTH = 100;
  public static final int DESCRIPCION_LENGTH = 250;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GrupoEquipoInstrumental.SEQUENCE_NAME)
  @SequenceGenerator(name = GrupoEquipoInstrumental.SEQUENCE_NAME, sequenceName = GrupoEquipoInstrumental.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Número Registro */
  @Column(name = "num_registro")
  @Size(max = NUM_REGISTRO_LENGTH)
  private String numRegistro;

  /** Nombre */
  @Column(name = "nombre", nullable = false)
  @Size(max = NOMBRE_LENGTH)
  @NotNull
  private String nombre;

  /** Descripción */
  @Column(name = "descripcion")
  @Size(max = DESCRIPCION_LENGTH)
  private String descripcion;

  /** Grupo */
  @Column(name = "grupo_id", nullable = false)
  @NotNull
  private Long grupoId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPOEQUIPOINSTRUMENTAL_GRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Grupo grupo = null;

}
