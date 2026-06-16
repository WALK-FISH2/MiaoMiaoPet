package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.WelfareLocationOption;
import com.ruoyi.system.domain.WelfarePet;

public interface WelfarePetMapper
{
    public List<WelfarePet> selectPetList(@Param("keyword") String keyword, @Param("status") String status,
            @Param("breed") String breed);

    public WelfarePet selectPetById(Long id);

    public int insertPet(WelfarePet pet);

    public int updatePet(WelfarePet pet);

    public int deletePetById(Long id);

    public int countPetReferences(Long id);

    public List<WelfareLocationOption> selectLocationOptions();
}
