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

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "requisitoequipo_nivelacademico")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class RequisitoEquipoNivelAcademico {
  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requisitoequipo_nivelacademico_seq")
  @SequenceGenerator(name = "requisitoequipo_nivelacademico_seq", sequenceName = "requisitoequipo_nivelacademico_seq", allocationSize = 1)
  private Long id;

  /** RequisitoEquipo Id */
  @Column(name = "requisitoequipo_id", nullable = false)
  private Long requisitoEquipoId;

  /** Referencia a la entidad externa NivelAcademico del ESB */
  @Column(name = "nivelacademico_ref", length = BaseEntity.EXTERNAL_REF_LENGTH, nullable = false)
  private String nivelAcademicoRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "requisitoequipo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REQUISITOEQUIPO_NIVELACADEMICO_REQUISITOEQUIPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final RequisitoEquipo requisitoEquipo = null;
}
