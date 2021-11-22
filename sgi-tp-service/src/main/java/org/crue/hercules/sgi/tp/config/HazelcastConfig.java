package org.crue.hercules.sgi.tp.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class HazelcastConfig {
  private static final String HAZELCAST_INSTANCE_NAME = "hazelcast-scheduler";
  private static final String HAZELCAST_CONFIGMAP_NAME = "configuration";
  private static final int HAZELCAST_MAX_SIZE_CONFIG = 200;
  private static final int HAZELCAST_TTL = -1;

  @Bean
  public Config hazelCastConfig() {
    Config config = new Config();
    config.setInstanceName(HAZELCAST_INSTANCE_NAME)
        .addMapConfig(new MapConfig().setName(HAZELCAST_CONFIGMAP_NAME)
            .setMaxSizeConfig(new MaxSizeConfig(HAZELCAST_MAX_SIZE_CONFIG, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
            .setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(HAZELCAST_TTL));
    return config;
  }
}
