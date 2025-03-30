package com.bank.duty.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件处理工具类
 */
public class FileUtils {

    /**
     * 文件上传路径
     */
    private static String uploadPath = "/opt/bank-duty/upload";

    /**
     * 默认的文件名最大长度
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 默认上传的地址
     */
    private static String defaultBaseDir = uploadPath;

    public static void setDefaultBaseDir(String defaultBaseDir) {
        FileUtils.defaultBaseDir = defaultBaseDir;
    }

    public static String getDefaultBaseDir() {
        return defaultBaseDir;
    }

    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件名称
     * @throws Exception
     */
    public static String upload(MultipartFile file) throws IOException {
        try {
            return upload(getDefaultBaseDir(), file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 文件上传
     *
     * @param baseDir 相对应用的基目录
     * @param file 上传的文件
     * @param allowedExtension 上传文件类型
     * @return 返回上传成功的文件名
     * @throws IOException 例如读写文件出错时
     */
    public static String upload(String baseDir, MultipartFile file, String[] allowedExtension)
            throws IOException {
        // 获取文件原始名称
        String originalFilename = file.getOriginalFilename();

        // 检查文件大小和类型
        assertAllowed(file, allowedExtension);

        // 创建年月日目录
        String datePath = DateUtils.dateTimeNow("yyyy/MM/dd");

        // 构建完整的文件保存路径
        String fileName = extractFilename(file, datePath);

        // 创建目标文件对象
        File desc = getAbsoluteFile(baseDir + "/" + fileName);

        // 保存文件
        file.transferTo(desc);

        // 返回文件的相对路径
        return fileName;
    }

    /**
     * 编码文件名
     */
    public static String extractFilename(MultipartFile file, String datePath) {
        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        fileName = datePath + "/" + UUID.randomUUID().toString().replaceAll("-", "") + "." + extension;
        return fileName;
    }

    /**
     * 创建目标文件，如果目录不存在会自动创建
     */
    private static File getAbsoluteFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }

    /**
     * 文件下载
     *
     * @param filePath 文件路径
     * @param response 响应对象
     * @param fileName 下载的文件名
     */
    public static void download(String filePath, HttpServletResponse response, String fileName) throws IOException {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        setAttachmentResponseHeader(response, fileName);
        FileUtils.writeBytes(filePath, response.getOutputStream());
    }

    /**
     * 将文件写入到输出流
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.close(os);
            IOUtils.close(fis);
        }
    }

    /**
     * 设置下载响应头
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
    }

    /**
     * 检查文件大小和类型
     */
    public static void assertAllowed(MultipartFile file, String[] allowedExtension) throws IOException {
        long size = file.getSize();
        if (size > MimeTypeUtils.DEFAULT_MAX_SIZE) {
            throw new IOException("文件大小超出限制，最大支持：" + (MimeTypeUtils.DEFAULT_MAX_SIZE / 1024 / 1024) + "MB");
        }

        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            throw new IOException("文件类型不支持，仅支持：" + String.join(", ", allowedExtension));
        }
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     */
    public static boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为图片
     */
    public static boolean isImage(MultipartFile file) {
        return StringUtils.startsWithIgnoreCase(file.getContentType(), "image/");
    }

    /**
     * 检测是否为允许上传的文件类型
     */
    public static boolean checkAllowedFile(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }
        Path path = Paths.get(filePath);
        try {
            Files.deleteIfExists(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

/**
 * 媒体类型工具类
 */
class MimeTypeUtils {
    public static final String IMAGE_PNG = "image/png";
    public static final String IMAGE_JPG = "image/jpg";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_BMP = "image/bmp";
    public static final String IMAGE_GIF = "image/gif";

    public static final String[] IMAGE_EXTENSION = { "bmp", "gif", "jpg", "jpeg", "png" };
    public static final String[] DOCUMENT_EXTENSION = { "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt" };
    public static final String[] COMPRESS_EXTENSION = { "zip", "rar", "gz", "tar" };

    public static final String[] DEFAULT_ALLOWED_EXTENSION = {
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png",
            // 文档
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt",
            // 压缩文件
            "zip", "rar", "gz", "tar"
    };

    // 默认最大文件大小 10MB
    public static final long DEFAULT_MAX_SIZE = 10 * 1024 * 1024;

    /**
     * 根据ContentType获取文件后缀
     */
    public static String getExtension(String contentType) {
        if (contentType == null) {
            return "";
        }
        if (contentType.equals(IMAGE_PNG)) {
            return "png";
        }
        if (contentType.equals(IMAGE_JPG) || contentType.equals(IMAGE_JPEG)) {
            return "jpg";
        }
        if (contentType.equals(IMAGE_BMP)) {
            return "bmp";
        }
        if (contentType.equals(IMAGE_GIF)) {
            return "gif";
        }
        return "";
    }
}

/**
 * 文件名工具类
 */
class FilenameUtils {
    /**
     * 获取文件名的后缀
     */
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return filename.substring(index + 1);
    }
}