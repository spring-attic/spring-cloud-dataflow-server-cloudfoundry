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

import org.springframework.http.HttpStatus;

/**
 * Authorization exceptions thrown to limit access to the endpoints.
 *
 * @author Madhura Bhave
 */
public class CloudFoundryAuthorizationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Reason reason;

	CloudFoundryAuthorizationException(Reason reason, String message) {
		this(reason, message, null);
	}

	CloudFoundryAuthorizationException(Reason reason, String message, Throwable cause) {
		super(message);
		this.reason = reason;
	}

	/**
	 * Return the status code that should be returned to the client.
	 * @return the HTTP status code
	 */
	public HttpStatus getStatusCode() {
		return getReason().getStatus();
	}

	/**
	 * Return the reason why the authorization exception was thrown.
	 * @return the reason
	 */
	public Reason getReason() {
		return this.reason;
	}

	/**
	 * Reasons why the exception can be thrown.
	 */
	enum Reason {

		ACCESS_DENIED(HttpStatus.FORBIDDEN),

		INVALID_AUDIENCE(HttpStatus.UNAUTHORIZED),

		INVALID_ISSUER(HttpStatus.UNAUTHORIZED),

		INVALID_KEY_ID(HttpStatus.UNAUTHORIZED),

		INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED),

		INVALID_TOKEN(HttpStatus.UNAUTHORIZED),

		MISSING_AUTHORIZATION(HttpStatus.UNAUTHORIZED),

		TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),

		UNSUPPORTED_TOKEN_SIGNING_ALGORITHM(HttpStatus.UNAUTHORIZED),

		SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE);

		private final HttpStatus status;

		Reason(HttpStatus status) {
			this.status = status;
		}

		public HttpStatus getStatus() {
			return this.status;
		}
	}
}
