package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Photo audit view object.
 */
public class WelfareAuditPhoto
{
    private Long id;
    private Long petId;
    private Long userId;
    private String images;
    private String description;
    private String location;
    private String status;
    private Integer isDailyWinner;
    private String rejectReason;
    private String petName;
    private String uploaderName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shootTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getPetId()
    {
        return petId;
    }

    public void setPetId(Long petId)
    {
        this.petId = petId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getImages()
    {
        return images;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getIsDailyWinner()
    {
        return isDailyWinner;
    }

    public void setIsDailyWinner(Integer isDailyWinner)
    {
        this.isDailyWinner = isDailyWinner;
    }

    public String getRejectReason()
    {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason)
    {
        this.rejectReason = rejectReason;
    }

    public String getPetName()
    {
        return petName;
    }

    public void setPetName(String petName)
    {
        this.petName = petName;
    }

    public String getUploaderName()
    {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName)
    {
        this.uploaderName = uploaderName;
    }

    public Date getShootTime()
    {
        return shootTime;
    }

    public void setShootTime(Date shootTime)
    {
        this.shootTime = shootTime;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }
}
