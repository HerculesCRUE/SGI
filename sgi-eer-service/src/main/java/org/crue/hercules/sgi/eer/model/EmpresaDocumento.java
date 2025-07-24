package org.crue.hercules.sgi.eer.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = EmpresaDocumento.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaDocumento extends BaseEntity {
  protected static final String TABLE_NAME = "empresa_documento";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EmpresaDocumento.SEQUENCE_NAME)
  @SequenceGenerator(name = EmpresaDocumento.SEQUENCE_NAME, sequenceName = EmpresaDocumento.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "empresa_documento_nombre", joinColumns = @JoinColumn(name = "empresa_documento_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<EmpresaDocumentoNombre> nombre = new HashSet<>();

  /** Referencia del documento */
  @Column(name = "documento_ref", length = REF_LENGTH, nullable = false)
  private String documentoRef;

  /** Comentarios */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "empresa_documento_comentarios", joinColumns = @JoinColumn(name = "empresa_documento_id"))
  @Valid
  @Builder.Default
  private Set<EmpresaDocumentoComentarios> comentarios = new HashSet<>();

  /** Id de la empresa */
  @Column(name = "empresa_id", nullable = false)
  private Long empresaId;

  /** Tipo documento. */
  @ManyToOne
  @JoinColumn(name = "tipo_documento_id", nullable = true, foreignKey = @ForeignKey(name = "FK_EMPRESADOCUMENTO_TIPODOCUMENTO"))
  @Valid
  @ActivableIsActivo(entityClass = TipoDocumento.class, groups = { BaseEntity.Create.class,
      OnActualizarTipoDocumento.class })
  private TipoDocumento tipoDocumento;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "empresa_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_EMPRESADOCUMENTO_EMPRESA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Empresa empresa = null;

  /**
   * Interfaz para marcar validaciones en la actualizacion del campo
   * TipoDocumento de la entidad.
   */
  public interface OnActualizarTipoDocumento {

  }
}
