package alertutil;

import com.alibaba.fastjson.JSONArray;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


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
    public static void printAllAlerts() {
        AlertUtilHandler.printAllAlerts();
    }
    public static void refresh() {
        AlertUtilHandler.refresh();
    }
}

class AlertUtilHandler {
    private static Map<String, AlertInfo> alerts = new ConcurrentHashMap<String, AlertInfo>();
    public static Map<String, AlertInfo> getAlerts() { return alerts; }

    public void setAlerts(ConcurrentHashMap<String, AlertInfo> alerts) {
        AlertUtilHandler.alerts = alerts;
    }

    public static void addLimitAlert(String key, String content, int expireTime, int expireCount) {
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
                rule.setAlert(alert);
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

abstract class Rule {
    public abstract boolean checkRemove(AlertInfo alert);        // If the alert should be removed from alerts
    public abstract boolean checkReachCount(AlertInfo alert);   // If the alert reaches its count limit
    public abstract void action(String content);                   // How to send email
    public abstract void setAlert(AlertInfo alert);             // How to update the params in an alert
}

class AlertInfo {
    private Rule rule;
    private long timeStamp;
    private int count;
    public AlertInfo(Rule rule) {
        this.rule = rule;
        this.timeStamp = System.currentTimeMillis();
        this.count = 1;
    }
    public Rule getRule() { return rule; }
    public long getTimeStamp() { return timeStamp; }
    public void addCount() { this.count ++; }
    public int getCount() { return count; }
}


class LimitRule extends Rule {
    private int expireTime;
    private int expireCount = 1;

    public LimitRule(int expireTime, int expireCount) {
        this.expireTime = expireTime;
        this.expireCount = expireCount;
    }

    @Override
    public boolean checkRemove(AlertInfo alert) {
        return (System.currentTimeMillis() - alert.getTimeStamp() >= expireTime);
    }

    @Override
    public boolean checkReachCount(AlertInfo alert) {
        return (alert.getCount() >= expireCount);
    }

    @Override
    public void action(String content) {
        System.out.println("action: Should send an email right now. Content: " + content);
    }

    @Override
    public void setAlert(AlertInfo alert) {
        alert.addCount();
    }
}