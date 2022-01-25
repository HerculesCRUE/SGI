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
@Table(name = CertificadoAutorizacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificadoAutorizacion extends BaseEntity {

  protected static final String TABLE_NAME = "certificado_autorizacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";
  public static final int MAX_LENGTH = 250;

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
  @Column(name = "documento_ref", length = CertificadoAutorizacion.MAX_LENGTH, nullable = true)
  @Size(max = CertificadoAutorizacion.MAX_LENGTH)
  private String documentoRef;

  /** Nombre */
  @Column(name = "nombre", length = CertificadoAutorizacion.MAX_LENGTH, nullable = true)
  @Size(max = CertificadoAutorizacion.MAX_LENGTH)
  private String nombre;

  /** Visible */
  @Column(name = "visible", nullable = false)
  @NotNull
  private Boolean visible;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "autorizacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CERTIFICADOAUTORIZACION_AUTORIZACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private static final Autorizacion autorizacion = null;

}
