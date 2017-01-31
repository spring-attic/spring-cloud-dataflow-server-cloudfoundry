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
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.deployer.resource.support.DelegatingResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.FileSystemUtils;

/**
 * A wrapper around a {@link org.springframework.core.io.ResourceLoader} that deletes returned Resources (assumed to
 * be on the file system) once disk space is getting low. Least Recently Used entries are removed first.
 *
 * <p>This wrapper is typically meant to be used to clean Maven {@literal .m2/repository} entries, but also works
 * with other files. For the former case, if entries are under the configured {@link #repositoryCache} path (typically
 * the {@literal .m2/repository} path), then the whole parent directory of the resource is removed. Otherwise, the sole
 * resource file is deleted.</p>
 *
 * @author Eric Bottard
 */
// NOTE: extends DelegatingResourceLoader as a
// work around https://github.com/spring-cloud/spring-cloud-dataflow/issues/1064 for now
/*default*/ class LRUCleaningResourceLoader extends DelegatingResourceLoader {

	private static final Logger logger = LoggerFactory.getLogger(LRUCleaningResourceLoader.class);

	private final File repositoryCache;

	private final ResourceLoader delegate;

	private final Map<File, Void> lruCache = this.new LRUCache();

	private final float targetFreeSpaceRatio;

	/**
	 * Instantiates a new LRUCleaning resource loader.
	 * @param delegate the ResourceLoader to wrap, assumed to be file system based.
	 * @param targetFreeSpaceRatio  The target free disk space ratio, between 0 and 1.
	 * @param repositoryCache  The directory location of the maven cache.
	 */
	public LRUCleaningResourceLoader(ResourceLoader delegate, float targetFreeSpaceRatio, File repositoryCache) {
		Assert.notNull(delegate, "delegate cannot be null");
		Assert.isTrue(0 <= targetFreeSpaceRatio && targetFreeSpaceRatio <= 1, "targetFreeSpaceRatio should between [0,1] inclusive.");
		this.delegate = delegate;
		this.targetFreeSpaceRatio = targetFreeSpaceRatio;
		this.repositoryCache = repositoryCache;
	}

	@Override
	public Resource getResource(String location) {
		Resource resource = delegate.getResource(location);
		try {
			File file = resource.getFile();
			synchronized (lruCache) {
				lruCache.put(file, null);
			}
			return resource;
		}
		catch (IOException e) {
			throw new RuntimeException(getClass().getSimpleName()
				+ " is meant to work with File resolvable Resources. Exception trying to resolve " + location, e);
		}
	}

	@Override
	public ClassLoader getClassLoader() {
		return delegate.getClassLoader();
	}

	private class LRUCache extends LinkedHashMap<File, Void> {

		LRUCache() {
			super(5, .75f, true);
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<File, Void> eldest) {
			for (Iterator<File> it = keySet().iterator(); it.hasNext(); ) {
				File file = it.next();
				String percentFreeSpace = String.format(java.util.Locale.US,"%.2f", 100f * file.getFreeSpace() / file.getTotalSpace());
				logger.info("Free Disk Space = {}, Target Free Space {}", percentFreeSpace, String.format(java.util.Locale.US,"%.2f", targetFreeSpaceRatio));
				logger.debug("Looking at LRU entry {}, Free Space = {}, Total Space = {} ", file, file.getFreeSpace(), file.getTotalSpace());
				if (shouldDelete(file) && it.hasNext()) { // never delete the most recent entry
					cleanup(file);
					it.remove();
				} else {
					logger.debug("No action taken for LRU entry {}", file);
				}
			}
			return false; // We already did some cleanup, don't let superclass do its logic
		}

		private void cleanup(File file) {
			if (repositoryCache != null && file.getPath().startsWith(repositoryCache.getPath())) {
				boolean success = FileSystemUtils.deleteRecursively(file.getParentFile());
				logger.info("[{}] Deleting {} parent directory to regain free disk space.", success ? "SUCCESS" : "FAILED", file);
			}
			else {
				boolean success = file.delete();
				logger.info("[{}] Deleting {} to regain free disk space", success ? "SUCCESS" : "FAILED", file);
			}
		}
	}

	private boolean shouldDelete(File file) {
		boolean shouldDelete = ((float) file.getFreeSpace()) / file.getTotalSpace() < targetFreeSpaceRatio;
		logger.trace("Should Delete {} [{}]", file, shouldDelete);
		return shouldDelete;
	}
}
