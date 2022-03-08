package org.crue.hercules.sgi.prc.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.crue.hercules.sgi.prc.PrcApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PrcApplicationIT extends BaseIT {

  @Test
  void givenRepApplication_ReturnVoid() throws Exception {
    String[] args = new String[] {};
    PrcApplication.main(args);
    assertNotNull(args);
  }

}