package top.mrghtchannel.mysqlwhitelistvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.Properties;

@Plugin(
        id = BuildConstants.ID,
        name = BuildConstants.NAME,
        version = BuildConstants.VERSION,
        url = BuildConstants.URL,
        description = BuildConstants.DESCRIPTION,
        authors = BuildConstants.AUTHORS
)
public class MySQLWhitelistVelocity {

    private static Connection connection;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final Path configFile;
    private Properties config;
    private final Metrics.Factory metricsFactory;

    @Inject
    public MySQLWhitelistVelocity(
            ProxyServer server,
            Logger logger,
            @DataDirectory Path dataDirectory,
            Metrics.Factory metricsFactory
    ) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.configFile = dataDirectory.resolve("config.properties");
        this.metricsFactory = metricsFactory;
        // Load the MariaDB JDBC driver
        try {
            Class.forName("top.mrghtchannel.mysqlwhitelistvelocity.libs.mariadb.Driver");
            logger.info("MariaDB JDBC driver loaded successfully");
        } catch (ClassNotFoundException e) {
            logger.error("Failed to load MariaDB JDBC driver", e);
            throw new RuntimeException("MariaDB JDBC driver not found", e);
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            // Initialize bStats
            Metrics metrics = metricsFactory.make(this, 27343);
            logger.info("bStats initialized with ID 27343");

            // Create data directory and default config if they don't exist
            saveDefaultConfig();

            // Load configuration
            this.config = loadConfig();

            // Test database connection on startup
            try {
                openConnection();
                logger.info("Successfully connected to database during initialization");
                closeConnection();
            } catch (Exception e) {
                logger.error("Failed to connect to database during initialization", e);
                throw new RuntimeException("Database connection test failed", e);
            }

            logger.info("{} {} loaded successfully!", BuildConstants.NAME, BuildConstants.VERSION);
        } catch (Exception e) {
            logger.error("Error during plugin initialization", e);
            throw new RuntimeException("Plugin initialization failed", e);
        }
    }

    private void saveDefaultConfig() {
        try {
            if (Files.notExists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
                logger.info("Created data directory: {}", dataDirectory);
            }
            if (Files.notExists(configFile)) {
                String defaultConfig = """
                        enabled=true
                        host=127.0.0.1
                        port=3306
                        database=lcdb
                        user=root
                        password=example
                        message=Sorry, you are not in the whitelist.
                        """;
                Files.write(configFile, defaultConfig.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                logger.info("Created default config at: {}", configFile);
            }
        } catch (IOException e) {
            logger.error("Error creating default config", e);
            throw new RuntimeException("Error creating default config", e);
        }
    }

    private Properties loadConfig() {
        Properties properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(configFile, StandardCharsets.UTF_8));
            logger.info("Config loaded successfully from: {}", configFile);
        } catch (IOException e) {
            logger.error("Error loading config", e);
            throw new RuntimeException("Error loading config", e);
        }
        return properties;
    }

    private synchronized void openConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    logger.debug("Reusing existing database connection");
                    return;
                }
            } catch (SQLException e) {
                logger.warn("Error checking connection status, reopening connection", e);
            }
        }
        try {
            String jdbcUrl = "jdbc:mariadb://" + config.getProperty("host") + ":" + config.getProperty("port") +
                    "/" + config.getProperty("database") + "?useSSL=false";
            connection = DriverManager.getConnection(
                    jdbcUrl,
                    config.getProperty("user"),
                    config.getProperty("password")
            );
            logger.info("Database connection established to: {}", jdbcUrl);
        } catch (SQLException e) {
            logger.error("Error opening database connection", e);
            throw new RuntimeException("Error opening database connection", e);
        }
    }

    private synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.debug("Database connection closed");
            } catch (SQLException e) {
                logger.warn("Error closing database connection", e);
            } finally {
                connection = null;
            }
        }
    }

    private boolean isWhitelisted(Player player) {
        try {
            openConnection();
            String query = "SELECT * FROM Whitelist WHERE MinecraftName = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, player.getUsername());
                ResultSet rs = stmt.executeQuery();
                boolean isWhitelisted = rs.next();
                logger.debug("Whitelist check for {}: {}", player.getUsername(), isWhitelisted);
                return isWhitelisted;
            }
        } catch (SQLException e) {
            logger.error("Error checking whitelist for player: {}", player.getUsername(), e);
            throw new RuntimeException("Error checking whitelist", e);
        } finally {
            closeConnection();
        }
    }

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        Player player = event.getPlayer();
        if (Boolean.parseBoolean(config.getProperty("enabled")) && !isWhitelisted(player)) {
            Component kickMessage = Component.text(config.getProperty("message"));
            event.setResult(LoginEvent.ComponentResult.denied(kickMessage));
            logger.info("Player {} was denied login (not whitelisted)", player.getUsername());
        } else {
            logger.info("Player {} allowed to login", player.getUsername());
        }
    }
}