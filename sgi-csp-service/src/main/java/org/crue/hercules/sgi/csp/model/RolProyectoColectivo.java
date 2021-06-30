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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rol_proyecto_colectivo")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolProyectoColectivo extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rol_proyecto_colectivo_seq")
  @SequenceGenerator(name = "rol_proyecto_colectivo_seq", sequenceName = "rol_proyecto_colectivo_seq", allocationSize = 1)
  private Long id;

  /** RolProyecto Id */
  @Column(name = "rol_proyecto_id", nullable = false)
  @NotNull
  private Long rolProyectoId;

  /** Colectivo */
  @Column(name = "colectivo_ref", nullable = false)
  @NotNull
  private String colectivoRef;

  // ** Rol Proyecto */
  @ManyToOne
  @JoinColumn(name = "rol_proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ROLPROYECTOCOLECTIVO_ROLPROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final RolProyecto rolProyecto = null;

}