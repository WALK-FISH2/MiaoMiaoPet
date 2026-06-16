package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.WelfareAdoptionApplication;
import com.ruoyi.system.domain.WelfareAdoptionStats;

public interface WelfareAdoptionMapper
{
    public List<WelfareAdoptionApplication> selectApplicationList(@Param("status") String status,
            @Param("keyword") String keyword);

    public WelfareAdoptionApplication selectApplicationById(Long id);

    public WelfareAdoptionStats selectApplicationStats();

    public int updateApplicationReview(@Param("id") Long id, @Param("status") String status,
            @Param("reviewedBy") Long reviewedBy, @Param("rejectReason") String rejectReason,
            @Param("adminNotes") String adminNotes);

    public int updatePetStatus(@Param("petId") Long petId, @Param("status") String status);
}
