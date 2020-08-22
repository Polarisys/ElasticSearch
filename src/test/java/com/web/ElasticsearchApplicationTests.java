package com.web;

import com.alibaba.fastjson.JSON;
import com.web.pojo.User;
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
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ElasticsearchApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    /**
     * 创建索引
     */
    @Test
    void createIndex() throws IOException {
        //创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("tolaris");
        //客户端执行请求
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 获取索引
     */
    @Test
    void getIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("tolaris");
        boolean response = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 删除索引
     */
    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("tolaris");
        AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());

    }


    /**
     * 添加文档
     */
    @Test
    void addDocument() throws IOException {
        User user = new User("test1", 18);
        //创建请求
        IndexRequest request = new IndexRequest("tolaris");
        //创建规则
        request.id("2");
        request.timeout("1s");
        //数据放入请求
        request.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        System.out.println(response.status());
    }


    /**
     * 判断文档是存在
     */
    @Test
    void existDocument() throws IOException {
        GetRequest request = new GetRequest("tolaris", "1");
        //不获取返回的_source的上下文
        request.fetchSourceContext(new FetchSourceContext(false));
        boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }


    /**
     * 获取文档的信息
     */
    @Test
    void getDocument() throws IOException {
        GetRequest request = new GetRequest("tolaris", "1");
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        String source = response.getSourceAsString();
        System.out.println(source);
    }


    /**
     * 更新文档
     */
    @Test
    void updateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("tolaris", "1");
        request.timeout("1s");
        User user = new User("test2", 2);
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        System.out.println(response);

    }

    /**
     * 删除文档
     */
    @Test
    void deleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("tolaris", "2");
        request.timeout("1s");
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }


    /**
     * 批量插入数据
     */
    @Test
    void bulkDocument() throws IOException {
        BulkRequest request = new BulkRequest();
        request.timeout("10s");
        ArrayList<User> list = new ArrayList<>();
        list.add(new User(("test1"), 1));
        list.add(new User(("test2"), 2));
        list.add(new User(("test3"), 3));
        list.add(new User(("test4"), 4));
        list.add(new User(("test5"), 5));
        list.add(new User(("test6"), 6));
        //批处理请求
        for (int i = 0; i < list.size(); i++) {
            request.add(new IndexRequest("tolaris")
                    .id("" + (i + 1)).source(JSON.toJSONString(list.get(i)),
                            XContentType.JSON));
        }
        BulkResponse responses = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(responses.hasFailures());
    }

    /**
     * 查询文档
     */
    @Test
    void searchDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("tolaris");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder termQuery = QueryBuilders.termQuery("name", "test3");
        sourceBuilder.query(termQuery);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(searchResponse.getHits());
        System.out.println(searchResponse.getHits().getHits());
        System.out.println("================");
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
            System.out.println(hit.getSourceAsString());
        }
    }

}
