package com.fcl.superagent.controller;

import com.fcl.superagent.agent.MyManus;
import com.fcl.superagent.app.Gymapp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private Gymapp gymApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource(name = "dashScopeChatModel")
    private ChatModel chatModel;

    /**
     * 同步调用 AI 恋爱大师应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/gym_app/chat/sync")
    public String doChatWithGymAppSync(String message, String chatId) {
        return gymApp.doChat(message, chatId);
    }

    /**
     * SSE 流式调用 AI 恋爱大师应用
     *
     * @param message
     * @param chatId
     * @return  定义以文本流的方式返回
     */
    @GetMapping(value = "/gym_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithGymAppSSE(String message, String chatId) {
        return gymApp.doChatByStream(message, chatId);
    }

    /**
     * SSE 流式调用 AI 应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/gym_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithGymAppServerSentEvent(String message, String chatId) {
        return gymApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE 流式调用 AI 应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/gym_app/chat/sse_emitter")
    public SseEmitter doChatWithGymAppServerSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(180000L); // 3 分钟超时
        // 获取 Flux 响应式数据流并且直接通过订阅推送给 SseEmitter
        gymApp.doChatByStream(message, chatId)  //flux.subscribe(onNext, onError, onComplete);
                .subscribe(chunk -> {//每当流里有一个新的字符串 chunk，就执行一次大括号里的逻辑 发给前
                    try {
                        sseEmitter.send(chunk); //给前端发消息
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        // 返回
        return sseEmitter;
        /*chunk：lambda 的形参，表示 Flux 每吐出来的一条数据。
         doChatByStream 是 Flux<String>，所以这里每个 chunk 实际上就是一段 字符串（模型流式回复里的一小块文本）。
        ->：lambda 箭头，左边是参数，右边是要执行的代码。
        { ... }：参数体；多行或需要写 try/catch 时用大括号包起来。*/
        /*静态方法引用 格式：类名::静态方法名
        实例方法引用（特定对象） 格式：对象名::实例方法名

        实例方法引用（任意对象） 格式：类名::实例方法名
        构造函数引用 格式：类名::new 示例：*/
    }

    /**
     * 流式调用 Manus 超级智能体
     *
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        MyManus yuManus = new MyManus(allTools, chatModel);
        return yuManus.runStream(message);
    }

}
