/*
 * Copyright 2017 the original author or authors.
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

import java.util.Collections;

import com.github.zafarkhaja.semver.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.cloud.dataflow.server.config.features.FeaturesProperties;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryDeployerAutoConfiguration;
import org.springframework.cloud.deployer.spi.cloudfoundry.UnsupportedVersionTaskLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * An {@link org.springframework.boot.env.EnvironmentPostProcessor} that turns off task support for Cloud Controller API
 * versions that are not supported.
 *
 * @author Eric Bottard
 */
public class TaskFeatureAutoToggleEnvironmentPostProcessor implements EnvironmentPostProcessor {

	private static final String TASKS_KEY = FeaturesProperties.FEATURES_PREFIX + "." + FeaturesProperties.TASKS_ENABLED;

	private static final Logger logger = LoggerFactory.getLogger(TaskFeatureAutoToggleEnvironmentPostProcessor.class);

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

		// Create a throwaway AppContext to connect to CC API and assess its version.
		try {
			AnnotationConfigApplicationContext applicationContext
				= new AnnotationConfigApplicationContext();
			applicationContext.register(CloudFoundryDeployerAutoConfiguration.EarlyConnectionConfiguration.class);
			applicationContext.setEnvironment(environment); // Inherit current environment
			applicationContext.refresh();

			Version version = applicationContext.getBean(Version.class);
			if (version.lessThan(UnsupportedVersionTaskLauncher.MINIMUM_SUPPORTED_VERSION)) {
				logger.warn("Targeting Cloud Foundry API {}, which is incompatible with TaskLauncher support. Forcing {} to false", version, TASKS_KEY);
				environment.getPropertySources().addFirst(new MapPropertySource("Task Features De-activation",
					Collections.singletonMap(TASKS_KEY, "false")));
			}
		}
		catch (Exception ignored) { // Might happen in particular in Integration Tests not targeting an actual CF runtime
			logger.warn("Could not connect to Cloud Foundry to probe API version", ignored);
		}
	}
}
