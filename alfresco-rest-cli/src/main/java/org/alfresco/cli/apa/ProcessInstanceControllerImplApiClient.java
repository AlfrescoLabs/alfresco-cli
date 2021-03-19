package org.alfresco.cli.apa;

import com.alfresco.activiti.runtime.handler.ProcessInstanceControllerImplApi;
import org.alfresco.cli.feign.OAuth2FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "runtime", url = "${activiti.service.runtime.url}",
        path = "${activiti.service.runtime.path}", configuration = OAuth2FeignClientConfiguration.class,
        decode404 = true)
public interface ProcessInstanceControllerImplApiClient extends ProcessInstanceControllerImplApi {
}
