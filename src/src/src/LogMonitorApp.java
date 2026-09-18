package src;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LogMonitorApp {
    public static void main(String[] args) {
        System.out.println("=== Initializing Multi-Threaded Real-Time Log Monitor ===");

        File outputDir = new File("alerts");
        if (!outputDir.exists()) outputDir.mkdir();

        String alertLogPath = "alerts/critical_incidents.txt";
        BlockingQueue<LogEntry> logQueue = new ArrayBlockingQueue<>(12);
        int totalLogVolume = 16;

        ExecutorService threadManager = Executors.newFixedThreadPool(3);

        threadManager.submit(new LogIngestor(logQueue, totalLogVolume));
        threadManager.submit(new LogAnalyzer(logQueue, alertLogPath));
        threadManager.submit(new LogAnalyzer(logQueue, alertLogPath));

        threadManager.shutdown();
        try {
            if (threadManager.awaitTermination(1, TimeUnit.MINUTES)) {
                System.out.println("=== Log analysis finished. Critical alerts saved to " + alertLogPath + " ===");
            } else {
                threadManager.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadManager.shutdownNow();
        }
    }
}
