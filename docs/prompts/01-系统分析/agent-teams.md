# 系统分析 - Agent Teams 编排方案

> 本文档定义系统分析的 Agent 团队编排、阶段调度、依赖管理和子任务并行策略。
>
> 由总控 `系统分析.md` 读取并按此方案执行任务分发。

---

## 团队结构

```
系统分析团队（system-analysis-team）
├── team-lead（总控）：流程编排、依赖管理、结果汇总
├── agent-architecture（底层架构分析师）
├── agent-backend（后端分析师）
├── agent-frontend（前端分析师）
└── agent-contract（接口契约分析师）
```

Agent 数量固定为 4 个，每个 Agent 的**内部子任务数量动态决定**，不写死 subagent 表。

---

## 阶段划分与依赖关系

```
Phase 1（并行 3 路，互不依赖）:
  agent-architecture  → 分析 Starter/自动配置/基础设施
  agent-backend       → 分析 Controller/Service/Mapper 业务层
  agent-frontend      → 分析组件/路由/状态/API

Phase 2（串行，依赖 Phase 1 全部完成）:
  agent-contract      → 消费 backend + frontend 结果做接口对齐

Phase 3（总控执行，依赖 Phase 2 完成）:
  业务规则汇总 + 规则交叉验证 + 规则依赖图 + 最终自检
```

**依赖关系**：
- `agent-contract` blockedBy `agent-backend`、`agent-frontend`
- 业务规则汇总 blockedBy `agent-architecture`、`agent-backend`、`agent-frontend`、`agent-contract`

---

## subagent 动态拆分规则

每个父 Agent 启动后按以下流程动态决定是否派 subagent、派几个：

### 第 1 步：快速扫描

读取目标目录，评估：
- **文件数量**：`.java` / `.vue` 文件总数
- **子包/子目录数量**：一级子目录数
- **代码行数**：粗略估算（文件数 × 平均行数）

### 第 2 步：按维度拆分

子模块 prompt（`_底层架构.md` / `_后端分析.md` / `_前端分析.md`）各自定义了若干分析维度。父 Agent 读取后，为每个维度分配一个 subagent：

| 父 Agent | 分析维度（来自子模块 prompt） |
|---------|----------------------------|
| agent-architecture | 入口点分析、能力清单提取、依赖与调用关系、配置体系、设计模式识别、业务规则挖掘 |
| agent-backend | 入口点分析、调用链分析、数据模型分析、安全与权限分析、业务规则挖掘 |
| agent-frontend | 入口点分析、组件层级分析、路由与权限分析、状态管理分析、API 调用层分析、交互与校验规则、业务规则挖掘 |

### 第 3 步：按需二次拆分

若某维度代码量特别大，按子包/子目录进一步拆分：

| 触发条件 | 拆分策略 |
|---------|---------|
| 某维度文件数 > 50 个 | 按一级子包/子目录拆分，每个子包一个 subagent |
| 某维度涉及多个业务模块（如多个 Controller 分组） | 按业务模块拆分 |
| 某维度涉及多个技术面（如多个数据库、多个中间件） | 按技术面拆分 |

### 第 4 步：小项目不拆

若目标代码文件总数 < 20 个，父 Agent 直接单 Agent 完成分析，不派 subagent。

### 并行原则

同一父 Agent 下的所有 subagent **完全并行**，各自分析独立的代码范围，不存在依赖关系。父 Agent 等待全部 subagent 完成后汇总。

---

## 各 Agent 定义

### agent-architecture（底层架构分析师）

**职责**：按 `_底层架构.md` 分析 Starter/自动配置/基础设施层

**输入**：
- 总控 `系统分析.md`
- 子模块 `_底层架构.md`
- 目标代码路径（来自总控"后端代码路径"或用户传入）

**分析维度**（按 `_底层架构.md` 定义）：
1. 入口点分析（自动装配机制、Starter 依赖、SPI 扩展）
2. 能力清单提取（自动装配能力、工具封装、基础设施能力）
3. 依赖与调用关系（被使用方分析、传递依赖、模块间依赖）
4. 配置体系分析（配置文件、覆盖优先级）
5. 设计模式识别
6. 业务规则挖掘

**二次拆分触发条件**：
- starter 模块数 > 5 个 → 按模块拆分
- 工具类文件数 > 50 个 → 按功能分类拆分

**汇总输出**：
- `底层架构-分析报告.md`（合并所有 subagent 结果 + 依赖关系分析 + 设计模式识别 + 业务规则挖掘）

---

### agent-backend（后端分析师）

**职责**：按 `_后端分析.md` 分析 Controller/Service/Mapper 业务层

**输入**：
- 总控 `系统分析.md`
- 子模块 `_后端分析.md`
- 目标代码路径（来自总控"后端业务路径"）

**分析维度**（按 `_后端分析.md` 定义）：
1. 入口点分析（Web 入口、非 Web 入口）
2. 调用链分析（Controller → Service → Mapper 链路）
3. 数据模型分析（实体类、DTO/VO、表关系）
4. 安全与权限分析（认证链路、授权控制、输入校验）
5. 业务规则挖掘

