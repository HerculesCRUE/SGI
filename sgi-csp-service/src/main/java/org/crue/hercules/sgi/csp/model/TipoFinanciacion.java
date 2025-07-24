package org.crue.hercules.sgi.csp.model;

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

import org.crue.hercules.sgi.csp.model.BaseActivableEntity.OnActivar;
import org.crue.hercules.sgi.csp.validation.UniqueNombreTipoFinanciacionActivo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_financiacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueNombreTipoFinanciacionActivo(groups = { BaseEntity.Create.class, BaseEntity.Update.class, OnActivar.class })
public class TipoFinanciacion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_financiacion_seq")
  @SequenceGenerator(name = "tipo_financiacion_seq", sequenceName = "tipo_financiacion_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_financiacion_nombre", joinColumns = @JoinColumn(name = "tipo_financiacion_id"))
  @NotEmpty
  @Valid
  private Set<TipoFinanciacionNombre> nombre = new HashSet<>();

  /** Descripcion */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_financiacion_descripcion", joinColumns = @JoinColumn(name = "tipo_financiacion_id"))
  @Valid
  private Set<TipoFinanciacionDescripcion> descripcion = new HashSet<>();

  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}
