package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.framework.spring.context.support.boot.autoconfigure.ApplicationContextSupportAutoConfiguration;
import org.crue.hercules.sgi.framework.test.context.support.SgiTestProfileResolver;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * BaseServiceTest
 */
@ActiveProfiles(resolver = SgiTestProfileResolver.class)
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@Import({ ValidationAutoConfiguration.class, MessageSourceAutoConfiguration.class,
    ApplicationContextSupportAutoConfiguration.class })
public class BaseServiceTest {

}
