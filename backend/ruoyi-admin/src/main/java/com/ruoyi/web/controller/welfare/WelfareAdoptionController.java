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
import com.ruoyi.system.domain.WelfareAdoptionApplication;
import com.ruoyi.system.service.IWelfareAdoptionService;

@RestController
@RequestMapping("/welfare/adoption")
public class WelfareAdoptionController extends BaseController
{
    @Autowired
    private IWelfareAdoptionService adoptionService;

    @GetMapping("/list")
    public TableDataInfo list(String status, String keyword)
    {
        startPage();
        List<WelfareAdoptionApplication> list = adoptionService.selectApplicationList(status, keyword);
        return getDataTable(list);
    }

    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(adoptionService.selectApplicationById(id));
    }

    @GetMapping("/stats")
    public AjaxResult stats()
    {
        return success(adoptionService.selectApplicationStats());
    }

    @Log(title = "领养申请审批", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/review")
    public AjaxResult review(@PathVariable Long id, @RequestBody AdoptionReviewBody body)
    {
        return toAjax(adoptionService.reviewApplication(id, body.getStatus(), getUserId(), body.getRejectReason(),
                body.getAdminNotes()));
    }

    public static class AdoptionReviewBody
    {
        private String status;
        private String rejectReason;
        private String adminNotes;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getRejectReason() { return rejectReason; }
        public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
        public String getAdminNotes() { return adminNotes; }
        public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    }
}
