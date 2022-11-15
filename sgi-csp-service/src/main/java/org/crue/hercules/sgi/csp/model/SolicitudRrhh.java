package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = SolicitudRrhh.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRrhh extends BaseEntity {

  protected static final String TABLE_NAME = "solicitud_rrhh";

  public static final int ENTIDAD_REF_LENGTH = 50;
  public static final int OBSERVACIONES_LENGTH = 4000;
  public static final int RESUMEN_LENGTH = 4000;
  public static final int TITULO_TRABAJO_LENGTH = 1000;
  public static final int UNIVERSIDAD_LENGTH = 250;

  /** Id de la Solicitud */
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  /** Universidad Ref */
  @Column(name = "universidad_ref", length = SolicitudRrhh.ENTIDAD_REF_LENGTH, nullable = true)
  @Size(max = SolicitudRrhh.ENTIDAD_REF_LENGTH)
  private String universidadRef;

  /** Area ANEP Ref */
  @Column(name = "area_anep_ref", length = SolicitudRrhh.ENTIDAD_REF_LENGTH, nullable = true)
  @Size(max = SolicitudRrhh.ENTIDAD_REF_LENGTH)
  private String areaAnepRef;

  /** Universidad */
  @Column(name = "universidad", length = SolicitudRrhh.UNIVERSIDAD_LENGTH, nullable = true)
  @Size(max = SolicitudRrhh.UNIVERSIDAD_LENGTH)
  private String universidad;

  /** Tutor Ref */
  @Column(name = "tutor_ref", length = SolicitudRrhh.ENTIDAD_REF_LENGTH, nullable = true)
  @Size(max = SolicitudRrhh.ENTIDAD_REF_LENGTH)
  private String tutorRef;

  /** Titulo tarbajo */
  @Column(name = "titulo_trabajo", length = SolicitudRrhh.TITULO_TRABAJO_LENGTH, nullable = true)
  @Size(max = SolicitudRrhh.TITULO_TRABAJO_LENGTH)
  private String tituloTrabajo;

  /** Resumen */
  @Column(name = "resumen", length = SolicitudRrhh.RESUMEN_LENGTH, nullable = true)
  @Size(max = SolicitudRrhh.RESUMEN_LENGTH)
  private String resumen;

  /** Observaciones */
  @Column(name = "observaciones", length = SolicitudRrhh.OBSERVACIONES_LENGTH, nullable = true)
  @Size(max = SolicitudRrhh.OBSERVACIONES_LENGTH)
  private String observaciones;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDRRHH_SOLICITUD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Solicitud solicitud = null;

}
