/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.dataflow.server.cloudfoundry.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.cloud.dataflow.server.config.features.FeaturesProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * An {@link EnvironmentPostProcessor} that sets the original task feature property based on the property
 * 'spring.cloud.dataflow.features.experimental.tasksEnabled' value.
 *
 * @author Ilayaperumal Gopinathan
 */
public class CloudFoundryEnvironmentPostProcessor implements EnvironmentPostProcessor {

	private static final Log logger = LogFactory.getLog(CloudFoundryEnvironmentPostProcessor.class);

	private static final String FEATURES_PREFIX = FeaturesProperties.FEATURES_PREFIX + ".";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		if (Boolean.valueOf(environment.getProperty(FEATURES_PREFIX + FeaturesProperties.TASKS_ENABLED))) {
			logger.warn(FEATURES_PREFIX + FeaturesProperties.TASKS_ENABLED + " has been set to true directly. " +
					"Be advised this is an EXPERIMENTAL feature, normally enabled via " + FEATURES_PREFIX + "experimental.tasks-enabled");
		}

		Map<String, Object> propertiesToOverride = new HashMap<>();
		boolean isTasksEnabled = Boolean.valueOf(environment.getProperty(FEATURES_PREFIX + "experimental.tasksEnabled"));
		if (isTasksEnabled) {
			propertiesToOverride.put(FEATURES_PREFIX + FeaturesProperties.TASKS_ENABLED, true);
			environment.getPropertySources().addFirst(new MapPropertySource("CFDataflowServerProperties", propertiesToOverride));
		}
	}
}
