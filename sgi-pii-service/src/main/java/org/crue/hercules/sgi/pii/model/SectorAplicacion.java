package org.crue.hercules.sgi.pii.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;
import org.crue.hercules.sgi.pii.model.SectorAplicacion.OnActivar;
import org.crue.hercules.sgi.pii.model.SectorAplicacion.OnActualizar;
import org.crue.hercules.sgi.pii.model.SectorAplicacion.OnCrear;
import org.crue.hercules.sgi.pii.validation.UniqueNombreSectorAplicacionActiva;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "sector_aplicacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@UniqueNombreSectorAplicacionActiva(groups = { OnActualizar.class, OnActivar.class, OnCrear.class })
@ActivableIsActivo(entityClass = SectorAplicacion.class, groups = { OnActualizar.class })
public class SectorAplicacion extends BaseActivableEntity {
  public static final int NOMBRE_LENGTH = 50;
  public static final int DESCRIPCION_LENGTH = 250;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sector_aplicacion_seq")
  @SequenceGenerator(name = "sector_aplicacion_seq", sequenceName = "sector_aplicacion_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  /** Si tiene padre equivale a abreviatura requerido size 5 y único */
  @Column(name = "nombre", length = NOMBRE_LENGTH, nullable = false)
  private String nombre;

  /** Descripción */
  /** Si tiene padre equivale a nombre requerido size 50 y único */
  @Column(name = "descripcion", length = DESCRIPCION_LENGTH, nullable = false)
  private String descripcion;

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
}
