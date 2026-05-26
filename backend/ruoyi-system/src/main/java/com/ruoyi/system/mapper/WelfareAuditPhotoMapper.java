package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.WelfareAuditPhoto;

public interface WelfareAuditPhotoMapper
{
    public List<WelfareAuditPhoto> selectPhotoAuditList(@Param("status") String status, @Param("keyword") String keyword);

    public int updatePhotoReview(@Param("id") Long id, @Param("status") String status,
            @Param("reviewedBy") Long reviewedBy, @Param("rejectReason") String rejectReason);
}
