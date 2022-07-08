package com.han.ruoji.controller;

import com.han.ruoji.common.R;
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

@RestController
@RequestMapping("/common")
@Slf4j
public class FileController {

    @Value("${regiee.path}")
    String basePath;

/*
* 文件上传接口
* */
    @PostMapping("/upload")
    public R<String> fileUpload(MultipartFile file){

        //file临时文件

        //使用UUID生成文件名防止重复
        String name = file.getOriginalFilename();
        String substring = name.substring(name.lastIndexOf("."));//截取后缀

        String filName = UUID.randomUUID().toString()+substring;

        //创建一个目录对象

        File dir = new File(basePath);
        if(!dir.exists()){
            //目录不存在
            dir.mkdirs();//创建
        }

        try {

            //将临时文件转存到指定目录
            file.transferTo(new File(basePath+filName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info(file.toString());
        return R.success(filName);
    }


    @GetMapping("/download")
    public void fileDownload(String name, HttpServletResponse response){



        response.setContentType("image/jpeg");

        try {
            //输入流读取文件
            FileInputStream fileInputStream=new FileInputStream(basePath+name);

            //输出流将读取的文件输入给前端浏览器显示

            OutputStream outputStream=response.getOutputStream();

            int len=0;
            byte[] bytes=new byte[1024];

            while((len=fileInputStream.read(bytes)) !=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();




        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
