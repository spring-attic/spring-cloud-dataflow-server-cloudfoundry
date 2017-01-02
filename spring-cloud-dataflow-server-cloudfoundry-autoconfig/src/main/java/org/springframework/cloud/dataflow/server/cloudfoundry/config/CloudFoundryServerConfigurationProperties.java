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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link ConfigurationProperties} class to configure various settings of Data Flow running on Cloud Foundry.
 *
 * @author Eric Bottard
 */
@ConfigurationProperties(CloudFoundryServerConfigurationProperties.PREFIX)
public class CloudFoundryServerConfigurationProperties {

	public static final String PREFIX = "spring.cloud.dataflow.server.cloudfoundry";

	/**
	 * The target ratio (%) of free disk space to always aim for when cleaning downloaded resources (typically via
	 * the local maven repository).
	 */
	private int freeDiskSpaceRatio = 25;

	@Min(0)	@Max(100)
	public int getFreeDiskSpaceRatio() {
		return freeDiskSpaceRatio;
	}

	public void setFreeDiskSpaceRatio(int freeDiskSpaceRatio) {
		this.freeDiskSpaceRatio = freeDiskSpaceRatio;
	}
}
