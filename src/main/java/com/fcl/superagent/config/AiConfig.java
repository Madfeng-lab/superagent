package com.fcl.superagent.config;

import com.fcl.superagent.advisor.MyLoggerAdvisor;
import com.fcl.superagent.advisor.ReReadingAdvisor;

import com.fcl.superagent.chatmemory.FileBasedChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    public static final String SYSTEM_PROMPT = "扮演一位深耕健身训练与运动营养领域的专家。开场向用户表明身份，告知用户可以咨询健身、饮食、减脂、增肌等相关问题。\n" +
            "围绕三种目标人群提问：\n" +
            "减脂人群：询问当前体重、体脂情况、饮食习惯以及是否遇到减脂瓶颈或反弹问题；\n" +
            "增肌人群：询问训练频率、蛋白质摄入、力量水平以及是否遇到增长停滞；\n" +
            "日常健康/塑形人群：询问运动习惯、久坐情况、作息规律以及是否存在体态或精力问题。\n" +
            "引导用户详细描述自己的训练内容、饮食结构、作息情况以及目前遇到的具体困扰，以便提供个性化的训练与营养优化方案。";
    //memory
    @Bean
    public ChatMemory chatMemory() {
        // String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        // return  new FileBasedChatMemory(fileDir);
       return MessageWindowChatMemory.builder()
               .chatMemoryRepository(new InMemoryChatMemoryRepository())
               .maxMessages(10)
               .build();
    }

    //ai初始化配置
    @Bean
    public ChatClient chatClient(@Qualifier("dashScopeChatModel") ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
               // .defaultSystem(SYSTEM_PROMPT) //系统提示词
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(), //对话记忆Advisor
                        new MyLoggerAdvisor()) //自定义日志Advisor，可按需开启
                        //,new ReReadingAdvisor())//自定义推理增强Advisor，可按需开启
                .build();
    }
}
