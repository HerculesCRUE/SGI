package org.crue.hercules.sgi.pii.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;
import org.crue.hercules.sgi.pii.model.Invencion.OnActualizar;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "invencion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@ActivableIsActivo(entityClass = Invencion.class, groups = { OnActualizar.class })
public class Invencion extends BaseActivableEntity {

  public static final int REF_LENGTH = 50;
  public static final int TITULO_LENGTH = 250;
  public static final int LONG_TEXT_LENGTH = 2000;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invencion_seq")
  @SequenceGenerator(name = "invencion_seq", sequenceName = "invencion_seq", allocationSize = 1)
  private Long id;

  /** Título */
  @Column(name = "titulo", length = TITULO_LENGTH, nullable = false)
  private String titulo;

  /** Fecha Comunicación. */
  @Column(name = "fecha_comunicacion", nullable = false)
  private Instant fechaComunicacion;

  /** Descripcion */
  @Column(name = "descripcion", length = LONG_TEXT_LENGTH, nullable = false)
  private String descripcion;

  /** Comentarios */
  @Column(name = "comentarios", length = LONG_TEXT_LENGTH, nullable = true)
  private String comentarios;

  /** Proyecto ref */
  @Column(name = "proyecto_ref", length = REF_LENGTH, nullable = true)
  private String proyectoRef;

  /** Tipo de protección. */
  @ManyToOne
  @JoinColumn(name = "tipo_proteccion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_INVENCION_TIPOPROTECCION"))
  @ActivableIsActivo(entityClass = TipoProteccion.class, groups = { OnCrear.class, OnActualizarTipoProteccion.class })
  private TipoProteccion tipoProteccion;

  // Relation mappings for JPA metamodel generation only
  @OneToMany(mappedBy = "invencion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<InvencionSectorAplicacion> sectoresAplicacion = null;

  @OneToMany(mappedBy = "invencion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<InvencionInventor> inventores = null;

  @OneToMany(mappedBy = "invencion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudProteccion> solicitudesProteccion = null;

  @OneToMany(mappedBy = "invencion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<InvencionPalabraClave> palabrasClave = null;

  @OneToMany(mappedBy = "invencion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<InvencionAreaConocimiento> areasConocimiento = null;

  @OneToMany(mappedBy = "invencion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<PeriodoTitularidad> periodosTitularidad = null;

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
   * TipoProteccion de la entidad.
   */
  public interface OnActualizarTipoProteccion {
  }
}
