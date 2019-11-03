package com.caicai.javaOrc.controller;

import org.jdesktop.swingx.util.OS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ORC {
    private final String LANG_OPTION = "-l";  //英文字母小写l，并非数字1
    private final String EOL = System.getProperty("line.separator");

    //window 配置
    //    private String tessPath = "C://Program Files//Tesseract-OCR";

    //mac os
    private String tessPath = "/usr/local/Cellar/tesseract/4.1.0/bin";

    public String recognizeText(File imageFile, String imageFormat) throws Exception {
        File tempImage = ImageHelper.createImage(imageFile, imageFormat);
        File outputFile = new File(imageFile.getParentFile(), "output");
        StringBuffer strB = new StringBuffer();
        List<String> cmd = new ArrayList<>();
        if (OS.isWindowsXP()) {
            cmd.add(tessPath + "/tesseract");
        } else if (OS.isLinux()) {
            cmd.add("tesseract");
        } else {
            cmd.add(tessPath + "/tesseract");
        }
        cmd.add("");
        cmd.add(outputFile.getName());
        cmd.add(LANG_OPTION);
        //多个语言包lang1+lang2
        cmd.add("chi_sim+eng");

        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(imageFile.getParentFile());

        cmd.set(1, tempImage.getName());
        pb.command(cmd);
        pb.redirectErrorStream(true);

        //生成output.txt
        Process process = pb.start();

        int w = process.waitFor();

        //删除临时正在工作文件
        tempImage.delete();

        //0是正常返回
        if (w == 0) {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile.getAbsolutePath() + ".txt"), "UTF-8"));

            String str;
            while ((str = in.readLine()) != null) {
                strB.append(str).append(EOL);
            }
            in.close();
        } else {
            String msg;
            switch (w) {
                case 1:
                    msg = "访问文件时出错。图像文件名中可能有空格。";
                    break;
                case 29:
                    msg = "Cannot recongnize the image or its selected region.";
                    break;
                case 31:
                    msg = "Unsupported image format.";
                    break;
                default:
                    msg = "Errors occurred.";
            }
            tempImage.delete();
            throw new RuntimeException(msg);
        }
        new File(outputFile.getAbsolutePath() + ".txt").delete();
        return strB.toString().replaceAll(" ", "");
    }
}
