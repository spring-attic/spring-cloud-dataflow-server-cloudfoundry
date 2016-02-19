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

package org.springframework.cloud.dataflow.deployer.spi.cloudfoundry;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.dataflow.module.deployer.cloudfoundry.CloudFoundryModuleDeployerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Configuration which creates deployers that deploy on Cloud Foundry.
 * Can be used either when running <i>in</i> Cloud Foundry, or <i>targeting</i> Cloud Foundry.
 *
 * @author Eric Bottard
 * @author Mark Fisher
 * @author Thomas Risberg
 * @author Ilayaperumal Gopinathan
 */
@Configuration
@Import(CloudFoundryModuleDeployerConfiguration.class)
public class CloudFoundryAutoConfiguration {

	//redis and datasource will come from auto-reconfiguration on CF

}
