package rocks.milspecsg.msessentials.events;

import com.google.inject.Inject;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;

import java.util.*;

public class ProxyMessageEvent {

    @Inject
    private static ProxyServer proxyServer;

    private final Player sender;

    public static Set<UUID> socialSpySet = new HashSet<>();
    public static Map<UUID, UUID> replyMap = new HashMap<>();

    public Player getSender() {
        return sender;
    }

    public static ProxyServer getProxyServer() {
        return proxyServer;
    }

    public Player getRecipient() {
        return recipient;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    private final Player recipient;

    private final String rawMessage;


    private static TextComponent legacyColor(String text) {
        return LegacyComponentSerializer.legacy().deserialize(text, '&');
    }

    public ProxyMessageEvent(Player sender, Player recipient, String rawMessage, ProxyServer proxyServer) {
        this.sender = sender;
        this.recipient = recipient;
        this.rawMessage = rawMessage;
        ProxyMessageEvent.proxyServer = proxyServer;
    }

    public static TextComponent message(String sender, String receiver, String rawMessage) {
        TextComponent msg = TextComponent.builder()
                .append(legacyColor("&8["))
                .append(legacyColor("&b" + sender))
                .append(legacyColor("&6 -> "))
                .append(legacyColor("&b" + receiver))
                .append(legacyColor("&8] "))
                .append(legacyColor("&7" + rawMessage))
                .build();
        return msg;
    }

    public static void sendMessage(Player sender, Player recipient, String message, ProxyServer proxyServer) {
        sender.sendMessage(message("Me", recipient.getUsername(), message));
        recipient.sendMessage(message(sender.getUsername(), "Me", message));
        socialSpy(sender, recipient, message, proxyServer);
    }

    public static void socialSpy(Player sender, Player receiver, String rawMessage, ProxyServer proxyServer) {
        TextComponent msg = TextComponent.builder()
                .append(legacyColor("&7[SocialSpy] "))
                .append(legacyColor("&8["))
                .append(legacyColor("&b" + sender.getUsername()))
                .append(legacyColor("&6 -> "))
                .append(legacyColor("&b" + receiver.getUsername()))
                .append(legacyColor("&8] "))
                .append(legacyColor("&7" + rawMessage))
                .build();

        if (socialSpySet.isEmpty()) {
            return;
        }
        for (Player player : proxyServer.getAllPlayers()) {
            if ((!(socialSpySet.isEmpty())) && socialSpySet.contains(player.getUniqueId())) {
                if (!sender.getUniqueId().equals(player.getUniqueId())) {
                    if (!receiver.getUniqueId().equals(player.getUniqueId())) {
                        System.out.println(player.getUsername());
                        player.sendMessage(msg);
                    }
                }
            }
        }
    }
}
