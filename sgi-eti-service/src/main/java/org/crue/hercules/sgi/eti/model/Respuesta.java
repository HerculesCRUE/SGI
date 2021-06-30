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

/**
 * Respuesta
 */

@Entity
@Table(name = "respuesta")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Respuesta extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "respuesta_seq")
  @SequenceGenerator(name = "respuesta_seq", sequenceName = "respuesta_seq", allocationSize = 1)
  private Long id;

  /** Formulario Memoria */
  @ManyToOne
  @JoinColumn(name = "memoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_RESPUESTA_MEMORIA"))
  private Memoria memoria;

  /** Apartado Formulario */
  @ManyToOne
  @JoinColumn(name = "apartado_id", nullable = false, foreignKey = @ForeignKey(name = "FK_RESPUESTA_APARTADO"))
  private Apartado apartado;

  /** Formulario Memoria */
  @ManyToOne
  @JoinColumn(name = "tipo_documento_id", nullable = true, foreignKey = @ForeignKey(name = "FK_RESPUESTA_TIPODOCUMENTO"))
  private TipoDocumento tipoDocumento;

  /** Valor */
  @Column(name = "valor", nullable = false, columnDefinition = "clob")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private String valor;

  @JsonRawValue
  public String getValor() {
    return valor;
  }

  public void setValor(final String valor) {
    this.valor = valor;
  }

  @JsonProperty(value = "valor")
  public void setEsquemaRaw(JsonNode jsonNode) throws IOException {
    // this leads to non-standard json:
    // setJson(jsonNode.toString());

    if (jsonNode.isNull()) {
      setValor(null);
    } else {
      StringWriter stringWriter = new StringWriter();
      ObjectMapper objectMapper = new ObjectMapper();
      try (JsonGenerator generator = new JsonFactory(objectMapper).createGenerator(stringWriter)) {
        generator.writeTree(jsonNode);
        setValor(stringWriter.toString());
      }
    }
  }
}