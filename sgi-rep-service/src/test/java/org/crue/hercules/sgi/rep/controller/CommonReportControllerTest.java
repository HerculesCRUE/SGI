package org.crue.hercules.sgi.rep.controller;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.service.SgiDynamicReportService;
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
 * CommonReportControllerTest
 */
@WebMvcTest(value = CommonReportController.class)
class CommonReportControllerTest extends BaseControllerTest {

  private final static String CONTENT_REPORT_TEST = "TEST";

  @MockBean
  private SgiDynamicReportService sgiDynamicReportService;

  @Test
  @WithMockUser(username = "user", authorities = {})
  void getDynamicReport_ReturnsResource() throws Exception {

    // given: data for report

    // @formatter:off
    List<SgiDynamicReportDto.SgiFilterReportDto> filters = new ArrayList<>();
    filters.add(SgiDynamicReportDto.SgiFilterReportDto.builder().name("Filtro1").filter("Valor de filtro 1").build());
    filters.add(SgiDynamicReportDto.SgiFilterReportDto.builder().name("Filtro2").filter("Valor de filtro 2").build());

    List<SgiDynamicReportDto.SgiColumReportDto> columns = new ArrayList<>();
    columns.add(SgiDynamicReportDto.SgiColumReportDto.builder().title("Nombre").name("nombre").type(SgiDynamicReportDto.ColumnType.STRING).build());
    columns.add(SgiDynamicReportDto.SgiColumReportDto.builder().title("Nombre").name("fecha").type(SgiDynamicReportDto.ColumnType.STRING).build());

    List<SgiDynamicReportDto.SgiRowReportDto> rows = new ArrayList<>();
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre1", "12/10/2021")).build());
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre2", "22/10/2021")).build());    
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre3", "12/10/2021")).build());
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre4", "22/10/2021")).build());    
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre5", "12/10/2021")).build());
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre6", "22/10/2021")).build());

    SgiDynamicReportDto report = SgiDynamicReportDto.builder()
      .name("informeDinamico")
      .outputType(OutputType.PDF)
      .title("Listado de prueba de informe dinámico")
      .filters(filters)
      .columns(columns)
      .rows(rows)
      .build();
    // @formatter:on

    BDDMockito.given(sgiDynamicReportService.generateDynamicReport(
        ArgumentMatchers.<SgiDynamicReportDto>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return CONTENT_REPORT_TEST.getBytes();
        });

    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.post(CommonReportController.MAPPING + "/dynamic")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(report)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns the configured time zone
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    Assertions.assertEquals(CONTENT_REPORT_TEST,
        requestResult.getResponse().getContentAsString(Charset.forName("UTF-8")));
  }

}
