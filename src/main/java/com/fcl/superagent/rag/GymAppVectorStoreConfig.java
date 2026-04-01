package com.fcl.superagent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/*
* 向量数据库配置（初始化基于内存的向量数据库 Bean）
* */


@Configuration
@Slf4j
public class GymAppVectorStoreConfig {

    @Resource
    private GymAppDocumentLoader gymAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;
    @Bean
    public VectorStore gymAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {

        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        List<Document> documents = gymAppDocumentLoader.loadMarkdowns();
        if (documents == null || documents.isEmpty()) {
            log.warn("知识库文档为空，返回空向量库（RAG 将不可用）");
            return simpleVectorStore;
        }
        //自主切割文档
        //List<Document>  splitDocuments = myTokenTextSplitter.splitCustomized(documents);
        // 自动补充关键词元信息(基于ai)
        List<Document>  enrichedDocuments = myKeywordEnricher.enrichDocuments(documents);
        if (enrichedDocuments == null || enrichedDocuments.isEmpty()) {
            log.warn("文档增强结果为空，返回空向量库（RAG 将不可用）");
            return simpleVectorStore;
        }
        simpleVectorStore.add(enrichedDocuments);
        return simpleVectorStore;
    }
}
