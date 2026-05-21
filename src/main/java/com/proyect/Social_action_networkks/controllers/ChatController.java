package com.proyect.Social_action_networkks.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyect.Social_action_networkks.dto.Chat;
import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.modelo.Proyecto;
import com.proyect.Social_action_networkks.repository.FundacionRepository;
import com.proyect.Social_action_networkks.repository.ProyectoRepository;

@RestController
@RequestMapping("/chat")
@CrossOrigin
public class ChatController {

    private final ChatClient chatClient;

    private final ProyectoRepository proyectoRepository;

    private final FundacionRepository fundacionRepository;

    // memoria simple por usuario
    private final Map<String, List<Chat>> chatsPorUsuario = new HashMap<>();

    public ChatController(
            ChatClient.Builder chatClient,
            ProyectoRepository proyectoRepository,
            FundacionRepository fundacionRepository
    ) {

        this.chatClient = chatClient.build();
        this.proyectoRepository = proyectoRepository;
        this.fundacionRepository = fundacionRepository;
    }

    @PostMapping
    public List<Chat> chat(@RequestBody Chat request) {

        String usuario = request.getUsuario();
        String prompt = request.getContenido();

        chatsPorUsuario.putIfAbsent(usuario, new ArrayList<>());

        List<Chat> chatList = chatsPorUsuario.get(usuario);

        // guardar mensaje usuario
        chatList.add(new Chat("User", prompt));

        String promptLower = prompt.toLowerCase();

        String respuesta;

        // =====================================================
        // CONSULTAR CANTIDAD DE PROYECTOS
        // =====================================================

        if (promptLower.contains("cuantos proyectos")
                || promptLower.contains("cantidad de proyectos")) {

            long cantidad = proyectoRepository.count();

            respuesta = chatClient.prompt()
                    .user("""
                        El usuario preguntó cuantos proyectos existen.

                        Actualmente hay %s proyectos registrados.

                        Responde de forma amigable.
                        """.formatted(cantidad))
                    .call()
                    .content();
        }

        // =====================================================
        // MOSTRAR FUNDACIONES
        // =====================================================

        else if (promptLower.contains("fundaciones")
                || promptLower.contains("que fundaciones hay")) {

            List<Fundacion> fundaciones = fundacionRepository.findAll();

            if (fundaciones.isEmpty()) {

                respuesta = """
                        Actualmente no hay fundaciones registradas en FAN.
                        Más adelante podrán agregarse nuevas fundaciones.
                        """;
            }

            else {

                String nombres = fundaciones.stream()
                        .map(Fundacion::getNombre)
                        .collect(Collectors.joining(", "));

                respuesta = chatClient.prompt()
                        .user("""
                            Las fundaciones registradas son:

                            %s

                            Responde amigablemente y explica brevemente
                            que el usuario puede apoyar estas fundaciones.
                            """.formatted(nombres))
                        .call()
                        .content();
            }
        }

        // =====================================================
        // MOSTRAR PROYECTOS
        // =====================================================

        else if (promptLower.contains("proyectos")) {

            List<Proyecto> proyectos = proyectoRepository.findAll();

            if (proyectos.isEmpty()) {

                respuesta = """
                        Actualmente no existen proyectos registrados.
                        Próximamente habrá nuevos proyectos sociales disponibles.
                        """;
            }

            else {

                String nombres = proyectos.stream()
                        .map(Proyecto::getNombre)
                        .collect(Collectors.joining(", "));

                respuesta = chatClient.prompt()
                        .user("""
                            Los proyectos registrados son:

                            %s

                            Responde amigablemente y motiva al usuario
                            a participar o donar.
                            """.formatted(nombres))
                        .call()
                        .content();
            }
        }

        // =====================================================
        // CHAT NORMAL
        // =====================================================

        else {

            StringBuilder contexto = new StringBuilder();

            contexto.append("""
                Eres el asistente oficial de FAN
                (Foundation Action Network).

                Ayudas a usuarios sobre:
                - fundaciones
                - donaciones
                - voluntariado
                - proyectos sociales

                Nunca realizas acciones administrativas.

                Responde de forma amigable y clara.
                """);

            // historial conversacional
            for (Chat chat : chatList) {

                contexto.append(chat.getUsuario())
                        .append(": ")
                        .append(chat.getContenido())
                        .append("\n");
            }

            respuesta = chatClient.prompt()
                    .user(contexto.toString())
                    .call()
                    .content();
        }

        // guardar respuesta IA
        chatList.add(new Chat("ChatBot", respuesta));

        return chatList;
    }
}