package org.crue.hercules.sgi.prc.model;

import java.time.Instant;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Autor.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Autor extends BaseEntity {

  protected static final String TABLE_NAME = "autor";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "firma", length = FIRMA_LENGTH, nullable = true)
  private String firma;

  @Column(name = "persona_ref", length = PERSONA_REF_LENGTH, nullable = true)
  private String personaRef;

  @Column(name = "nombre", length = NOMBRE_LENGTH, nullable = true)
  private String nombre;

  @Column(name = "apellidos", length = APELLIDOS_LENGTH, nullable = true)
  private String apellidos;

  @Column(name = "orden", nullable = true)
  private Integer orden;

  @Column(name = "orcid_id", length = ORCID_ID_LENGTH, nullable = true)
  private String orcidId;

  @Column(name = "fecha_inicio", nullable = true)
  private Instant fechaInicio;

  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  @Column(name = "ip", columnDefinition = "boolean default false", nullable = false)
  private Boolean ip;

  @Column(name = "produccion_cientifica_id", nullable = false)
  private Long produccionCientificaId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "produccion_cientifica_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_AUTOR_PRODUCCIONCIENTIFICA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProduccionCientifica produccionCientifica = null;

  @OneToMany(mappedBy = "autor")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<AutorGrupo> autoresGrupo = null;

}
