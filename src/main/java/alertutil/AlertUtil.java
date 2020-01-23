package alertutil;

class AlertUtil {
    public static void addLimitAlert(String key, String content) {
        AlertUtilHandler.addLimitAlert(key, content, 1000, 1);
    }
    public static void addLimitAlert(String key, String content, int expireTime) {
        AlertUtilHandler.addLimitAlert(key, content, expireTime, 1);
    }
    public static void addLimitAlert(String key, String content, int expireTime, int expireCount) {
        AlertUtilHandler.addLimitAlert(key, content, expireTime, expireCount);
    }
    public static void printAllAlerts() { AlertUtilHandler.printAllAlerts(); }
    public static void refresh() {
        AlertUtilHandler.refresh();
    }
}