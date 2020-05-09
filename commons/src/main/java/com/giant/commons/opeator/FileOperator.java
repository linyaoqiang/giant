package com.giant.commons.opeator;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class FileOperator {
    private static Logger logger = Logger.getLogger(FileOperator.class);

    public static Properties createProperties(String fileName) {
        InputStream is = createInByClassPath(fileName);
        if (is == null) {
            return null;
        }
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("无法解析properties文件:\t" + properties);
        }
        return properties;
    }

    public static Element createRootElement(String fileName) {
        Document document = createDocument(fileName);
        return document == null ? null : document.getRootElement();
    }

    public static Document createDocument(String fileName) {
        InputStream in=null;
        try {
            in=createInByClassPath(fileName);
            SAXReader saxReader = new SAXReader();
            return saxReader.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String classToPath(String className){
        return className.replace(".","/");
    }
    
    public static String classToPath(Class<?> clazz){
        return classToPath(clazz.getName());
    } 
    
    public static Element createRootElement(InputStream in) {
        try {
            return new SAXReader().read(in).getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream createInByClassPath(String fileName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
    }

    public static URL createURLFromBuildPath(String fileName){
        return Thread.currentThread().getContextClassLoader().getResource(fileName);
    }

    public static InputStream createIn(String fileName) {
        return createIn(new File(fileName));
    }

    public static InputStream createIn(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.info("资源未找到:\t" + e.getMessage());
        }
        return null;
    }

    public static void in2Out(InputStream in, OutputStream out) {
        BufferedInputStream bis = new BufferedInputStream(in);
        BufferedOutputStream bos = new BufferedOutputStream(out);
        byte[] bys = new byte[1024];
        int flush = 0;
        try {
            while ((flush = bis.read(bys)) != -1) {
                bos.write(bys, 0, flush);
            }
        } catch (IOException e) {
            logger.error("输入流中的数据读取到输出流中发生错误:\t" + e.getMessage());
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.error("关闭输入流时失败!!!\t" + e.getMessage());
            }
            try {
                bos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.error("关闭输出时失败!!!\t" + e.getMessage());
            }
        }
    }

    /**
     * @param src  源文件
     * @param dest 目标文件
     *             拷贝源文件成目标文件
     */
    public static void copyFile(File src, File dest) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = getInputByFile(src);
            bos = getOutputByFile(dest);
            byte[] datas = new byte[1024];
            int flush = 0;
            while ((flush = bis.read(datas)) != -1) {
                bos.write(datas, 0, flush);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            try {
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void copyFile(String srcName, String destName) {
        copyFile(new File(srcName), new File(destName));
    }

    /**
     * @param file 用于获取流的文件
     * @return 高效缓存文件输入流
     * @throws FileNotFoundException 可能发生文件我找到的异常
     */
    public static BufferedInputStream getInputByFile(File file) throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    /**
     * @param file 用于获取流的文件
     * @return 高效缓存文件输出流
     * @throws FileNotFoundException 可能发生文件我找到的异常
     */
    public static BufferedOutputStream getOutputByFile(File file) throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(file));
    }

    /**
     * @param srcFolder  源文件夹
     * @param destFolder 目标文件夹
     * @throws IOException 可能抛出的IO异常
     *                     将源文件夹下的东西拷贝到目标文件夹下
     */
    public static void copyFolder(File srcFolder, File destFolder) throws IOException {
        if (srcFolder == null || destFolder == null) {
            throw new IOException("传入的参数为null");
        }
        if (!srcFolder.isDirectory()) {
            throw new IOException("srcFolder不是文件夹" + srcFolder);
        }
        if (!srcFolder.exists()) {
            throw new IOException("源文件不存在" + srcFolder);
        }
        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }
        File[] files = srcFolder.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                copyFile(file, new File(destFolder, file.getName()));
            }
            if (file.isDirectory()) {
                copyFolder(file, new File(destFolder, file.getName()));
            }
        }
    }

    /**
     * @param folder 需要删除的文件夹
     * @throws IOException 可能抛出的异常
     */
    public static void deleteFolder(File folder) throws IOException {
        if (!folder.exists()) {
            throw new IOException("文件夹不存在");
        }
        if (folder.isFile()) {
            folder.delete();
            return;
        }
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            }
            if (file.isDirectory()) {
                deleteFolder(file);
            }
        }
        folder.delete();
    }

    /**
     * @param srcFolder  需要剪切的文件夹
     * @param destFolder 将被剪切的文件夹下的内容拷贝包目标文件夹下
     * @throws IOException 可能抛出的IO异常
     */
    public static void cutFolder(File srcFolder, File destFolder) throws IOException {
        copyFolder(srcFolder, destFolder);
        deleteFolder(srcFolder);
    }

    public static void cutFolder(String srcFolderName, String destFolderName) throws IOException {
        cutFolder(new File(srcFolderName), new File(destFolderName));
    }

    /**
     * @param srcFolder  需要剪切的文件夹
     * @param destFolder 将被剪切的文件夹拷贝包目标文件夹下
     * @throws IOException 可能抛出的IO异常
     */
    public static void cutToFolder(File srcFolder, File destFolder) throws IOException {
        copyToFolder(srcFolder, destFolder);
        deleteFolder(srcFolder);
    }

    public static void cutToFolder(String srcFolderName, String destFolderName) throws IOException {
        cutToFolder(new File(srcFolderName), new File(destFolderName));
    }

    /**
     * @param srcFolderName  需要拷贝的文件夹
     * @param destFolderName 将源文件夹下的内容拷贝到目标文件夹
     * @throws IOException 可能抛出的异常
     */
    public static void copyFolder(String srcFolderName, String destFolderName) throws IOException {
        copyFolder(new File(srcFolderName), new File(destFolderName));
    }

    /**
     * @param srcFolder  需要拷贝的文件夹
     * @param destFolder 将源文件夹拷贝到目标文件夹
     * @throws IOException 可能抛出的异常
     */
    public static void copyToFolder(File srcFolder, File destFolder) throws IOException {
        copyFolder(srcFolder, new File(destFolder, srcFolder.getName()));
    }

    public static void copyToFolder(String srcFolderName, String destFolderName) throws IOException {
        copyToFolder(new File(srcFolderName), new File(destFolderName));
    }

    /**
     * @param folder       查找文件的文件夹
     * @param containsName 文件的模糊名
     * @param fileList     存储查找文件的集合
     * @throws IOException 可能抛出的IO异常
     */
    public static void searchFile(File folder, String containsName, List<File> fileList) throws IOException {
        if (containsName == null) {
            throw new FileNotFoundException("查找条件为null:" + containsName);
        }
        if (folder == null || !folder.exists() || folder.isFile()) {
            throw new FileNotFoundException("文件夹为空或者不是文件夹:" + folder);
        }
        if (fileList == null) {
            throw new NullPointerException("没有集合装载File");
        }
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                if (file.getName().contains(containsName)) {
                    fileList.add(file);
                }
            }
            if (file.isDirectory()) {
                searchFile(file, containsName, fileList);
            }
        }
    }
}
