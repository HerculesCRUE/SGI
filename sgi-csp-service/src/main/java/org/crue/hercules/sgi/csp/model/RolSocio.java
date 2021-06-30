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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rol_socio")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolSocio extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rol_socio_seq")
  @SequenceGenerator(name = "rol_socio_seq", sequenceName = "rol_socio_seq", allocationSize = 1)
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

  /** Coordinador */
  @Column(name = "coordinador", columnDefinition = "boolean default false", nullable = true)
  private Boolean coordinador;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}