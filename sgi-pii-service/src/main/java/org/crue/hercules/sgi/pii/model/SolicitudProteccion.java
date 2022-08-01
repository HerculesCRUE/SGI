package org.crue.hercules.sgi.pii.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion.OnActivar;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion.OnActualizar;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion.OnCrear;
import org.crue.hercules.sgi.pii.validation.UniqueSolicitudViaProteccion;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = SolicitudProteccion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@UniqueSolicitudViaProteccion(groups = { OnActualizar.class, OnActivar.class, OnCrear.class })
@ActivableIsActivo(entityClass = SolicitudProteccion.class, groups = { OnActualizar.class })
public class SolicitudProteccion extends BaseActivableEntity {
  /*
   * 
   */
  private static final long serialVersionUID = 1L;

  protected static final String TABLE_NAME = "solicitud_proteccion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int TITULO_MAX_LENGTH = 250;
  public static final int NUMERO_SOLICITUD_MAX_LENGTH = 24;
  public static final int NUMERO_PUBLICACION_MAX_LENGTH = 24;
  public static final int NUMERO_CONCESION_MAX_LENGTH = 24;
  public static final int NUMERO_REGISTRO_MAX_LENGTH = 24;
  public static final int COMENTARIOS_MAX_LENGTH = 500;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "invencion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROTECCION_INVENCION"))
  @Valid
  @ActivableIsActivo(entityClass = Invencion.class, groups = { OnCrear.class, OnActualizarInvencion.class })
  private Invencion invencion;

  @Column(name = "titulo", length = TITULO_MAX_LENGTH, nullable = false)
  private String titulo;

  @Column(name = "fecha_prioridad_solicitud", nullable = false)
  private Instant fechaPrioridadSolicitud;

  @Column(name = "fecha_fin_prior_pres_fas_nac_rec", nullable = true)
  private Instant fechaFinPriorPresFasNacRec;

  @Column(name = "fecha_publicacioin", nullable = true)
  private Instant fechaPublicacion;

  @Column(name = "fecha_concesion", nullable = true)
  private Instant fechaConcesion;

  @Column(name = "fecha_caducidad", nullable = true)
  private Instant fechaCaducidad;

  @ManyToOne
  @JoinColumn(name = "via_proteccion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROTECCION_VIAPROTECCION"))
  @Valid
  @ActivableIsActivo(entityClass = ViaProteccion.class, groups = { OnCrear.class, OnActualizarViaProteccion.class })
  private ViaProteccion viaProteccion;

  @Column(name = "numero_solicitud", nullable = false, length = NUMERO_SOLICITUD_MAX_LENGTH)
  private String numeroSolicitud;

  @Column(name = "numero_publicacion", length = NUMERO_PUBLICACION_MAX_LENGTH, nullable = true)
  private String numeroPublicacion;

  @Column(name = "numero_concesion", length = NUMERO_CONCESION_MAX_LENGTH, nullable = true)
  private String numeroConcesion;

  @Column(name = "numero_registro", length = NUMERO_REGISTRO_MAX_LENGTH, nullable = true)
  private String numeroRegistro;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false)
  private EstadoSolicitudProteccion estado;

  @ManyToOne
  @JoinColumn(name = "tipo_caducidad_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROTECCION_TIPOCADUCIDAD"))
  private TipoCaducidad tipoCaducidad;

  @Column(name = "agente_propiedad_ref", nullable = true)
  private String agentePropiedadRef;

  @Column(name = "pais_proteccion_ref", nullable = true)
  private String paisProteccionRef;

  @Column(name = "comentarios", length = COMENTARIOS_MAX_LENGTH, nullable = true)
  private String comentarios;

  @OneToMany(mappedBy = "solicitudProteccion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Procedimiento> procedimientos = null;

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

  /**
   * Interfaz para marcar validaciones en las activaciones de la entidad.
   */
  public interface OnActivar {
  }

  /**
   * Interfaz para marcar validaciones en la actualizacion del campo viaProteccion
   * de la entidad.
   */
  public interface OnActualizarViaProteccion {
  }

  /**
   * Interfaz para marcar validaciones en la actualizacion del campo invencion de
   * la entidad.
   */
  public interface OnActualizarInvencion {
  }

  @PrePersist
  public void setup() {
    this.estado = EstadoSolicitudProteccion.SOLICITADA;
  }

  public enum EstadoSolicitudProteccion {
    SOLICITADA, PUBLICADA, CONCEDIDA, CADUCADA
  }
}
