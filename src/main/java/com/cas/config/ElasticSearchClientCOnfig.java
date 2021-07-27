package com.cas.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiang_long
 * @version 1.0
 * @date 2021/7/27 4:13 下午
 * @desc
 */
@Configuration
public class ElasticSearchClientCOnfig {

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost("172.16.116.150", 9200, "http")));
    }

}
