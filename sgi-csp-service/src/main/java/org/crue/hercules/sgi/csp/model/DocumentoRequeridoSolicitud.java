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
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "documento_requerido_solicitud")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoRequeridoSolicitud extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documento_requerido_solicitud_seq")
  @SequenceGenerator(name = "documento_requerido_solicitud_seq", sequenceName = "documento_requerido_solicitud_seq", allocationSize = 1)
  private Long id;

  /** ConfiguracionSolicitud Id */
  @Column(name = "configuracion_solicitud_id", nullable = false)
  @NotNull
  private Long configuracionSolicitudId;

  /** Tipo Documento */
  @ManyToOne
  @JoinColumn(name = "tipo_documento_id", nullable = false, foreignKey = @ForeignKey(name = "FK_DOCUMENTOREQUERIDOSOLICITUD_TIPODOCUMENTO"))
  @NotNull
  private TipoDocumento tipoDocumento;

  /** Observaciones */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "documento_requerido_solicitud_observaciones", joinColumns = @JoinColumn(name = "documento_requerido_solicitud_id"))
  @Valid
  @Builder.Default
  private Set<DocumentoRequeridoSolicitudObservaciones> observaciones = new HashSet<>();

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "configuracion_solicitud_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_DOCUMENTOREQUERIDOSOLICITUD_CONFIGURACIONSOLICITUD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ConfiguracionSolicitud configuracionSolicitud = null;
}