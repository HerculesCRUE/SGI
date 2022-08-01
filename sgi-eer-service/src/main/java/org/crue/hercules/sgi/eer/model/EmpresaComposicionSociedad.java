package org.crue.hercules.sgi.eer.model;

import java.math.BigDecimal;
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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.eer.validation.MiembroSociedadEmpresaOrMiembroSociedadPersonaEmpresaComposicionSociedadNotNull;
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
@Table(name = EmpresaComposicionSociedad.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Validacion de fechas
@ScriptAssert(lang = "spel", alias = "_this", script = "#_this.getFechaInicio() == null || #_this.getFechaFin() == null || #_this.getFechaFin().compareTo(#_this.getFechaInicio()) >= 0", reportOn = "fechaFin", message = "{org.crue.hercules.sgi.eer.validation.FechaInicialMayorFechaFinal.message}")
@MiembroSociedadEmpresaOrMiembroSociedadPersonaEmpresaComposicionSociedadNotNull(groups = { BaseEntity.Create.class,
    BaseEntity.Update.class })
public class EmpresaComposicionSociedad extends BaseEntity {

  protected static final String TABLE_NAME = "empresa_composicion_sociedad";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int REF_LENGTH = 50;
  public static final int PARTICIPACION_MIN = 0;
  public static final int PARTICIPACION_MAX = 100;
  public static final int TIPO_APORTACION_LENGTH = 20;

  /** Enumerado Tipo de Aportación */
  public enum TipoAportacion {
    /** DINERARIA */
    DINERARIA,
    /** NO_DINERARIA */
    NO_DINERARIA;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EmpresaComposicionSociedad.SEQUENCE_NAME)
  @SequenceGenerator(name = EmpresaComposicionSociedad.SEQUENCE_NAME, sequenceName = EmpresaComposicionSociedad.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Miembro sociedad persona ref */
  @Column(name = "miembro_sociedad_persona_ref", length = REF_LENGTH, nullable = true)
  @Size(max = REF_LENGTH)
  private String miembroSociedadPersonaRef;

  /** Miembro sociedad empresa ref */
  @Column(name = "miembro_sociedad_empresa_ref", length = REF_LENGTH, nullable = true)
  @Size(max = REF_LENGTH)
  private String miembroSociedadEmpresaRef;

  /** Fecha inicio */
  @Column(name = "fecha_inicio", nullable = false)
  @NotNull
  private Instant fechaInicio;

  /** Fecha fin */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  /** Participacion */
  @Column(name = "participacion", nullable = false)
  @Min(PARTICIPACION_MIN)
  @Max(PARTICIPACION_MAX)
  @NotNull
  private BigDecimal participacion;

  /** Tipo aportación */
  @Column(name = "tipo_aportacion", length = TIPO_APORTACION_LENGTH, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private TipoAportacion tipoAportacion;

  /** Capital social */
  @Column(name = "capital_social", nullable = true)
  private BigDecimal capitalSocial;

  /** Empresa */
  @Column(name = "empresa_id", nullable = false)
  @NotNull
  private Long empresaId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "empresa_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_EMPRESACOMPOSICIONSOCIEDAD_EMPRESA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Empresa empresa = null;

}
