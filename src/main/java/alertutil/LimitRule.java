package alertutil;

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