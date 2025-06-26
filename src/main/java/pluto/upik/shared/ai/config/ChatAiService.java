package pluto.upik.shared.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatAiService {
    private final ChatClient chatClient;
    public ChatAiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    public String askToDeepSeekAI(String question){
        return chatClient.prompt(question)
                .call().content();
    }
    public Flux<String> askToDeepSeekAiWithStream(String quest){
        return chatClient.prompt(quest).stream().content();
    }
}
