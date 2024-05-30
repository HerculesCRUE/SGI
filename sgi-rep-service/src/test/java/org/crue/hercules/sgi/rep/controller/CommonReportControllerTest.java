package org.crue.hercules.sgi.rep.controller;

import org.crue.hercules.sgi.rep.service.SgiReportExcelService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * CommonReportControllerTest
 */
@WebMvcTest(value = CommonReportController.class)
class CommonReportControllerTest extends BaseControllerTest {

  @MockBean
  private SgiReportExcelService sgiReportExcelService;

}
