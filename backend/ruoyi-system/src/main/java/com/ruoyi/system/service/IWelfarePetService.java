package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.WelfareLocationOption;
import com.ruoyi.system.domain.WelfarePet;

public interface IWelfarePetService
{
    public List<WelfarePet> selectPetList(String keyword, String status, String breed);

    public WelfarePet selectPetById(Long id);

    public int insertPet(WelfarePet pet);

    public int updatePet(WelfarePet pet);

    public int deletePetById(Long id);

    public List<WelfareLocationOption> selectLocationOptions();
}
