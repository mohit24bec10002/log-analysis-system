package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class LogAnalyzer implements Runnable {
    private final BlockingQueue<LogEntry> streamQueue;
    private final String alertLogPath;

    public LogAnalyzer(BlockingQueue<LogEntry> streamQueue, String alertLogPath) {
        this.streamQueue = streamQueue;
        this.alertLogPath = alertLogPath;
    }

    @Override
    public void run() {
        try (BufferedWriter alertWriter = new BufferedWriter(new FileWriter(alertLogPath, true))) {
            while (true) {
                LogEntry log = streamQueue.take();

                if (log.getEntryId() == -1) {
                    streamQueue.put(log);
                    break;
                }

                Thread.sleep(180);

                if (log.isCritical()) {
                    String alertRecord = String.format("ALERT TRIGGERED -> LogID: %d | Source: %s | Level: %s | Details: %s%n",
                            log.getEntryId(), log.getServiceName(), log.getLogLevel(), log.getMessage());
                    
                    System.out.print("[Analyzer " + Thread.currentThread().getName() + "] " + alertRecord);

                    synchronized (alertWriter) {
                        alertWriter.write(alertRecord);
                        alertWriter.flush();
                    }
                } else {
                    System.out.println("[Analyzer " + Thread.currentThread().getName() + "] Parsed non-critical: #" + log.getEntryId());
                }
            }
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }
    }
}
