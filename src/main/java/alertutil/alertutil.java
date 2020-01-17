package alertutil;

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

    public static class Rule {
        private int expire_time;

        public Rule(int expire_time) {
            this.expire_time = expire_time;
        }

        public int getExpire_time() {
            return expire_time;
        }

        public void setExpire_time(int expire_time) {
            this.expire_time = expire_time;
        }
    }

    private static Map<String, Rule> rules;

    private AlertUtil() {
        rules = new HashMap<String, Rule>();
        rules.put("default", new Rule(5000));
    }

    public static AlertUtil getInstance() {
        return Holder.instance;
    }

    public static class Alert {
        private Rule rule;
        private String content;
        private long timestamp;

        public Alert(Rule rule, String content) {
            this.rule = rule;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }

        public Alert(String rule_name, String content) {
            this.rule = rules.get(rule_name);
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }

        public Rule getRule() {
            return rule;
        }

        public void setRule(Rule rule) {
            this.rule = rule;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    private static Map<String, Alert> alerts = new ConcurrentHashMap<String, Alert>();

    public void add_alert(Alert alert) {
        String key = String.valueOf(alert.content.hashCode());
        if (alerts.get(key) == null) {
            alerts.put(key, alert);
            System.out.println("In thread " + alert.content + " key= " + key + " " + alert.timestamp);
        }
    }

    public void print_alerts() {
        System.out.println("last size " + alerts.size() + ". Contents: ");
        for (int i = 0; i < 5; i ++) {
            Alert a = alerts.get(String.valueOf(String.valueOf(i).hashCode()));
            System.out.println(a.content + " " + a.timestamp);
        }
    }

}
