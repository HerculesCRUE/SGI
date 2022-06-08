package org.crue.hercules.sgi.csp.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.BaseEntity.Create;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN.AsociarProyecto;
import org.crue.hercules.sgi.csp.validation.NotificacionProyectoExternoCvnAutorizacionState;
import org.crue.hercules.sgi.csp.validation.SameSolicitanteNotificacionCvnAutorizacion;
import org.crue.hercules.sgi.csp.validation.UniqueRelationNotificacionCvnAutorizacion;
import org.crue.hercules.sgi.csp.validation.UniqueRelationNotificacionCvnProyecto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = NotificacionProyectoExternoCVN.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SameSolicitanteNotificacionCvnAutorizacion(groups = { Create.class, AsociarProyecto.class })
@NotificacionProyectoExternoCvnAutorizacionState(groups = { Create.class, AsociarProyecto.class })
public class NotificacionProyectoExternoCVN extends BaseEntity {

  protected static final String TABLE_NAME = "notificacion_proyecto_externo_cvn";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";
  public static final int MAX_LENGTH = 250;
  public static final int MAX_PERCENTAGE = 100;
  public static final int MIN_PERCENTAGE = 0;
  public static final int MIN_IMPORTE = 0;

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = NotificacionProyectoExternoCVN.SEQUENCE_NAME)
  @SequenceGenerator(name = NotificacionProyectoExternoCVN.SEQUENCE_NAME, sequenceName = NotificacionProyectoExternoCVN.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Titulo */
  @Column(name = "titulo", length = NotificacionProyectoExternoCVN.MAX_LENGTH, nullable = false)
  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  @NotBlank
  private String titulo;

  /** Autorizacion */
  @Column(name = "autorizacion_id", nullable = true)
  @UniqueRelationNotificacionCvnAutorizacion(groups = { Create.class, AsociarAutorizacion.class })
  private Long autorizacionId;

  /** Proyecto */
  @Column(name = "proyecto_id", nullable = true)
  @UniqueRelationNotificacionCvnProyecto(groups = { Create.class, AsociarProyecto.class })
  private Long proyectoId;

  /** Ambito Geogrñafico */
  @Column(name = "ambito_geografico", length = NotificacionProyectoExternoCVN.MAX_LENGTH, nullable = true)
  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String ambitoGeografico;

  /** Código Externo */
  @Column(name = "cod_externo", nullable = true)
  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String codExterno;

  /** Datos Entidad Participacion */
  @Column(name = "datos_entidad_participacion", length = NotificacionProyectoExternoCVN.MAX_LENGTH, nullable = true)
  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String datosEntidadParticipacion;

  /** Datos Responsable */
  @Column(name = "datos_responsable", length = NotificacionProyectoExternoCVN.MAX_LENGTH, nullable = true)
  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String datosResponsable;

  /** Documento Ref */
  @Column(name = "documento_ref", length = NotificacionProyectoExternoCVN.MAX_LENGTH, nullable = true)
  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String documentoRef;

  /** Entidad Participacion Ref */
  @Column(name = "entidad_participacion_ref", nullable = true)
  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String entidadParticipacionRef;

  /** Fecha Inicio */
  @Column(name = "fecha_inicio")
  @NotNull
  private Instant fechaInicio;

  /** Fecha Fin */
  @Column(name = "fecha_fin")
  @NotNull
  private Instant fechaFin;

  /** Grado Contribucion */
  @Column(name = "grado_contribucion", length = NotificacionProyectoExternoCVN.MAX_LENGTH, nullable = true)
  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String gradoContribucion;

  /** Importe Total */
  @Column(name = "importe_total", nullable = true)
  @Min(NotificacionProyectoExternoCVN.MIN_IMPORTE)
  private Integer importeTotal;

  /** Nombre Programa */
  @Column(name = "nombre_programa", length = NotificacionProyectoExternoCVN.MAX_LENGTH, nullable = true)
  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String nombrePrograma;

  /** Importe Total */
  @Column(name = "porcentaje_subvencion", nullable = true)
  @Max(NotificacionProyectoExternoCVN.MAX_PERCENTAGE)
  @Min(NotificacionProyectoExternoCVN.MIN_PERCENTAGE)
  private Integer porcentajeSubvencion;

  /** Proyecto CVN id */
  @Column(name = "proyecto_cvn_id", nullable = false)
  @NotBlank
  private String proyectoCVNId;

  /** Responsable Ref */
  @Column(name = "responsable_ref", nullable = true)
  private String responsableRef;

  /** Solicitante Ref */
  @Column(name = "solicitante_ref", nullable = false)
  @NotBlank
  private String solicitanteRef;

  /** Url Documento acreditacion */
  @Column(name = "url_documento_acreditacion", length = NotificacionProyectoExternoCVN.MAX_LENGTH, nullable = true)
  @Size(max = NotificacionProyectoExternoCVN.MAX_LENGTH)
  private String urlDocumentoAcreditacion;

  // Relation mappings for JPA metamodel generation only
  @OneToOne
  @JoinColumn(name = "autorizacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_NOTIFICACIONPROYECTOEXTERNOCVN_AUTORIZACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Autorizacion autorizacion = null;

  @OneToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_NOTIFICACIONPROYECTOEXTERNOCVN_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "notificacionProyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<NotificacionCVNEntidadFinanciadora> notificaciones = null;

  public interface AsociarAutorizacion {
  }

  public interface AsociarProyecto {
  }

}
