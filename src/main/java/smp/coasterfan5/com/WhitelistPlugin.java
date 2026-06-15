package smp.coasterfan5.com;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

public class WhitelistPlugin extends JavaPlugin implements Listener {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("On Enable Called");
    }

    @EventHandler
    public void checkWl(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        getLogger().info(String.format("Join event from %s", uuid));

        var uriString = String.format(
            "https://smputils.coasterfan5.com/api/v1/%s",
            uuid
        );

        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(uriString))
            .build();

        try {
            HttpResponse<String> resp = httpClient.send(
                req,
                HttpResponse.BodyHandlers.ofString()
            );
            if (resp.body().equalsIgnoreCase("Invalid User")) {
                getLogger().info("Denying login");

                event.disallow(
                    Result.KICK_WHITELIST,
                    "You are not whitelisted."
                );
            }
        } catch (IOException | InterruptedException e) {
            getLogger().warning(
                String.format("whitelist check failed: %s", e.getMessage())
            );
            event.disallow(
                Result.KICK_OTHER,
                "Whitelist service unreachable (ping @Coaster)"
            );
        }
    }
}
