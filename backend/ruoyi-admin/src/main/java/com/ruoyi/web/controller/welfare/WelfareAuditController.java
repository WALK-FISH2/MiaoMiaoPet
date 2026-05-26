package com.ruoyi.web.controller.welfare;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.WelfareAuditPhoto;
import com.ruoyi.system.service.IWelfareAuditPhotoService;

@RestController
@RequestMapping("/welfare/audit")
public class WelfareAuditController extends BaseController
{
    @Autowired
    private IWelfareAuditPhotoService photoService;

    @GetMapping("/photos/list")
    public TableDataInfo listPhotos(String status, String keyword)
    {
        startPage();
        List<WelfareAuditPhoto> list = photoService.selectPhotoAuditList(status, keyword);
        return getDataTable(list);
    }

    @Log(title = "照片审核", businessType = BusinessType.UPDATE)
    @PutMapping("/photos/{id}/review")
    public AjaxResult reviewPhoto(@PathVariable Long id, @RequestBody PhotoReviewBody body)
    {
        return toAjax(photoService.reviewPhoto(id, body.getStatus(), getUserId(), body.getRejectReason()));
    }

    public static class PhotoReviewBody
    {
        private String status;
        private String rejectReason;

        public String getStatus()
        {
            return status;
        }

        public void setStatus(String status)
        {
            this.status = status;
        }

        public String getRejectReason()
        {
            return rejectReason;
        }

        public void setRejectReason(String rejectReason)
        {
            this.rejectReason = rejectReason;
        }
    }
}
