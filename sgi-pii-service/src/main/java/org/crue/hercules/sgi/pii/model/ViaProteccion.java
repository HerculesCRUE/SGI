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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;
import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
import org.crue.hercules.sgi.pii.validation.UniqueNombreViaProteccion;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@UniqueNombreViaProteccion(groups = { BaseEntity.Update.class, BaseActivableEntity.OnActivar.class,
    BaseEntity.Create.class })
@ActivableIsActivo(entityClass = ViaProteccion.class, groups = { BaseEntity.Update.class })
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

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "via_proteccion_nombre", joinColumns = @JoinColumn(name = "via_proteccion_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<ViaProteccionNombre> nombre = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "via_proteccion_descripcion", joinColumns = @JoinColumn(name = "via_proteccion_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<ViaProteccionDescripcion> descripcion = new HashSet<>();

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

}
