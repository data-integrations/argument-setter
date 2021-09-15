/*
 * Copyright © 2016-2021 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.plugin.http;

import io.cdap.cdap.etl.api.FailureCollector;
import io.cdap.cdap.etl.api.PipelineConfigurer;
import io.cdap.cdap.etl.api.action.Action;
import io.cdap.cdap.etl.api.action.ActionContext;
import io.cdap.common.http.HttpMethod;
import io.cdap.common.http.HttpRequest;
import io.cdap.common.http.HttpRequestConfig;
import io.cdap.common.http.HttpRequests;
import io.cdap.common.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;

/**
 * Sets pipeline arguments for the run based on the contents of an http call.
 */
public abstract class HTTPArgumentSetter<T extends HTTPConfig> extends Action {
  private static final Logger LOG = LoggerFactory.getLogger(HTTPArgumentSetter.class);
  protected final T conf;
  protected final HttpRequestConfig httpRequestConfig;

  public HTTPArgumentSetter(T conf) {
    this.conf = conf;
    this.httpRequestConfig = new HttpRequestConfig(conf.getConnectTimeout(), conf.getReadTimeout());
  }

  @Override
  public void configurePipeline(PipelineConfigurer pipelineConfigurer) {
    FailureCollector collector = pipelineConfigurer.getStageConfigurer().getFailureCollector();
    conf.validate(collector);
  }

  @Override
  public void run(ActionContext context) throws Exception {
    FailureCollector collector = context.getFailureCollector();
    conf.validate(collector);
    collector.getOrThrowException();

    HttpMethod method = HttpMethod.valueOf(conf.getMethod().toUpperCase());
    URL url = new URL(conf.getUrl());
    Map<String, String> headers = conf.getHeaders();
    HttpRequest.Builder requestBuilder = HttpRequest.builder(method, url).addHeaders(headers);
    if (conf.getBody() != null) {
      requestBuilder.withBody(conf.getBody());
    }

    int retries = 0;
    Exception exception;
    do {
      try {
        HttpResponse response = HttpRequests.execute(requestBuilder.build(), httpRequestConfig);
        int responseCode = response.getResponseCode();

        LOG.debug("Request to {} resulted in response code {}.", conf.getUrl(), responseCode);
        if (responseCode != 200) {
          throw new IllegalStateException(String.format("Received non-200 response code %d. Response message = %s",
                                                        responseCode, response.getResponseMessage()));
        }
        handleResponse(context, response.getResponseBodyAsString());
        return;
      } catch (Exception e) {
        LOG.warn("Error making {} request to url {} with headers {}: {}",
                 conf.getMethod(), conf.getUrl(), headers, e.getMessage());
        exception = e;
        retries++;
      }
    } while (retries < conf.getNumRetries());

    throw exception;
  }

  abstract protected void handleResponse(ActionContext context, String responseBody) throws Exception;
}
