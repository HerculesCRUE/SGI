package org.crue.hercules.sgi.rep.controller;

import java.nio.charset.Charset;
import java.time.Instant;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.rep.service.InformeActaReportService;
import org.crue.hercules.sgi.rep.service.InformeEvaluacionFavorableModificacionReportService;
import org.crue.hercules.sgi.rep.service.InformeEvaluacionFavorableNuevaReportService;
import org.crue.hercules.sgi.rep.service.InformeEvaluacionFavorableRatificacionReportService;
import org.crue.hercules.sgi.rep.service.InformeEvaluacionReportService;
import org.crue.hercules.sgi.rep.service.InformeEvaluacionRetrospectivaReportService;
import org.crue.hercules.sgi.rep.service.InformeEvaluadorReportService;
import org.crue.hercules.sgi.rep.service.InformeMemoriaReportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * EtiReportControllerTest
 */
@WebMvcTest(value = EtiReportController.class)
class EtiReportControllerTest extends BaseControllerTest {

  @MockBean
  private InformeMemoriaReportService mxxReportService;
  @MockBean
  private InformeEvaluacionReportService informeEvaluacionReportService;
  @MockBean
  private InformeEvaluadorReportService informeEvaluadorReportService;
  @MockBean
  private InformeEvaluacionFavorableNuevaReportService informeFavorableMemoriaReportService;
  @MockBean
  private InformeActaReportService informeActaReportService;
  @MockBean
  private InformeEvaluacionRetrospectivaReportService informeEvaluacionRetrospectivaReportService;
  @MockBean
  private InformeEvaluacionFavorableModificacionReportService informeFavorableModificacionReportService;
  @MockBean
  private InformeEvaluacionFavorableRatificacionReportService informeFavorableRatificacionReportService;

  private final static String CONTENT_REPORT_TEST = "TEST";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL",
      "ETI-EVC-INV-EVALR" })
  void getReportInformeEvaluacion_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    final String url = new StringBuffer(EtiReportController.MAPPING).append("/informe-evaluacion/{idEvaluacion}")
        .toString();

    BDDMockito.given(informeEvaluacionReportService.getReport(
        ArgumentMatchers
            .<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return CONTENT_REPORT_TEST.getBytes();
        });

    // when: Se genera el informe
    MvcResult requestResult = mockMvc.perform(MockMvcRequestBuilders.get(url,
        idEvaluacion).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera el informe
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    Assertions.assertEquals(CONTENT_REPORT_TEST,
        requestResult.getResponse().getContentAsString(Charset.forName("UTF-8")));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL",
      "ETI-EVC-INV-EVALR" })
  void getReportInformeEvaluador_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    final String url = new StringBuffer(EtiReportController.MAPPING)
        .append("/informe-ficha-evaluador/{idEvaluacion}")
        .toString();

    BDDMockito.given(informeEvaluadorReportService.getReport(
        ArgumentMatchers
            .<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return CONTENT_REPORT_TEST.getBytes();
        });

    // when: Se genera el informe
    MvcResult requestResult = mockMvc.perform(MockMvcRequestBuilders.get(url,
        idEvaluacion).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera el informe
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    Assertions.assertEquals(CONTENT_REPORT_TEST,
        requestResult.getResponse().getContentAsString(Charset.forName("UTF-8")));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL",
      "ETI-EVC-INV-EVALR" })
  void getReportInformeFavorableMemoria_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    final String url = new StringBuffer(EtiReportController.MAPPING)
        .append("/informe-favorable-memoria/{idEvaluacion}")
        .toString();

    BDDMockito.given(informeFavorableMemoriaReportService.getReport(
        ArgumentMatchers
            .<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return CONTENT_REPORT_TEST.getBytes();
        });

    // when: Se genera el informe
    MvcResult requestResult = mockMvc.perform(MockMvcRequestBuilders.get(url,
        idEvaluacion).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera el informe
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    Assertions.assertEquals(CONTENT_REPORT_TEST,
        requestResult.getResponse().getContentAsString(Charset.forName("UTF-8")));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-DES",
      "ETI-ACT-DESR" })
  void getReportInformeActa_ReturnsResource() throws Exception {
    Long idActa = 1L;

    final String url = new StringBuffer(EtiReportController.MAPPING)
        .append("/informe-acta/{idActa}?l={lang}")
        .toString();

    BDDMockito.given(informeActaReportService.getReport(
        ArgumentMatchers
            .<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return CONTENT_REPORT_TEST.getBytes();
        });

    // when: Se genera el informe
    MvcResult requestResult = mockMvc.perform(MockMvcRequestBuilders.get(url,
        idActa, Language.ES.getCode()).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera el informe
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    Assertions.assertEquals(CONTENT_REPORT_TEST,
        requestResult.getResponse().getContentAsString(Charset.forName("UTF-8")));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL",
      "ETI-EVC-INV-EVALR" })
  void getReportInformeEvaluacionRetrospectiva_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    InformeEvaluacionReportInput input = InformeEvaluacionReportInput.builder().fecha(Instant.now())
        .idEvaluacion(idEvaluacion).build();

    final String url = new StringBuffer(EtiReportController.MAPPING)
        .append("/informe-evaluacion-retrospectiva")
        .toString();

    BDDMockito.given(informeEvaluacionRetrospectivaReportService.getReport(
        ArgumentMatchers
            .<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return CONTENT_REPORT_TEST.getBytes();
        });

    // when: Se genera el informe
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.post(url, Language.ES.getCode()).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera el informe
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    Assertions.assertEquals(CONTENT_REPORT_TEST,
        requestResult.getResponse().getContentAsString(Charset.forName("UTF-8")));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL",
      "ETI-EVC-INV-EVALR" })
  void getReportInformeFavorableModificacion_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    final String url = new StringBuffer(EtiReportController.MAPPING)
        .append("/informe-favorable-modificacion/{idEvaluacion}")
        .toString();

    BDDMockito.given(informeFavorableModificacionReportService.getReport(
        ArgumentMatchers
            .<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return CONTENT_REPORT_TEST.getBytes();
        });

    // when: Se genera el informe
    MvcResult requestResult = mockMvc.perform(MockMvcRequestBuilders.get(url,
        idEvaluacion).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera el informe
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    Assertions.assertEquals(CONTENT_REPORT_TEST,
        requestResult.getResponse().getContentAsString(Charset.forName("UTF-8")));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL",
      "ETI-EVC-INV-EVALR" })
  void getReportInformeFavorableRatificacion_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    final String url = new StringBuffer(EtiReportController.MAPPING)
        .append("/informe-favorable-ratificacion/{idEvaluacion}")
        .toString();

    BDDMockito.given(informeFavorableRatificacionReportService.getReport(
        ArgumentMatchers
            .<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return CONTENT_REPORT_TEST.getBytes();
        });

    // when: Se genera el informe
    MvcResult requestResult = mockMvc.perform(MockMvcRequestBuilders.get(url,
        idEvaluacion).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera el informe
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    Assertions.assertEquals(CONTENT_REPORT_TEST,
        requestResult.getResponse().getContentAsString(Charset.forName("UTF-8")));

  }

}
