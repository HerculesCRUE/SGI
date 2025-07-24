package org.crue.hercules.sgi.csp.model;

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
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion.OnActualizar;
import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion.OnCrear;
import org.crue.hercules.sgi.csp.validation.UniqueCertificadoAutorizacionVisible;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = CertificadoAutorizacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueCertificadoAutorizacionVisible(groups = { OnActualizar.class, OnCrear.class })
public class CertificadoAutorizacion extends BaseEntity {

  protected static final String TABLE_NAME = "certificado_autorizacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = CertificadoAutorizacion.SEQUENCE_NAME)
  @SequenceGenerator(name = CertificadoAutorizacion.SEQUENCE_NAME, sequenceName = CertificadoAutorizacion.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Autorizacion */
  @Column(name = "autorizacion_id", nullable = false)
  @NotNull
  private Long autorizacionId;

  /** Documento Ref */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "certificado_autorizacion_documento_ref", joinColumns = @JoinColumn(name = "certificado_autorizacion_id"))
  @Valid
  @NotEmpty
  @Builder.Default
  private Set<CertificadoAutorizacionDocumentoRef> documentoRef = new HashSet<>();

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "certificado_autorizacion_nombre", joinColumns = @JoinColumn(name = "certificado_autorizacion_id"))
  @Valid
  @Builder.Default
  private Set<CertificadoAutorizacionNombre> nombre = new HashSet<>();

  /** Visible */
  @Column(name = "visible", nullable = false)
  @NotNull
  private Boolean visible;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "autorizacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CERTIFICADOAUTORIZACION_AUTORIZACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Autorizacion autorizacion = null;

  /**
   * Interfaz para marcar validaciones en la creación de la entidad.
   */
  public interface OnCrear {
  }

  /**
   * Interfaz para marcar validaciones en la actualizacion de la entidad.
   */
  public interface OnActualizar {
  }

}
