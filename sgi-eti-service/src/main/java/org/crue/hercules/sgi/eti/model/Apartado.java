package org.crue.hercules.sgi.eti.model;

import java.io.IOException;
import java.io.StringWriter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "apartado")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Apartado extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "apartado_seq")
  @SequenceGenerator(name = "apartado_seq", sequenceName = "apartado_seq", allocationSize = 1)
  private Long id;

  /** Bloque. */
  @ManyToOne
  @JoinColumn(name = "bloque_id", nullable = false, foreignKey = @ForeignKey(name = "FK_APARTADO_BLOQUE"))
  private Bloque bloque;

  /** Nombre. */
  @Column(name = "nombre", length = 250, nullable = false)
  private String nombre;

  /** Apartado Formulario Padre. */
  @ManyToOne
  @JoinColumn(name = "padre_id", nullable = true, foreignKey = @ForeignKey(name = "FK_APARTADO_PADRE"))
  private Apartado padre;

  /** Orden. */
  @Column(name = "orden", nullable = false)
  private Integer orden;

  /** Esquema. */
  @Column(name = "esquema", nullable = false, columnDefinition = "clob")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private String esquema;

  @JsonRawValue
  public String getEsquema() {
    return esquema;
  }

  public void setEsquema(final String esquema) {
    this.esquema = esquema;
  }

  @JsonProperty(value = "esquema")
  public void setEsquemaRaw(JsonNode jsonNode) throws IOException {
    // this leads to non-standard json:

    if (jsonNode.isNull()) {
      setEsquema(null);
    } else {
      StringWriter stringWriter = new StringWriter();
      ObjectMapper objectMapper = new ObjectMapper();
      try (JsonGenerator generator = new JsonFactory(objectMapper).createGenerator(stringWriter)) {
        generator.writeTree(jsonNode);
        setEsquema(stringWriter.toString());
      }
    }
  }
}
