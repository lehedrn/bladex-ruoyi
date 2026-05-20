# Blade LiteRule 规则引擎

BladeX框架的轻量级规则引擎，提供简单易用的规则执行和编排能力。

## 特性

- 支持顺序规则、分支规则和并行规则三种执行模式
- 提供流式API用于构建规则链
- 支持同步和异步执行
- 完整的生命周期管理和监控
- 基于Spring管理规则实例
- 线程安全的上下文管理
- 规则链缓存和预加载
- 循环依赖检测

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>org.springblade</groupId>
    <artifactId>blade-starter-literule</artifactId>
    <version>${blade.tool.version}</version>
</dependency>
```

### 2. 定义规则上下文

```java
import org.springblade.core.literule.core.RuleContextComponent;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderContext extends RuleContextComponent {
    private String orderId;
    private BigDecimal amount;
    private String paymentType;
    private String status;
    private Address shippingAddress;
    // 其他业务字段...
}
```

### 3. 实现普通规则

```java
import org.springblade.core.literule.annotation.LiteRuleComponent;

@Slf4j
@LiteRuleComponent("orderValidateRule")
public class OrderValidateRule extends RuleComponent {
    @Override
    protected void process() throws Exception {
        OrderContext context = getContextBean(OrderContext.class);

        // 执行业务逻辑
        if (StringUtils.isEmpty(context.getOrderId())) {
            context.addError("订单ID不能为空");
            return;
        }

        if (context.getAmount() == null || context.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            context.addError("订单金额无效");
            return;
        }

        log.info("[规则1] 订单校验通过：{}", context.getOrderId());
    }
}
```

### 4. 实现分支规则

```java
import org.springblade.core.literule.annotation.LiteRuleComponent;

@Slf4j
@LiteRuleComponent("paymentRouteRule")
public class PaymentRouteRule extends SwitchRuleComponent {
    @Override
    protected List<String> process() throws Exception {
        OrderContext context = getContextBean(OrderContext.class);
        String paymentType = context.getPaymentType();

        log.info("[规则3] 支付路由选择：{}", paymentType);

        // 根据支付类型返回不同的规则ID
        if ("ALIPAY".equals(paymentType)) {
            return Collections.singletonList("alipayRule");
        } else if ("WECHAT".equals(paymentType)) {
            return Collections.singletonList("wechatPayRule");
        } else if ("BANK".equals(paymentType)) {
            return Collections.singletonList("bankPayRule");
        } else {
            context.addError("不支持的支付方式：" + paymentType);
            return Collections.emptyList();
        }
    }
}
```

### 5. 构建规则链

```java
@EngineComponent("orderChain")
public class OrderRuleBuilder implements RuleBuilder {
    @Override
    public RuleChain build() {
        // 创建支付处理分支规则
        RuleChain paymentRule = LiteRule.SWITCH("paymentRouteRule")
            .TO("alipayRule", "wechatPayRule", "bankPayRule")
            .build();
            
        // 创建完整规则链
        return LiteRule.THEN("orderValidateRule", "orderAmountRule")
            .THEN(paymentRule)
            .build();
    }
}
```

### 6. 执行规则链

```java
import org.springblade.core.literule.provider.LiteRuleResponse;

@Autowired
private RuleEngineExecutor ruleEngine;

public void processOrder(OrderRequest request) {
    // 构建上下文
    OrderContext context = OrderContext.builder()
        .orderId(request.getOrderId())
        .amount(request.getAmount())
        .paymentType(request.getPaymentType())
        .shippingAddress(request.getAddress())
        .build();

    // 构建配置
    RuleConfig config = RuleConfig.builder()
        .enableTimeMonitor(true)
        .printExecutionTime(true)
        .enableLogging(true)
        .build();

    // 执行规则链
    LiteRuleResponse<OrderContext> response = ruleEngine.execute(
        "orderChain",
        context,
        config
    );

    // 处理响应
    if (response.isSuccess()) {
        log.info("规则执行成功，耗时: {}ms", response.getExecutionTime());
    } else {
        log.error("规则执行失败: {}", response.getMessage());
        // 获取详细错误信息
        context.getErrorMessages().forEach(error -> log.error("错误: {}", error));
    }
}
```

### 7. 异步执行规则链

```java
@Autowired
private RuleEngineExecutor ruleEngine;

@Autowired
@Qualifier("ruleExecutorThreadPool")
private ThreadPoolExecutor threadPool;

