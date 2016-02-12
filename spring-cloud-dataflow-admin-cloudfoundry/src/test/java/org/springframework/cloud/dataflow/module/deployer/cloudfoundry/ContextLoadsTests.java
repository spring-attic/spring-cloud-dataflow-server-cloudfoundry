/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.module.deployer.cloudfoundry;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {org.springframework.cloud.dataflow.admin.AdminApplication.class,
        ContextLoadsTests.MockCloudFoundryClientConfig.class})
@IntegrationTest({"cloudfoundry.space=test", "cloudfoundry.apiEndpoint=http://example.com"})
public class ContextLoadsTests {


    @Configuration
    public static class MockCloudFoundryClientConfig {

        @Bean
        public CloudFoundryClient cloudFoundryClient() {
            CloudFoundryClient client = Mockito.mock(CloudFoundryClient.class);
            return client;
        }
    }

    @Test
    public void contextLoads() {

    }

}
