package com.cinefiles.backend;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseEngine {

    // The Fleet Manager
    private static HikariDataSource dataSource;

    static {
        // We still get the URL from the environment because URLs aren't secrets!
        String url = System.getenv("DB_URL");
        String user = null;
        String password = null;

        System.out.println("[SYSTEM] Attempting to fetch credentials from AWS Secrets Manager...");

        try {
            // 1. Create a secure connection to the AWS Vault in Mumbai
            Region region = Region.AP_SOUTH_1;
            SecretsManagerClient client = SecretsManagerClient.builder()
                    .region(region)
                    .build();

            // 2. Ask for the specific secret
            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId("prod/cinefiles/db")
                    .build();

            GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            String secretJsonString = getSecretValueResponse.secretString();

            // 3. AWS returns a JSON string. We parse it to grab just the username and password
            ObjectMapper mapper = new ObjectMapper();
            JsonNode secretNode = mapper.readTree(secretJsonString);

            user = secretNode.get("username").asText();
            password = secretNode.get("password").asText();

            System.out.println("[SUCCESS] Zero-Trust Vault Accessed. Database credentials loaded into volatile memory.");

            // 4. Close the vault connection
            client.close();

        } catch (Exception e) {
            // If we are testing on a local laptop without AWS keys, fallback to the old way so the app doesn't die.
            System.out.println("[WARNING] AWS Vault access failed (Expected for local testing). Falling back to local Environment Variables.");
            user = System.getenv("DB_USER");
            password = System.getenv("DB_PASSWORD");
        }

        if (url == null || user == null || password == null) {
            System.err.println("[FATAL] Database Bridge Setup Failed! No credentials found in AWS or locally.");
            System.exit(1);
        }

        // Configure the Taxi Lot
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);

        dataSource = new HikariDataSource(config);
        System.out.println("[SYSTEM] HikariCP Connection Pool Initialized Successfully.");
    }

    public static Connection connect() throws SQLException {
        return dataSource.getConnection();
    }
}
