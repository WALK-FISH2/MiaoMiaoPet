package com.ruoyi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "upload")
public class UploadConfig
{
    private String backend = "local";

    public String getBackend()
    {
        return backend;
    }

    public void setBackend(String backend)
    {
        this.backend = backend;
    }

    public boolean isCos()
    {
        return "cos".equalsIgnoreCase(backend);
    }
}
