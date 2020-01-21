package alertutil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class AlertUtil {
    public static synchronized void addAlert(String key, String content, int expireTime, int expireCount) {
        AlertUtilHandler.addAlert(key, content, expireTime, expireCount);
    }
    public static synchronized void printAllAlerts() {
        AlertUtilHandler.printAllAlerts();
    }
    public static synchronized void refresh() {
        AlertUtilHandler.refresh();
    }
}

class AlertUtilHandler {

    private static Map<String, AlertInfo> alerts = new ConcurrentHashMap<String, AlertInfo>(); // TODO: Only save keys in alerts.
    public static Map<String, AlertInfo> getAlerts() {
        return alerts;
    }

    public void setAlerts(ConcurrentHashMap<String, AlertInfo> alerts) {
        AlertUtilHandler.alerts = alerts;
    }

    public static Rule makeRule(Object... params) {
        if (params.length == 1) {
            return(new LimitRule((int)params[0]));
        }
        if (params.length == 2) {
            return(new LimitRule((int)params[0], (int)params[1]));
        }
        return(new LimitRule(1000));
    }

    public static synchronized void addAlert(String key, String content, int expireTime, int expireCount) {
        if (alerts.get(key) == null) {
            Rule rule = makeRule(expireTime, expireCount);
            Alert alert = new Alert(rule, content);
            alerts.put(key, (AlertInfo)alert);
            System.out.println("addAlert: key= " + key + " " + "timestamp: " + alert.getTimeStamp());
        } else {
            AlertInfo alert = alerts.get(key);
            alert.addCount();
        }
        refresh();
    }

    // TODO: synchronized or not?
    public static synchronized void refresh() {
        try {
            for (String key: alerts.keySet()) {
                AlertInfo alert = alerts.get(key);
                Rule rule = alert.getRule();
                if (rule.checkRemove(alert)) {
                    alerts.remove(key);
                    System.out.println("Remove: alert with key " + key + " has been removed. ");
                }
                else if (!rule.checkReachCount(alert)) {
                    rule.setAlert(alert);
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    public static synchronized void printAllAlerts() {
        System.out.println("↓↓↓↓↓ Lengths of alerts: " + alerts.size() + " ↓↓↓↓↓");
        for (String key: alerts.keySet()) {
            AlertInfo alert = alerts.get(key);
            System.out.println("Key: " + key + " TimeStamp: " + alert.getTimeStamp() + " Count: " + alert.getCount());
        }
    }
}

abstract class Rule {
    public abstract boolean checkRemove(AlertInfo alert);       // If the alert should be removed from alerts
    public abstract boolean checkReachCount(AlertInfo alert);   // If the alert reaches its count limit
    public abstract void action(Alert alert);                   // How to send email
    public abstract void setAlert(AlertInfo alert);             // How to update the params in an alert
}

class AlertInfo {
    private Rule rule;
    private long timeStamp;
    private int count;
    public AlertInfo(Rule rule) {
        this.rule = rule;
        this.timeStamp = System.currentTimeMillis();
        this.count = 0;
    }
    public Rule getRule() { return rule; }
    public long getTimeStamp() { return timeStamp; }
    public void addCount() { this.count ++; }
    public int getCount() { return count; }

}

class Alert extends AlertInfo {
    private String content;
    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
    }
    public Alert(Rule rule, String content) {
        super(rule);
        this.content = content;
    }
}

class LimitRule extends Rule {
    private int expireTime;
    private int expireCount = 1;

    public LimitRule(int expireTime, int expireCount) {
        this.expireTime = expireTime;
        this.expireCount = expireCount;
    }

    public LimitRule(int expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public boolean checkRemove(AlertInfo alert) {
        return (System.currentTimeMillis() - alert.getTimeStamp() >= expireTime);
            // TODO: Check if reach n times.
    }

    @Override
    public boolean checkReachCount(AlertInfo alert) {
        return (alert.getCount() >= expireCount);
    }

    @Override
    public void action(Alert alert) {
        System.out.println("action: Should send an email right now. Content: " + alert.getContent());
    }

    @Override
    public void setAlert(AlertInfo alert) {
        alert.addCount();
    }
}