package com.fcl.superagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class LangChainAiInvoke {

    public static void main(String[] args) {
        String apiKey = System.getenv("DASHSCOPE_API_KEY");
        ChatLanguageModel qwenChatModel = QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-max")
                .build();
        String answer = qwenChatModel.chat("你好");
        System.out.println(answer);
    }
}
