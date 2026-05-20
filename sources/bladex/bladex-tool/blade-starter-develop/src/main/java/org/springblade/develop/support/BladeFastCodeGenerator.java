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
package org.springblade.develop.support;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.BeetlTemplateEngine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.develop.constant.DevelopConstant;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 代码生成器快速配置类
 *
 * @author Chill
 */
@Data
@Slf4j
public class BladeFastCodeGenerator {
	/**
	 * 代码所在系统
	 */
	private String systemName = DevelopConstant.BOOT_NAME;
	/**
	 * 代码模块名称
	 */
	private String codeName;
	/**
	 * 代码所在服务名
	 */
	private String serviceName = "blade-service";
	/**
	 * 代码生成的包名
	 */
	private String packageName = "org.springblade.test";
	/**
	 * 代码后端生成的地址
	 */
	private String packageDir;
	/**
	 * 需要去掉的表前缀
	 */
	private String[] tablePrefix = {"blade_"};
	/**
	 * 需要生成的表名(两者只能取其一)
	 */
	private String[] includeTables = {"blade_test"};
	/**
	 * 需要排除的表名(两者只能取其一)
	 */
	private String[] excludeTables = {};
	/**
	 * 是否包含基础业务字段
	 */
	private Boolean hasSuperEntity = Boolean.TRUE;
	/**
	 * 是否包含包装器
	 */
	private Boolean hasWrapper = Boolean.TRUE;
	/**
	 * 是否包含服务名
	 */
	private Boolean hasServiceName = Boolean.FALSE;
	/**
	 * 基础业务字段
	 */
	private String[] superEntityColumns = {"create_time", "create_user", "create_dept", "update_time", "update_user", "status", "is_deleted"};
	/**
	 * 租户字段
	 */
	private String tenantColumn = "tenant_id";
	/**
	 * 数据库驱动名
	 */
	private String driverName;
	/**
	 * 数据库链接地址
	 */
	private String url;
	/**
	 * 数据库用户名
	 */
	private String username;
	/**
	 * 数据库密码
	 */
	private String password;

