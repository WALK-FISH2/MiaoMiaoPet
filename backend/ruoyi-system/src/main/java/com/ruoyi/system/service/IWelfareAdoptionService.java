package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.WelfareAdoptionApplication;
import com.ruoyi.system.domain.WelfareAdoptionStats;

public interface IWelfareAdoptionService
{
    public List<WelfareAdoptionApplication> selectApplicationList(String status, String keyword);

    public WelfareAdoptionApplication selectApplicationById(Long id);

    public WelfareAdoptionStats selectApplicationStats();

    public int reviewApplication(Long id, String status, Long reviewedBy, String rejectReason, String adminNotes);
}
