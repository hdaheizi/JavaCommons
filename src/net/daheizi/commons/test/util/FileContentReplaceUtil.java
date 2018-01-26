package net.daheizi.commons.test.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件内容替换
 * @author daheizi
 * @Date 2018年1月26日 上午12:23:05
 */
public class FileContentReplaceUtil {
    private static final String src = "\t";
    private static final String dest = "    ";
    
    public static void main(String[] args) {
        File file = new File("D:\\JavaWorckBench\\hdaheizi\\src\\org\\daheizi\\commons");
        
        listFile(file);
 
    }
 
    private static void listFile(File file) {
        if (!file.exists() || !file.isDirectory()) {
            System.out.println("文件路径不合法!");
            return;
        }
 
        String[] strings = file.list();
 
        File javaFile;
        for (String filename : strings) {
            javaFile = new File(file.getPath() + File.separator + filename);
            if (javaFile.isFile()) {
                if (filename.endsWith(".java")) {
                    try {
                        modifyFile(javaFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (javaFile.isDirectory()) {
                listFile(javaFile);
            }
        }
 
    }
 
    private static void modifyFile(File javaFile) throws IOException {
        BufferedReader bReader = new BufferedReader(new FileReader(javaFile));
 
        File tempFile = new File(javaFile.getParent() + File.separator + javaFile.getName() + ".tmp");
        if (!tempFile.exists() && !tempFile.createNewFile()) {
            System.out.println("创建临时文件失败.临时文件路径为:" + tempFile.getPath());
            bReader.close();
            return;
        }
 
        BufferedWriter bWriter = new BufferedWriter(new FileWriter(tempFile));
 
        String temp;
        boolean modify = false;
        while ((temp = bReader.readLine()) != null) {
            if (!modify && temp.indexOf(src) != -1) {
                modify = true;
            }
            bWriter.write(temp.replaceAll(src, dest)+"\r\n");
        }
 
        bWriter.close();
        bReader.close();
 
        if (modify) {
            System.out.println("该文件需要修改.File=" + javaFile.getPath());
            File bakFile = new File(javaFile.getPath() + ".bak");
            if (!javaFile.renameTo(bakFile)) {
                System.out.println("重命名源文件失败.源文件为:" + javaFile);
                tempFile.delete();
                return;
            }
 
            if (!tempFile.renameTo(javaFile)) {
                System.out.println("重命名临时文件失败.");
                if (!bakFile.renameTo(javaFile)) {
                    System.out.println("还原源文件失败，源文件现在的路径是:" + bakFile);
                }
                tempFile.delete();
                return;
            }
            bakFile.delete();
        } else {
            System.out.println("该文件未存在需要修改的内容.File=" + javaFile.getPath());
            tempFile.delete();
        }
    }
}
