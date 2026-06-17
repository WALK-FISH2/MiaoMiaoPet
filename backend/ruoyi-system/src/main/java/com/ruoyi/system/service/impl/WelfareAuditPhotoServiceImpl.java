package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.service.FileStorageService;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.WelfareAuditPhoto;
import com.ruoyi.system.mapper.WelfareAuditPhotoMapper;
import com.ruoyi.system.service.IWelfareAuditPhotoService;

@Service
public class WelfareAuditPhotoServiceImpl implements IWelfareAuditPhotoService
{
    @Autowired
    private WelfareAuditPhotoMapper photoMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public List<WelfareAuditPhoto> selectPhotoAuditList(String status, String keyword)
    {
        List<WelfareAuditPhoto> list = photoMapper.selectPhotoAuditList(status, keyword);
        list.forEach(photo -> photo.setImages(fileStorageService.signJsonArray(photo.getImages())));
        return list;
    }

    @Override
    public int reviewPhoto(Long id, String status, Long reviewedBy, String rejectReason)
    {
        if (!StringUtils.equals("approved", status) && !StringUtils.equals("rejected", status))
        {
            throw new ServiceException("审核状态只能是 approved 或 rejected");
        }
        return photoMapper.updatePhotoReview(id, status, reviewedBy, rejectReason);
    }
}
