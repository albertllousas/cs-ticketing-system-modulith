package prioritization.fixtures.ai

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.Generation
import org.springframework.ai.chat.prompt.Prompt

class FakeChatModel(var fixedAnswer: String) : ChatModel {
    override fun call(prompt: Prompt?): ChatResponse =
        ChatResponse.builder().generations(listOf(Generation(AssistantMessage(fixedAnswer)))).build()
}

class FakeChatClient(var fixedAnswer: String) : ChatClient {

    var receivedPrompt: String? = null

    override fun prompt(): ChatClient.ChatClientRequestSpec = TODO("Not yet implemented")
    override fun prompt(content: String): ChatClient.ChatClientRequestSpec =
        ChatClient.builder(FakeChatModel(fixedAnswer)).build().prompt(content).also { receivedPrompt = content }

    override fun prompt(prompt: Prompt): ChatClient.ChatClientRequestSpec = TODO("Not yet implemented")
    override fun mutate(): ChatClient.Builder = TODO("Not yet implemented")
}