public CompletableFuture<LiteRuleResponse<OrderContext>> processOrderAsync(OrderRequest request) {
    // 构建上下文
    OrderContext context = OrderContext.builder()
        .orderId(request.getOrderId())
        .amount(request.getAmount())
        .paymentType(request.getPaymentType())
        .build();
        
    // 异步执行规则链
    return ruleEngine.executeAsync(
        "orderChain",
        context,
        threadPool
    ).thenApply(response -> {
        if (response.isSuccess()) {
            log.info("异步规则执行成功，耗时: {}ms", response.getExecutionTime());
        } else {
            log.error("异步规则执行失败: {}", response.getMessage());
        }
        return response;
    });
}
```

## 核心概念

### 规则(Rule)

规则是业务逻辑的最小执行单元，分为两种类型：

- 普通规则(Rule): 执行固定的业务逻辑
- 分支规则(SwitchRule): 根据条件返回下一个要执行的规则列表

### 规则链(RuleChain)

规则链定义了规则的执行顺序，支持三种组合方式：

- THEN: 顺序执行一组规则
- SWITCH: 条件分支执行不同规则
- PARALLEL: 并行执行一组规则

### 规则上下文(RuleContext)

在规则执行过程中传递数据和状态的载体，包含：

- 上下文数据
- 错误信息
- 执行状态
- 执行时间统计

### 规则配置(RuleConfig)

控制规则执行的行为，包含：

- 是否启用执行时间监控
- 是否打印执行时间
- 是否启用日志

## 高级特性

### 1. 规则链缓存

规则引擎会自动缓存构建好的规则链，提高执行效率。缓存支持以下配置：

```yaml
literule:
  cache:
    enabled: true                # 是否启用缓存
```

### 2. 规则预加载

在应用启动时预加载所有规则，提前发现配置错误，减少首次执行的延迟：

```yaml
literule:
  preload:
    enabled: true  # 是否启用预加载
```

### 3. 并行执行配置

控制并行规则执行的行为：

```yaml
literule:
  execution:
    timeout: 30000           # 默认超时时间(毫秒)
    enable-parallel: true    # 是否启用并行执行
    max-parallel-threads: 10 # 最大并行线程数
```

### 4. 循环依赖检测

规则引擎会自动检测规则链中的循环依赖，避免无限递归执行。

## 项目结构

```
blade-starter-literule/
├── annotation/                    # 注解
│   ├── EngineComponent.java       # 引擎组件注解
│   └── RuleComponent.java         # 规则组件注解
│
├── builder/                       # 规则构建
│   ├── LiteRule.java              # 规则构建器
│   ├── RuleBuilder.java           # 规则构建器接口
│   ├── RuleBuilderExecutor.java   # 规则构建器执行器
│   └── chain/                     # 规则链构建器
│       ├── AbstractRuleChain.java # 规则链抽象基类
│       ├── ParallelRuleChain.java # 并行规则链构建器
│       ├── RuleChain.java         # 并行规则链接口
│       ├── SwitchRuleChain.java   # 分支规则链构建器
│       └── ThenRuleChain.java     # 顺序规则链构建器
│
├── config/                        # 配置类
│   ├── AbstractComponentRegistrar.java  # 组件注册器抽象基类
│   ├── EngineComponentRegistrar.java    # 引擎组件注册器
│   ├── RuleComponentRegistrar.java      # 规则组件注册器
│   ├── RuleEngineAutoConfiguration.java # 规则引擎自动配置
│   └── RuleEngineProperties.java        # 规则引擎配置属性
│
├── context/                       # 上下文管理
│   ├── RuleContextHolder.java     # 规则上下文持有者
│   └── RuleContextManager.java    # 规则上下文管理器
│
├── core/                          # 核心实现
│   ├── AbstractBaseRule.java      # 规则基类
│   ├── AbstractRule.java          # 普通规则抽象实现
│   ├── AbstractRuleContext.java   # 规则上下文抽象实现
│   └── AbstractSwitchRule.java    # 分支规则抽象实现
│
├── engine/                        # 规则引擎执行
│   ├── DefaultRuleEngineExecutor.java  # 默认规则引擎执行器
│   ├── RuleEngineExecutor.java    # 规则引擎执行器接口
│   └── RulePreloadRunner.java     # 规则预加载服务
│
├── exception/                     # 异常
│   └── RuleException.java         # 规则异常
│
└── provider/                      # 接口和模型
    ├── Rule.java                  # 规则接口
    ├── RuleConfig.java            # 规则配置类
    ├── RuleContext.java           # 规则上下文接口
    ├── RuleResponse.java          # 规则响应类
    └── SwitchRule.java            # 分支规则接口
```

## 最佳实践

### 1. 规则设计原则

- **单一职责**：一个规则只做一件事，便于维护和测试
- **无状态**：规则不应保存状态，所有状态应通过上下文传递
- **幂等性**：多次执行结果一致，避免副作用
- **异常处理**：规则内部应处理异常，避免中断规则链执行

### 2. 规则链设计原则

- **层次清晰**：明确的执行顺序，避免复杂的嵌套
- **解耦合**：规则之间通过上下文交互，避免直接依赖
- **可维护**：便于添加、删除、修改规则，不影响整体流程
- **合理分组**：相关规则放在一起，便于理解和维护

### 3. 性能优化

- **合理使用缓存**：对于频繁执行的规则链，启用缓存
- **并行执行**：对于独立的规则，使用并行执行提高效率
- **异步执行**：对于耗时长的规则链，使用异步执行
- **资源管理**：及时清理上下文资源，避免内存泄漏

## 完整示例

参考 `org.springblade.test.literule.LiteRuleTest` 类，提供了完整的使用示例。 