package org.crue.hercules.sgi.csp.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPCategoriaProfesionalRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;

class RequisitoIPCategoriaProfesionalServiceTest extends BaseServiceTest {

  @Mock
  private RequisitoIPCategoriaProfesionalRepository requisitoIPCategoriaProfesionalRepository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  @Mock
  private SolicitudRepository solicitudRepository;

  private RequisitoIPCategoriaProfesionalService requisitoIPCategoriaProfesionalService;

  @BeforeEach
  public void setup(){
    //@formatter:off
    this.requisitoIPCategoriaProfesionalService = new RequisitoIPCategoriaProfesionalService(
                                                        requisitoIPCategoriaProfesionalRepository,
                                                        convocatoriaRepository,
                                                        configuracionSolicitudRepository,
                                                        solicitudRepository);
    //@formatter:on
  }

  @Test
  void updateCategoriasProfesionales_WithRequisitoIPIdNull_ThrowsIllegalArgumentException() {
    
    Long requisitoIPId = 1L;

    List<RequisitoIPCategoriaProfesional>categoriasProfesionales = Arrays.asList(buildMockRequisitoIPCategoriaProfesional(1L, 2L));

    Assertions.assertThatThrownBy(() -> this.requisitoIPCategoriaProfesionalService.updateCategoriasProfesionales(requisitoIPId, categoriasProfesionales)).isInstanceOf(IllegalArgumentException.class);
  }

  @WithMockUser(authorities = { "CSP-CON-INV-V" })
  @Test
  void findByConvocatoria() {
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = buildMockConvocatoria(convocatoriaId);

    BDDMockito.given(this.convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    //BDDMockito.given(SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-CON-INV-V")).willReturn(true);
    BDDMockito.given(this.configuracionSolicitudRepository.findByConvocatoriaId(convocatoriaId)).willReturn(Optional.of(new ConfiguracionSolicitud()));

    Assertions.assertThatThrownBy(() -> this.requisitoIPCategoriaProfesionalService.findByConvocatoria(convocatoriaId)).isInstanceOf(UserNotAuthorizedToAccessConvocatoriaException.class);
  }

  private RequisitoIPCategoriaProfesional buildMockRequisitoIPCategoriaProfesional(Long id, Long requisitoIPId) {
    return RequisitoIPCategoriaProfesional.builder()
    .id(id)
    .requisitoIPId(requisitoIPId)
    .build();
  }

  private Convocatoria buildMockConvocatoria(Long id) {
    return Convocatoria
    .builder()
    .id(id)
    .activo(Boolean.TRUE)
    .estado(Estado.BORRADOR)    
    .build();
  }
}