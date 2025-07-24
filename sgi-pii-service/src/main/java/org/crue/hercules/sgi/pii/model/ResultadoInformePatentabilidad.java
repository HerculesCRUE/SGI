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
import org.crue.hercules.sgi.pii.validation.UniqueNombreResultadoInformePatentabilidad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "resultado_informe_patentabilidad")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@UniqueNombreResultadoInformePatentabilidad(groups = { BaseEntity.Create.class, BaseEntity.Update.class,
    BaseActivableEntity.OnActivar.class })
@ActivableIsActivo(entityClass = ResultadoInformePatentabilidad.class, groups = { BaseEntity.Update.class })
public class ResultadoInformePatentabilidad extends BaseActivableEntity {

  public static final int NOMBRE_LENGTH = 50;
  public static final int DESCRIPCION_LENGTH = 250;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resultado_informe_patentabilidad_seq")
  @SequenceGenerator(name = "resultado_informe_patentabilidad_seq", sequenceName = "resultado_informe_patentabilidad_seq", allocationSize = 1)
  private Long id;

  /** Nombre */

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "resultado_informe_patentabilidad_nombre", joinColumns = @JoinColumn(name = "resultado_informe_patentabilidad_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<ResultadoInformePatentabilidadNombre> nombre = new HashSet<>();

  /** Descripcion */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "resultado_informe_patentabilidad_descripcion", joinColumns = @JoinColumn(name = "resultado_informe_patentabilidad_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<ResultadoInformePatentabilidadDescripcion> descripcion = new HashSet<>();

}