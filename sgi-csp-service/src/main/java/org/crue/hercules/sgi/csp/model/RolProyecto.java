package org.crue.hercules.sgi.csp.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.crue.hercules.sgi.csp.validation.UniqueRolPrincipalOrdenPrimarioRolProyectoActivo;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity.OnActivar;
import org.crue.hercules.sgi.csp.validation.UniqueAbreviaturaRolProyectoActivo;
import org.crue.hercules.sgi.csp.validation.UniqueNombreRolProyectoActivo;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "rol_proyecto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@UniqueNombreRolProyectoActivo(groups = { BaseEntity.Create.class,
    BaseEntity.Update.class, OnActivar.class })
@UniqueAbreviaturaRolProyectoActivo(groups = { BaseEntity.Create.class,
    BaseEntity.Update.class, OnActivar.class })
@UniqueRolPrincipalOrdenPrimarioRolProyectoActivo(groups = { BaseEntity.Create.class,
    BaseEntity.Update.class, OnActivar.class })
@ActivableIsActivo(entityClass = RolProyecto.class, groups = { BaseEntity.Update.class })
public class RolProyecto extends BaseActivableEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /**
   * Equipos.
   *
   */
  public enum Equipo {

    /** Equipo de investigación */
    INVESTIGACION,
    /** Equipo de trabajo */
    TRABAJO;
  }

  /**
   * Tipos de orden.
   *
   */
  public enum Orden {

    /** Orden primario */
    PRIMARIO,
    /** Orden Secundario */
    SECUNDARIO;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rol_proyecto_seq")
  @SequenceGenerator(name = "rol_proyecto_seq", sequenceName = "rol_proyecto_seq", allocationSize = 1)
  private Long id;

  /** Abreviatura */
  @Column(name = "abreviatura", length = 5, nullable = false)
  @NotBlank
  @Size(max = 5)
  private String abreviatura;

  /** Nombre */
  @Column(name = "nombre", length = 50, nullable = false)
  @NotBlank
  @Size(max = 50)
  private String nombre;

  /** Descripción */
  @Column(name = "descripcion", length = 250, nullable = true)
  @Size(max = 250)
  private String descripcion;

  /** Rol principal */
  @Column(name = "rol_principal", columnDefinition = "boolean default false", nullable = true)
  private Boolean rolPrincipal;

  /** Baremable PRC */
  @Column(name = "baremable_prc", columnDefinition = "boolean default false", nullable = true)
  private Boolean baremablePRC;

  /** Orden */
  @Column(name = "orden", nullable = true)
  @Enumerated(EnumType.STRING)
  private Orden orden;

  /** Tipo Formulario Solicitud */
  @Column(name = "equipo", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Equipo equipo;

  /** Listado rol proyecto colectivo */
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "rolProyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @JsonIgnore
  private final List<RolProyectoColectivo> colectivos = null;

}