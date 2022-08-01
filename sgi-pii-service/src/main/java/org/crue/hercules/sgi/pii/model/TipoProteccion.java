package org.crue.hercules.sgi.pii.model;

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

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;
import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
import org.crue.hercules.sgi.pii.model.TipoProteccion.OnActivar;
import org.crue.hercules.sgi.pii.model.TipoProteccion.OnActualizar;
import org.crue.hercules.sgi.pii.model.TipoProteccion.OnCrear;
import org.crue.hercules.sgi.pii.validation.UniqueNombreTipoProteccion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tipo_proteccion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@UniqueNombreTipoProteccion(groups = { OnActualizar.class, OnActivar.class, OnCrear.class })
@ActivableIsActivo(entityClass = TipoProteccion.class, groups = { OnActualizar.class })
public class TipoProteccion extends BaseActivableEntity {
  /*
   * 
   */
  private static final long serialVersionUID = 1L;

  public static final int NOMBRE_LENGTH = 50;
  public static final int DESCRIPCION_LENGTH = 250;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_proteccion_seq")
  @SequenceGenerator(name = "tipo_proteccion_seq", sequenceName = "tipo_proteccion_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @Column(name = "nombre", length = TipoProteccion.NOMBRE_LENGTH, nullable = false)
  private String nombre;

  /** Descripcion */
  @Column(name = "descripcion", length = TipoProteccion.DESCRIPCION_LENGTH, nullable = true)
  private String descripcion;

  /** TipoProteccion padre */
  @ManyToOne
  @JoinColumn(name = "tipo_proteccion_padre_id", nullable = true, foreignKey = @ForeignKey(name = "FK_TIPO_PROTECCION_PADRE"))
  private TipoProteccion padre;

  /** Tipo de Propiedad */
  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_propiedad", length = 20, nullable = false)
  private TipoPropiedad tipoPropiedad;

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
