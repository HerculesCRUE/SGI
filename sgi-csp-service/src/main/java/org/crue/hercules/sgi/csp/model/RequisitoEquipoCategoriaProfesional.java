package org.crue.hercules.sgi.csp.model;

import java.io.Serializable;

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

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "requisitoequipo_categoriaprofesional")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class RequisitoEquipoCategoriaProfesional implements Serializable {
  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requisitoequipo_categoriaprofesional_seq")
  @SequenceGenerator(name = "requisitoequipo_categoriaprofesional_seq", sequenceName = "requisitoequipo_categoriaprofesional_seq", allocationSize = 1)
  private Long id;

  /** RequisitoEquipo Id */
  @Column(name = "requisitoequipo_id", nullable = false)
  private Long requisitoEquipoId;

  /** Referencia a la entidad externa CategoriaProfesional del ESB */
  @Column(name = "categoriaprofesional_ref", length = BaseEntity.EXTERNAL_REF_LENGTH, nullable = false)
  private String categoriaProfesionalRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "requisitoequipo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REQUISITOEQUIPO_CATEGORIAPROFESIONAL_REQUISITOEQUIPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final RequisitoEquipo requisitoEquipo = null;
}
