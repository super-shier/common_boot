package com.shier.elasticseach.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shier.elasticseach.model.param.ManSearchParam;
import com.shier.elasticseach.model.param.UserSearchParam;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liyunbiao
 * @Date: 2019/5/5 9:49
 */
@Component
public class ElasticOperationService {

    private final Logger logger = LoggerFactory.getLogger(ElasticOperationService.class);

    @Autowired
    private Client client;

    private BulkProcessor bulkProcessor;

    @PostConstruct
    public void initBulkProcessor() {

        bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {

            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                logger.info("序号：{} 开始执行{} 条记录保存",executionId,request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                logger.error(String.format("序号：%s 执行失败; 总记录数：%s",executionId,request.numberOfActions()),failure);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                logger.info("序号：{} 执行{}条记录保存成功,耗时：{}毫秒,",executionId,request.numberOfActions(),response.getIngestTookInMillis());
            }
        }).setBulkActions(1000)
                .setBulkSize(new ByteSizeValue(10, ByteSizeUnit.MB))
                .setConcurrentRequests(4)
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(500),3))  //失败后等待多久及重试次数
                .build();
    }


    @PreDestroy
    public void closeBulk() {
        if(bulkProcessor != null) {
            try {
                bulkProcessor.close();
            }catch (Exception e) {
                logger.error("close bulkProcessor exception",e);
            }
        }
    }


    /**
     * 批量添加,性能最好
     *
     */
    public void addDocumentToBulkProcessor(String indices, String type, Object object) {
        bulkProcessor.add(client.prepareIndex(indices, type).setSource(JSONObject.toJSON(object)).request());
    }


    public void addDocument(String indices, String type, Object object) {
        String jsonStr= JSON.toJSONString(object);
        IndexRequest req = new IndexRequest(indices, type);
        req.source(jsonStr, XContentType.JSON);
        ActionFuture<IndexResponse> response = client.index(req);
        logger.info("添加结果：{}",response.toString());
    }

    /**
     * 按id删除
     *
     */
    public void deleteDocumentById(String index, String type, String id) {
        DeleteResponse resp = client.prepareDelete(index, type, id).get();
        logger.info("删除结果：{}",resp.toString());
    }

    /**
     * 按条件删除
     *
     */
    public void deleteDocumentByQuery(String index, String type, UserSearchParam param) {

        //DeleteByQueryRequestBuilder builder = new DeleteByQueryRequestBuilder(client,DeleteByQueryAction.INSTANCE);
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(client);

        //builder.filter(convertParam(param));
        builder.source().setIndices(index).setTypes(type).setQuery(convertParam(param));
        BulkByScrollResponse resp = builder.get();
        logger.info("删除结果：{}",resp.toString());
    }

    /**
     * 按ID更新
     *
     */
    public void updateDocument(String indices, String type,String id,Object object) throws Exception {
        UpdateRequest update = new UpdateRequest(indices, type, id);
        Field[] fields = object.getClass().getDeclaredFields();

            XContentBuilder builder= XContentFactory.jsonBuilder().startObject();
            for(int i = 0 , len = fields.length; i < len; i++) {
                // 对于每个属性，获取属性名
                String varName = fields[i].getName();
                    //获取原来的访问控制权限
                    boolean accessFlag = fields[i].isAccessible();
                    // 修改访问控制权限
                    fields[i].setAccessible(true);
                    // 获取在对象f中属性fields[i]对应的对象中的变量
                    Object o= fields[i].get(object);
                        if (o != null){
                            builder.field(varName, o);
                        }
                    // 恢复访问控制权限
                    fields[i].setAccessible(accessFlag);
            }
            builder.endObject();
            update.doc(builder);
            client.update(update).get();
    }

    /**
     * 按条件更新
     *
     */
    public void updateDocumentByQuery(String indices, String type, Object object,UserSearchParam param) {
        //UpdateByQueryRequestBuilder builder = new UpdateByQueryRequestBuilder(client,UpdateByQueryAction.INSTANCE);
        UpdateByQueryRequestBuilder builder = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
        builder.source().setIndices(indices).setTypes(type).setQuery(convertParam(param));
    }


    public <T> List<T> queryDocumentByParam(String indices, String type,UserSearchParam param,Class<T> clazz) {
        SearchRequestBuilder builder = buildRequest(indices,type);
        builder.addSort("birthday", SortOrder.ASC);
        builder.setQuery(convertParam(param));
        builder.setFrom(0).setSize(10);
        SearchResponse resp = builder.get();
        return convertResponse(resp,clazz);
    }

    public <T> List<T> queryDocumentByManParam(String indices, String type, ManSearchParam param, Class<T> clazz) {
        SearchRequestBuilder builder = buildRequest(indices,type);
        builder.addSort("date", SortOrder.ASC);
        builder.setQuery(convertManParam(param));
        builder.setFrom(0).setSize(10);
        SearchResponse resp = builder.get();
        return convertResponse(resp,clazz);
    }

    public Object queryDocumentById(String index, String type,String id){
        if(id.isEmpty()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        GetResponse result = this.client.prepareGet(index, type, id).get();
        if(!result.isExists()){
            //return new ResponseEntity(HttpStatus.NOT_FOUND);
            return null;
        }
        return result.getSource();
    }
    private BoolQueryBuilder convertParam(UserSearchParam param) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.hasText(param.getUserName())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("userName", param.getUserName()));
        }
        if (param.getAge() != null) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("age").gt(param.getAge()));
        }
        if (StringUtils.hasText(param.getDescription())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("description", param.getDescription()));
        }
        if(StringUtils.hasText(param.getRoleName())) {
            boolQueryBuilder.must(QueryBuilders.nestedQuery("roles", QueryBuilders.termQuery("roles.name", param.getRoleName()), ScoreMode.None));
        }

        return boolQueryBuilder;
    }

    private BoolQueryBuilder convertManParam(ManSearchParam param) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.hasText(param.getName())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("name", param.getName()));
        }
        if (StringUtils.hasText(param.getId())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("id", param.getId()));
        }
        if (param.getAge() != null) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("age").gt(param.getAge()));
        }
        if (StringUtils.hasText(param.getCountry())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("country", param.getCountry()));
        }
