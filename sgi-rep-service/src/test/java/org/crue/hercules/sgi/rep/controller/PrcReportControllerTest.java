package org.crue.hercules.sgi.rep.controller;

import java.nio.charset.Charset;

import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeDetalleGrupo;
import org.crue.hercules.sgi.rep.service.prc.InformeDetalleGrupoReportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * PrcReportControllerTest
 */
@WebMvcTest(value = PrcReportController.class)
class PrcReportControllerTest extends BaseControllerTest {

  @MockBean
  private InformeDetalleGrupoReportService informeDetalleGrupoReportService;

  private final static String CONTENT_REPORT_TEST = "TEST";

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-INF-G" })
  void getReportDetalleGrupo_ReturnsResource() throws Exception {

    Long grupoId = 1L;
    Integer anio = 2021;

    final String url = new StringBuffer(PrcReportController.MAPPING).append("/informedetallegrupo/{anio}/{grupoRef}")
        .toString();

    BDDMockito.given(informeDetalleGrupoReportService.getReportDetalleGrupo(
        ArgumentMatchers.<ReportInformeDetalleGrupo>any(),
        ArgumentMatchers.<Integer>any(),
        ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return CONTENT_REPORT_TEST.getBytes();
        });

    // when: Se genera el informe
    MvcResult requestResult = mockMvc.perform(MockMvcRequestBuilders.get(url,
        grupoId, anio).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera el informe
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    Assertions.assertEquals(CONTENT_REPORT_TEST,
        requestResult.getResponse().getContentAsString(Charset.forName("UTF-8")));

  }
}
