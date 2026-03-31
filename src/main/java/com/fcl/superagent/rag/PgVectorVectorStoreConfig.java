package com.fcl.superagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

// 为方便开发调试和部署，临时注释，如果需要使用 PgVector 存储知识库，取消注释即可
//@Configuration
public class PgVectorVectorStoreConfig {
    private static final int DASHSCOPE_EMBEDDING_BATCH_SIZE = 10;
    @Resource
    private GymAppDocumentLoader gymAppDocumentLoader;

    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
       

        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)
                .distanceType(COSINE_DISTANCE)
                .indexType(HNSW)
                .initializeSchema(true)
                .schemaName("public")
                .vectorTableName("superagentgym_vector_store")
                .maxDocumentBatchSize(DASHSCOPE_EMBEDDING_BATCH_SIZE)
                .build();
        // 加载文档
        List<Document> documents = gymAppDocumentLoader.loadMarkdowns();
        for (int i = 0; i < documents.size(); i += DASHSCOPE_EMBEDDING_BATCH_SIZE) {
            int end = Math.min(i + DASHSCOPE_EMBEDDING_BATCH_SIZE, documents.size());
            vectorStore.add(documents.subList(i, end));
        }
        return vectorStore;
    }
}
