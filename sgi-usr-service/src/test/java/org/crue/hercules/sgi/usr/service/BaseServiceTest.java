package org.crue.hercules.sgi.usr.service;

import org.crue.hercules.sgi.framework.spring.context.support.boot.autoconfigure.ApplicationContextSupportAutoConfiguration;
import org.crue.hercules.sgi.usr.config.SgiConfigProperties;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * BaseServiceTest
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@Import({ ValidationAutoConfiguration.class, MessageSourceAutoConfiguration.class,
    ApplicationContextSupportAutoConfiguration.class })
@EnableConfigurationProperties(value = SgiConfigProperties.class)
@TestPropertySource(locations = { "classpath:application.yml" })
@ContextConfiguration(initializers = { ConfigFileApplicationContextInitializer.class })
abstract class BaseServiceTest {

}
