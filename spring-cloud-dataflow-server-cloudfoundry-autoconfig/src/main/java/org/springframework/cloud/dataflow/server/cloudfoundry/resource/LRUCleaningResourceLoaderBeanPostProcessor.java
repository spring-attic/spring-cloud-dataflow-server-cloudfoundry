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

package org.springframework.cloud.dataflow.server.cloudfoundry.resource;

import java.io.File;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.deployer.resource.support.DelegatingResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 * Decorates the default ResourceLoader with a {@link LRUCleaningResourceLoader} when running
 * <em>in</em> Cloud Foundry.
 *
 * @author Eric Bottard
 */
public class LRUCleaningResourceLoaderBeanPostProcessor implements BeanPostProcessor {

	private final float targetFreeSpaceRatio;

	private final File repositoryCache;

	public LRUCleaningResourceLoaderBeanPostProcessor(float targetFreeSpaceRatio, File repositoryCache) {
		this.targetFreeSpaceRatio = targetFreeSpaceRatio;
		this.repositoryCache = repositoryCache;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof DelegatingResourceLoader) {
			return new LRUCleaningResourceLoader((ResourceLoader) bean, targetFreeSpaceRatio, repositoryCache);
		}
		return bean;
	}
}
