package org.crue.hercules.sgi.eer.model;

import java.time.Instant;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.ScriptAssert;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = EmpresaAdministracionSociedad.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Validacion de fechas
@ScriptAssert(lang = "spel", alias = "_this", script = "#_this.getFechaInicio() == null || #_this.getFechaFin() == null || #_this.getFechaFin().compareTo(#_this.getFechaInicio()) >= 0", reportOn = "fechaFin", message = "{org.crue.hercules.sgi.eer.validation.FechaInicialMayorFechaFinal.message}")
public class EmpresaAdministracionSociedad extends BaseEntity {

  protected static final String TABLE_NAME = "empresa_administracion_sociedad";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int REF_LENGTH = 50;
  public static final int PARTICIPACION_MIN = 0;
  public static final int PARTICIPACION_MAX = 100;
  public static final int TIPO_ADMINISTRACION_LENGTH = 30;

  /** Enumerado Tipo de Administración */
  public enum TipoAdministracion {
    /** ADMINISTRADOR_UNICO */
    ADMINISTRADOR_UNICO,
    /** ADMINISTRADOR_SOLIDARIO */
    ADMINISTRADOR_SOLIDARIO,
    /** ADMINISTRADOR_MANCOMUNADO */
    ADMINISTRADOR_MANCOMUNADO,
    /** CONSEJO_ADMINISTRACION */
    CONSEJO_ADMINISTRACION;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EmpresaAdministracionSociedad.SEQUENCE_NAME)
  @SequenceGenerator(name = EmpresaAdministracionSociedad.SEQUENCE_NAME, sequenceName = EmpresaAdministracionSociedad.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Miembro equipo administracion ref */
  @Column(name = "miembro_equipo_administracion_ref", length = REF_LENGTH, nullable = false)
  @Size(max = REF_LENGTH)
  @NotNull
  private String miembroEquipoAdministracionRef;

  /** Fecha inicio */
  @Column(name = "fecha_inicio", nullable = false)
  @NotNull
  private Instant fechaInicio;

  /** Fecha fin */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  /** Tipo administracion */
  @Column(name = "tipo_administracion", length = TIPO_ADMINISTRACION_LENGTH, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private TipoAdministracion tipoAdministracion;

  /** Empresa */
  @Column(name = "empresa_id", nullable = false)
  @NotNull
  private Long empresaId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "empresa_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_EMPRESAADMINISTRACIONSOCIEDAD_EMPRESA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Empresa empresa = null;

  /**
   * Interfaz para marcar validaciones al eliminar la entidad
   */
  public interface OnDelete {

  }

}
