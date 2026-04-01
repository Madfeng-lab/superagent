package com.fcl.superagent.agent;

import com.fcl.superagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 鱼皮的 AI 超级智能体（拥有自主规划能力，可以直接使用）
 */
@Component
public class MyManus extends ToolCallAgent {
    public MyManus(ToolCallback[] allTools, @Qualifier("dashScopeChatModel") ChatModel dashscopeChatModel) {
        super(allTools);//创建子类对象时，先初始化父类型特征。super只能找父类构造方法，this只能找当前类构造方法
        super.setName("MyManus");//这里和super等价，this找不到子类中的Name时，主动找父类中的Name,父类中没有，再找爷类 （父类中有了 就不会找爷类）
        //你是 MyManus，一位全能的 AI 助手，旨在解决用户提出的任何任务。你拥有各种工具，可以调用这些工具高效地完成复杂的请求。
        String SYSTEM_PROMPT = """
                You are MyManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        //根据用户需求，主动选择最合适的工具或工具组合。对于复杂任务，可以将问题分解，逐步使用不同的工具来解决。在使用每个工具后，清楚地解释执行结果并建议下一步操作。如果你想在任何时候停止互动，请使用 `terminate` 工具/函数调用。
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(20);
        // 初始化 AI 对话客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }

}
