package com.philocoder.philocoder_api.config

import com.philocoder.philocoder_api.util.TimeUtil.FIVE_SECONDS
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ElasticsearchConfig {

    @Bean
    open fun elasticSearchClient(): RestHighLevelClient {
        val httpHost = HttpHost("localhost", 9200, "http")

        val requestConfigCallback =
            RequestConfigCallback { requestConfigBuilder: RequestConfig.Builder ->
                requestConfigBuilder
                    .setConnectionRequestTimeout(0)
                    .setSocketTimeout(FIVE_SECONDS.toInt())
                    .setConnectTimeout(FIVE_SECONDS.toInt())
            }

        val httpClientConfigCallback =
            HttpClientConfigCallback { httpClientBuilder: HttpAsyncClientBuilder ->
                httpClientBuilder
                    .setMaxConnTotal(60)
                    .setMaxConnPerRoute(20)
            }

        val builder: RestClientBuilder = RestClient.builder(httpHost)
            .setRequestConfigCallback(requestConfigCallback)
            .setHttpClientConfigCallback(httpClientConfigCallback)

        return RestHighLevelClient(builder)
    }
}