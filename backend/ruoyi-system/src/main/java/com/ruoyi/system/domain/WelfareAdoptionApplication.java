package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class WelfareAdoptionApplication
{
    private Long id;
    private Long petId;
    private Long userId;
    private String name;
    private String phone;
    private String wechat;
    private String city;
    private String address;
    private Integer age;
    private String occupation;
    private Float income;
    private Integer hasHouse;
    private String houseType;
    private Float houseArea;
    private Integer allowPet;
    private String petExperience;
    private Integer hasPets;
    private String currentPets;
    private String adoptionReason;
    private Integer acceptRegularVisit;
    private String houseImages;
    private String otherInfo;
    private String status;
    private Long reviewedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewedAt;

    private String rejectReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date adoptionDate;

    private String adoptionContract;
    private String adminNotes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    private String petName;
    private String petBreed;
    private String petGender;
    private String petAge;
    private String petStatus;
    private String petMainImage;
    private String locationName;
    private String userNickname;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getWechat() { return wechat; }
    public void setWechat(String wechat) { this.wechat = wechat; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public Float getIncome() { return income; }
    public void setIncome(Float income) { this.income = income; }
    public Integer getHasHouse() { return hasHouse; }
    public void setHasHouse(Integer hasHouse) { this.hasHouse = hasHouse; }
    public String getHouseType() { return houseType; }
    public void setHouseType(String houseType) { this.houseType = houseType; }
    public Float getHouseArea() { return houseArea; }
    public void setHouseArea(Float houseArea) { this.houseArea = houseArea; }
    public Integer getAllowPet() { return allowPet; }
    public void setAllowPet(Integer allowPet) { this.allowPet = allowPet; }
    public String getPetExperience() { return petExperience; }
    public void setPetExperience(String petExperience) { this.petExperience = petExperience; }
    public Integer getHasPets() { return hasPets; }
    public void setHasPets(Integer hasPets) { this.hasPets = hasPets; }
    public String getCurrentPets() { return currentPets; }
    public void setCurrentPets(String currentPets) { this.currentPets = currentPets; }
    public String getAdoptionReason() { return adoptionReason; }
    public void setAdoptionReason(String adoptionReason) { this.adoptionReason = adoptionReason; }
    public Integer getAcceptRegularVisit() { return acceptRegularVisit; }
    public void setAcceptRegularVisit(Integer acceptRegularVisit) { this.acceptRegularVisit = acceptRegularVisit; }
    public String getHouseImages() { return houseImages; }
    public void setHouseImages(String houseImages) { this.houseImages = houseImages; }
    public String getOtherInfo() { return otherInfo; }
    public void setOtherInfo(String otherInfo) { this.otherInfo = otherInfo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Long reviewedBy) { this.reviewedBy = reviewedBy; }
    public Date getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Date reviewedAt) { this.reviewedAt = reviewedAt; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public Date getAdoptionDate() { return adoptionDate; }
    public void setAdoptionDate(Date adoptionDate) { this.adoptionDate = adoptionDate; }
    public String getAdoptionContract() { return adoptionContract; }
    public void setAdoptionContract(String adoptionContract) { this.adoptionContract = adoptionContract; }
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getPetBreed() { return petBreed; }
    public void setPetBreed(String petBreed) { this.petBreed = petBreed; }
    public String getPetGender() { return petGender; }
    public void setPetGender(String petGender) { this.petGender = petGender; }
    public String getPetAge() { return petAge; }
    public void setPetAge(String petAge) { this.petAge = petAge; }
    public String getPetStatus() { return petStatus; }
    public void setPetStatus(String petStatus) { this.petStatus = petStatus; }
    public String getPetMainImage() { return petMainImage; }
    public void setPetMainImage(String petMainImage) { this.petMainImage = petMainImage; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
}
