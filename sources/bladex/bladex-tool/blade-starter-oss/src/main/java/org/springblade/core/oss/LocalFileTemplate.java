/**
 * BladeX Commercial License Agreement
 * Copyright (c) 2018-2099, https://bladex.cn. All rights reserved.
 * <p>
 * Use of this software is governed by the Commercial License Agreement
 * obtained after purchasing a license from BladeX.
 * <p>
 * 1. This software is for development use only under a valid license
 * from BladeX.
 * <p>
 * 2. Redistribution of this software's source code to any third party
 * without a commercial license is strictly prohibited.
 * <p>
 * 3. Licensees may copyright their own code but cannot use segments
 * from this software for such purposes. Copyright of this software
 * remains with BladeX.
 * <p>
 * Using this software signifies agreement to this License, and the software
 * must not be used for illegal purposes.
 * <p>
 * THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY. The author is
 * not liable for any claims arising from secondary or illegal development.
 * <p>
 * Author: Chill Zhuang (bladejava@qq.com)
 */
package org.springblade.core.oss;

import org.springblade.core.oss.exception.OssException;
import org.springblade.core.oss.model.BladeFile;
import org.springblade.core.oss.model.OssFile;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class LocalFileTemplate implements OssTemplate {

	/**
	 * 存储桶命名规则
	 */
	private final OssRule ossRule;

	/**
	 * 配置类
	 */
	private final OssProperties ossProperties;

	/**
	 * 文件存放路径
	 */
	private final String localPath;

	public LocalFileTemplate(OssRule ossRule, OssProperties ossProperties) {
		this.ossRule = ossRule;
		this.ossProperties = ossProperties;
		if (Func.isEmpty(ossProperties.getAccessKey())) {
			throw new OssException("请配置文件上传基础路径：oss.access-key");
		}
		this.localPath = ossProperties.getAccessKey();
	}

	@Override
	public void makeBucket(String bucketName) {
		File dir = new File(getBucketName(bucketName));
		if (!dir.exists()) {
			boolean temp = dir.mkdirs();
			if (!temp) {
				throw new OssException("创建文件夹失败");
			}
		}
	}

	@Override
	public void removeBucket(String bucketName) {
		File dir = new File(getBucketName(bucketName));
		if (dir.exists()) {
			boolean temp = dir.delete();
			if (!temp) {
				throw new OssException("删除文件夹失败");
			}
		}
	}

	@Override
	public boolean bucketExists(String bucketName) {
		File dir = new File(getBucketName(bucketName));
		return dir.exists();
	}

	@Override
	public void copyFile(String bucketName, String fileName, String destBucketName) {
		copyFile(bucketName, fileName, destBucketName, fileName);
	}

	@Override
	public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
		try {
			Files.copy(
				Paths.get(getBucketName(bucketName) + StringPool.SLASH + fileName),
				Paths.get(getBucketName(destBucketName) + StringPool.SLASH + destFileName)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public OssFile statFile(String fileName) {
		return statFile(ossProperties.getBucketName(), fileName);
	}

	@Override
	public OssFile statFile(String bucketName, String fileName) {
		File file = new File(getBucketName(bucketName) + StringPool.SLASH + fileName);
		OssFile ossFile = new OssFile();
		if (file.exists()) {
			ossFile.setName(fileName);
			ossFile.setLength(file.length());
		}
		return ossFile;
	}

	@Override
	public String filePath(String fileName) {
		return getBucketName() + StringPool.SLASH + fileName;
	}

	@Override
	public String filePath(String bucketName, String fileName) {
		return localPath + StringPool.SLASH + bucketName + StringPool.SLASH + fileName;
	}

	@Override
	public String fileLink(String fileName) {
		return fileLink(ossProperties.getBucketName(), fileName);
	}

	@Override
	public String fileLink(String bucketName, String fileName) {
		return getOssHost(bucketName) + StringPool.SLASH + fileName;
	}

	@Override
	public BladeFile putFile(MultipartFile file) {
		return putFile(ossProperties.getBucketName(), file.getOriginalFilename(), file);
	}

	@Override
	public BladeFile putFile(String fileName, MultipartFile file) {
		return putFile(ossProperties.getBucketName(), fileName, file);
	}

	@Override
	public BladeFile putFile(String bucketName, String fileName, MultipartFile file) {
		makeBucket(bucketName); // 确保存储桶目录存在
		try {
			String originalName = fileName;
			fileName = getFileName(fileName);

			// 获取目标文件的父路径，并确保所有中间目录都存在
			File targetFile = new File(getBucketName(bucketName) + StringPool.SLASH + fileName);

			// 递归创建完整路径文件夹
			File parentDir = targetFile.getParentFile();
			if (!parentDir.exists()) {
				boolean temp = parentDir.mkdirs();// 递归创建父目录
				if (!temp) {
					throw new OssException("创建文件夹失败");
				}
			}

			// 将文件保存到目标路径
			file.transferTo(targetFile);

			// 构建并返回 BladeFile 对象
			BladeFile bladeFile = new BladeFile();
			bladeFile.setOriginalName(originalName);
			bladeFile.setName(fileName);
			bladeFile.setLink(fileLink(bucketName, fileName));

			return bladeFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BladeFile putFile(String fileName, InputStream stream) {
		return putFile(ossProperties.getBucketName(), fileName, stream);
	}

	@Override
	public BladeFile putFile(String bucketName, String fileName, InputStream stream) {
		makeBucket(bucketName); // 确保存储桶目录存在
		try {
			String originalName = fileName;
			fileName = getFileName(fileName);

			// 获取目标文件对象
			File targetFile = new File(getBucketName(bucketName) + StringPool.SLASH + fileName);

			// 递归创建完整路径文件夹
			File parentDir = targetFile.getParentFile();
			if (!parentDir.exists()) {
				boolean temp = parentDir.mkdirs();// 递归创建父目录
				if (!temp) {
					throw new OssException("创建文件夹失败");
				}
			}

			// 将 InputStream 中的内容写入到目标文件
			FileOutputStream outputStream = new FileOutputStream(targetFile);
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = stream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
			stream.close(); // 确保 InputStream 也被关闭

			// 构建并返回 BladeFile 对象
			BladeFile bladeFile = new BladeFile();
			bladeFile.setOriginalName(originalName);
			bladeFile.setName(fileName);
			bladeFile.setLink(fileLink(bucketName, fileName));

			return bladeFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public void removeFile(String fileName) {
		removeFile(ossProperties.getBucketName(), fileName);
	}

	@Override
	public void removeFile(String bucketName, String fileName) {
		File file = new File(getBucketName(bucketName) + StringPool.SLASH + fileName);
		if (file.exists()) {
			boolean temp = file.delete();
			if (!temp) {
				throw new OssException("删除文件失败");
			}
		}
	}

	@Override
	public void removeFiles(List<String> fileNames) {
		removeFiles(ossProperties.getBucketName(), fileNames);
	}

	@Override
	public void removeFiles(String bucketName, List<String> fileNames) {
		for (String fileName : fileNames) {
			removeFile(bucketName, fileName);
		}
	}

	@Override
	public InputStream statFileStream(String fileName) {
		return statFileStream(ossProperties.getBucketName(), fileName);
	}

	@Override
	public InputStream statFileStream(String bucketName, String fileName) {
		try {
			return new FileInputStream(getBucketName(bucketName) + StringPool.SLASH + fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据规则生成存储桶名称规则
	 *
	 * @return String
	 */
	private String getBucketName() {
		return getBucketName(localPath + StringPool.SLASH + ossProperties.getBucketName());
	}

	/**
	 * 根据规则生成存储桶名称规则
	 *
	 * @param bucketName 存储桶名称
	 * @return String
	 */
	private String getBucketName(String bucketName) {
		return localPath + StringPool.SLASH + ossRule.bucketName(bucketName);
	}

	/**
	 * 根据规则生成文件名称规则
	 *
	 * @param originalFilename 原始文件名
	 * @return string
	 */
	private String getFileName(String originalFilename) {
		return ossRule.fileName(originalFilename);
	}


	/**
	 * 获取域名
	 *
	 * @param bucketName 存储桶名称
	 * @return String
	 */
	public String getOssHost(String bucketName) {
		return getEndpoint() + StringPool.SLASH + ossRule.bucketName(bucketName);
	}

	/**
	 * 获取域名
	 *
	 * @return String
	 */
	public String getOssHost() {
		return getOssHost(ossProperties.getBucketName());
	}

	/**
	 * 获取服务地址
	 *
	 * @return String
	 */
	public String getEndpoint() {
		if (StringUtil.isBlank(ossProperties.getTransformEndpoint())) {
			return ossProperties.getEndpoint();
		}
		return StringUtil.removeSuffix(ossProperties.getTransformEndpoint(), StringPool.SLASH);
	}

}
