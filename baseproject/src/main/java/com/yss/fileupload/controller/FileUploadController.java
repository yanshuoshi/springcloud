package com.yss.fileupload.controller;

import cn.hutool.core.date.DateUtil;
import com.yss.common.bean.ApiResult;
import com.yss.fileupload.constant.FileConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Shuyu.Wang
 * @package:com.ganinfo.file.controller
 * @className:
 * @description:文件上传工具类
 * @date 2018-03-06 13:43
 **/
@RestController
@RequestMapping(value = "/file")
@RefreshScope
@Slf4j
public class FileUploadController {

  @Value("${file.save.path}")
  private String savePath;
  @Value("${file.download.path}")
  private String downloadPath;

  /**
   * 单个文件上传
   * @param file
   * @param request
   * @param resp
   * @return
   */
  @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
  @ResponseBody
  public ApiResult fileupload(@RequestParam(value = "file", required = true) MultipartFile file,
                                 HttpServletRequest request, HttpServletResponse resp) {
    log.info("文件上传");
    ApiResult apiResult = new ApiResult();

    try {
      if (file.isEmpty()) {
        apiResult.setMessage(FileConstant.FILE_IS_NULL);
        return apiResult;
      }
      long maxSize = 100000000;
      long fileSize = file.getSize();
      if (fileSize > maxSize) {
        // 100M
        apiResult.setMessage("上传文件尺寸过大！");
        return apiResult;
      }
      // 获取文件名
      String fileName = file.getOriginalFilename();
      log.info("上传的文件名为：" + fileName);
      // 获取文件的后缀名
      String suffixName = fileName.substring(fileName.lastIndexOf("."));
      log.info("文件的后缀名为：" + suffixName);

      // 设置文件存储路径
      String path = savePath + File.separator + System.currentTimeMillis();

      File dest = new File(path);
      // 检测是否存在目录
      if (!dest.getParentFile().exists()) {
        dest.getParentFile().mkdirs();// 新建文件夹
      }
      file.transferTo(dest);// 文件写入
      apiResult.setSuccess();
      apiResult.setMessage(FileConstant.FILE_UPLOAD_SUCCESS);
      apiResult.setData(path);
      return apiResult;
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    apiResult.setMessage(FileConstant.FILE_UPLOAD_ERROR);
    return apiResult;

  }

  /**
   * 多文件上传
   * @param request
   * @return
   */
  @RequestMapping(value = "/uploadMore", method = RequestMethod.POST)
  @ResponseBody
  public ApiResult handleFileUpload(HttpServletRequest request) {
    log.info("多文件上传");
    ApiResult apiResult = new ApiResult();

    List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
    MultipartFile file = null;
    BufferedOutputStream stream = null;
    for (int i = 0; i < files.size(); ++i) {
      file = files.get(i);
      String filePath = savePath;
      if (!file.isEmpty()) {
        try {
          byte[] bytes = file.getBytes();
          stream = new BufferedOutputStream(new FileOutputStream(
              new File(filePath + file.getOriginalFilename())));//设置文件路径及名字
          stream.write(bytes);// 写入
          stream.close();
        } catch (Exception e) {
          stream = null;
          apiResult.setMessage("第 " + i + " 个文件上传失败 ==> "
              + e.getMessage());
          return apiResult;
        }
      } else {
        apiResult.setMessage("第 " + i
            + " 个文件上传失败因为文件为空");
        return apiResult;
      }
    }
    apiResult.setSuccess();
    apiResult.setMessage(FileConstant.FILE_UPLOAD_SUCCESS);
    return apiResult;
  }

  /**
   * 文件下载相关代码
   * @param request
   * @param path
   * @param response
   * @return
   */
  @RequestMapping("/download")
  public ApiResult downloadFile(HttpServletRequest request, String path, HttpServletResponse response) {
    ApiResult apiResult = new ApiResult();
    String fileName = path.substring(path.lastIndexOf("/") + 1);// 设置文件名，根据业务需要替换成要下载的文件名
    if (fileName != null) {
      //设置文件路径
      String realPath = downloadPath;
      File file = new File(realPath , fileName);
      if (file.exists()) {
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
          fis = new FileInputStream(file);
          bis = new BufferedInputStream(fis);
          OutputStream os = response.getOutputStream();
          int i = bis.read(buffer);
          while (i != -1) {
            os.write(buffer, 0, i);
            i = bis.read(buffer);
          }
          System.out.println("success");
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (bis != null) {
            try {
              bis.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          if (fis != null) {
            try {
              fis.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }

    apiResult.setSuccess();
    apiResult.setMessage(FileConstant.FILE_DOWNLOAD_SUCCESS);
    apiResult.setData("");
    return apiResult;
  }



  public static void main(String[] args) {
    String str = "12121/121212/a.jpg";
    System.out.println(str.substring(str.lastIndexOf("/") + 1));
  }

}