	/**
	 * 代码生成执行
	 */
	public void run() {
		Properties props = getProperties();
		String url = Func.toStr(this.url, props.getProperty("spring.datasource.url"));
		String username = Func.toStr(this.username, props.getProperty("spring.datasource.username"));
		String password = Func.toStr(this.password, props.getProperty("spring.datasource.password"));

		Map<String, Object> customMap = new HashMap<>(11);
		customMap.put("codeName", codeName);
		customMap.put("serviceName", serviceName);
		customMap.put("packageName", packageName);
		customMap.put("tenantColumn", tenantColumn);
		customMap.put("hasSuperEntity", hasSuperEntity);
		customMap.put("hasWrapper", hasWrapper);
		customMap.put("hasServiceName", hasServiceName);
		Map<String, String> customFile = new HashMap<>(15);
		customFile.put("entityVO.java", "/templates/api-fast/entityVO.java.btl");
		customFile.put("entityDTO.java", "/templates/api-fast/entityDTO.java.btl");
		customFile.put("entityExcel.java", "/templates/api-fast/entityExcel.java.btl");
		if (hasWrapper) {
			customFile.put("wrapper.java", "/templates/api-fast/wrapper.java.btl");
		}

		FastAutoGenerator.create(url, username, password)
			.globalConfig(builder -> builder.author(props.getProperty("author")).dateType(DateType.TIME_PACK).enableSwagger().outputDir(getOutputDir()).disableOpenDir())
			.packageConfig(builder -> builder.parent(packageName).controller("controller").entity("pojo.entity").service("service").serviceImpl("service.impl").mapper("mapper").xml("mapper"))
			.strategyConfig(builder -> builder.addTablePrefix(tablePrefix).addInclude(includeTables).addExclude(excludeTables)
				.entityBuilder().naming(NamingStrategy.underline_to_camel).columnNaming(NamingStrategy.underline_to_camel).enableLombok().superClass("org.springblade.core.tenant.mp.TenantEntity").formatFileName("%sEntity").addSuperEntityColumns(superEntityColumns).enableFileOverride()
				.javaTemplate("/templates/api-fast/entity.java")
				.serviceBuilder().superServiceClass("org.springblade.core.mp.base.BaseService").superServiceImplClass("org.springblade.core.mp.base.BaseServiceImpl").formatServiceFileName("I%sService").formatServiceImplFileName("%sServiceImpl").enableFileOverride()
				.serviceTemplate("/templates/api-fast/service.java")
				.serviceImplTemplate("/templates/api-fast/serviceImpl.java")
				.mapperBuilder().mapperAnnotation(Mapper.class).enableBaseResultMap().enableBaseColumnList().formatMapperFileName("%sMapper").formatXmlFileName("%sMapper").enableFileOverride()
				.mapperTemplate("/templates/api-fast/mapper.java")
				.mapperXmlTemplate("/templates/api-fast/mapper.xml")
				.controllerBuilder().superClass("org.springblade.core.boot.ctrl.BladeController").formatFileName("%sController").enableRestStyle().enableHyphenStyle().enableFileOverride()
				.template("/templates/api-fast/controller.java")
			)
			.injectionConfig(builder -> builder.beforeOutputFile(
					(tableInfo, objectMap) -> {
						System.out.println("---------------------------------------------");
						System.out.println("tableName: " + tableInfo.getName());
						System.out.println("tableComment: " + tableInfo.getComment());
						System.out.println("tableInfo: " + tableInfo.getEntityName());
						System.out.println("result: success");
						System.out.println("---------------------------------------------");
					}
				).customMap(customMap).customFile(customFile)
			)
			.templateEngine(new BeetlTemplateEngine() {
				@Override
				protected void outputCustomFile(List<CustomFile> customFiles, TableInfo tableInfo, Map<String, Object> objectMap) {
					String entityName = tableInfo.getEntityName();
					String entitySimpleName = StringUtil.removeSuffix(entityName, "Entity");

					customFiles.forEach(customFile -> {
						String key = customFile.getFileName();
						String value = customFile.getTemplatePath();
						String outputPath = getPathInfo(OutputFile.parent);
						objectMap.put("entityKey", entitySimpleName);
						objectMap.put("entityKeyPath", StringUtil.firstCharToLower(entitySimpleName));
						if (StringUtil.equals(key, "entityVO.java")) {
							outputPath = getOutputDir() + StringPool.SLASH + packageName.replace(StringPool.DOT, StringPool.SLASH) + StringPool.SLASH + "/pojo/vo" + StringPool.SLASH + entitySimpleName + "VO" + StringPool.DOT_JAVA;
						}

						if (StringUtil.equals(key, "entityDTO.java")) {
							outputPath = getOutputDir() + StringPool.SLASH + packageName.replace(StringPool.DOT, StringPool.SLASH) + StringPool.SLASH + "/pojo/dto" + StringPool.SLASH + entitySimpleName + "DTO" + StringPool.DOT_JAVA;
						}

						if (StringUtil.equals(key, "entityExcel.java")) {
							outputPath = getOutputDir() + StringPool.SLASH + packageName.replace(StringPool.DOT, StringPool.SLASH) + StringPool.SLASH + "/excel" + StringPool.SLASH + entitySimpleName + "Excel" + StringPool.DOT_JAVA;
						}

						if (StringUtil.equals(key, "wrapper.java")) {
							outputPath = getOutputDir() + StringPool.SLASH + packageName.replace(StringPool.DOT, StringPool.SLASH) + StringPool.SLASH + "wrapper" + StringPool.SLASH + entitySimpleName + "Wrapper" + StringPool.DOT_JAVA;
						}
						outputFile(new File(String.valueOf(outputPath)), objectMap, value, Boolean.TRUE);
					});
				}
			})
			.execute();

	}


	/**
	 * 获取配置文件
	 *
	 * @return 配置Props
	 */
	private Properties getProperties() {
		// 读取配置文件
		Resource resource = new ClassPathResource("/templates/code.properties");
		Properties props = new Properties();
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}

	/**
	 * 生成到项目中
	 *
	 * @return outputDir
	 */
	public String getOutputDir() {
		String projectPath = StringUtil.equals(systemName, DevelopConstant.CLOUD_NAME) ? "/blade-ops/blade-develop" : "";
		return (Func.isBlank(packageDir) ? System.getProperty("user.dir") + projectPath : packageDir) + "/src/main/java";
	}

}
