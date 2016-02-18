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

package org.springframework.cloud.dataflow.module.deployer.cloudfoundry;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Configuration properties to determine how to connect to Cloud Foundry, and some
 * defaults for module deployment.
 *
 * @author Eric Bottard
 */
@ConfigurationProperties("cloudfoundry")
class CloudFoundryModuleDeployerProperties {

	/**
	 * The names of services to bind to all applications deployed as a module.
	 * This should typically contain a service capable of playing the role of a binding transport.
	 */
	private Set<String> services = new HashSet<>(Arrays.asList("redis"));

	/**
	 * The domain to use when mapping routes for applications.
	 */
	private String domain;

	/**
	 * The organization to use when registering new applications.
	 */
	private String organization;

	/**
	 * The space to use when registering new applications.
	 */
	private String space;

	/**
	 * Location of the ModuleLauncher uber-jar to be uploaded.
	 */
	private Resource moduleLauncherLocation = new ClassPathResource("spring-cloud-stream-module-launcher.jar");

	/**
	 * Location of the CloudFoundry REST API endpoint to use.
	 */
	private URL apiEndpoint;

	/**
	 * Username to use to authenticate against the Cloud Foundry API.
	 */
	private String username;

	/**
	 * Password to use to authenticate against the Cloud Foundry API.
	 */
	private String password;

	/**
	 * Allow operation using self-signed certificates.
	 */
	private boolean skipSslValidation = false;

	/**
	 * The buildpack to use for deploying the application.
	 */
	private String buildpack = "https://github.com/cloudfoundry/java-buildpack.git#master";

	/**
	 * The amount of memory (MB) to allocate, if not overridden per-module.
	 */
	private int memory = 1024;

	/**
	 * The amount of disk space (MB) to allocate, if not overridden per-module.
	 */
	private int disk = 1024;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@NotNull
	public URL getApiEndpoint() {
		return apiEndpoint;
	}

	public void setApiEndpoint(URL apiEndpoint) {
		this.apiEndpoint = apiEndpoint;
	}

	public Set<String> getServices() {
		return services;
	}

	public void setServices(Set<String> services) {
		this.services = services;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@NotBlank
	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public boolean isSkipSslValidation() {
		return skipSslValidation;
	}

	public void setSkipSslValidation(boolean skipSslValidation) {
		this.skipSslValidation = skipSslValidation;
	}

	@NotNull
	public Resource getModuleLauncherLocation() {
		return moduleLauncherLocation;
	}

	public void setModuleLauncherLocation(Resource moduleLauncherLocation) {
		this.moduleLauncherLocation = moduleLauncherLocation;
	}

	public String getBuildpack() {
		return buildpack;
	}

	public void setBuildpack(String buildpack) {
		this.buildpack = buildpack;
	}

	@Min(0)
	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	@Min(0)
	public int getDisk() {
		return disk;
	}

	public void setDisk(int disk) {
		this.disk = disk;
	}
}
