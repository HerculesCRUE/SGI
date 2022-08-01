package org.crue.hercules.sgi.pii.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;
import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
import org.crue.hercules.sgi.pii.model.ViaProteccion.OnActivar;
import org.crue.hercules.sgi.pii.model.ViaProteccion.OnActualizar;
import org.crue.hercules.sgi.pii.model.ViaProteccion.OnCrear;
import org.crue.hercules.sgi.pii.validation.UniqueNombreViaProteccion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = ViaProteccion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@UniqueNombreViaProteccion(groups = { OnActualizar.class, OnActivar.class, OnCrear.class })
@ActivableIsActivo(entityClass = ViaProteccion.class, groups = { OnActualizar.class })
public class ViaProteccion extends BaseActivableEntity {
  /*
   * 
   */
  private static final long serialVersionUID = 1L;

  protected static final String TABLE_NAME = "via_proteccion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int NOMBRE_LENGTH = 50;
  public static final int DESCRIPCION_LENGTH = 250;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "nombre", length = ViaProteccion.NOMBRE_LENGTH, nullable = false)
  private String nombre;

  @Column(name = "descripcion", length = ViaProteccion.DESCRIPCION_LENGTH, nullable = true)
  private String descripcion;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_propiedad")
  TipoPropiedad tipoPropiedad;

  @Column(name = "meses_prioridad")
  Integer mesesPrioridad;

  @Column(name = "pais_especifico")
  Boolean paisEspecifico;

  @Column(name = "extension_internacional")
  Boolean extensionInternacional;

  @Column(name = "varios_paises")
  Boolean variosPaises;

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
