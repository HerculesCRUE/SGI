package org.crue.hercules.sgi.pii.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;
import org.crue.hercules.sgi.pii.validation.UniqueNombreSectorAplicacionActiva;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "sector_aplicacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@UniqueNombreSectorAplicacionActiva(groups = { BaseEntity.Create.class, BaseEntity.Update.class,
    BaseActivableEntity.OnActivar.class })
@ActivableIsActivo(entityClass = SectorAplicacion.class, groups = { BaseEntity.Update.class })
public class SectorAplicacion extends BaseActivableEntity {
  /*
   * 
   */
  private static final long serialVersionUID = 1L;

  public static final int NOMBRE_LENGTH = 50;
  public static final int DESCRIPCION_LENGTH = 250;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sector_aplicacion_seq")
  @SequenceGenerator(name = "sector_aplicacion_seq", sequenceName = "sector_aplicacion_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  /** Si tiene padre equivale a abreviatura requerido size 5 y único */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "sector_aplicacion_nombre", joinColumns = @JoinColumn(name = "sector_aplicacion_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<SectorAplicacionNombre> nombre = new HashSet<>();

  /** Descripción */
  /** Si tiene padre equivale a nombre requerido size 50 y único */

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "sector_aplicacion_descripcion", joinColumns = @JoinColumn(name = "sector_aplicacion_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<SectorAplicacionDescripcion> descripcion = new HashSet<>();

}
