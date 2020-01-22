package alertutil;

public class AlertInfo {
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
