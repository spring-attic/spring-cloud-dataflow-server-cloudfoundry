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
package org.springframework.cloud.dataflow.server.cloudfoundry.config.security.support;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.dataflow.server.cloudfoundry.config.security.support.CloudFoundryAuthorizationException.Reason;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Cloud Foundry security service to handle REST calls to the cloud controller and UAA.
 *
 * @author Madhura Bhave
 * @author Gunnar Hillert
 *
 */
public class CloudFoundrySecurityService implements EnvironmentAware, InitializingBean, ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(CloudFoundrySecurityService.class);

	private OAuth2RestTemplate oAuth2RestTemplate;

	private String cloudControllerUrl;
	private String applicationId;

	private Environment environment;
	private ApplicationContext applicationContext;

	/**
	 * Returns {@code true} if the user (using the access-token from {@link OAuth2RestTemplate})
	 * has full {@link AccessLevel#FULL} for the provided {@code applicationId}
	 *
	 * @return true of the user is a space developer in Cloud Foundry
	 */
	public boolean isSpaceDeveloper() {
		final OAuth2AccessToken accessToken = this.oAuth2RestTemplate.getAccessToken();
		final AccessLevel accessLevel = getAccessLevel(
			accessToken.getValue(), applicationId);

		if (AccessLevel.FULL.equals(accessLevel)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Return the access level that should be granted to the given token.
	 * @param token the token
	 * @param applicationId the cloud foundry application ID
	 * @return the access level that should be granted
	 * @throws CloudFoundryAuthorizationException if the token is not authorized
	 */
	public AccessLevel getAccessLevel(String token, String applicationId)
			throws CloudFoundryAuthorizationException {
		try {
			final URI permissionsUri = getPermissionsUri(applicationId);
			logger.info("Using PermissionsUri: " + permissionsUri);
			RequestEntity<?> request = RequestEntity.get(permissionsUri)
					.header("Authorization", "bearer " + token).build();
			Map<?, ?> body = this.oAuth2RestTemplate.exchange(request, Map.class).getBody();
			if (Boolean.TRUE.equals(body.get("read_sensitive_data"))) {
				return AccessLevel.FULL;
			}
			else {
				return AccessLevel.RESTRICTED;
			}
		}
		catch (HttpClientErrorException ex) {
			if (ex.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
				return AccessLevel.NONE;
			}
			throw new CloudFoundryAuthorizationException(Reason.INVALID_TOKEN,
					"Invalid token", ex);
		}
		catch (HttpServerErrorException ex) {
			throw new CloudFoundryAuthorizationException(Reason.SERVICE_UNAVAILABLE,
					"Cloud controller not reachable");
		}
	}

	private URI getPermissionsUri(String applicationId) {
		try {
			return new URI(this.cloudControllerUrl + "/v2/apps/" + applicationId
					+ "/permissions");
		}
		catch (URISyntaxException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Setting up Cloud Foundry Security Service.");

		Assert.notNull(applicationContext, "The applicationContext must not be null.");
		this.oAuth2RestTemplate = applicationContext.getBean(OAuth2RestTemplate.class);

		final String cloudControllerPropertyKey = "vcap.application.cf_api";
		final String applicationIdKey = "vcap.application.application_id";
		this.cloudControllerUrl = environment.getProperty(cloudControllerPropertyKey);
		Assert.hasText(cloudControllerUrl, "Unabled to retrieve the CloudController Url from environment property " + cloudControllerPropertyKey);
		logger.info("Using Cloud Foundry Cloud Controller Url: " + cloudControllerUrl);

		this.applicationId = environment.getProperty(applicationIdKey);
		Assert.notNull(applicationId, "Unabled to retrieve the Application Id from environment property " + applicationIdKey);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
