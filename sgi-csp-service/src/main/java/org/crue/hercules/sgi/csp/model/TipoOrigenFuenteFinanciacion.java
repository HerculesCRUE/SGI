package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.validation.UniqueNombreTipoOrigenFuenteFinanciacionActiva;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tipo_origen_fuente_financiacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@UniqueNombreTipoOrigenFuenteFinanciacionActiva(groups = {
    TipoOrigenFuenteFinanciacion.OnActualizar.class, TipoOrigenFuenteFinanciacion.OnCrear.class,
    BaseActivableEntity.OnActivar.class })
@ActivableIsActivo(entityClass = TipoOrigenFuenteFinanciacion.class, groups = {
    TipoOrigenFuenteFinanciacion.OnActualizar.class })
public class TipoOrigenFuenteFinanciacion extends BaseActivableEntity {
  public static final int NOMBRE_LENGTH = 50;

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_origen_fuente_financiacion_seq")
  @SequenceGenerator(name = "tipo_origen_fuente_financiacion_seq", sequenceName = "tipo_origen_fuente_financiacion_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @Column(name = "nombre", length = NOMBRE_LENGTH, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String nombre;

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
