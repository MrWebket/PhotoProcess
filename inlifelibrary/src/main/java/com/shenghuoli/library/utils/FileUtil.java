
package com.shenghuoli.library.utils;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * <p>
 * 类描述：文件操作工具类
 * 
 * @author dbzhuang
 */
public class FileUtil {
    private FileUtil() {
    }

    /**
     * 按默认顺序，即不排序
     */
    public static final int SORT_BY_DEFAULT = 0;

    /**
     * 按修改时间排序，正数为升序，负数为降序
     */
    public static final int SORT_BY_LAST_MODIFIED = 1;

    /**
     * 按名称排序，正数为升序，负数为降序
     */
    public static final int SORT_BY_NAME = 2;

    /**
     * 按文件大小排序（目录大小为0），正数为升序，负数为降序
     */
    public static final int SORT_BY_LENGTH = 3;

    private static final int MAX_CACHE_BYTES = 1024 * 1024 * 5; // 5M max cache

    /**
     * 创建指定File的上一级目录
     * 
     * @param file 指定的File
     * @return true，如果上一级已存在或创建成功；false，其他情况
     */
    public static boolean makeParentDirs(File file) {
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            return false;
        }
        if (!parentFile.exists()) {
            return parentFile.mkdirs();
        }
        return true;
    }

    /**
     * 在指定的目录下搜索一个文件，遍历子目录
     * 
     * @param dir 要搜索的目录
     * @param filter 搜索条件过滤器，{@link FileFilter#accept(File)}
     *            返回true表示文件为所要的文件，搜索结束。
     * @param sortBy 指定文件排序方式，值为：{@link #SORT_BY_DEFAULT}、
     *            {@link #SORT_BY_LAST_MODIFIED}、 {@link #SORT_BY_NAME}、
     *            {@link #SORT_BY_LENGTH} 其中之一，正数为升序，负数为降序
     * @return 搜索到的文件
     */
    public static File searchFile(File dir, FileFilter filter, int sortBy) {
        File[] files = dir.listFiles();
        if (files == null || files.length <= 0) {
            return null;
        }
        if (sortBy != 0) {
            final int sortType = Math.abs(sortBy);
            final int sortSign = sortBy / sortType;
            Arrays.sort(files, new Comparator<File>() {

                @Override
                public int compare(File lhs, File rhs) {
                    switch (sortType) {
                        case SORT_BY_LAST_MODIFIED: {
                            long dif = rhs.lastModified() - lhs.lastModified();
                            return sortSign * (dif == 0 ? 0 : (dif > 0 ? 1 : -1));
                        }
                        case SORT_BY_NAME: {
                            return sortSign * rhs.getName().compareTo(lhs.getName());
                        }
                        case SORT_BY_LENGTH: {
                            long dif = rhs.length() - lhs.length();
                            return sortSign * (dif == 0 ? 0 : (dif > 0 ? 1 : -1));
                        }
                    }
                    return 0;
                }
            });
        }
        for (File file : files) {
            if (filter.accept(file)) {
                return file;
            }
            if (file.isDirectory()) {
                File result = searchFile(file, filter, sortBy);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * 获取文件大小，单独的文件或目录
     * 
     * @param file 指定的文件
     * @return 文件大小
     */
    public static long getFileSize(File file) {
        if (file == null) {
            return 0l;
        }
        if (!file.exists()) {
            return 0l;
        }
        if (file.isFile()) {
            return file.length();
        } else if (file.isDirectory()) {
            long total = 0l;
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    total += getFileSize(f);
                }
            }
            return total;
        }
        return 0l;
    }

    /**
     * 用File.separatorChar生成文件路径
     * 
     * @param fileNames 文件/目录名数组
     * @return 生成的路径
     */
    public static String makeFilePath(String... fileNames) {
        if (fileNames == null || fileNames.length <= 0) {
            return null;
        }
        StringBuilder path = new StringBuilder();
        int count = 0;
        for (String name : fileNames) {
            if (count > 0) {
                path.append(File.separatorChar);
            }
            path.append(name);
            count++;
        }
        return path.toString();
    }

    /**
     * 通配符匹配文件名
     * 
     * @param wildcardName 通配符文件名
     * @param fileName 要匹配的文件名
     * @return 是否匹配
     */
    public static boolean wildcardMatches(String wildcardName, String fileName) {
        if (wildcardName == null || fileName == null) {
            return false;
        }
        String regex = wildcardName.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*")
                .replaceAll("\\?", ".{1}");
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(fileName).matches();
    }

    /**
     * 统计文件夹内的文件数，不包括文件夹
     * 
     * @param dirPath 要统计的文件夹目录
     * @return 文件数量
     */
    public static int countAllFiles(String dirPath) {
        return countAllFiles(new File(dirPath), null);
    }

    /**
     * 统计文件夹内的文件数，不包括文件夹
     * 
     * @param dirPath 要统计的文件夹目录
     * @param filter 文件过滤器
     * @return 文件数量
     */
    public static int countAllFiles(String dirPath, FileFilter filter) {
        return countAllFiles(new File(dirPath), filter);
    }

    /**
     * 统计文件夹内的文件数，不包括文件夹
     * 
     * @param dirFile 要统计的文件夹
     * @return 文件数量
     */
    public static int countAllFiles(File dirFile) {
        return countAllFiles(dirFile, null);
    }

    /**
     * 统计文件夹内的文件数，不包括文件夹
     * 
     * @param dirFile 要统计的文件夹
     * @param filter 文件过滤器
     * @return 文件数量
     */
    public static int countAllFiles(File dirFile, FileFilter filter) {
        if (!dirFile.isDirectory()) {
            return 0;
        }
        int count = 0;
        File[] files = dirFile.listFiles(filter);
        for (File file : files) {
            if (file.isDirectory()) {
                count += countAllFiles(file, filter);
            } else {
                count++;
            }
        }
        return count;
    }

    /**
     * 列出指定目录下的所有文件，递归子目录，不包括指定目录本身
     * 
     * @param dirPath 指定目录路径
     * @return 文件列表
     */
    public static ArrayList<File> listAllFiles(String dirPath) {
        return listAllFiles(new File(dirPath), null);
    }

    /**
     * 列出指定目录下的所有文件，递归子目录，不包括指定目录本身
     * 
     * @param dirPath 指定目录路径
     * @param filter 文件过滤器
     * @return 文件列表
     */
    public static ArrayList<File> listAllFiles(String dirPath, FileFilter filter) {
        return listAllFiles(new File(dirPath), filter);
    }

    /**
     * 列出指定目录下的所有文件，递归子目录，不包括指定目录本身
     * 
     * @param dirFile 指定目录
     * @return 文件列表
     */
    public static ArrayList<File> listAllFiles(File dirFile) {
        return listAllFiles(dirFile, null);
    }

    /**
     * 列出指定目录下的所有文件，递归子目录，不包括指定目录本身
     * 
     * @param dirFile 指定目录
     * @param filter 文件过滤器
     * @return 文件列表
     */
    public static ArrayList<File> listAllFiles(File dirFile, FileFilter filter) {
        if (!dirFile.isDirectory()) {
            return null;
        }
        ArrayList<File> fileList = new ArrayList<File>();
        File[] files = dirFile.listFiles(filter);
        for (File file : files) {
            fileList.add(file);
            if (file.isDirectory()) {
                fileList.addAll(listAllFiles(file, filter));
            }
        }
        return fileList;
    }

    /**
     * 读取文件，适用于小文件读取
     * 
     * @param path 文件路径
     * @return 文件内容
     */
    public static byte[] readFile(String path) {
        return readFile(path != null ? new File(path) : null);
    }

    /**
     * 读取文件，适用于小文件读取
     * 
     * @param file 要读取的文件
     * @return 文件内容
     */
    public static byte[] readFile(File file) {
        if (file == null) {
            return null;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            int total = fis.read(buffer);
            return total > 0 ? buffer : null;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            IOUtil.closeSilently(fis);
        }
        return null;
    }

    /**
     * 写文件，覆盖方式，适用于少内容的写入
     * 
     * @param file 目标文件路径
     * @param data 数据内容
     * @return 成功与否
     */
    public static boolean writeFile(String path, byte[] data) {
        return writeFile(path, data, false);
    }

    /**
     * 写文件，适用于少内容的写入
     * 
     * @param path 目标文件路径
     * @param data 数据内容
     * @param append 是否用追加方式
     * @return 成功与否
     */
    public static boolean writeFile(String path, byte[] data, boolean append) {
        return writeFile(path != null ? new File(path) : null, data, append);
    }

    /**
     * 写文件，覆盖方式，适用于少内容的写入
     * 
     * @param file 目标文件
     * @param data 数据内容
     * @return 成功与否
     */
    public static boolean writeFile(File file, byte[] data) {
        return writeFile(file, data, false);
    }

    /**
     * 写文件，适用于少内容的写入
     * 
     * @param file 目标文件
     * @param data 数据内容
     * @param append 是否用追加方式
     * @return 成功与否
     */
    public static boolean writeFile(File file, byte[] data, boolean append) {
        if (file == null) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, append);
            fos.write(data);
            fos.flush();
            return true;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            IOUtil.closeSilently(fos);
        }
        return false;
    }

    /**
     * 删除目录
     * 
     * @param directiory 要删除的目录路径
     */
    public static boolean deleteDirectiory(String directiory) {
        return deleteDirectiory(directiory, null);
    }

    /**
     * 删除目录
     * 
     * @param directiory 要删除的目录路径
     */
    public static boolean deleteDirectiory(File directiory) {
        return deleteDirectiory(directiory, null);
    }

    /**
     * 删除目录
     * 
     * @param directiory 要删除的目录路径
     * @param fileFilter 文件过滤器
     */
    public static boolean deleteDirectiory(String directiory, FileFilter fileFilter) {
        return deleteDirectiory(new File(directiory), fileFilter);
    }

    /**
     * 删除目录
     * 
     * @param directiory 要删除的目录路径
     * @param fileFilter 文件过滤器
     */
    public static boolean deleteDirectiory(File directiory, FileFilter fileFilter) {
        if (!directiory.isDirectory()) {
            return false;
        }
        if (fileFilter != null) {
            if (!fileFilter.accept(directiory)) {
                return false;
            }
        }
        boolean isSuccess = true;
        File[] files = directiory.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                if (fileFilter != null) {
                    if (!fileFilter.accept(file)) {
                        isSuccess = false;
                        continue;
                    }
                }
                isSuccess = file.delete() && isSuccess;
            } else if (file.isDirectory()) {
                isSuccess = deleteDirectiory(file, fileFilter) && isSuccess;
            }
        }
        isSuccess = directiory.delete() && isSuccess;
        return isSuccess;
    }

    /**
     * 拷贝文件/目录
     * 
     * @param sourceFile 源文件/目录
     * @param targetFile 目标文件/目录
     */
    public static boolean copy(String sourceFile, String targetFile) {
        return copy(sourceFile, targetFile, null);
    }

    /**
     * 拷贝文件/目录
     * 
     * @param sourceFile 源文件/目录
     * @param targetFile 目标文件/目录
     */
    public static boolean copy(File sourceFile, File targetFile) {
        return copy(sourceFile, targetFile, null);
    }

    /**
     * 拷贝文件/目录
     * 
     * @param sourceFile 源文件/目录
     * @param targetFile 目标文件/目录
     * @param fileFilter 文件过滤器
     */
    public static boolean copy(String sourceFile, String targetFile, FileFilter fileFilter) {
        return copy(new File(sourceFile), new File(targetFile), fileFilter);
    }

    /**
     * 拷贝文件/目录
     * 
     * @param sourceFile 源文件/目录
     * @param targetFile 目标文件/目录
     * @param fileFilter 文件过滤器
     */
    public static boolean copy(File sourceFile, File targetFile, FileFilter fileFilter) {
        if (sourceFile.isFile()) {
            return copyFile(sourceFile, targetFile, fileFilter);
        } else if (sourceFile.isDirectory()) {
            return copyDirectiory(sourceFile, targetFile, fileFilter);
        }
        return false;
    }

    /**
     * 拷贝文件
     * 
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     */
    public static boolean copyFile(String sourceFile, String targetFile) {
        return copyFile(sourceFile, targetFile, null);
    }

    /**
     * 拷贝文件
     * 
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     */
    public static boolean copyFile(File sourceFile, File targetFile) {
        return copyFile(sourceFile, targetFile, null);
    }

    /**
     * 拷贝文件
     * 
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @param fileFilter 文件过滤器
     */
    public static boolean copyFile(String sourceFile, String targetFile, FileFilter fileFilter) {
        return copyFile(new File(sourceFile), new File(targetFile), fileFilter);
    }

    /**
     * 拷贝文件
     * 
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @param fileFilter 文件过滤器
     */
    public static boolean copyFile(File sourceFile, File targetFile, FileFilter fileFilter) {
        if (!sourceFile.isFile()) {
            return false;
        }
        if (fileFilter != null) {
            if (!fileFilter.accept(sourceFile)) {
                return false;
            }
        }
        File parent = targetFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(targetFile);
            byte[] buffer = new byte[Math.max(1, Math.min(MAX_CACHE_BYTES, in.available()))];
            int readTotal = 0;
            while (true) {
                readTotal = in.read(buffer);
                if (readTotal == -1) {
                    break;
                }
                out.write(buffer, 0, readTotal);
            }
            return true;
        } catch (IOException e) {
        } finally {
            IOUtil.closeSilently(in);
            IOUtil.closeSilently(out);
        }
        return false;
    }

    /**
     * 拷贝目录
     * 
     * @param sourceDir 源目录
     * @param targetDir 目标目录
     */
    public static boolean copyDirectiory(String sourceDir, String targetDir) {
        return copyDirectiory(sourceDir, targetDir, null);
    }

    /**
     * 拷贝目录
     * 
     * @param sourceDir 源目录
     * @param targetDir 目标目录
     */
    public static boolean copyDirectiory(File sourceDir, File targetDir) {
        return copyDirectiory(sourceDir, targetDir, null);
    }

    /**
     * 拷贝目录
     * 
     * @param sourceDir 源目录
     * @param targetDir 目标目录
     * @param fileFilter 文件过滤器
     */
    public static boolean copyDirectiory(String sourceDir, String targetDir, FileFilter fileFilter) {
        return copyDirectiory(new File(sourceDir), new File(targetDir), fileFilter);
    }

    /**
     * 拷贝目录
     * 
     * @param sourceDir 源目录
     * @param targetDir 目标目录
     * @param fileFilter 文件过滤器
     */
    public static boolean copyDirectiory(File sourceDir, File targetDir, FileFilter fileFilter) {
        if (!sourceDir.isDirectory()) {
            return false;
        }
        if (fileFilter != null) {
            if (!fileFilter.accept(sourceDir)) {
                return false;
            }
        }
        targetDir.mkdirs();
        File[] files = sourceDir.listFiles();
        boolean isSuccess = true;
        for (File file : files) {
            if (file.isFile()) {
                isSuccess = copyFile(file, new File(targetDir.getAbsolutePath() + File.separator
                        + file.getName()), fileFilter)
                        && isSuccess;
            } else if (file.isDirectory()) {
                isSuccess = copyDirectiory(file, new File(targetDir.getAbsolutePath()
                        + File.separator + file.getName()), fileFilter)
                        && isSuccess;
            }
        }
        return isSuccess;
    }

    /**
     * 将文本信息写入文件,注意编码格式
     * 
     * @param fileName：文件名
     * @param content：要写入的信息
     * @param encoder：编码格式
     */
    public static void writeToFile(String dir, String fileName, String content, String encoder) {
        File file = new File(dir, fileName);
        File parentFile = file.getParentFile();
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            osw = new OutputStreamWriter(new FileOutputStream(file, true), encoder);
            bw = new BufferedWriter(osw);
            bw.append(content);
            bw.append("\r\n");
            bw.flush();
        } catch (IOException e) {
        } finally {
            IOUtil.closeSilently(bw);
            IOUtil.closeSilently(osw);
        }
    }

    /**
     * 将流写入文件中
     * 
     * @param fileName
     * @param is
     */
    public static void writeStreamToFile(String fileName, InputStream is) {
        File file = new File(fileName);
        FileOutputStream fos = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            int length = -1;
            byte[] buffer = new byte[2 * 1024];
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
        } catch (IOException e) {
        } finally {
            IOUtil.closeSilently(fos);
            IOUtil.closeSilently(is);
        }
    }
    
    static class IOUtil {
        private IOUtil() {
        };

        /**
         * 关闭流操作
         */
        public static void closeSilently(Closeable closeable) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                }
            }
        }

        public static ByteArrayOutputStream convertInToOutStream(InputStream is)
                throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = is.read(b, 0, b.length)) != -1) {
                baos.write(b, 0, len);
            }
            return baos;
        }

        public static ByteArrayOutputStream convertInputToOutPutStream(InputStream is)
                throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = is.read(b, 0, b.length)) != -1) {
                baos.write(b, 0, len);
            }
            return baos;
        }

        /**
         * 获取连接返回数据
         * 
         * @throws Exception
         */
        public static byte[] readStream(InputStream inputStream) throws Exception {
            byte[] buffer = new byte[1024];
            int len = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            byte[] data = baos.toByteArray();
            inputStream.close();
            baos.close();
            return data;
        }
    }
    
    /**
     * 读取文本数据
     * 
     * @param context 程序上下文
     * @param fileName 文件名
     * @return String 读取到的文本内容，失败返回null
     */
    public static String readAssets(Context context, String fileName)
    {
        InputStream is = null;
        String content = null;
        try {
            is = context.getAssets().open(fileName);
            if (is != null) {
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true)
                {
                    int readLength = is.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
            content = null;
        }
        finally {
            try {
                if (is != null) is.close();
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);

        if(file.exists()) {
            file.delete();
        }
    }
}
