package com.cas;

import com.alibaba.fastjson.JSON;
import com.cas.po.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@SpringBootTest
class CasEsApplicationTests {
    private static final String INDEX = "xianglong";

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    // 测试获取索引
    @Test
    void contextLoads() throws IOException {
        CreateIndexRequest cas = new CreateIndexRequest(INDEX);
        CreateIndexResponse response = client.indices().create(cas, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    // 测试删除索引
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(INDEX);
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    /**
     * 测试添加文档
     */
    @Test
    void testAddDocument() throws IOException {
        // 创建对象
        User user = new User("向龙", 3);

        // 创建请求
        IndexRequest request = new IndexRequest(INDEX);

        // 规则 put /xianglong/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));

        // 将我们的数据放入请求 json
        request.source(JSON.toJSONString(user), XContentType.JSON);

        // 客户端发送请求，获取响应的结果
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status()); //
    }

    // 获取文档，判断是否存在 get /index/doc/1
    @Test
    void testIsExists() throws IOException {
        GetRequest request = new GetRequest(INDEX, "1");
        // 不获取返回的 _source 的上下文
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");

        boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    // 获取文档信息
    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest(INDEX, "4");
        GetResponse getResponse = client.get(request, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());
    }

    // 更新文档信息
    @Test
    void testUpdateRequest() throws IOException {
        UpdateRequest request = new UpdateRequest(INDEX, "1");
        request.timeout("1s");

        User user = new User("向龙123", 24);
        request.doc(JSON.toJSONString(user), XContentType.JSON);

        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);

        System.out.println(response.status());
    }

    // 删除文档信息
    @Test
    void testDeleteRequest() throws IOException {
        DeleteRequest request = new DeleteRequest(INDEX, "1");
        request.timeout("1s");

        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

     // 批量插入？
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("xl", 25));
        userList.add(new User("xl", 25));
        userList.add(new User("xl", 25));
        userList.add(new User("xl", 26));
        userList.add(new User("ll", 27));

        for (int i = 0; i < userList.size(); i++) {
            bulkRequest.add(
                    new IndexRequest(INDEX)
                    .id("" + (i + 1))
                    .source(JSON.toJSONString(userList.get(i)),XContentType.JSON));
        }

        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());
    }

    // 查询
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("eslog");
        // 构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 查询条件，我们可以使用 QueryBuilders 工具来实现
        // QueryBuilders.termQuery 精确
        // QueryBuilders.matchAllQuery() 匹配所有
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();

        SearchSourceBuilder query = sourceBuilder.query(matchAllQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(query);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(searchResponse.getHits());
        System.out.println("===================");
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            System.out.println(documentFields.getSourceAsString());
        }

    }


}
