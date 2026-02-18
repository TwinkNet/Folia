package network.twink.limiter;

import network.twink.limiter.yml.YMLParser;

import java.io.File;
import java.io.IOException;

public class GlobalChunkSendRateLimiterTracker {

    private boolean enabled = false;
    private int limit = 64;
    private int current = 0;
    private int seconds = 0;

    public GlobalChunkSendRateLimiterTracker() {
        File file = new File("twinknet-folia-config.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                setupConfig(file);
            } catch (IOException e) {
                // don't care right now
            }
        }
        YMLParser parser = new YMLParser(file);
        enabled = parser.getBoolean("enabled", false);
        limit = parser.getInt("global-chunk-send-rate-limit", 64);
        seconds = parser.getInt("per-seconds", 1);
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(seconds * 1000L);
                    current = 0;
                } catch (InterruptedException e) {
                    //
                }
            }
        }).start();
    }

    private void setupConfig(File file) {
        YMLParser parser = new YMLParser(file);
        parser.set("enabled", true);
        parser.set("global-chunk-send-rate-limit", 64);
        parser.set("per-seconds", 1);
        parser.save();
    }

    public int getLimit() {
        return limit;
    }

    public int getCurrent() {
        return current;
    }

    public void incrementCurrent() {
        current++;
    }

    public boolean isOverLimit() {
        return current >= limit;
    }
}
