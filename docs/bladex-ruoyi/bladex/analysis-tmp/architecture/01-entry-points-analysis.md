# 入口点分析中间输出

## spring.factories / AutoConfiguration.imports 文件扫描结果

**发现**：BladeX 不使用手动编写的 `spring.factories` 或 `AutoConfiguration.imports` 文件。
而是通过 `blade-core-auto` 模块的编译期注解处理器（`AutoFactoriesProcessor`）自动生成。

### AutoFactoriesProcessor 工作原理

来源：`/home/workspace/com/bladex-ruoyi/sources/bladex/bladex-tool/blade-core-auto/src/main/java/org/springblade/core/auto/factories/AutoFactoriesProcessor.java`

1. 扫描所有类上的 `@AutoConfiguration`、`@Configuration`、`@Component`、`@RestController`、`@FeignClient` 等注解
2. 支持组合注解递归查找（通过 `isAnnotation()` 方法）
3. 生成 `META-INF/spring.factories`（兼容 Spring Boot 2.x）
4. 生成 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（Spring Boot 2.7+/3.x）
5. 生成 `META-INF/spring-devtools.properties` 支持热重载

### AutoServiceProcessor 工作原理

来源：`/home/workspace/com/bladex-ruoyi/sources/bladex/bladex-tool/blade-core-auto/src/main/java/org/springblade/core/auto/service/AutoServiceProcessor.java`

1. 扫描 `@AutoService` 注解
2. 生成 `META-INF/services/` 下的 SPI 文件

### 全部 @AutoConfiguration 类清单（85个）

见分析报告第1章 1.1 节完整表格。

## 配置类完整清单

见分析报告第2章 2.1 节。
