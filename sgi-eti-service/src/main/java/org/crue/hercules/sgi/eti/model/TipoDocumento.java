package org.crue.hercules.sgi.eti.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_documento")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TipoDocumento extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  /** Codigo */
  @Column(name = "codigo", length = 50, nullable = false)
  private String codigo;

  /** Nombre. */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_documento_nombre", joinColumns = @JoinColumn(name = "tipo_documento_id"))
  @Valid
  private Set<TipoDocumentoNombre> nombre = new HashSet<>();

  /** Formulario Id */
  @Column(name = "formulario_id", nullable = false)
  @NotNull
  private Long formularioId;

  /** Adicional */
  @Column(name = "adicional", columnDefinition = "boolean default false", nullable = false)
  private boolean adicional = false;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}