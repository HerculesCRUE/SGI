package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.validation.UniqueAbreviaturaRolSocioActivo;
import org.crue.hercules.sgi.csp.validation.UniqueNombreRolSocioActivo;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "rol_socio")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@UniqueNombreRolSocioActivo(groups = {
    RolSocio.OnActualizar.class, BaseActivableEntity.OnActivar.class,
    RolSocio.OnCrear.class })
@UniqueAbreviaturaRolSocioActivo(groups = {
    RolSocio.OnActualizar.class, BaseActivableEntity.OnActivar.class,
    RolSocio.OnCrear.class })
@ActivableIsActivo(entityClass = RolSocio.class, groups = { RolSocio.OnActualizar.class })
public class RolSocio extends BaseActivableEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  public static final int NOMBRE_LENGTH = 50;
  public static final int DESCRIPCION_LENGTH = 250;
  public static final int ABREVIATURA_LENGTH = 5;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rol_socio_seq")
  @SequenceGenerator(name = "rol_socio_seq", sequenceName = "rol_socio_seq", allocationSize = 1)
  private Long id;

  /** Abreviatura */
  @Column(name = "abreviatura", length = ABREVIATURA_LENGTH, nullable = false)
  @NotBlank
  @Size(max = 5)
  private String abreviatura;

  /** Nombre */
  @Column(name = "nombre", length = NOMBRE_LENGTH, nullable = false)
  @NotBlank
  @Size(max = 50)
  private String nombre;

  /** Descripción */
  @Column(name = "descripcion", length = DESCRIPCION_LENGTH, nullable = true)
  @Size(max = 250)
  private String descripcion;

  /** Coordinador */
  @Column(name = "coordinador", columnDefinition = "boolean default false", nullable = true)
  private Boolean coordinador;

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

}