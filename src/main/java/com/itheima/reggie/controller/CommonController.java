package com.itheima.reggie.controller;


import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){ //形参要与name保持一致
//        file是一个临时文件，需要存储到指定路径，否则本次请求后临时文件会被删除

//        原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

//        使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String filename = UUID.randomUUID().toString() + suffix;

//        创建一个目录对象，判断该目录是否存在
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
//            将临时文件转存到指定位置
            file.transferTo(new File(basePath+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return R.success(filename);
    }

    /**
     * 文件下载
     * @param response
     * @param name
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response,String name){
//        输入流，通过输入流读取文件数据
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

 //        输出流，通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

//            使用字节流进行文件传输************************
            int len=0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1 ){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
