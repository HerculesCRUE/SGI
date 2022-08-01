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
@Table(name = "requisitoip_categoriaprofesional")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class RequisitoIPCategoriaProfesional implements Serializable {
  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requisitoip_categoriaprofesional_seq")
  @SequenceGenerator(name = "requisitoip_categoriaprofesional_seq", sequenceName = "requisitoip_categoriaprofesional_seq", allocationSize = 1)
  private Long id;

  /** RequisitoIP Id */
  @Column(name = "requisitoip_id", nullable = false)
  private Long requisitoIPId;

  /** Referencia a la entidad externa CategoriaProfesional del ESB */
  @Column(name = "categoriaprofesional_ref", length = BaseEntity.EXTERNAL_REF_LENGTH, nullable = false)
  private String categoriaProfesionalRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "requisitoip_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REQUISITOIP_CATEGORIAPROFESIONAL_REQUISITOIP"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final RequisitoIP requisitoIP = null;

  @OneToMany(mappedBy = "requisitoIPCategoriaProfesional")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudRrhhRequisitoCategoria> categoriasProfesionalesSolicitudRrhh = null;

}
