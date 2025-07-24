package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionTitulo;
import org.crue.hercules.sgi.eti.repository.custom.CustomActaRepositoryImpl;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemoriaEvaluada implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  private Long id;
  private Long evaluacionId;
  private String numReferencia;
  private String personaRef;
  private Long dictamenId;
  private Integer version;
  private Long tipoEvaluacionId;
  private Collection<PeticionEvaluacionTitulo> titulo;

  /**
   * Constructor especializado para la query
   * {@link CustomActaRepositoryImpl#findAllMemoriasEvaluadasSinRevMinimaByActaId(Long)}
   * 
   * @param id
   * @param evaluacionId
   * @param numReferencia
   * @param dictamenId
   * @param version
   * @param tipoEvaluacionId
   * @param peticionEvaluacion
   */
  public MemoriaEvaluada(Long id, Long evaluacionId, String numReferencia, Long dictamenId, Integer version,
      Long tipoEvaluacionId, PeticionEvaluacion peticionEvaluacion) {
    this.id = id;
    this.evaluacionId = evaluacionId;
    this.numReferencia = numReferencia;
    this.personaRef = peticionEvaluacion.getPersonaRef();
    this.dictamenId = dictamenId;
    this.version = version;
    this.tipoEvaluacionId = tipoEvaluacionId;
    this.titulo = peticionEvaluacion.getTitulo();
  }
}
