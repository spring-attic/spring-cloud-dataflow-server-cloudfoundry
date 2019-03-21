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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import reactor.core.publisher.Hooks;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.dataflow.server.cloudfoundry.config.security.CloudFoundryOAuthSecurityConfiguration;
import org.springframework.cloud.deployer.resource.docker.DockerResource;
import org.springframework.cloud.deployer.resource.docker.DockerResourceLoader;
import org.springframework.cloud.deployer.resource.maven.MavenProperties;
import org.springframework.cloud.deployer.resource.maven.MavenResource;
import org.springframework.cloud.deployer.resource.maven.MavenResourceLoader;
import org.springframework.cloud.deployer.resource.support.DelegatingResourceLoader;
import org.springframework.cloud.deployer.resource.support.LRUCleaningResourceLoaderBeanPostProcessor;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryConnectionProperties;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryDeploymentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;

/**
 * Configuration class for customizing Cloud Foundry deployer.
 *
 * @author Eric Bottard
 */
@Configuration
@Import(CloudFoundryOAuthSecurityConfiguration.class)
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
	public DelegatingResourceLoader delegatingResourceLoader(MavenProperties mavenProperties) {
		Map<String, ResourceLoader> loaders = new HashMap<>();
		loaders.put(DockerResource.URI_SCHEME, new DockerResourceLoader());
		loaders.put(MavenResource.URI_SCHEME, new MavenResourceLoader(mavenProperties));
		return new DelegatingResourceLoader(loaders);
	}


	@Bean
	public LRUCleaningResourceLoaderBeanPostProcessor lruCleaningResourceLoaderInstaller(
			CloudFoundryServerConfigurationProperties serverConfiguration,
			MavenProperties mavenProperties) {
		File repositoryCache = new File(mavenProperties.getLocalRepository());
		float fRatio = serverConfiguration.getFreeDiskSpacePercentage() / 100F;
		return new LRUCleaningResourceLoaderBeanPostProcessor(fRatio, repositoryCache);
	}

	@PostConstruct
	public void afterPropertiesSet() {
		if (cloudFoundryServerConfigurationProperties().isDebugReactor()) {
			Hooks.onOperatorDebug();
		}
	}

}
