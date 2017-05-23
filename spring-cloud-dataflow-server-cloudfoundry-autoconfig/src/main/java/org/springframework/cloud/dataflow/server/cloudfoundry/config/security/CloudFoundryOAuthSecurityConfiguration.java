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
package org.springframework.cloud.dataflow.server.cloudfoundry.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.cloud.dataflow.server.cloudfoundry.config.security.support.CloudFoundryDataflowAuthoritiesExtractor;
import org.springframework.cloud.dataflow.server.cloudfoundry.config.security.support.CloudFoundrySecurityService;
import org.springframework.cloud.dataflow.server.config.security.support.OnSecurityEnabledAndOAuth2Enabled;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * When running inside Cloud Foundry, this {@link Configuration} class will reconfigure Spring Cloud Data Flow's security
 * setup in {@link OAuthSecurityConfiguration}, so that only users with the
 * {@link CloudFoundryOAuthSecurityConfiguration#CF_SPACE_DEVELOPER_ROLE} can access the REST APIs.
 * <p>
 * Therefore, this configuration will ensure that only Cloud Foundry {@code Space Developers} have access to the
 * underlying REST API's.
 * <p>
 * For this to happen, a REST call will be made to the Cloud Foundry Permissions API via
 * CloudFoundrySecurityService inside the {@link DefaultDataflowAuthoritiesExtractor}.
 * <p>
 * If the user has the respective permissions, the
 * {@link CloudFoundryOAuthSecurityConfiguration#CF_SPACE_DEVELOPER_ROLE} will be assigned to the user.
 * <p>
 * See also: https://apidocs.cloudfoundry.org/258/apps/retrieving_permissions_on_a_app.html
 *
 * @author Gunnar Hillert
 */
@Configuration
@Conditional(OnSecurityEnabledAndOAuth2Enabled.class)
@ConditionalOnCloudPlatform(CloudPlatform.CLOUD_FOUNDRY)
public class CloudFoundryOAuthSecurityConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(CloudFoundryOAuthSecurityConfiguration.class);

	@Bean
	@ConditionalOnProperty(name="spring.cloud.dataflow.security.cf-use-uaa", havingValue="true")
	public CloudFoundryDataflowAuthoritiesExtractor authoritiesExtractor() {
		return new CloudFoundryDataflowAuthoritiesExtractor(cloudFoundrySecurityService());
	}

	@Bean
	@ConditionalOnProperty(name="spring.cloud.dataflow.security.cf-use-uaa", havingValue="true")
	public UserInfoTokenServicesPostProcessor UserInfoTokenServicesPostProcessor() {
		UserInfoTokenServicesPostProcessor userInfoTokenServicesPostProcessor = new UserInfoTokenServicesPostProcessor();
		return userInfoTokenServicesPostProcessor;
	}

	@Bean
	@ConditionalOnProperty(name="spring.cloud.dataflow.security.cf-use-uaa", havingValue="true")
	public CloudFoundrySecurityService cloudFoundrySecurityService() {
		return new CloudFoundrySecurityService();
	}

	public class UserInfoTokenServicesPostProcessor implements BeanPostProcessor, ApplicationContextAware {

		private ApplicationContext ctx;

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			if (bean instanceof UserInfoTokenServices) {
				logger.info("Setting up Cloud Foundry AuthoritiesExtractor for UAA.");
				final UserInfoTokenServices userInfoTokenServices = (UserInfoTokenServices) bean;
				userInfoTokenServices.setAuthoritiesExtractor(ctx.getBean(AuthoritiesExtractor.class));
			}
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.ctx = applicationContext;
		}
	}

}
