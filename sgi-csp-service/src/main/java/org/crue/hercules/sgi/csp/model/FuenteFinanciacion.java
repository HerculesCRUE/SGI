package org.crue.hercules.sgi.csp.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.crue.hercules.sgi.csp.model.FuenteFinanciacion.OnActivar;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion.OnActualizar;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion.OnCrear;
import org.crue.hercules.sgi.csp.validation.UniqueNombreFuenteFinanciacionActiva;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

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
@ActivableIsActivo(entityClass = FuenteFinanciacion.class, groups = { OnActualizar.class })
public class FuenteFinanciacion extends BaseActivableEntity {

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fuente_financiacion_seq")
  @SequenceGenerator(name = "fuente_financiacion_seq", sequenceName = "fuente_financiacion_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "fuente_financiacion_nombre", joinColumns = @JoinColumn(name = "fuente_financiacion_id"))
  @NotEmpty
  @Valid
  private Set<FuenteFinanciacionNombre> nombre = new HashSet<>();

  /** Descripcion */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "fuente_financiacion_descripcion", joinColumns = @JoinColumn(name = "fuente_financiacion_id"))
  @Valid
  private Set<FuenteFinanciacionDescripcion> descripcion = new HashSet<>();

  /** Fondo estructural */
  @Column(name = "fondo_estructural", nullable = false)
  private Boolean fondoEstructural;

  /** Tipo ambito geografico. */
  @ManyToOne
  @JoinColumn(name = "tipo_ambito_geografico_id", nullable = false, foreignKey = @ForeignKey(name = "FK_FUENTEFINANCIACION_TIPOAMBITOGEOGRAFICO"))
  @Valid
  @ActivableIsActivo(entityClass = TipoAmbitoGeografico.class, groups = { OnCrear.class,
      OnActualizarTipoAmbitoGeografico.class })
  private TipoAmbitoGeografico tipoAmbitoGeografico;

  /** Tipo origen fuente financiacion. */
  @ManyToOne
  @JoinColumn(name = "tipo_origen_fuente_financiacion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_FUENTEFINANCIACION_TIPOORIGENFUENTEFINANCIACION"))
  @Valid
  @ActivableIsActivo(entityClass = TipoOrigenFuenteFinanciacion.class, groups = { OnCrear.class,
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