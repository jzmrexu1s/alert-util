package alertutil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class AlertUtilHandler {
    private static Map<String, AlertInfo> alerts = new ConcurrentHashMap<String, AlertInfo>();
    private static volatile boolean refresherStarted = false;
    public static void initRefresher() {
        if (!refresherStarted) {
            Thread t = new Thread() {
                public void run() {
                    while(true) {
                        AlertUtil.refresh();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) { break; }
                    }
                }
            };
            t.setDaemon(true);
            try {t.start();} catch (Exception e) {e.printStackTrace();}
            refresherStarted = true;
            System.out.println("Refresher started. "); }
        }


    public void setAlerts(ConcurrentHashMap<String, AlertInfo> alerts) {
        AlertUtilHandler.alerts = alerts;
    }

    public static void addLimitAlert(String key, String content, int expireTime, int expireCount) {
        initRefresher();
        if (alerts.get(key) == null) {
            Rule rule = new LimitRule(expireTime, expireCount);
            AlertInfo alert = new AlertInfo(rule);
            alerts.put(key, alert);
            rule.action(content);
            System.out.println("addAlert: key= " + key + " " + "timestamp: " + alert.getTimeStamp());
        }
        else {
            AlertInfo alert = alerts.get(key);
            Rule rule = alert.getRule();
            if (!rule.checkReachCount(alert)) {
                rule.action(content);
                alert.addCount();
            }
        }
//        refresh();
    }

    public static void refresh() {
        try {
            for (String key: alerts.keySet()) {
                AlertInfo alert = alerts.get(key);
                Rule rule = alert.getRule();
                if (rule.checkRemove(alert)) {
                    alerts.remove(key);
                    System.out.println("Remove: alert with key " + key + " has been removed. ");
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    public static void printAllAlerts() {
        System.out.println("↓↓↓↓↓ Lengths of alerts: " + alerts.size() + " ↓↓↓↓↓");
        for (String key: alerts.keySet()) {
            AlertInfo alert = alerts.get(key);
            System.out.println("Key: " + key + " TimeStamp: " + alert.getTimeStamp() + " Count: " + alert.getCount());
        }
    }
}