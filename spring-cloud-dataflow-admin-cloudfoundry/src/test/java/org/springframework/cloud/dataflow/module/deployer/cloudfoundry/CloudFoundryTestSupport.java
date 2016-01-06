/*
 * Copyright 2016 the original author or authors.
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

package org.springframework.cloud.dataflow.module.deployer.cloudfoundry;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.test.junit.AbstractExternalResourceTestSupport;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

/**
 * JUnit {@link org.junit.Rule} that detects the fact that a Cloud Foundry installation is available.
 *
 * @author Eric Bottard
 */
public class CloudFoundryTestSupport extends AbstractExternalResourceTestSupport<CloudFoundryClient> {

	private ConfigurableApplicationContext context;

	protected CloudFoundryTestSupport(String resourceDescription) {
		super("CLOUDFOUNDRY");
	}

	@Override
	protected void cleanupResource() throws Exception {
		context.close();
	}

	@Override
	protected void obtainResource() throws Exception {
		context = SpringApplication.run(Config.class);
		resource = context.getBean(CloudFoundryClient.class);
	}

	@Configuration
	@EnableConfigurationProperties(CloudFoundryModuleDeployerProperties.class)
	private static class Config {

		@Bean
		public CloudFoundryClient cloudFoundryClient(CloudFoundryModuleDeployerProperties properties) {
			CloudCredentials credentials = new CloudCredentials(properties.getUsername(), properties.getPassword());
			CloudFoundryClient cloudFoundryClient = new CloudFoundryClient(credentials,
					properties.getApiEndpoint(),
					properties.getOrganization(),
					properties.getSpace(),
					properties.isSkipSslValidation());
			return cloudFoundryClient;
		}

	}
}
