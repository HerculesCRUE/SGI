package org.crue.hercules.sgi.eer.controller;

import org.crue.hercules.sgi.eer.converter.EmpresaEquipoEmprendedorConverter;
import org.crue.hercules.sgi.eer.dto.EmpresaEquipoEmprendedorOutput;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
import org.crue.hercules.sgi.eer.service.EmpresaEquipoEmprendedorService;
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
 * EmpresaEquipoEmprendedorControllerTest
 */

@WebMvcTest(EmpresaEquipoEmprendedorController.class)
class EmpresaEquipoEmprendedorControllerTest extends BaseControllerTest {

  @MockBean
  private EmpresaEquipoEmprendedorService service;
  @MockBean
  private EmpresaEquipoEmprendedorConverter converter;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/empresasequiposemprendedores";

  @Test
  @WithMockUser(username = "user", authorities = { "EER-EER-V" })
  void findById_WithExistingId_ReturnsEmpresaEquipoEmprendedor() throws Exception {

    BDDMockito.given(converter.convert(
        ArgumentMatchers.<EmpresaEquipoEmprendedor>any()))
        .willReturn(generarMockEmpresaEquipoEmprendedorOutput(1L));
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<EmpresaEquipoEmprendedor>() {
      @Override
      public EmpresaEquipoEmprendedor answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockEmpresaEquipoEmprendedor(id);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested EmpresaEquipoEmprendedor is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  /**
   * Función que devuelve un objeto EmpresaEquipoEmprendedor
   * 
   * @param id
   * @param activo
   * @return EmpresaEquipoEmprendedor
   */
  private EmpresaEquipoEmprendedor generarMockEmpresaEquipoEmprendedor(Long id) {
    return EmpresaEquipoEmprendedor.builder().id(id).empresaId(1L).miembroEquipoRef("miembroEquipoRef")
        .build();
  }

  /**
   * Función que devuelve un objeto EmpresaEquipoEmprendedor
   * 
   * @param id
   * @return EmpresaEquipoEmprendedor
   */
  private EmpresaEquipoEmprendedorOutput generarMockEmpresaEquipoEmprendedorOutput(Long id) {
    return EmpresaEquipoEmprendedorOutput.builder().id(id).miembroEquipoRef("miembroEquipoRef")
        .build();
  }

}
