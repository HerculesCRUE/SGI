package org.crue.hercules.sgi.eer.controller;

import java.math.BigDecimal;
import java.time.Instant;

import org.crue.hercules.sgi.eer.converter.EmpresaAdministracionSociedadConverter;
import org.crue.hercules.sgi.eer.dto.EmpresaAdministracionSociedadOutput;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad.TipoAdministracion;
import org.crue.hercules.sgi.eer.service.EmpresaAdministracionSociedadService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * EmpresaAdministracionSociedadControllerTest
 */

@WebMvcTest(EmpresaAdministracionSociedadController.class)
public class EmpresaAdministracionSociedadControllerTest extends BaseControllerTest {

  @MockBean
  private EmpresaAdministracionSociedadService service;
  @MockBean
  private EmpresaAdministracionSociedadConverter converter;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/empresasadministracionessociedades";

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-V" })
  public void findById_WithExistingId_ReturnsEmpresaAdministracionSociedad() throws Exception {

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<EmpresaAdministracionSociedad>any()))
        .willReturn(generarMockEmpresaAdministracionSociedadOutput(1L));
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<EmpresaAdministracionSociedad>() {
          @Override
          public EmpresaAdministracionSociedad answer(InvocationOnMock invocation) throws Throwable {
            Long id = invocation.getArgument(0, Long.class);
            return generarMockEmpresaAdministracionSociedad(id);
          }
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested EmpresaAdministracionSociedad is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  /**
   * Función que devuelve un objeto EmpresaAdministracionSociedad
   * 
   * @param id
   * @param activo
   * @return EmpresaAdministracionSociedad
   */
  private EmpresaAdministracionSociedad generarMockEmpresaAdministracionSociedad(Long id) {
    return EmpresaAdministracionSociedad.builder().id(id).empresaId(1L)
        .miembroEquipoAdministracionRef("miembroEquipoAdministracionRef")
        .tipoAdministracion(TipoAdministracion.ADMINISTRADOR_MANCOMUNADO).fechaInicio(Instant.now())
        .build();
  }

  /**
   * Función que devuelve un objeto EmpresaAdministracionSociedad
   * 
   * @param id
   * @return EmpresaAdministracionSociedad
   */
  private EmpresaAdministracionSociedadOutput generarMockEmpresaAdministracionSociedadOutput(Long id) {
    return EmpresaAdministracionSociedadOutput.builder().id(id)
        .miembroEquipoAdministracionRef("miembroEquipoAdministracionRef")
        .tipoAdministracion(TipoAdministracion.ADMINISTRADOR_MANCOMUNADO).fechaInicio(Instant.now())
        .build();
  }

}
