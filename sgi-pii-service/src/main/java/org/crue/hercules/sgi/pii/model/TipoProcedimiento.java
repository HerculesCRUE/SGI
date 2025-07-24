package org.crue.hercules.sgi.pii.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
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
import org.crue.hercules.sgi.pii.validation.UniqueNombreTipoProcedimiento;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = TipoProcedimiento.TABLE_NAME)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@ActivableIsActivo(entityClass = TipoProcedimiento.class, groups = { BaseEntity.Update.class })
@UniqueNombreTipoProcedimiento(groups = { BaseEntity.Update.class, BaseActivableEntity.OnActivar.class,
    BaseEntity.Create.class })
public class TipoProcedimiento extends BaseActivableEntity {
  /*
   * 
   */
  private static final long serialVersionUID = 1L;

  protected static final String TABLE_NAME = "tipo_procedimiento";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";
  public static final int NOMBRE_LENGTH = 50;
  public static final int DESCRIPCION_LENGTH = 250;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_procedimiento_nombre", joinColumns = @JoinColumn(name = "tipo_procedimiento_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<TipoProcedimientoNombre> nombre = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_procedimiento_descripcion", joinColumns = @JoinColumn(name = "tipo_procedimiento_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<TipoProcedimientoDescripcion> descripcion = new HashSet<>();

}
