package com.fcl.superagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档加载器
 */
@Component
@Slf4j
public class GymAppDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;//资源解析器

    public GymAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载多篇 Markdown 文档
     * @return
     */
    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            String strlist = "习惯,健康,健身,增肌,减脂,饮食,营养,运动";
            String[] liststatu = strlist.split(",");
            int i = 0;
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()//构建配置
                        .withHorizontalRuleCreateDocument(true) //是否创建文档
                        .withIncludeCodeBlock(false)    //是否包含代码块
                        .withIncludeBlockquote(false)  //是否包含引用块
                        .withAdditionalMetadata("filename", filename) //文件名
                        //.withAdditionalMetadata("status", liststatu[i]) //状态
                        .build(); //构建配置
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(markdownDocumentReader.get());
                i++;
            }

        } catch (IOException e) {
           log.error("Markdown 文档加载失败", e);
        }
        return allDocuments;
    }
}
