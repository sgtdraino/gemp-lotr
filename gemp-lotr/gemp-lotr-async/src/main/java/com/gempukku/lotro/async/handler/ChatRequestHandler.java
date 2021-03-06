package com.gempukku.lotro.async.handler;

import com.gempukku.lotro.PrivateInformationException;
import com.gempukku.lotro.SubscriptionExpiredException;
import com.gempukku.lotro.async.HttpProcessingException;
import com.gempukku.lotro.async.ResponseWriter;
import com.gempukku.lotro.chat.ChatCommandErrorException;
import com.gempukku.lotro.chat.ChatMessage;
import com.gempukku.lotro.chat.ChatRoomMediator;
import com.gempukku.lotro.chat.ChatServer;
import com.gempukku.lotro.game.ChatCommunicationChannel;
import com.gempukku.lotro.game.Player;
import com.gempukku.polling.LongPollingResource;
import com.gempukku.polling.LongPollingSystem;
import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.*;

public class ChatRequestHandler extends LotroServerRequestHandler implements UriRequestHandler {
    private ChatServer _chatServer;
    private LongPollingSystem _longPollingSystem;

    public ChatRequestHandler(Map<Type, Object> context) {
        super(context);
        _chatServer = extractObject(context, ChatServer.class);
        _longPollingSystem = extractObject(context, LongPollingSystem.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if (uri.startsWith("/") && request.getMethod() == HttpMethod.GET) {
            getMessages(request, URLDecoder.decode(uri.substring(1)), responseWriter);
        } else if (uri.startsWith("/") && request.getMethod() == HttpMethod.POST) {
            postMessages(request, URLDecoder.decode(uri.substring(1)), responseWriter);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void postMessages(HttpRequest request, String room, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String message = getFormParameterSafely(postDecoder, "message");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        ChatRoomMediator chatRoom = _chatServer.getChatRoom(room);
        if (chatRoom == null)
            throw new HttpProcessingException(404);

        try {
            if (message != null && message.trim().length() > 0) {
                chatRoom.sendMessage(resourceOwner.getName(), StringEscapeUtils.escapeHtml(message), resourceOwner.getType().contains("a"));
                responseWriter.writeXmlResponse(null);
            } else {
                ChatCommunicationChannel pollableResource = chatRoom.getChatRoomListener(resourceOwner.getName());
                ChatUpdateLongPollingResource polledResource = new ChatUpdateLongPollingResource(chatRoom, room, resourceOwner.getName(), responseWriter);
                _longPollingSystem.processLongPollingResource(polledResource, pollableResource);
            }
        } catch (SubscriptionExpiredException exp) {
            throw new HttpProcessingException(410);
        } catch (PrivateInformationException exp) {
            throw new HttpProcessingException(403);
        } catch (ChatCommandErrorException exp) {
            throw new HttpProcessingException(400);
        }
    }

    private class ChatUpdateLongPollingResource implements LongPollingResource {
        private ChatRoomMediator _chatRoom;
        private String _room;
        private String _playerId;
        private ResponseWriter _responseWriter;
        private boolean _processed;

        private ChatUpdateLongPollingResource(ChatRoomMediator chatRoom, String room, String playerId, ResponseWriter responseWriter) {
            _chatRoom = chatRoom;
            _room = room;
            _playerId = playerId;
            _responseWriter = responseWriter;
        }

        @Override
        public synchronized boolean wasProcessed() {
            return _processed;
        }

        @Override
        public synchronized void processIfNotProcessed() {
            if (!_processed) {
                try {
                    List<ChatMessage> chatMessages = _chatRoom.getChatRoomListener(_playerId).consumeMessages();

                    Collection<String> usersInRoom = _chatRoom.getUsersInRoom();

                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                    Document doc = documentBuilder.newDocument();

                    serializeChatRoomData(_room, chatMessages, usersInRoom, doc);

                    _responseWriter.writeXmlResponse(doc);
                } catch (SubscriptionExpiredException exp) {
                    _responseWriter.writeError(410);
                } catch (Exception exp) {
                    _responseWriter.writeError(500);
                }
                _processed = true;
            }
        }
    }

    private void getMessages(HttpRequest request, String room, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        ChatRoomMediator chatRoom = _chatServer.getChatRoom(room);
        if (chatRoom == null)
            throw new HttpProcessingException(404);
        try {
            List<ChatMessage> chatMessages = chatRoom.joinUser(resourceOwner.getName(), resourceOwner.getType().contains("a"));
            Collection<String> usersInRoom = chatRoom.getUsersInRoom();

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();

            serializeChatRoomData(room, chatMessages, usersInRoom, doc);

            responseWriter.writeXmlResponse(doc);
        } catch (PrivateInformationException exp) {
            throw new HttpProcessingException(403);
        }
    }

    private void serializeChatRoomData(String room, List<ChatMessage> chatMessages, Collection<String> usersInRoom, Document doc) {
        Element chatElem = doc.createElement("chat");
        chatElem.setAttribute("roomName", room);
        doc.appendChild(chatElem);

        for (ChatMessage chatMessage : chatMessages) {
            Element message = doc.createElement("message");
            message.setAttribute("from", chatMessage.getFrom());
            message.setAttribute("date", String.valueOf(chatMessage.getWhen().getTime()));
            message.appendChild(doc.createTextNode(chatMessage.getMessage()));
            chatElem.appendChild(message);
        }

        Set<String> users = new TreeSet<String>(new CaseInsensitiveStringComparator());
        for (String userInRoom : usersInRoom)
            users.add(formatPlayerNameForChatList(userInRoom));

        for (String userInRoom : users) {
            Element user = doc.createElement("user");
            user.appendChild(doc.createTextNode(userInRoom));
            chatElem.appendChild(user);
        }
    }

    private class CaseInsensitiveStringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    }

    private String formatPlayerNameForChatList(String userInRoom) {
        final Player player = _playerDao.getPlayer(userInRoom);
        if (player != null) {
            final String playerType = player.getType();
            if (playerType.contains("a"))
                return "* "+userInRoom;
            else if (playerType.contains("l"))
                return "+ "+userInRoom;
        }
        return userInRoom;
    }
}
