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
@Table(name = "proyecto_socio_periodo_justificacion_documento")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoSocioPeriodoJustificacionDocumento extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_socio_periodo_justificacion_documento_seq")
  @SequenceGenerator(name = "proyecto_socio_periodo_justificacion_documento_seq", sequenceName = "proyecto_socio_periodo_justificacion_documento_seq", allocationSize = 1)
  private Long id;

  /** ProyectoSocioPeriodoJustificacion Id */
  @Column(name = "proyecto_socio_periodo_justificacion_id", nullable = false)
  @NotNull
  private Long proyectoSocioPeriodoJustificacionId;

  /** Nombre. */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "proyecto_socio_periodo_justificacion_documento_nombre", joinColumns = @JoinColumn(name = "proyecto_socio_periodo_justificacion_documento_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<ProyectoSocioPeriodoJustificacionDocumentoNombre> nombre = new HashSet<>();

  /** Referemcoa documento. */
  @Column(name = "documento_ref", length = 250, nullable = false)
  @Size(max = 250)
  @NotNull
  private String documentoRef;

  /** Tipo Documento */
  @ManyToOne
  @JoinColumn(name = "tipo_documento_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTOSOCIOPERIODOJUSTIFICACIONDOCUMENTO_TIPODOCUMENTO"))
  private TipoDocumento tipoDocumento;

  /** Comentario. */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "proyecto_socio_periodo_justificacion_documento_comentario", joinColumns = @JoinColumn(name = "proyecto_socio_periodo_justificacion_documento_id"))
  @Valid
  @Builder.Default
  private Set<ProyectoSocioPeriodoJustificacionDocumentoComentario> comentario = new HashSet<>();

  /** Visible */
  @Column(name = "visible", nullable = false)
  @NotNull
  private Boolean visible;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_socio_periodo_justificacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOSOCIOPERIODOJUSTIFICACIONDOCUMENTO_PROYECTOSOCIOPERIODOJUSTIFICACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = null;
}