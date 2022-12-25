package fj.up.recognition.config;


import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;

public class AcrConfig extends HashMap<String, Object> {
    private AcrConfig() {
        Dotenv dotenv = Dotenv.load();

        if (dotenv.get("ACR_HOST") == null
                || dotenv.get("ACR_ACCESS_KEY") == null
                || dotenv.get("ACR_ACCESS_SECRET") == null) {
            throw new IllegalStateException(
                    "ACR_HOST, ACR_ACCESS_KEY and ACR_ACCESS_SECRET must be set as environment variables");
        }

        put("host", dotenv.get("ACR_HOST"));
        put("access_key", dotenv.get("ACR_ACCESS_KEY"));
        put("access_secret", dotenv.get("ACR_ACCESS_SECRET"));
    }

    public static AcrConfig loadConfig() {
        return new AcrConfig();
    }
}