//        if(StringUtils.hasText(param.getRoleName())) {
//            boolQueryBuilder.must(QueryBuilders.nestedQuery("roles", QueryBuilders.termQuery("roles.name", param.getRoleName()), ScoreMode.None));
//        }
        return boolQueryBuilder;
    }


    /**
     * 通用的装换返回结果
     *
     */
    public <T> List<T> convertResponse(SearchResponse response, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        if(response != null && response.getHits() != null) {
            String result ="";
            T e = null;
            Field idField = ReflectionUtils.findField(clazz, "id");
            if (idField != null) {
                ReflectionUtils.makeAccessible(idField);
            }
            for(SearchHit hit : response.getHits()) {
                result = hit.getSourceAsString();
                if (StringUtils.hasText(result)) {
                    e = JSONObject.toJavaObject(JSONObject.parseObject(result), clazz);
                }
                if (e != null) {
                    if (idField != null) {
                        ReflectionUtils.setField(idField, e, hit.getId());
                    }
                    list.add(e);
                }
            }
        }
        return list;
    }

    public SearchRequestBuilder buildRequest(String indices, String type) {
        return client.prepareSearch(indices).setTypes(type);
    }

    /**
     * 不存在就创建索引
     *
     */
    public boolean createIndexIfNotExist(String index, String type) {
        IndicesAdminClient adminClient = client.admin().indices();
        IndicesExistsRequest request = new IndicesExistsRequest(index);
        IndicesExistsResponse response = adminClient.exists(request).actionGet();
        if (!response.isExists()) {
            return createIndex(index, type);
        }
        return true;
    }

    /**
     * 创建索引
     *
     */
    /**
     * 创建索引
     *
     */
    public boolean createIndex(String index, String type) {
        XContentBuilder mappingBuilder;
        try {
            mappingBuilder = this.getMapping(type);
        } catch (Exception e) {
            logger.error(String.format("创建Mapping 异常；index:%s type:%s,", index, type), e);
            return false;
        }
        Settings settings = Settings.builder().put("index.number_of_shards", 2)
                .put("index.number_of_replicas", 1)
                .put("index.refresh_interval", "5s").build();
        IndicesAdminClient adminClient = client.admin().indices();
        CreateIndexRequestBuilder builder = adminClient.prepareCreate(index);
        builder.setSettings(settings);
        CreateIndexResponse response = builder.addMapping(type, mappingBuilder).get();
        logger.info("创建索引：{} 类型:{} 是否成功：{}", index, type, response.isAcknowledged());
        return response.isAcknowledged();
    }

    /***
     * 创建索引的Mapping信息  注意声明的roles为nested类型
     */
    private XContentBuilder getMapping(String type) throws Exception {
        XContentBuilder mappingBuilder = XContentFactory.jsonBuilder().startObject().startObject(type)
                .startObject("_all").field("enabled", false).endObject()
                .startObject("properties")
                .startObject("userName").field("type", "keyword").endObject()
                .startObject("age").field("type", "integer").endObject()
                .startObject("birthday").field("type", "date").endObject()
                .startObject("description").field("type", "text").field("analyzer", "ik_smart").endObject()
                .startObject("roles").field("type", "nested")
                .startObject("properties")
                .startObject("createTime").field("type","date").endObject()
                .startObject("name").field("type","keyword").endObject()
                .startObject("description").field("type","text").field("analyzer", "ik_smart").endObject()
                .endObject()
                .endObject()
                .endObject()
                .endObject().endObject();
        return mappingBuilder;
    }

}
