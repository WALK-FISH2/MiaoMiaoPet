package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.WelfareAuditPhoto;

public interface IWelfareAuditPhotoService
{
    public List<WelfareAuditPhoto> selectPhotoAuditList(String status, String keyword);

    public int reviewPhoto(Long id, String status, Long reviewedBy, String rejectReason);
}
