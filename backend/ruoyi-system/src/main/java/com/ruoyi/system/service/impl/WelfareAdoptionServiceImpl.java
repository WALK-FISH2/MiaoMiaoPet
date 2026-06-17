package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.service.FileStorageService;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.WelfareAdoptionApplication;
import com.ruoyi.system.domain.WelfareAdoptionStats;
import com.ruoyi.system.mapper.WelfareAdoptionMapper;
import com.ruoyi.system.service.IWelfareAdoptionService;

@Service
public class WelfareAdoptionServiceImpl implements IWelfareAdoptionService
{
    @Autowired
    private WelfareAdoptionMapper adoptionMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public List<WelfareAdoptionApplication> selectApplicationList(String status, String keyword)
    {
        List<WelfareAdoptionApplication> list = adoptionMapper.selectApplicationList(status, keyword);
        list.forEach(this::signApplicationImages);
        return list;
    }

    @Override
    public WelfareAdoptionApplication selectApplicationById(Long id)
    {
        WelfareAdoptionApplication application = adoptionMapper.selectApplicationById(id);
        signApplicationImages(application);
        return application;
    }

    @Override
    public WelfareAdoptionStats selectApplicationStats()
    {
        return adoptionMapper.selectApplicationStats();
    }

    @Override
    public int reviewApplication(Long id, String status, Long reviewedBy, String rejectReason, String adminNotes)
    {
        if (!StringUtils.equals("approved", status) && !StringUtils.equals("rejected", status)
                && !StringUtils.equals("completed", status))
        {
            throw new ServiceException("审批状态只能是 approved、rejected 或 completed");
        }
        if (StringUtils.equals("rejected", status) && StringUtils.isBlank(rejectReason))
        {
            throw new ServiceException("拒绝申请时必须填写审核意见");
        }

        WelfareAdoptionApplication application = adoptionMapper.selectApplicationById(id);
        if (application == null)
        {
            throw new ServiceException("领养申请不存在");
        }

        int rows = adoptionMapper.updateApplicationReview(id, status, reviewedBy, rejectReason, adminNotes);
        if (rows > 0 && StringUtils.equals("approved", status))
        {
            adoptionMapper.updatePetStatus(application.getPetId(), "adopting");
        }
        else if (rows > 0 && StringUtils.equals("completed", status))
        {
            adoptionMapper.updatePetStatus(application.getPetId(), "adopted");
        }
        return rows;
    }

    private void signApplicationImages(WelfareAdoptionApplication application)
    {
        if (application == null)
        {
            return;
        }
        application.setHouseImages(fileStorageService.signJsonArray(application.getHouseImages()));
        application.setPetMainImage(fileStorageService.signUrl(application.getPetMainImage()));
        application.setAdoptionContract(fileStorageService.signUrl(application.getAdoptionContract()));
    }
}
