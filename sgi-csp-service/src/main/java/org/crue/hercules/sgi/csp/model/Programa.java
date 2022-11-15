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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "programa")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Programa extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public static final int NOMBRE_LENGTH = 200;
  public static final int DESCRIPCION_LENGTH = 4000;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programa_seq")
  @SequenceGenerator(name = "programa_seq", sequenceName = "programa_seq", allocationSize = 1)
  private Long id;

  /** Nombre. */
  @Column(name = "nombre", length = Programa.NOMBRE_LENGTH, nullable = false)
  @NotEmpty
  @Size(max = Programa.NOMBRE_LENGTH)
  private String nombre;

  /** Descripcion. */
  @Column(name = "descripcion", length = Programa.DESCRIPCION_LENGTH, nullable = true)
  @Size(max = Programa.DESCRIPCION_LENGTH)
  private String descripcion;

  /** Programa padre. */
  @ManyToOne
  @JoinColumn(name = "programa_padre_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROGRAMA_PADRE"))
  private Programa padre;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @NotNull(groups = { Update.class })
  private Boolean activo;

}
