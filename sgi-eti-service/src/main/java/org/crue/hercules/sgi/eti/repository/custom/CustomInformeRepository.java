package org.crue.hercules.sgi.eti.repository.custom;

import org.crue.hercules.sgi.eti.dto.InformeOutput;
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Informe}.
 */
@Component
public interface CustomInformeRepository {

  /**
   * Devuelve el último informe asociado a la memoria ordenado por la versión desc
   * y correspondiente al idioma indicado
   * 
   * @param idMemoria identificador de la {@link Memoria}
   * @param lang      El {@link Language} sobre el que buscar.
   * @return el {@link InformeOutput}
   */
  InformeOutput findFirstByMemoriaIdAndLangOrderByVersionDesc(Long idMemoria, Language lang);

  Page<InformeOutput> findByMemoriaIdAndLang(Long idMemoria, Language lang, Pageable pageable);

  /**
   * Devuelve el {@link Informe} filtrado por la {@link Memoria}, su tipo de
   * evaluación e idioma
   * 
   * @param idMemoria        identificador de la {@link Memoria}
   * @param idTipoEvaluacion identificador del {@link TipoEvaluacion}
   * @param lang             El {@link Language} sobre el que buscar.
   * @return el {@link InformeOutput}
   */
  InformeOutput findFirstByMemoriaIdAndTipoEvaluacionIdAndLangOrderByVersionDesc(Long idMemoria,
      Long idTipoEvaluacion, Language lang);

}
