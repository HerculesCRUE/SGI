package org.crue.hercules.sgi.sgdoc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "sgi.store")
@Data
public class StoreProperties {

  private String path;
}
