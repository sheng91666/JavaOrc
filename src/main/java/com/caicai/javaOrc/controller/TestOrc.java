package com.caicai.javaOrc.controller;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TestOrc {
    private Logger logger = LoggerFactory.getLogger(TestOrc.class);

    @RequestMapping("/toHome")
    public ModelAndView toHome(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("Home");
        return mv;
    }


    @RequestMapping("/uploadFile")
    @ResponseBody
    public String javaOrc(HttpServletRequest request, MultipartFile file) {

        String ipAddress = getIpAddress(request);
        logger.info("javaORC--开始：{}", ipAddress);
        String originalFilename = file.getOriginalFilename();
        String[] split = originalFilename.split("\\.");
        String endStr = split[split.length - 1];

        Map<String, String> map = new HashMap<>();
        map.put("status", "0");

        List<String> strings = new ArrayList<String>() {{
            add("png");
            add("jpg");
        }};

        if (!strings.contains(endStr)) {
            map.put("msg", "只能上传.png或者.jpg的图片");
            return JSON.toJSONString(map);
        }

        //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        String savePath = request.getServletContext().getRealPath("/WEB-INF/upload");
        File file1 = new File(savePath);
        //判断上传文件的保存目录是否存在
        if (!file1.exists() && !file1.isDirectory()) {
            System.out.println(savePath + "目录不存在，需要创建");
            //创建目录
            file1.mkdir();

        }

        try {

            InputStream inputStream = file.getInputStream();

            //创建一个文件输出流
            FileOutputStream out = new FileOutputStream(savePath + "/" + originalFilename);
            //创建一个缓冲区
            byte buffer[] = new byte[1024];
            //判断输入流中的数据是否已经读完的标识
            int len = 0;
            //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
            while ((len = inputStream.read(buffer)) > 0) {
                //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                out.write(buffer, 0, len);
            }

            //关闭输入流
            inputStream.close();
            //关闭输出流
            out.close();

            String path = savePath + "/" + originalFilename;
            File file2 = new File(path);

            String valCode = new ORC().recognizeText(file2, "png");
            if (!ObjectUtils.isEmpty(valCode)) {
                String data = valCode.replaceAll(" ", "");
                map.put("status", "1");
                map.put("msg", data);
                logger.info("javaORC--解析文件--ip：{},返回内容：{}", ipAddress, data);
            }

            //删除文件
            logger.info("javaORC--解析文件--删除文件");
            deleteFile(file2);

        } catch (Exception e) {
            map.put("msg", e.getMessage());
            logger.info("javaORC--ip：{},异常：{}", ipAddress, e);
        }

        return JSON.toJSONString(map);
    }


    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public boolean deleteFile(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
        }
        return file.delete();
    }


    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * <p>
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * <p>
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,192.168.1.100
     * 用户真实IP为： 192.168.1.110
     *
     * @param request
     * @return
     */
    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
