package org.crue.hercules.sgi.rep.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.crue.hercules.sgi.rep.RepApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RepApplicationIT extends BaseIT {

  @Test
  void givenRepApplication_ReturnVoid() throws Exception {
    String[] args = new String[] {};
    RepApplication.main(args);
    assertNotNull(args);
  }

}