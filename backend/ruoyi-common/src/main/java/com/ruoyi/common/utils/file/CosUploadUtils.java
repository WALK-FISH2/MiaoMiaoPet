package com.ruoyi.common.utils.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.ruoyi.common.config.CosConfig;
import com.ruoyi.common.core.domain.UploadedFile;
import com.ruoyi.common.exception.file.FileNameLengthLimitExceededException;
import com.ruoyi.common.exception.file.FileSizeLimitExceededException;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.common.utils.StringUtils;

public class CosUploadUtils
{
    public static UploadedFile upload(MultipartFile file, CosConfig config, String directory, String[] allowedExtension)
            throws IOException, FileSizeLimitExceededException, FileNameLengthLimitExceededException,
            InvalidExtensionException
    {
        assertConfig(config);
        FileUploadUtils.assertAllowed(file, allowedExtension);

        String fileName = FileUploadUtils.uuidFilename(file);
        String key = joinKey(config.getPrefix(), directory, fileName);

        COSClient client = createClient(config);
        try (InputStream inputStream = file.getInputStream())
        {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            if (StringUtils.isNotEmpty(file.getContentType()))
            {
                metadata.setContentType(file.getContentType());
            }
            client.putObject(new PutObjectRequest(config.getBucket(), key, inputStream, metadata));
        }
        finally
        {
            client.shutdown();
        }

        String url = buildUrl(config, key);
        return new UploadedFile(url, url, FilenameUtils.getName(key));
    }

    public static String generateSignedUrl(String pathOrUrl, CosConfig config)
    {
        if (StringUtils.isEmpty(pathOrUrl))
        {
            return pathOrUrl;
        }
        assertConfig(config);

        String objectKey = extractObjectKey(pathOrUrl, config);
        if (StringUtils.isEmpty(objectKey))
        {
            return pathOrUrl;
        }

        COSClient client = createClient(config);
        try
        {
            int expires = config.getSignedUrlExpires() > 0 ? config.getSignedUrlExpires() : 3600;
            Date expirationDate = new Date(System.currentTimeMillis() + expires * 1000L);
            URL url = client.generatePresignedUrl(config.getBucket(), objectKey, expirationDate, HttpMethodName.GET);
            return url.toString();
        }
        finally
        {
            client.shutdown();
        }
    }

    private static COSClient createClient(CosConfig config)
    {
        COSCredentials credentials = new BasicCOSCredentials(config.getSecretId(), config.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(config.getRegion()));
        return new COSClient(credentials, clientConfig);
    }

    private static void assertConfig(CosConfig config)
    {
        if (config == null || StringUtils.isEmpty(config.getRegion()) || StringUtils.isEmpty(config.getBucket())
                || StringUtils.isEmpty(config.getSecretId()) || StringUtils.isEmpty(config.getSecretKey()))
        {
            throw new IllegalArgumentException("COS configuration is incomplete");
        }
    }

    private static String joinKey(String prefix, String directory, String fileName)
    {
        String key = trimSlash(prefix) + "/" + trimSlash(directory) + "/" + trimSlash(fileName);
        return key.replaceAll("/{2,}", "/").replaceAll("^/", "");
    }

    private static String trimSlash(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return "";
        }
        return value.replaceAll("^/+", "").replaceAll("/+$", "");
    }

    private static String buildUrl(CosConfig config, String key)
    {
        String domain = StringUtils.isNotEmpty(config.getDomain())
                ? config.getDomain()
                : "https://" + config.getBucket() + ".cos." + config.getRegion() + ".myqcloud.com";
        return domain.replaceAll("/+$", "") + "/" + encodeKey(key);
    }

    private static String extractObjectKey(String pathOrUrl, CosConfig config)
    {
        String value = pathOrUrl.trim();
        if (value.startsWith("http://") || value.startsWith("https://"))
        {
            try
            {
                URL url = new URL(value);
                String host = url.getHost();
                if (!isCosHost(host, config))
                {
                    return null;
                }
                return url.getPath().replaceAll("^/+", "");
            }
            catch (Exception e)
            {
                return null;
            }
        }
        return value.replaceAll("^/+", "");
    }

    private static boolean isCosHost(String host, CosConfig config)
    {
        if (StringUtils.isEmpty(host))
        {
            return false;
        }
        if (StringUtils.isNotEmpty(config.getDomain()))
        {
            try
            {
                return host.equalsIgnoreCase(new URL(config.getDomain()).getHost());
            }
            catch (Exception ignored)
            {
            }
        }
        return host.contains("myqcloud.com") || host.contains(".cos.");
    }

    private static String encodeKey(String key)
    {
        String[] parts = key.split("/");
        StringBuilder builder = new StringBuilder();
        for (String part : parts)
        {
            if (builder.length() > 0)
            {
                builder.append('/');
            }
            try
            {
                builder.append(URLEncoder.encode(part, StandardCharsets.UTF_8.toString()).replace("+", "%20"));
            }
            catch (UnsupportedEncodingException e)
            {
                throw new IllegalStateException("UTF-8 is not supported", e);
            }
        }
        return builder.toString();
    }
}
