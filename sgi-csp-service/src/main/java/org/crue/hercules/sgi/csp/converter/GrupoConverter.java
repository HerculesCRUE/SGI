package org.crue.hercules.sgi.csp.converter;

import javax.annotation.PostConstruct;

import org.crue.hercules.sgi.csp.dto.GrupoInput;
import org.crue.hercules.sgi.csp.dto.GrupoOutput;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoTipo.Tipo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoConverter {

  private final ModelMapper modelMapper;

  @PostConstruct
  public void mapperConfig() {
    modelMapper.typeMap(GrupoInput.class, Grupo.class)
        .addMappings(mapper -> mapper.<Boolean>map(GrupoInput::getEspecialInvestigacion,
            (dest, value) -> dest.getEspecialInvestigacion().setEspecialInvestigacion(value)))
        .addMappings(mapper -> mapper.<Tipo>map(GrupoInput::getTipo, (dest, value) -> dest.getTipo().setTipo(value)));

    modelMapper.typeMap(Grupo.class, GrupoOutput.class)
        .addMappings(mapper -> mapper.<Boolean>map(src -> src.getEspecialInvestigacion().getEspecialInvestigacion(),
            GrupoOutput::setEspecialInvestigacion))
        .addMappings(mapper -> mapper.<Tipo>map(src -> src.getTipo().getTipo(), GrupoOutput::setTipo));
  }

  public Grupo convert(GrupoInput input) {
    return convert(null, input);
  }

  public Grupo convert(Long id, GrupoInput input) {
    Grupo grupo = modelMapper.map(input, Grupo.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoOutput convert(Grupo entity) {
    GrupoOutput grupoOutput = modelMapper.map(entity, GrupoOutput.class);
    return grupoOutput;
  }

  public Page<GrupoOutput> convert(Page<Grupo> page) {
    return page.map(this::convert);
  }

}
