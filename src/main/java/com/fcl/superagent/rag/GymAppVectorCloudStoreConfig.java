package com.fcl.superagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class GymAppVectorCloudStoreConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String dashScopeApiKey;

    @Bean
    public Advisor gymAppVectorCloudStore() {
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(dashScopeApiKey).build(); //初始化阿里云API
        final String KNOWLEDGE_INDEX = "智能健身ai知识库";
        DocumentRetriever dashScopeDocumentRetriever = new DashScopeDocumentRetriever(dashScopeApi, //初始化文档检索器
            DashScopeDocumentRetrieverOptions.builder()
                    .withIndexName(KNOWLEDGE_INDEX)
                    .build()); //设置索引名称
        return RetrievalAugmentationAdvisor.builder() //构建检索增强Advisor
                .documentRetriever(dashScopeDocumentRetriever) //设置文档检索器
                .build(); //构建Advisor
    }

}
