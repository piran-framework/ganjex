/*
 * Copyright (c) 2018 Behsa Corporation.
 *
 *   This file is part of Ganjex.
 *
 *    Ganjex is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Ganjex is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Ganjex.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.behsacorp.ganjex.config;

import com.behsacorp.ganjex.GanjexHook;
import com.behsacorp.ganjex.api.Ganjex;
import com.behsacorp.ganjex.api.GanjexConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author hekmatof
 * @since 1.0
 */
@Configuration
@ConditionalOnBean(GanjexMarkerConfiguration.Marker.class)
@EnableConfigurationProperties(GanjexProperties.class)
public class GanjexAutoConfiguration {
  private static final Logger log = LoggerFactory.getLogger(GanjexAutoConfiguration.class);

  @Bean
  @ConditionalOnMissingBean
  public Ganjex ganjex(ApplicationContext applicationContext, GanjexProperties ganjexProperties) {
    Map<String, Object> hookMap =
        applicationContext.getBeansWithAnnotation(GanjexHook.class);
    Ganjex ganjex = Ganjex.run(new GanjexConfiguration.Builder()
        .servicePath(ganjexProperties.getServicePath())
        .libPath(ganjexProperties.getLibPath())
        .watcherDelay(ganjexProperties.getWatchDelay())
        .hooks(hookMap.values().toArray()).build()
    );
    log.info("ganjex container bootstrap finished");
    log.info("ganjex registered hooks: {}", hookMap.values());
    return ganjex;
  }
}
