package org.crue.hercules.sgi.eer.model;

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
import javax.validation.constraints.NotBlank;
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
@Table(name = EmpresaEquipoEmprendedor.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaEquipoEmprendedor extends BaseEntity {

  protected static final String TABLE_NAME = "empresa_equipo_emprendedor";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int MIEMBRO_EQUIPO_REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EmpresaEquipoEmprendedor.SEQUENCE_NAME)
  @SequenceGenerator(name = EmpresaEquipoEmprendedor.SEQUENCE_NAME, sequenceName = EmpresaEquipoEmprendedor.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Miembro del equipo */
  @Column(name = "miembro_equipo_ref", length = MIEMBRO_EQUIPO_REF_LENGTH, nullable = false)
  @Size(max = MIEMBRO_EQUIPO_REF_LENGTH)
  @NotBlank
  private String miembroEquipoRef;

  /** Empresa */
  @Column(name = "empresa_id", nullable = false)
  @NotNull
  private Long empresaId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "empresa_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_EMPRESAEQUIPOEMPRENDEDOR_EMPRESA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Empresa empresa = null;

  /**
   * Interfaz para marcar validaciones al eliminar la entidad
   */
  public interface OnDelete {

  }

}
