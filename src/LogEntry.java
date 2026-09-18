package src;

public class LogEntry {
    private final int entryId;
    private final String serviceName;
    private final String logLevel;
    private final String message;

    public LogEntry(int entryId, String serviceName, String logLevel, String message) {
        this.entryId = entryId;
        this.serviceName = serviceName;
        this.logLevel = logLevel;
        this.message = message;
    }

    public int getEntryId() { return entryId; }
    public String getServiceName() { return serviceName; }
    public String getLogLevel() { return logLevel; }
    public String getMessage() { return message; }

    public boolean isCritical() {
        return "ERROR".equalsIgnoreCase(logLevel) || "CRITICAL".equalsIgnoreCase(logLevel);
    }

    @Override
    public String toString() {
        return String.format("[%s] #%d %s: %s", logLevel, entryId, serviceName, message);
    }
}
