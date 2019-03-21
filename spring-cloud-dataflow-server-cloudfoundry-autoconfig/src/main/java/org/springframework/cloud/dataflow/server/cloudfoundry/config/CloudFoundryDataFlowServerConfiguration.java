/*
 * Copyright 2016-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.dataflow.server.cloudfoundry.config;

import javax.annotation.PostConstruct;

import reactor.core.publisher.Hooks;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.dataflow.server.cloudfoundry.config.security.CloudFoundryOAuthSecurityConfiguration;
import org.springframework.cloud.deployer.autoconfigure.DelegatingResourceLoaderBuilderCustomizer;
import org.springframework.cloud.deployer.resource.docker.DockerResource;
import org.springframework.cloud.deployer.resource.docker.DockerResourceLoader;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryConnectionProperties;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryDeployerAutoConfiguration;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryDeploymentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration class for customizing Cloud Foundry deployer.
 *
 * @author Eric Bottard
 */
@Configuration
@Import(CloudFoundryOAuthSecurityConfiguration.class)
@AutoConfigureBefore(CloudFoundryDeployerAutoConfiguration.class)
public class CloudFoundryDataFlowServerConfiguration {

	@Bean
	@ConfigurationProperties(prefix = CloudFoundryConnectionProperties.CLOUDFOUNDRY_PROPERTIES + ".stream")
	public CloudFoundryDeploymentProperties appDeploymentProperties() {
		return new CloudFoundryDeploymentProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = CloudFoundryConnectionProperties.CLOUDFOUNDRY_PROPERTIES + ".task")
	public CloudFoundryDeploymentProperties taskDeploymentProperties() {
		return new CloudFoundryDeploymentProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = CloudFoundryServerConfigurationProperties.PREFIX)
	public CloudFoundryServerConfigurationProperties cloudFoundryServerConfigurationProperties() {
		return new CloudFoundryServerConfigurationProperties();
	}

	@Bean
	public DelegatingResourceLoaderBuilderCustomizer dockerDelegatingResourceLoaderBuilderCustomizer() {
		return customizer -> customizer.loader(DockerResource.URI_SCHEME, new DockerResourceLoader());
	}

	@PostConstruct
	public void afterPropertiesSet() {
		if (cloudFoundryServerConfigurationProperties().isDebugReactor()) {
			Hooks.onOperatorDebug();
		}
	}

}
