package org.crue.hercules.sgi.pii.model;

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

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sector_licenciado")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorLicenciado extends BaseEntity {
  public static final int REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sector_licenciado_seq")
  @SequenceGenerator(name = "sector_licenciado_seq", sequenceName = "sector_licenciado_seq", allocationSize = 1)
  private Long id;

  /** Fecha Inicio Licencia */
  @Column(name = "fecha_inicio_licencia", nullable = false)
  private Instant fechaInicioLicencia;

  /** Fecha Fin Licencia */
  @Column(name = "fecha_fin_licencia", nullable = false)
  private Instant fechaFinLicencia;

  /** Invencion Id */
  @Column(name = "invencion_id", nullable = false)
  private Long invencionId;

  /** Sector Aplicación */
  @ManyToOne
  @JoinColumn(name = "sector_aplicacion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SECTORLICENCIADO_SECTORAPLICACION"))
  @ActivableIsActivo(entityClass = SectorAplicacion.class, groups = { OnCrear.class,
      OnActualizarSectorAplicacion.class })
  private SectorAplicacion sectorAplicacion;

  /** Referencia a un Contrato */
  @Column(name = "contrato_ref", length = REF_LENGTH, nullable = false)
  private String contratoRef;

  /** Referencia a un País */
  @Column(name = "pais_ref", length = REF_LENGTH, nullable = false)
  private String paisRef;

  /** Exclusividad */
  @Column(name = "exclusividad", nullable = false)
  private Boolean exclusividad;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "invencion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SECTORLICENCIADO_INVENCION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Invencion invencion = null;

  /**
   * Interfaz para marcar validaciones en la creacion de la entidad.
   */
  public interface OnCrear {
  }

  /**
   * Interfaz para marcar validaciones en la actualizacion de la entidad.
   */
  public interface OnActualizar {
  }

  /**
   * Interfaz para marcar validaciones en la actualizacion del campo
   * sectorAplicacion de la entidad.
   */
  public interface OnActualizarSectorAplicacion {
  }
}
