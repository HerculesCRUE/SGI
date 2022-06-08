package org.crue.hercules.sgi.prc.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.crue.hercules.sgi.prc.model.BaseEntity.Create;
import org.crue.hercules.sgi.prc.model.BaseEntity.Update;
import org.crue.hercules.sgi.prc.validation.BaremoConfiguracionBaremo;
import org.crue.hercules.sgi.prc.validation.BaremoCuantia;
import org.crue.hercules.sgi.prc.validation.BaremoPeso;
import org.crue.hercules.sgi.prc.validation.BaremoPuntos;
import org.crue.hercules.sgi.prc.validation.UniqueFieldsValues;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Baremo.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "configuracion_baremo_id",
        "convocatoria_baremacion_id" }, name = "UK_BAREMO_CONFIGURACIONBAREMOID_CONVOCATORIABAREMACIONID") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueFieldsValues(groups = { Create.class }, entityClass = Baremo.class, fieldsNames = {
    Baremo_.CONFIGURACION_BAREMO_ID, Baremo_.CONVOCATORIA_BAREMACION_ID })
@BaremoCuantia(groups = { Create.class, Update.class })
@BaremoPeso(groups = { Create.class, Update.class })
@BaremoPuntos(groups = { Create.class, Update.class })
@BaremoConfiguracionBaremo(groups = { Create.class, Update.class })
public class Baremo extends BaseEntity {

  protected static final String TABLE_NAME = "baremo";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public enum TipoCuantia {
    PUNTOS,
    RANGO;
  }

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "peso", nullable = true)
  private Integer peso;

  @Column(name = "puntos", nullable = true)
  private BigDecimal puntos;

  @Column(name = "cuantia", nullable = true)
  private BigDecimal cuantia;

  @Column(name = "tipo_cuantia", length = TIPO_CUANTIA_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  private TipoCuantia tipoCuantia;

  @Column(name = "configuracion_baremo_id", nullable = false)
  private Long configuracionBaremoId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "configuracion_baremo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_BAREMO_CONFIGURACIONBAREMO"))
  private ConfiguracionBaremo configuracionBaremo;

  @Column(name = "convocatoria_baremacion_id", nullable = true)
  private Long convocatoriaBaremacionId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_baremacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_BAREMO_CONVOCATORIABAREMACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ConvocatoriaBaremacion convocatoriaBaremacion = null;

  @OneToMany(mappedBy = "baremo")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<PuntuacionBaremoItem> puntuacionesBaremoItem = null;

}
