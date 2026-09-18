package src;

import java.util.concurrent.BlockingQueue;

public class LogIngestor implements Runnable {
    private final BlockingQueue<LogEntry> streamQueue;
    private final int streamLimit;

    public LogIngestor(BlockingQueue<LogEntry> streamQueue, int streamLimit) {
        this.streamQueue = streamQueue;
        this.streamLimit = streamLimit;
    }

    @Override
    public void run() {
        String[] services = {"AuthService", "PaymentGateway", "DatabasePool", "InventoryService"};
        String[] levels = {"INFO", "WARN", "ERROR", "CRITICAL", "INFO"};
        String[] messages = {
            "User session established",
            "Slow query execution detected",
            "Connection timeout encountered",
            "Transaction rollback initiated",
            "Heartbeat check passed"
        };

        try {
            for (int i = 1; i <= streamLimit; i++) {
                String service = services[i % services.length];
                String level = levels[i % levels.length];
                String msg = messages[i % messages.length];

                LogEntry entry = new LogEntry(i, service, level, msg);
                streamQueue.put(entry);
                System.out.println("[Ingestor] Ingested: " + entry);
                Thread.sleep(120);
            }
            streamQueue.put(new LogEntry(-1, "SYSTEM", "SHUTDOWN", "STREAM_COMPLETE"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
