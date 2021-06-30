package org.crue.hercules.sgi.csp.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tipo_origen_fuente_financiacion", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "nombre" }, name = "UK_TIPOORIGENFUENTEFINANCIACION_NOMBRE") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class TipoOrigenFuenteFinanciacion extends BaseActivableEntity implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_origen_fuente_financiacion_seq")
  @SequenceGenerator(name = "tipo_origen_fuente_financiacion_seq", sequenceName = "tipo_origen_fuente_financiacion_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @Column(name = "nombre", length = 50, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String nombre;
}
