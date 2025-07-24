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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = TipoCaducidad.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoCaducidad extends BaseEntity {
  /*
   * 
   */
  private static final long serialVersionUID = 1L;

  protected static final String TABLE_NAME = "tipo_caducidad";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int DESCRIPCION_MAX_LENGTH = 250;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_caducidad_descripcion", joinColumns = @JoinColumn(name = "tipo_caducidad_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<TipoCaducidadDescripcion> descripcion = new HashSet<>();

}
