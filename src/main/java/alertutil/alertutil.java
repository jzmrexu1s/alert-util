package alertutil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class AlertUtil {
    private static class Holder {
        private static AlertUtil instance = new AlertUtil();
    }

    public static AlertUtil getInstance() {
        return Holder.instance;
    }
    public static class Rule { }

    public static class TimeLimitRule extends Rule {
        private int expireTime;
        public TimeLimitRule(int expireTime) {
            this.expireTime = expireTime;
        }
        public int getExpireTime() {
            return expireTime;
        }
        public void setExpireTime(int expireTime) {
            this.expireTime = expireTime;
        }
    }

    private static Map<String, Rule> rules;

    private AlertUtil() {
        rules = new HashMap<String, Rule>();
        System.out.println(addRule("default", "TimeLimit", 1000));
    }

    public boolean addRule(String ruleName, String ruleType, Object... params) {
        try {
            Class<?> cl = Class.forName("alertutil.AlertUtil$" + ruleType + "Rule");
            Constructor<?>[] cons = cl.getConstructors();
            Rule rule = (Rule) cons[0].newInstance(params);
//            TimeLimitRule rule = new TimeLimitRule(1000);
//            System.out.println(rule.getClass());
            rules.put(ruleName, rule);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class Alert {
        private String ruleName;
        private String content;
        private long timestamp;

        public Alert(String ruleName, String content) {
            this.ruleName = ruleName;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }
        public String getRuleName() {
            return ruleName;
        }
        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }
        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }
    }

    private static Map<String, Alert> alerts = new ConcurrentHashMap<String, Alert>();

    public void addAlert(String key, String ruleName, String content) {
        if (alerts.get(key) == null) {
            Alert alert = new Alert(ruleName, content);
            alerts.put(key, alert);
            System.out.println("In thread " + alert.content + " key= " + key + " " + alert.timestamp);
        }
    }

    public void printAlerts() {
        System.out.println("last size " + alerts.size() + ". Contents: ");
        for (int i = 0; i < alerts.size(); i ++) {
            Alert a = alerts.get(String.valueOf(i));
            System.out.println(a.content + " " + a.timestamp);
        }
    }

    public void getRuleAndPrint(String ruleName) {
        TimeLimitRule rule = (TimeLimitRule) rules.get(ruleName);
        System.out.println(rule.getExpireTime());
    }
}
