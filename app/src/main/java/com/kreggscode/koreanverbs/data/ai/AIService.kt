package com.kreggscode.koreanverbs.data.ai

import android.content.Context
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.net.URLEncoder

@Serializable
data class Message(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class AIRequest(
    val model: String = "openai",
    val messages: List<AIMessage>,
    val temperature: Float = 1.0f,
    @kotlinx.serialization.SerialName("max_tokens")
    val maxTokens: Int = 1000,
    val stream: Boolean = false
)

@Serializable
data class AIMessage(
    val role: String,
    val content: String
)

@Serializable
data class AIResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: AIResponseMessage,
    val index: Int,
    @kotlinx.serialization.SerialName("finish_reason")
    val finishReason: String? = null
)

@Serializable
data class AIResponseMessage(
    val role: String,
    val content: String
)

class AIService(private val context: Context) {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = 120000
            connectTimeoutMillis = 120000
            socketTimeoutMillis = 120000
        }
    }
    
    private val systemPrompt = """You are a Korean language tutor. CRITICAL RULES:
        
        1. RESPOND IN THE SAME LANGUAGE AS THE USER
           - English question → English answer
           - Spanish question → Spanish answer  
           - Russian question → Russian answer
           - Korean question → Korean answer
        
        2. NEVER respond in Korean unless the user writes in Korean
        
        3. Format: Explanation in user's language + Korean examples with romanization
           Example: 안녕하세요 (annyeonghaseyo - hello)
        
        4. Structure:
           - Explain concepts in user's language
           - Show Korean words with romanization
           - Translate meanings to user's language
        
        ENGLISH example:
        "The verb 가다 (gada) means 'to go'.
        
        **Present tense:**
        • 갑니다 (gamnida) - I go (formal)
        • 가요 (gayo) - I go (polite)"
        
        SPANISH example:
        "El verbo 가다 (gada) significa 'ir'.
        
        **Tiempo presente:**
        • 갑니다 (gamnida) - Yo voy (formal)
        • 가요 (gayo) - Yo voy (cortés)"
        
        NEVER write full explanations in Korean. Korean is only for examples."""
    
    suspend fun getResponse(userMessage: String, conversationHistory: List<Message>): String {
        return withContext(Dispatchers.IO) {
            try {
                // Use POST method with JSON body as per Pollinations.AI documentation
                return@withContext getOpenAIResponse(userMessage)
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext getFallbackResponse(userMessage)
            }
        }
    }
    
    private suspend fun getOpenAIResponse(userMessage: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://text.pollinations.ai/openai"
                
                val requestBody = AIRequest(
                    model = "openai",
                    messages = listOf(
                        AIMessage(
                            role = "system",
                            content = systemPrompt
                        ),
                        AIMessage(
                            role = "user",
                            content = userMessage
                        )
                    ),
                    temperature = 1.0f,
                    maxTokens = 1500,
                    stream = false
                )
                
                println("🔗 AI Request URL: $url")
                println("📤 Request body: model=${requestBody.model}, temp=${requestBody.temperature}")
                
                val response: HttpResponse = client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }
                
                println("📥 AI Response Status: ${response.status}")
                
                if (response.status.isSuccess()) {
                    val aiResponse = response.bodyAsText()
                    println("✅ Raw response: ${aiResponse.take(200)}...")
                    
                    // Parse the JSON response
                    val json = Json { ignoreUnknownKeys = true }
                    val parsedResponse = json.decodeFromString<AIResponse>(aiResponse)
                    
                    val content = parsedResponse.choices.firstOrNull()?.message?.content
                    if (content.isNullOrBlank()) {
                        println("⚠️ WARNING: Response content is empty!")
                        return@withContext "I received an empty response. Please try again."
                    }
                    
                    println("✅ AI Response SUCCESS: ${content.take(100)}...")
                    return@withContext content
                } else {
                    val errorBody = response.bodyAsText()
                    println("❌ AI Request failed with status: ${response.status}")
                    println("❌ Error body: $errorBody")
                    return@withContext "Connection error: ${response.status}. Please check your internet connection and try again."
                }
            } catch (e: Exception) {
                println("❌ AI Request exception: ${e.javaClass.simpleName} - ${e.message}")
                e.printStackTrace()
                return@withContext "Network error: ${e.message}. Please check your internet connection and try again."
            }
        }
    }
    
    private fun getFallbackResponse(userMessage: String): String {
        val lowercaseMessage = userMessage.toLowerCase()
        
        // Check if this is a verb explanation request - don't give generic response
        if (lowercaseMessage.contains("explain the korean verb") || 
            lowercaseMessage.contains("detailed meaning") ||
            lowercaseMessage.contains("conjugation tips")) {
            return "I apologize, but I'm currently unable to provide a detailed explanation for this specific verb. " +
                   "Please check your internet connection and try again. The AI service will provide comprehensive " +
                   "information about this verb including usage patterns, conjugation tips, and example sentences."
        }
        
        return when {
            lowercaseMessage.contains("hello") || lowercaseMessage.contains("hi") || 
            lowercaseMessage.contains("안녕") -> {
                "안녕하세요! (annyeonghaseyo) - Hello! 👋\n\n" +
                "I'm your Korean language tutor. I can help you learn Korean verbs, grammar, and conversation. " +
                "What would you like to practice today?"
            }
            
            lowercaseMessage.contains("conjugate") && lowercaseMessage.contains("가다") -> {
                "Let me explain how to conjugate 가다 (gada - to go):\n\n" +
                "**Present Tense:**\n" +
                "• Formal: 갑니다 (gamnida)\n" +
                "• Polite: 가요 (gayo)\n" +
                "• Casual: 가 (ga)\n\n" +
                "**Past Tense:**\n" +
                "• Formal: 갔습니다 (gasseumnida)\n" +
                "• Polite: 갔어요 (gasseoyo)\n" +
                "• Casual: 갔어 (gasseo)\n\n" +
                "**Future Tense:**\n" +
                "• Will go: 갈 거예요 (gal geoyeyo)\n" +
                "• Going to go: 갈 것입니다 (gal geosimnida)\n\n" +
                "Practice sentence: 학교에 가요 (hakgyoe gayo) - I go to school 📚"
            }
            
            lowercaseMessage.contains("verb") || lowercaseMessage.contains("동사") -> {
                "Here are some essential Korean verbs to learn:\n\n" +
                "📚 **Daily Actions:**\n" +
                "• 먹다 (meokda) - to eat\n" +
                "• 마시다 (masida) - to drink\n" +
                "• 자다 (jada) - to sleep\n" +
                "• 일어나다 (ireonada) - to wake up\n" +
                "• 공부하다 (gongbuhada) - to study\n\n" +
                "💡 **Tip:** Most Korean verbs end in -다 (-da) in their dictionary form. " +
                "To conjugate them, you remove -다 and add the appropriate ending!\n\n" +
                "Would you like to practice conjugating any of these verbs?"
            }
            
            lowercaseMessage.contains("grammar") || lowercaseMessage.contains("문법") -> {
                "Korean grammar has some unique features! Here are key points:\n\n" +
                "📝 **Word Order:** Korean follows Subject-Object-Verb (SOV)\n" +
                "Example: 나는 사과를 먹어요 (naneun sagwareul meogeoyo)\n" +
                "I (는) apple (를) eat\n\n" +
                "📝 **Particles:** Small words that mark grammatical functions\n" +
                "• 은/는 - topic marker\n" +
                "• 이/가 - subject marker\n" +
                "• 을/를 - object marker\n" +
                "• 에 - location/time marker\n\n" +
                "📝 **Honorifics:** Different speech levels show respect\n" +
                "• Formal (습니다)\n" +
                "• Polite (요)\n" +
                "• Casual (no ending)\n\n" +
                "Which aspect would you like to explore more? 😊"
            }
            
            lowercaseMessage.contains("thank") || lowercaseMessage.contains("고맙") || 
            lowercaseMessage.contains("감사") -> {
                "천만에요! (cheonmaneyo) - You're welcome! 😊\n\n" +
                "Other ways to say thank you in Korean:\n" +
                "• 감사합니다 (gamsahamnida) - Thank you (formal)\n" +
                "• 고맙습니다 (gomapseumnida) - Thank you (formal)\n" +
                "• 고마워요 (gomawoyo) - Thank you (polite)\n" +
                "• 고마워 (gomawo) - Thanks (casual)\n\n" +
                "Keep practicing, you're doing great! 화이팅! (hwaiting - fighting/you can do it!)"
            }
            
            else -> {
                "That's an interesting question about Korean! While I'm currently offline, " +
                "I can help you with:\n\n" +
                "• Korean verb conjugations\n" +
                "• Basic grammar rules\n" +
                "• Common phrases and expressions\n" +
                "• Hangul (Korean alphabet) basics\n" +
                "• Pronunciation tips\n\n" +
                "Please try asking about any of these topics, or check your internet connection " +
                "for more detailed AI-powered responses! 📚✨"
            }
        }
    }
    
    fun onDestroy() {
        client.close()
    }
}
