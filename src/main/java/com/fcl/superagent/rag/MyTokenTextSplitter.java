package com.fcl.superagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 自定义基于 Token 的切词器
 */
@Configuration
class MyTokenTextSplitter {
 public List<Document> splitDocuments(List<Document> documents) {
    TokenTextSplitter splitter = new TokenTextSplitter();
    return splitter.apply(documents);
 }

 public List<Document> splitCustomized(List<Document> documents) {
   TokenTextSplitter splitter = new TokenTextSplitter(200, 100, 10, 5000, true);
   return splitter.apply(documents);
 }
}

