package org.crue.hercules.sgi.rep.service.prc;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.service.BaseReportServiceTest;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiPrcService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * InformeDetalleGrupoReportServiceTest
 */
class InformeDetalleGrupoReportServiceTest extends BaseReportServiceTest {

  private InformeDetalleGrupoReportService informeDetalleGrupoReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @Mock
  private SgiApiConfService sgiApiConfService;

  @Mock
  private SgiApiPrcService sgiApiPrcService;

  @BeforeEach
  public void setUp() throws Exception {
    informeDetalleGrupoReportService = new InformeDetalleGrupoReportService(
        sgiConfigProperties, sgiApiConfService, sgiApiPrcService);
  }

}
