package com.fcl.superagent.app;

import com.fcl.superagent.advisor.MyLoggerAdvisor;
import com.fcl.superagent.rag.GymAppRagCustomAdvisorFactory;
import com.fcl.superagent.rag.QueryRewriter;
import com.fcl.superagent.tools.ToolRegistration;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.fcl.superagent.config.AiConfig.SYSTEM_PROMPT;


@Component
@Slf4j
public class Gymapp {

    private final ChatClient chatClient;

    public Gymapp(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

     @Resource
     private VectorStore gymAppVectorStore;
     @Resource
     private Advisor gymAppVectorCloudStore;

//     @Resource
//     private VectorStore pgVectorVectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    /**
     * AI 基础对话（支持多轮对话记忆）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


    record Gymeport(String title, List<String> suggestions) {

    }

    /**
     * AI gym报告功能（实战结构化输出）
     *
     * @param message
     * @param chatId
     * @return
     */
    public Gymeport doChatWithReport(String message, String chatId) {
        Gymeport gymReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成训练/饮食结果，标题为{用户名}的健康报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(Gymeport.class);
        log.info("gymReport: {}", gymReport);
        return gymReport;
    }

    /**
     * AI 基础对话（Rag）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatwithRag(String message, String chatId) {
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);//查询重写和翻译
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(rewrittenMessage)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor()) //自定义日志Advisor
                .advisors(QuestionAnswerAdvisor.builder(gymAppVectorStore).build())// 应用 RAG 知识库问答
                .advisors(gymAppVectorCloudStore) // RAG 检索增强服务（基于云知识库服务）
                //.advisors(new QuestionAnswerAdvisor(pgVectorVectorStore)) //应用 RAG 检索增强服务（基于 PgVector 向量存储）
                // 应用自定义的 RAG 检索增强服务（文档查询器 + 上下文增强器）
//                .advisors(
//                        GymAppRagCustomAdvisorFactory.createGymAppRagCustomAdvisor(
//                                gymAppVectorStore, "增肌"
//                        ))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        return content;
    }

    @Resource
    ToolCallback allTools[];//工具回调

    // AI 调用 MCP 服务
    //@Resource
    //private ToolCallbackProvider toolCallbackProvider;

    /**
     * AI 基础对话（Tools）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatTools(String message, String chatId) {
        ChatClient.ChatClientRequestSpec requestSpec = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor()); //自定义日志Advisor
           requestSpec = requestSpec.toolCallbacks(allTools);

        ChatResponse chatResponse = requestSpec
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * AI 基础对话（Tools）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithMcp(String message, String chatId) {
        ChatClient.ChatClientRequestSpec requestSpec = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor()); //自定义日志Advisor
            //requestSpec = requestSpec.toolCallbacks(toolCallbackProvider);
        ChatResponse chatResponse = requestSpec
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * AI 基础对话（支持多轮对话记忆，SSE 流式传输）
     *
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }
}
