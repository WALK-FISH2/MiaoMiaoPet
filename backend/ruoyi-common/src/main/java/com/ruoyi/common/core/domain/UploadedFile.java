package com.ruoyi.common.core.domain;

public class UploadedFile
{
    private final String url;
    private final String fileName;
    private final String newFileName;

    public UploadedFile(String url, String fileName, String newFileName)
    {
        this.url = url;
        this.fileName = fileName;
        this.newFileName = newFileName;
    }

    public String getUrl()
    {
        return url;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getNewFileName()
    {
        return newFileName;
    }
}
