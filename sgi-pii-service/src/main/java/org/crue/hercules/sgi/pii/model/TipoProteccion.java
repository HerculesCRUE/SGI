package org.crue.hercules.sgi.pii.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;
import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
import org.crue.hercules.sgi.pii.validation.UniqueNombreTipoProteccion;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@UniqueNombreTipoProteccion(groups = { BaseEntity.Create.class, BaseEntity.Update.class,
    BaseActivableEntity.OnActivar.class })
@ActivableIsActivo(entityClass = TipoProteccion.class, groups = { BaseEntity.Update.class })
public class TipoProteccion extends BaseActivableEntity {
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
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_proteccion_nombre", joinColumns = @JoinColumn(name = "tipo_proteccion_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<TipoProteccionNombre> nombre = new HashSet<>();

  /** Descripcion */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_proteccion_descripcion", joinColumns = @JoinColumn(name = "tipo_proteccion_id"))
  @Valid
  @Builder.Default
  private Set<TipoProteccionDescripcion> descripcion = new HashSet<>();

  /** TipoProteccion padre */
  @ManyToOne
  @JoinColumn(name = "tipo_proteccion_padre_id", nullable = true, foreignKey = @ForeignKey(name = "FK_TIPO_PROTECCION_PADRE"))
  private TipoProteccion padre;

  /** Tipo de Propiedad */
  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_propiedad", length = 20, nullable = false)
  private TipoPropiedad tipoPropiedad;

}
