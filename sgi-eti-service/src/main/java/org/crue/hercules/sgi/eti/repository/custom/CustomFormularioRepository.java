package org.crue.hercules.sgi.eti.repository.custom;

import org.crue.hercules.sgi.eti.model.Formulario;
import org.springframework.stereotype.Component;

@Component
public interface CustomFormularioRepository {
  Formulario findByMemoriaId(Long idMemoria);
}