**二次拆分触发条件**：
- Controller 数量 > 10 个 → 按 Controller 分组拆分
- Service 实现类总数 > 20 个 → 按业务模块拆分
- 数据库表数量 > 30 个 → 按业务域拆分数据模型分析

**汇总输出**：
- `后端-分析报告.md`（合并所有 subagent 结果 + 调用链去重 + 业务规则挖掘）

---

### agent-frontend（前端分析师）

**职责**：按 `_前端分析.md` 分析前端应用层

**输入**：
- 总控 `系统分析.md`
- 子模块 `_前端分析.md`
- 目标代码路径（来自总控"前端代码路径"）

**分析维度**（按 `_前端分析.md` 定义）：
1. 入口点分析（应用入口、路由入口、状态管理入口、构建配置）
2. 组件层级分析（组件树、复用、组合式函数）
3. 路由与权限分析（路由表、导航守卫、权限控制）
4. 状态管理分析（Store 模块、状态变更、响应式数据）
5. API 调用层分析（请求封装、API 模块化、错误处理）
6. 交互与校验规则（表单校验、交互状态机、数据转换）
7. 业务规则挖掘

**二次拆分触发条件**：
- `.vue` 组件数 > 50 个 → 按 views/components 拆分，或按业务模块拆分
- Store 模块数 > 5 个 → 按模块拆分
- API 文件数 > 20 个 → 按业务模块拆分

**汇总输出**：
- `前端-分析报告.md`（合并所有 subagent 结果 + 交互状态机 + 业务规则挖掘）

---

### agent-contract（接口契约分析师）

**职责**：按 `_接口契约.md` 对齐前后端接口与业务规则

**输入**：
- 总控 `系统分析.md`
- 子模块 `_接口契约.md`
- `agent-backend` 输出的 `后端-分析报告.md`
- `agent-frontend` 输出的 `前端-分析报告.md`

**执行阶段**：Phase 2（依赖 Phase 1 全部完成）

**无需 subagent**：接口对齐本质是比对工作，单 Agent 即可高效完成。

**输出文件**：
- `接口契约-分析报告.md`（接口对齐矩阵、数据结构对齐、业务规则对齐、差异清单）

---

## 执行流程

### Step 1：项目特征识别（总控执行）

总控按 `系统分析.md` 第 0 章判断需要启用哪些 Agent。

### Step 2：派发 Phase 1 Agent（并行）

总控根据 Step 1 的判断，并行派发需要启用的 Agent：

```
启动 agent-architecture（如需）→ run_in_background
启动 agent-backend（如需）       → run_in_background
启动 agent-frontend（如需）      → run_in_background
等待全部完成
```

每个 Agent 内部按动态拆分规则派发 subagent（如有需要）。

### Step 3：派发 Phase 2 Agent（串行）

Phase 1 全部完成后，总控启动 `agent-contract`（如需）。

### Step 4：汇总（总控执行）

Phase 2 完成后，总控执行：
- 合并各 Agent 输出的业务规则（按五分类汇总）
- 构建规则依赖图
- 生成最终输出文档（01~07 文档）
- 执行自检清单

---

## subagent 输出约定

- 每个 subagent 输出到按 Agent 隔离的临时目录，避免并行写入冲突（所有路径基于总控"输出目录"参数）：
  - `{输出目录}/analysis-tmp/architecture/` → agent-architecture 的 subagent 输出
  - `{输出目录}/analysis-tmp/backend/` → agent-backend 的 subagent 输出
  - `{输出目录}/analysis-tmp/frontend/` → agent-frontend 的 subagent 输出
- 父 Agent 读取各自目录下所有 subagent 输出，合并后写入正式报告（正式报告也存放在"输出目录"下）
- 父 Agent 完成后清理对应的临时目录

---

## 错误处理与重试

- 某个 subagent 失败 → 父 Agent 重新派发一次，若仍失败则标记为【缺失/无法分析】
- 某个 Agent 失败 → 总控不等待，继续执行其他 Agent，最终汇总时标注缺失
- 所有失败/无法确认的内容 → 汇总为《待澄清问题清单》

---

## 输出文件清单

| 文件 | 来源 | 阶段 |
|------|------|------|
| `底层架构-分析报告.md` | agent-architecture 汇总 | Phase 1 |
| `后端-分析报告.md` | agent-backend 汇总 | Phase 1 |
| `前端-分析报告.md` | agent-frontend 汇总 | Phase 1 |
| `接口契约-分析报告.md` | agent-contract | Phase 2 |
| `01_系统架构分析.md` | 总控汇总 Phase 1+2 | Phase 3 |
| `02_专业术语词汇表.md` | 总控汇总 | Phase 3 |
| `03_数据模型使用手册.md` | 总控汇总 | Phase 3 |
| `04_业务逻辑公式手册.md` | 总控汇总 | Phase 3 |
| `05_开发实践指南.md` | 总控汇总 | Phase 3 |
| `06_项目结构分析.md` | 总控汇总 | Phase 3 |
| `07_接口契约文档.md` | 总控基于 agent-contract 输出 | Phase 3 |

> 注意：subagent 的中间输出文件是临时文件，父 Agent 汇总后会被清理，不作为最终产物保留。
