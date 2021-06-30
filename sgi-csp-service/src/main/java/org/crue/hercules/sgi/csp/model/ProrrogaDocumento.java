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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prorroga_documento")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProrrogaDocumento extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prorroga_documento_seq")
  @SequenceGenerator(name = "prorroga_documento_seq", sequenceName = "prorroga_documento_seq", allocationSize = 1)
  private Long id;

  /** Proyecto Id */
  @Column(name = "proyecto_prorroga_id", nullable = false)
  @NotNull
  private Long proyectoProrrogaId;

  /** Nombre */
  @Column(name = "nombre", length = 50, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String nombre;

  /** Referencia documento */
  @Column(name = "documento_ref", length = 250, nullable = false)
  @NotEmpty
  @Size(max = 250)
  private String documentoRef;

  /** Tipo documento */
  @ManyToOne
  @JoinColumn(name = "tipo_documento_id", foreignKey = @ForeignKey(name = "FK_PRORROGADOCUMENTO_TIPODOCUMENTO"))
  private TipoDocumento tipoDocumento;

  /** Comentario */
  @Column(name = "comentario", length = 2000)
  @Size(max = 2000)
  private String comentario;

  /** Visible */
  @Column(name = "visible", columnDefinition = "boolean default true", nullable = false)
  @NotNull
  private Boolean visible;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_prorroga_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PRORROGADOCUMENTO_PROYECTOPRORROGA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoProrroga proyectoProrroga = null;
}