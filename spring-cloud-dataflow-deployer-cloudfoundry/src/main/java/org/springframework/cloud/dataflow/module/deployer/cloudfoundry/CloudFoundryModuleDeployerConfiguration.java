/*
 * Copyright 2015 the original author or authors.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.dataflow.module.deployer.ModuleDeployer;
import org.springframework.cloud.dataflow.server.config.DataFlowServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires up all the necessary components for providing {@link ModuleDeployer}s targeting
 * Cloud Foundry.
 *
 * @author Eric Bottard
 */
@Configuration
@EnableConfigurationProperties({CloudFoundryModuleDeployerProperties.class, DataFlowServerProperties.class})
public class CloudFoundryModuleDeployerConfiguration {

    @Autowired
    private CloudFoundryModuleDeployerProperties properties;

    @Autowired
    private DataFlowServerProperties serverProperties;

    @Bean
    public ModuleDeployer processModuleDeployer(CloudFoundryClient cloudFoundryClient) {
        return new ApplicationModuleDeployer(serverProperties, cloudFoundryClient, properties);
    }

    @Bean
    @ConditionalOnMissingBean(CloudFoundryClient.class)
    public CloudFoundryClient cloudFoundryClient() {
        CloudCredentials credentials = new CloudCredentials(properties.getUsername(), properties.getPassword());
        CloudFoundryClient cloudFoundryClient = new CloudFoundryClient(credentials,
                properties.getApiEndpoint(),
                properties.getOrganization(),
                properties.getSpace(),
                properties.isSkipSslValidation());
        return cloudFoundryClient;
    }

    @Bean
    public ModuleDeployer taskModuleDeployer(CloudFoundryClient cloudFoundryClient) {
        return new ApplicationModuleDeployer(serverProperties, cloudFoundryClient, properties);
    }

}
