package org.crue.hercules.sgi.eer.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.eer.validation.EntidadEmpresaOrNombreRazonSocialEmpresaNotNull;
import org.crue.hercules.sgi.eer.validation.ValidateEstado;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = Empresa.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ActivableIsActivo(entityClass = Empresa.class, groups = { BaseEntity.Update.class })
@EntidadEmpresaOrNombreRazonSocialEmpresaNotNull(groups = { BaseEntity.Update.class, BaseEntity.Create.class })
@ValidateEstado(groups = { BaseEntity.Update.class, BaseEntity.Create.class })
public class Empresa extends BaseActivableEntity {

  protected static final String TABLE_NAME = "empresa";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int NOMBRE_RAZON_SOCIAL_LENGTH = 250;
  public static final int OBJETO_SOCIAL_LENGTH = 1000;
  public static final int DESCRIPCION_LENGTH = 250;
  public static final int REFERENCIAS_LENGTH = 50;
  public static final int TIPO_EMPRESA_LENGTH = 10;
  public static final int CONOCIMIENTO_TECNOLOGIA_LENGTH = 1000;
  public static final int NUMERO_PROTOCOLO_LENGTH = 50;
  public static final int NOTARIO_LENGTH = 250;
  public static final int OBSERVACIONES_LENGTH = 2000;
  public static final int ESTADO_EMPRESA_LENGTH = 20;

  /** Enumerado con los estados de la Empresa */
  public enum EstadoEmpresa {
    /** EN TRAMITACIÓN */
    EN_TRAMITACION,
    /** NO APROBADA */
    NO_APROBADA,
    /** ACTIVA */
    ACTIVA,
    /** SIN ACTIVIDAD */
    SIN_ACTIVIDAD,
    /** DISUELTA */
    DISUELTA;
  }

  /** Enumerado Tipo de Empresa */
  public enum TipoEmpresa {
    /** EBT */
    EBT,
    /** EINCNT */
    EINCNT;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = Empresa.SEQUENCE_NAME)
  @SequenceGenerator(name = Empresa.SEQUENCE_NAME, sequenceName = Empresa.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Fecha solicitud */
  @Column(name = "fecha_solicitud", nullable = false)
  @NotNull
  private Instant fechaSolicitud;

  /** Tipo empresa */
  @Column(name = "tipo_empresa", length = Empresa.TIPO_EMPRESA_LENGTH, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private TipoEmpresa tipoEmpresa;

  /** SolicitanteRef */
  @Column(name = "solicitante_ref", length = Empresa.REFERENCIAS_LENGTH, nullable = true)
  @Size(max = Empresa.REFERENCIAS_LENGTH)
  private String solicitanteRef;

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "empresa_nombre_razon_social", joinColumns = @JoinColumn(name = "empresa_id"))
  @Valid
  @Builder.Default
  private Set<EmpresaNombreRazonSocial> nombreRazonSocial = new HashSet<>();

  /** EntidadRef */
  @Column(name = "entidad_ref", length = Empresa.REFERENCIAS_LENGTH, nullable = true)
  @Size(max = Empresa.REFERENCIAS_LENGTH)
  private String entidadRef;

  /** Objeto social */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "empresa_objeto_social", joinColumns = @JoinColumn(name = "empresa_id"))
  @Valid
  @NotEmpty
  @Builder.Default
  private Set<EmpresaObjetoSocial> objetoSocial = new HashSet<>();

  /** Conocimiento tecnologica */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "empresa_conocimiento_tecnologia", joinColumns = @JoinColumn(name = "empresa_id"))
  @Valid
  @NotEmpty
  @Builder.Default
  private Set<EmpresaConocimientoTecnologia> conocimientoTecnologia = new HashSet<>();

  /** Número protocolo */
  @Column(name = "numero_protocolo", length = Empresa.NUMERO_PROTOCOLO_LENGTH, nullable = true)
  @Size(max = Empresa.NUMERO_PROTOCOLO_LENGTH)
  private String numeroProtocolo;

  /** Notario */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "empresa_notario", joinColumns = @JoinColumn(name = "empresa_id"))
  @Valid
  @Builder.Default
  private Set<EmpresaNotario> notario = new HashSet<>();

  /** Fecha constitución */
  @Column(name = "fecha_constitucion", nullable = true)
  private Instant fechaConstitucion;

  /** Fecha aprobación CG */
  @Column(name = "fecha_aprobacion_cg", nullable = true)
  private Instant fechaAprobacionCG;

  /** Fecha incorporación */
  @Column(name = "fecha_incorporacion", nullable = true)
  private Instant fechaIncorporacion;

  /** Fecha desvinculación */
  @Column(name = "fecha_desvinculacion", nullable = true)
  private Instant fechaDesvinculacion;

  /** Fecha cese */
  @Column(name = "fecha_cese", nullable = true)
  private Instant fechaCese;

  /** Observaciones */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "empresa_observaciones", joinColumns = @JoinColumn(name = "empresa_id"))
  @Valid
  @Builder.Default
  private Set<EmpresaObservaciones> observaciones = new HashSet<>();

  /** Estado empresa */
  @Column(name = "estado", length = Empresa.ESTADO_EMPRESA_LENGTH, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private EstadoEmpresa estado;

  @OneToMany(mappedBy = "empresa")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<EmpresaEquipoEmprendedor> miembrosEquipoEmprendedor = null;

  @OneToMany(mappedBy = "empresa")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<EmpresaComposicionSociedad> composicionSociedad = null;

  @OneToMany(mappedBy = "empresa")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<EmpresaAdministracionSociedad> administradores = null;

  @OneToMany(mappedBy = "empresa")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<EmpresaDocumento> documentos = null;

}
