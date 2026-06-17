package com.ruoyi.common.service;

import java.io.IOException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.config.CosConfig;
import com.ruoyi.common.config.UploadConfig;
import com.ruoyi.common.core.domain.UploadedFile;
import com.ruoyi.common.exception.file.FileNameLengthLimitExceededException;
import com.ruoyi.common.exception.file.FileSizeLimitExceededException;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.common.utils.file.CosUploadUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;

@Service
public class FileStorageService
{
    @Autowired
    private UploadConfig uploadConfig;

    @Autowired
    private CosConfig cosConfig;

    public UploadedFile upload(String localBaseDir, String directory, MultipartFile file, String[] allowedExtension,
            boolean useCustomNaming)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException,
            InvalidExtensionException
    {
        if (uploadConfig.isCos())
        {
            return CosUploadUtils.upload(file, cosConfig, directory, allowedExtension);
        }

        String fileName = FileUploadUtils.upload(localBaseDir, file, allowedExtension, useCustomNaming);
        return new UploadedFile(fileName, fileName, FileUtils.getName(fileName));
    }

    public boolean isCos()
    {
        return uploadConfig.isCos();
    }

    public String signUrl(String pathOrUrl)
    {
        if (!uploadConfig.isCos())
        {
            return pathOrUrl;
        }
        try
        {
            return CosUploadUtils.generateSignedUrl(pathOrUrl, cosConfig);
        }
        catch (Exception e)
        {
            return pathOrUrl;
        }
    }

    public String signJsonArray(String jsonArray)
    {
        if (!uploadConfig.isCos() || jsonArray == null || jsonArray.trim().isEmpty())
        {
            return jsonArray;
        }
        try
        {
            JSONArray source = JSON.parseArray(jsonArray);
            JSONArray signed = new JSONArray();
            for (Object item : source)
            {
                if (item instanceof String)
                {
                    signed.add(signUrl((String) item));
                }
                else
                {
                    signed.add(item);
                }
            }
            return signed.toJSONString();
        }
        catch (Exception e)
        {
            return signUrl(jsonArray);
        }
    }

}
