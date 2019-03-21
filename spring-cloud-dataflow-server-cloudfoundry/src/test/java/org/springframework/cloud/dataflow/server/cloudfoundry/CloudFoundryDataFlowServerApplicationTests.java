/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.server.cloudfoundry;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.cloud.deployer.cloudfoundry.org=myorg," +
        "spring.cloud.deployer.cloudfoundry.username=myusername",
        "spring.cloud.deployer.cloudfoundry.password=mypassword",
        "spring.cloud.deployer.cloudfoundry.space=myspace",
        "spring.cloud.deployer.cloudfoundry.url=https://api.run.pivotal.io"})
public class CloudFoundryDataFlowServerApplicationTests {

    @Test
    public void contextLoads() {
    }


    @Configuration
    static public class TaskLauncherConfiguration {

        @Bean
        @Primary
        public TaskLauncher taskLauncher() {
            return Mockito.mock(TaskLauncher.class);
        }
    }
}
