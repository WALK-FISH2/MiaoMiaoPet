package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class WelfarePet
{
    private Long id;
    private Long locationId;
    private String locationName;
    private String name;
    private String nickname;
    private String gender;
    private String age;
    private String breed;
    private String color;
    private String personality;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date foundDate;

    private String foundLocation;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastSeenDate;

    private String lastSeenLocation;
    private String frequentLocations;
    private String mainImage;
    private String images;
    private Float heatScore;
    private Integer likeCount;
    private Integer commentCount;
    private Integer photoCount;
    private Integer followCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getPersonality() { return personality; }
    public void setPersonality(String personality) { this.personality = personality; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getFoundDate() { return foundDate; }
    public void setFoundDate(Date foundDate) { this.foundDate = foundDate; }
    public String getFoundLocation() { return foundLocation; }
    public void setFoundLocation(String foundLocation) { this.foundLocation = foundLocation; }
    public Date getLastSeenDate() { return lastSeenDate; }
    public void setLastSeenDate(Date lastSeenDate) { this.lastSeenDate = lastSeenDate; }
    public String getLastSeenLocation() { return lastSeenLocation; }
    public void setLastSeenLocation(String lastSeenLocation) { this.lastSeenLocation = lastSeenLocation; }
    public String getFrequentLocations() { return frequentLocations; }
    public void setFrequentLocations(String frequentLocations) { this.frequentLocations = frequentLocations; }
    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public Float getHeatScore() { return heatScore; }
    public void setHeatScore(Float heatScore) { this.heatScore = heatScore; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public Integer getPhotoCount() { return photoCount; }
    public void setPhotoCount(Integer photoCount) { this.photoCount = photoCount; }
    public Integer getFollowCount() { return followCount; }
    public void setFollowCount(Integer followCount) { this.followCount = followCount; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
