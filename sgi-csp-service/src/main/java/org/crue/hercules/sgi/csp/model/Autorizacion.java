package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
@Table(name = Autorizacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Autorizacion extends BaseEntity {

  protected static final String TABLE_NAME = "autorizacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";
  public static final int MAX_LENGTH = 250;

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = Autorizacion.SEQUENCE_NAME)
  @SequenceGenerator(name = Autorizacion.SEQUENCE_NAME, sequenceName = Autorizacion.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Observaciones */
  @Column(name = "observaciones", length = Autorizacion.MAX_LENGTH, nullable = true)
  @Size(max = Autorizacion.MAX_LENGTH)
  private String observaciones;

  /** Responsable Ref */
  @Column(name = "responsable_ref", length = Autorizacion.MAX_LENGTH, nullable = true)
  @Size(max = Autorizacion.MAX_LENGTH)
  private String responsableRef;

  /** Solicitante Ref */
  @Column(name = "solicitante_ref", nullable = true)
  @Size(max = Autorizacion.MAX_LENGTH)
  private String solicitanteRef;

  /** Titulo Proyecto */
  @Column(name = "titulo_proyecto", length = Autorizacion.MAX_LENGTH, nullable = true)
  @Size(max = Autorizacion.MAX_LENGTH)
  private String tituloProyecto;

  /** Entidad Ref */
  @Column(name = "entidad_ref", nullable = true)
  private String entidadRef;

  /** Horas Dedicacion Ref */
  @Column(name = "horas_dedicacion", nullable = true)
  private Integer horasDedicacion;

  /** Datos Responsable */
  @Column(name = "datos_responsable", length = Autorizacion.MAX_LENGTH, nullable = true)
  @Size(max = Autorizacion.MAX_LENGTH)
  private String datosResponsable;

  /** Datos Entidad */
  @Column(name = "datos_entidad", length = Autorizacion.MAX_LENGTH, nullable = true)
  @Size(max = Autorizacion.MAX_LENGTH)
  private String datosEntidad;

  /** Datos Convocatoria */
  @Column(name = "datos_convocatoria", length = Autorizacion.MAX_LENGTH, nullable = true)
  @Size(max = Autorizacion.MAX_LENGTH)
  private String datosConvocatoria;

  /** Convocatoria */
  @Column(name = "convocatoria_id", nullable = true)
  private Long convocatoriaId;

  /** Estado Autorizacion */
  @ManyToOne
  @JoinColumn(name = "estado_id", foreignKey = @ForeignKey(name = "FK_AUTORIZACION_AUTORIZACIONESTADO"))
  private EstadoAutorizacion estado;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_AUTORIZACION_CONVOCATORIA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Convocatoria convocatoria = null;

  @OneToOne(mappedBy = "autorizacion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final NotificacionProyectoExternoCVN notificacionProyectoExternoCvn = null;

}
