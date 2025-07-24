package org.crue.hercules.sgi.csp.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
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

  /** Titulo trabajo */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "solicitud_rrhh_titulo_trabajo", joinColumns = @JoinColumn(name = "solicitud_rrhh_id"))
  @Valid
  @Builder.Default
  private Set<SolicitudRrhhTituloTrabajo> tituloTrabajo = new HashSet<>();

  /** Resumen */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "solicitud_rrhh_resumen", joinColumns = @JoinColumn(name = "solicitud_rrhh_id"))
  @Valid
  @Builder.Default
  private Set<SolicitudRrhhResumen> resumen = new HashSet<>();

  /** Observaciones */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "solicitud_rrhh_observaciones", joinColumns = @JoinColumn(name = "solicitud_rrhh_id"))
  @Valid
  @Builder.Default
  private Set<SolicitudRrhhObservaciones> observaciones = new HashSet<>();

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDRRHH_SOLICITUD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Solicitud solicitud = null;

}
