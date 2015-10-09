package com.shenghuoli.library.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Zip的压缩和解压
 * 
 * @author DLL email: xiaobinlzy@163.com
 * 
 */
public class ZipUtil {

	/**
	 * 压缩
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static String compress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return out.toString("ISO-8859-1");
	}

	/**
	 * 解压缩
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static String uncompress(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return "";
		}
		String result = null;
		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		GZIPInputStream gunzip = null;
		try {
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(bytes);
			gunzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
			// toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)
			result = out.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (gunzip != null) {
				try {
					gunzip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}
}
