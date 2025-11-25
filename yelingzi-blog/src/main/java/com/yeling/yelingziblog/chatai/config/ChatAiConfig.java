package com.yeling.yelingziblog.chatai.config;




import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeAgentProperties;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeChatProperties;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ChatAiConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem(
                        """
                                # 角色定位
                                你是叶玲子的小栈博客专属看板娘"小玲"，性格活泼开朗、元气满满，擅长用亲切可爱的语气与访客互动。你的存在是让每位来访者感受到博客的温暖与活力，像朋友一样自然交流。
                                                        
                                ## 沟通风格
                                1. 称呼用户时用"{nickname}"，回复开头可加可爱语气词（如"哈喽~"、"嗨呀~"）
                                2. 多用轻松的口语化表达，适当加入颜文字(≧∇≦)ﾉ或小符号增加亲切感
                                3. 遇到技术问题时保持专业但不失活泼，复杂内容会耐心拆解说明
                                                        
                                # 核心任务
                                - 热情回应访客的咨询，包括博客内容、技术问题、日常交流等
                                - 维护友好轻松的沟通氛围，让访客感受到被重视
                                - 展现对叶玲子博客的熟悉度，成为连接博主与访客的桥梁
                                - 调用合适的工具满足用户的需求，可向这些工具提供参数：user="{user}"
                                                        
                                # 系统工具
                                1. 添加友链
                                2. 留言板留言
                                3. 查看热门文章
                                                        
                                # 禁止行为
                                1. 不脱离 “博客看板娘” 身份，不聊与博客无关的敏感话题（如时政、争议事件）。
                                2. 不随意承诺未明确的内容（如 “一定让玲子明天回复你”，需改为 “会尽快帮你转达，玲子看到后会及时回复哦～”）。
                                """)
                .build();
    }

    /***
     * open-ai多模型接口  已废弃
     *
     */
//    @Bean
//    @Primary
//    public OpenAiApi baseOpenAiApi(RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder) {
//        // 注意：这里使用默认的路径，这些路径通常与OpenAI API兼容
//        String completionsPath = "/chat/completions";
//        String embeddingsPath = "/embeddings";
//
//        // 创建一个假的ApiKey，因为我们会通过mutate覆盖
//        ApiKey apiKey = new SimpleApiKey("your-api-key");
//
//        //  headers 可以根据需要设置，比如Content-Type
//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//        headers.add("Content-Type", "application/json");
//
//        //  responseErrorHandler 可以使用默认的，或者自定义
//        ResponseErrorHandler responseErrorHandler = new DefaultResponseErrorHandler();
//
//        return new OpenAiApi(
//                "https://api.openai.com", // 基础URL，后面会被覆盖
//                apiKey,
//                headers,
//                completionsPath,
//                embeddingsPath,
//                restClientBuilder,
//                webClientBuilder,
//                responseErrorHandler
//        );
//    }

//    @Bean
//    public ChatClient planningChatClient(DashScopeChatModel chatModel,
//                                         DashScopeChatProperties  properties) {
//
//
//        DashScopeChatOptions options = DashScopeChatOptions.fromOptions(properties.getOptions());
//        options.setTemperature(0.5);
//
//        return ChatClient.builder(chatModel)
//                .defaultSystem("""
//                # 博客助手任务规则
//                ## 1.要求
//                ### 1.1 根据用户输入识别任务
//
//                ## 2.任务
//                ### 2.1 JobType: 添加友链(AddFriendLink) 要求用户提供网站标题、封面图片、简介以及链接。
//                """)
//                .defaultOptions(options)
//                .build();
//    }


}
