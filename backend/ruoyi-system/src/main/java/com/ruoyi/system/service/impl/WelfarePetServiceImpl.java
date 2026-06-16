package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.WelfareLocationOption;
import com.ruoyi.system.domain.WelfarePet;
import com.ruoyi.system.mapper.WelfarePetMapper;
import com.ruoyi.system.service.IWelfarePetService;

@Service
public class WelfarePetServiceImpl implements IWelfarePetService
{
    @Autowired
    private WelfarePetMapper petMapper;

    @Override
    public List<WelfarePet> selectPetList(String keyword, String status, String breed)
    {
        return petMapper.selectPetList(keyword, status, breed);
    }

    @Override
    public WelfarePet selectPetById(Long id)
    {
        return petMapper.selectPetById(id);
    }

    @Override
    public int insertPet(WelfarePet pet)
    {
        validatePet(pet);
        return petMapper.insertPet(pet);
    }

    @Override
    public int updatePet(WelfarePet pet)
    {
        if (pet.getId() == null)
        {
            throw new ServiceException("宠物ID不能为空");
        }
        validatePet(pet);
        return petMapper.updatePet(pet);
    }

    @Override
    public int deletePetById(Long id)
    {
        if (id == null)
        {
            throw new ServiceException("宠物ID不能为空");
        }
        if (petMapper.countPetReferences(id) > 0)
        {
            throw new ServiceException("该宠物存在领养、照片、评论或关注等关联数据，不能直接删除");
        }
        return petMapper.deletePetById(id);
    }

    @Override
    public List<WelfareLocationOption> selectLocationOptions()
    {
        return petMapper.selectLocationOptions();
    }

    private void validatePet(WelfarePet pet)
    {
        if (StringUtils.isBlank(pet.getName()))
        {
            throw new ServiceException("宠物名称不能为空");
        }
        if (pet.getLocationId() == null)
        {
            throw new ServiceException("所属地点不能为空");
        }
        if (StringUtils.isBlank(pet.getGender()))
        {
            throw new ServiceException("性别不能为空");
        }
        if (!StringUtils.equals("male", pet.getGender()) && !StringUtils.equals("female", pet.getGender()))
        {
            throw new ServiceException("性别只能是 male 或 female");
        }
        if (StringUtils.isBlank(pet.getStatus()))
        {
            pet.setStatus("available");
        }
        if (!StringUtils.equals("available", pet.getStatus()) && !StringUtils.equals("adopting", pet.getStatus())
                && !StringUtils.equals("adopted", pet.getStatus()))
        {
            throw new ServiceException("宠物状态只能是 available、adopting 或 adopted");
        }
    }
}
