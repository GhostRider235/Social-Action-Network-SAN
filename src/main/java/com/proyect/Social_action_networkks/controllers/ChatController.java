package com.proyect.Social_action_networkks.controllers;

import com.proyect.Social_action_networkks.dto.Chat;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/chat")
@CrossOrigin
public class ChatController {

    private final ChatClient chatClient;

    // chats por usuario
    private final Map<String, List<Chat>> chatsPorUsuario = new HashMap<>();

    public ChatController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @PostMapping
    public List<Chat> chat(@RequestBody Chat request) {

    String usuario = request.getUsuario();
    String prompt = request.getContenido();

    chatsPorUsuario.putIfAbsent(usuario, new ArrayList<>());

    List<Chat> chatList = chatsPorUsuario.get(usuario);

    chatList.add(new Chat("User", prompt));

    String respuesta = chatClient.prompt().user(prompt).call().content();

    chatList.add(new Chat("ChatBot", respuesta));

    return chatList;
    }
}