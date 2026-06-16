package com.ruoyi.web.controller.welfare;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.WelfarePet;
import com.ruoyi.system.service.IWelfarePetService;

@RestController
@RequestMapping("/welfare/pet")
public class WelfarePetController extends BaseController
{
    @Autowired
    private IWelfarePetService petService;

    @GetMapping("/list")
    public TableDataInfo list(String keyword, String status, String breed)
    {
        startPage();
        List<WelfarePet> list = petService.selectPetList(keyword, status, breed);
        return getDataTable(list);
    }

    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(petService.selectPetById(id));
    }

    @Log(title = "宠物管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WelfarePet pet)
    {
        return toAjax(petService.insertPet(pet));
    }

    @Log(title = "宠物管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WelfarePet pet)
    {
        return toAjax(petService.updatePet(pet));
    }

    @Log(title = "宠物管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(petService.deletePetById(id));
    }

    @GetMapping("/locations")
    public AjaxResult locations()
    {
        return success(petService.selectLocationOptions());
    }
}
