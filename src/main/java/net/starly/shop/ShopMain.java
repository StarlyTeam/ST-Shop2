package net.starly.shop;

import net.milkbowl.vault.economy.Economy;
import net.starly.core.bstats.Metrics;
import net.starly.shop.command.ShopCmd;
import net.starly.shop.command.tabcomplete.ShopTab;
import net.starly.shop.context.ConfigContent;
import net.starly.shop.data.ChatInputMap;
import net.starly.shop.data.InventoryOpenMap;
import net.starly.shop.listener.AsyncPlayerChatListener;
import net.starly.shop.listener.InventoryClickListener;
import net.starly.shop.listener.InventoryCloseListener;
import net.starly.shop.listener.PlayerCommandPreprocessListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopMain extends JavaPlugin {
    private static JavaPlugin plugin;
    private static Economy economy;

    @Override
    public void onEnable() {
        // DEPENDENCY
        if (!isPluginEnabled("net.starly.core.StarlyCore")) {
            Bukkit.getLogger().warning("[" + getName() + "] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            Bukkit.getLogger().warning("[" + getName() + "] 다운로드 링크 : http://starly.kr/");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (!isPluginEnabled("net.milkbowl.vault.Vault")) {
            Bukkit.getLogger().warning("[" + getName() + "] Vault 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            Bukkit.getLogger().warning("[" + getName() + "] 다운로드 링크 : https://www.spigotmc.org/resources/vault.34315/");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        if (economy == null) {
            Bukkit.getLogger().warning("[" + getName() + "] Vault와 연동되는 Economy 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            Bukkit.getLogger().warning("[" + getName() + "] 다운로드 링크 : https://essentialsx.net/downloads.html");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        plugin = this;
        new Metrics(plugin, 17609);

        // CONFIG
        ConfigContent.getInstance();

        // VARIABLES
        InventoryOpenMap inventoryOpenMap = new InventoryOpenMap();
        ChatInputMap chatInputMap = new ChatInputMap();

        // COMMAND
        getServer().getPluginCommand("shop").setExecutor(new ShopCmd(inventoryOpenMap));
        getServer().getPluginCommand("shop").setTabCompleter(new ShopTab(inventoryOpenMap));

        // EVENT
        getServer().getPluginManager().registerEvents(new InventoryClickListener(inventoryOpenMap, economy, chatInputMap), plugin);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(inventoryOpenMap), plugin);
        getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(inventoryOpenMap, chatInputMap), plugin);
        getServer().getPluginManager().registerEvents(new PlayerCommandPreprocessListener(chatInputMap), plugin);
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static Economy getEconomy() {
        return economy;
    }

    private boolean isPluginEnabled(String path) {
        try {
            Class.forName(path);
            return true;
        } catch (NoClassDefFoundError ignored) {
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }
}
