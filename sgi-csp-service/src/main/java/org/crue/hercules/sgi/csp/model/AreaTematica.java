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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "area_tematica")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaTematica extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "area_tematica_seq")
  @SequenceGenerator(name = "area_tematica_seq", sequenceName = "area_tematica_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  /** Si tiene padre equivale a abreviatura requerido size 5 y único */
  @Column(name = "nombre", length = 50, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String nombre;

  /** Descripción */
  /** Si tiene padre equivale a nombre requerido size 50 y único */
  @Column(name = "descripcion", length = 250, nullable = true)
  @Size(max = 250)
  private String descripcion;

  /** Area Tematica padre. */
  @ManyToOne
  @JoinColumn(name = "area_tematica_padre_id", nullable = true, foreignKey = @ForeignKey(name = "FK_AREATEMATICA_PADRE"))
  private AreaTematica padre;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}