package org.crue.hercules.sgi.csp.model;

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
import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.FuenteFinanciacion.OnActivar;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion.OnActualizar;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion.OnCrear;
import org.crue.hercules.sgi.csp.validation.EntidadActiva;
import org.crue.hercules.sgi.csp.validation.UniqueNombreFuenteFinanciacionActiva;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "fuente_financiacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@UniqueNombreFuenteFinanciacionActiva(groups = { OnActualizar.class, OnActivar.class, OnCrear.class })
@EntidadActiva(entityClass = FuenteFinanciacion.class, groups = { OnActualizar.class })
public class FuenteFinanciacion extends BaseActivableEntity {
  public static final int NOMBRE_LENGTH = 50;
  public static final int DESCRIPCION_LENGTH = 250;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fuente_financiacion_seq")
  @SequenceGenerator(name = "fuente_financiacion_seq", sequenceName = "fuente_financiacion_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @Column(name = "nombre", length = NOMBRE_LENGTH, nullable = false)
  private String nombre;

  /** Descripcion */
  @Column(name = "descripcion", length = DESCRIPCION_LENGTH, nullable = true)
  private String descripcion;

  /** Fondo estructural */
  @Column(name = "fondo_estructural", nullable = false)
  private Boolean fondoEstructural;

  /** Tipo ambito geografico. */
  @ManyToOne
  @JoinColumn(name = "tipo_ambito_geografico_id", nullable = false, foreignKey = @ForeignKey(name = "FK_FUENTEFINANCIACION_TIPOAMBITOGEOGRAFICO"))
  @Valid
  @EntidadActiva(entityClass = TipoAmbitoGeografico.class, groups = { OnCrear.class,
      OnActualizarTipoAmbitoGeografico.class })
  private TipoAmbitoGeografico tipoAmbitoGeografico;

  /** Tipo origen fuente financiacion. */
  @ManyToOne
  @JoinColumn(name = "tipo_origen_fuente_financiacion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_FUENTEFINANCIACION_TIPOORIGENFUENTEFINANCIACION"))
  @Valid
  @EntidadActiva(entityClass = TipoOrigenFuenteFinanciacion.class, groups = { OnCrear.class,
      OnActualizarTipoOrigenFuenteFinanciacion.class })
  private TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion;

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
   * Interfaz para marcar validaciones en la actualizacion del campo
   * TipoAmbitoGeografico de la entidad.
   */
  public interface OnActualizarTipoAmbitoGeografico {

  }

  /**
   * Interfaz para marcar validaciones en la actualizacion del campo
   * TipoOrigenFuenteFinanciacion de la entidad.
   */
  public interface OnActualizarTipoOrigenFuenteFinanciacion {

  }

  /**
   * Interfaz para marcar validaciones en las activaciones de la entidad.
   */
  public interface OnActivar {
  }

}