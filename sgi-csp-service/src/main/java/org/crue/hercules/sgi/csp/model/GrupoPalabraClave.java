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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = GrupoPalabraClave.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoPalabraClave extends BaseEntity {

  protected static final String TABLE_NAME = "grupo_palabra_clave";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GrupoPalabraClave.SEQUENCE_NAME)
  @SequenceGenerator(name = GrupoPalabraClave.SEQUENCE_NAME, sequenceName = GrupoPalabraClave.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Grupo */
  @Column(name = "grupo_id", nullable = false)
  private Long grupoId;

  /** Palabra Clave Ref */
  @Column(name = "palabra_clave_ref", length = REF_LENGTH, nullable = false)
  private String palabraClaveRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPOPALABRACLAVE_GRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Grupo grupo = null;

}
