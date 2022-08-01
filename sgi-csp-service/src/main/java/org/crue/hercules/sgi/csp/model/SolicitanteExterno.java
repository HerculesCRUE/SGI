package org.crue.hercules.sgi.csp.model;

import java.time.Instant;

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
import javax.validation.constraints.NotBlank;
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
@Table(name = SolicitanteExterno.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitanteExterno extends BaseEntity {

  protected static final String TABLE_NAME = "solicitante_externo";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int APELLIDOS_LENGTH = 250;
  public static final int CIUDAD_LENGTH = 250;
  public static final int CODIGO_POSTAL_LENGTH = 50;
  public static final int DIRECCION_LENGTH = 250;
  public static final int EMAIL_LENGTH = 250;
  public static final int ENTIDAD_REF_LENGTH = 50;
  public static final int NOMBRE_LENGTH = 250;
  public static final int NUMERO_DOCUMENTO_LENGTH = 50;
  public static final int TELEFONO_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SolicitanteExterno.SEQUENCE_NAME)
  @SequenceGenerator(name = SolicitanteExterno.SEQUENCE_NAME, sequenceName = SolicitanteExterno.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Solicitud */
  @Column(name = "solicitud_id", nullable = false)
  @NotNull
  private Long solicitudId;

  /** Nombre */
  @Column(name = "nombre", length = SolicitanteExterno.NOMBRE_LENGTH, nullable = false)
  @Size(max = SolicitanteExterno.NOMBRE_LENGTH)
  @NotBlank
  private String nombre;

  /** Apellidos */
  @Column(name = "apellidos", length = SolicitanteExterno.APELLIDOS_LENGTH, nullable = false)
  @Size(max = SolicitanteExterno.APELLIDOS_LENGTH)
  @NotBlank
  private String apellidos;

  /** Tipo documento Ref */
  @Column(name = "tipo_documento_ref", length = SolicitanteExterno.ENTIDAD_REF_LENGTH, nullable = false)
  @Size(max = SolicitanteExterno.ENTIDAD_REF_LENGTH)
  @NotBlank
  private String tipoDocumentoRef;

  /** Numero documento */
  @Column(name = "numero_documento", length = SolicitanteExterno.NUMERO_DOCUMENTO_LENGTH, nullable = false)
  @Size(max = SolicitanteExterno.NUMERO_DOCUMENTO_LENGTH)
  @NotBlank
  private String numeroDocumento;

  /** Sexo Ref */
  @Column(name = "sexo_ref", length = SolicitanteExterno.ENTIDAD_REF_LENGTH, nullable = true)
  @Size(max = SolicitanteExterno.ENTIDAD_REF_LENGTH)
  private String sexoRef;

  /** Fecha nacimiento */
  @Column(name = "fecha_nacimiento", nullable = true)
  private Instant fechaNacimiento;

  /** Pais nacimiento Ref */
  @Column(name = "pais_nacimiento_ref", length = SolicitanteExterno.ENTIDAD_REF_LENGTH, nullable = true)
  @Size(max = SolicitanteExterno.ENTIDAD_REF_LENGTH)
  private String paisNacimientoRef;

  /** Telefono */
  @Column(name = "telefono", length = SolicitanteExterno.TELEFONO_LENGTH, nullable = true)
  @Size(max = SolicitanteExterno.TELEFONO_LENGTH)
  private String telefono;

  /** Email */
  @Column(name = "email", length = SolicitanteExterno.EMAIL_LENGTH, nullable = true)
  @Size(max = SolicitanteExterno.EMAIL_LENGTH)
  private String email;

  /** Direccion */
  @Column(name = "direccion", length = SolicitanteExterno.DIRECCION_LENGTH, nullable = true)
  @Size(max = SolicitanteExterno.DIRECCION_LENGTH)
  private String direccion;

  /** Pais contacto Ref */
  @Column(name = "pais_contacto_ref", length = SolicitanteExterno.ENTIDAD_REF_LENGTH, nullable = true)
  @Size(max = SolicitanteExterno.ENTIDAD_REF_LENGTH)
  private String paisContactoRef;

  /** Comunidad Ref */
  @Column(name = "comunidad_ref", length = SolicitanteExterno.ENTIDAD_REF_LENGTH, nullable = true)
  @Size(max = SolicitanteExterno.ENTIDAD_REF_LENGTH)
  private String comunidadRef;

  /** Provincia Ref */
  @Column(name = "provincia_ref", length = SolicitanteExterno.ENTIDAD_REF_LENGTH, nullable = true)
  @Size(max = SolicitanteExterno.ENTIDAD_REF_LENGTH)
  private String provinciaRef;

  /** Ciudad */
  @Column(name = "ciudad", length = SolicitanteExterno.CIUDAD_LENGTH, nullable = true)
  @Size(max = SolicitanteExterno.CIUDAD_LENGTH)
  private String ciudad;

  /** Codigo postal */
  @Column(name = "codigo_postal", length = SolicitanteExterno.CODIGO_POSTAL_LENGTH, nullable = true)
  @Size(max = SolicitanteExterno.CODIGO_POSTAL_LENGTH)
  private String codigoPostal;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITANTEEXTERNO_SOLICITUD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Solicitud solicitud = null;

}
