package org.crue.hercules.sgi.eer.controller;

import java.math.BigDecimal;
import java.time.Instant;

import org.crue.hercules.sgi.eer.converter.EmpresaComposicionSociedadConverter;
import org.crue.hercules.sgi.eer.dto.EmpresaComposicionSociedadOutput;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad.TipoAportacion;
import org.crue.hercules.sgi.eer.service.EmpresaComposicionSociedadService;
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
 * EmpresaComposicionSociedadControllerTest
 */

@WebMvcTest(EmpresaComposicionSociedadController.class)
public class EmpresaComposicionSociedadControllerTest extends BaseControllerTest {

  @MockBean
  private EmpresaComposicionSociedadService service;
  @MockBean
  private EmpresaComposicionSociedadConverter converter;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/empresascomposicionessociedades";

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-V" })
  public void findById_WithExistingId_ReturnsEmpresaComposicionSociedad() throws Exception {

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<EmpresaComposicionSociedad>any()))
        .willReturn(generarMockEmpresaComposicionSociedadOutput(1L));
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<EmpresaComposicionSociedad>() {
      @Override
      public EmpresaComposicionSociedad answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockEmpresaComposicionSociedad(id);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested EmpresaComposicionSociedad is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  /**
   * Función que devuelve un objeto EmpresaComposicionSociedad
   * 
   * @param id
   * @param activo
   * @return EmpresaComposicionSociedad
   */
  private EmpresaComposicionSociedad generarMockEmpresaComposicionSociedad(Long id) {
    return EmpresaComposicionSociedad.builder().id(id).empresaId(1L)
        .miembroSociedadPersonaRef("miembroSociedadPersonaRef").participacion(new BigDecimal(30))
        .tipoAportacion(TipoAportacion.DINERARIA).fechaInicio(Instant.now())
        .build();
  }

  /**
   * Función que devuelve un objeto EmpresaComposicionSociedad
   * 
   * @param id
   * @return EmpresaComposicionSociedad
   */
  private EmpresaComposicionSociedadOutput generarMockEmpresaComposicionSociedadOutput(Long id) {
    return EmpresaComposicionSociedadOutput.builder().id(id).miembroSociedadPersonaRef("miembroSociedadPersonaRef")
        .participacion(new BigDecimal(30))
        .tipoAportacion(TipoAportacion.DINERARIA).fechaInicio(Instant.now())
        .build();
  }

}
