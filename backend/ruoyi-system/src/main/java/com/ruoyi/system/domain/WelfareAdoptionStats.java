package com.ruoyi.system.domain;

public class WelfareAdoptionStats
{
    private Integer pending;
    private Integer approved;
    private Integer rejected;
    private Integer completed;

    public Integer getPending() { return pending; }
    public void setPending(Integer pending) { this.pending = pending; }
    public Integer getApproved() { return approved; }
    public void setApproved(Integer approved) { this.approved = approved; }
    public Integer getRejected() { return rejected; }
    public void setRejected(Integer rejected) { this.rejected = rejected; }
    public Integer getCompleted() { return completed; }
    public void setCompleted(Integer completed) { this.completed = completed; }
}
