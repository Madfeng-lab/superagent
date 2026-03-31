package com.fcl.superagent.rag;

import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * 基于 AI 的文档元信息增强器（为文档补充元信息）
 */
@Component
public class MyKeywordEnricher {

    @Resource
    @Qualifier("dashScopeChatModel")
    private ChatModel dashscopeChatModel;

    public List<Document> enrichDocuments(List<Document> documents) {
        org.springframework.ai.model.transformer.KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(dashscopeChatModel, 5);
        return  keywordMetadataEnricher.apply(documents);
    }
}

