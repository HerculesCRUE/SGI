package org.crue.hercules.sgi.csp.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "requisitoip_nivelacademico")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class RequisitoIPNivelAcademico implements Serializable {
  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requisitoip_nivelacademico_seq")
  @SequenceGenerator(name = "requisitoip_nivelacademico_seq", sequenceName = "requisitoip_nivelacademico_seq", allocationSize = 1)
  private Long id;

  /** RequisitoIP Id */
  @Column(name = "requisitoip_id", nullable = false)
  private Long requisitoIPId;

  /** Referencia a la entidad externa NivelAcademico del ESB */
  @Column(name = "nivelacademico_ref", length = BaseEntity.EXTERNAL_REF_LENGTH, nullable = false)
  private String nivelAcademicoRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "requisitoip_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REQUISITOIP_NIVELACADEMICO_REQUISITOIP"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final RequisitoIP requisitoIP = null;

  @OneToMany(mappedBy = "requisitoIPNivelAcademico")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudRrhhRequisitoNivelAcademico> nivelesAcademicosSolicitudRrhh = null;

}
