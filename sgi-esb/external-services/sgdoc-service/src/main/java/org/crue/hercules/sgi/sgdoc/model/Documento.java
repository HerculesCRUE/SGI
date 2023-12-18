package org.crue.hercules.sgi.sgdoc.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "documento")
public class Documento implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "documento_ref")
  private String documentoRef;

  @Column(name = "nombre")
  private String nombre;

  @Column(name = "version")
  private Integer version;

  @Column(name = "fecha_creacion")
  private LocalDateTime fechaCreacion;

  @Column(name = "tipo")
  private String tipo;

  @Column(name = "autor_ref")
  private String autorRef;

  @Column(name = "hash")
  private String hash;
}