# API 接口文档

> **基础路径**: `http://localhost:8080`
> **认证方式**: JWT Token，通过 `Authorization: Bearer <token>` Header 传递
> **响应格式**: `AjaxResult`（`code: 200` 表示成功，`msg` 为消息，`data` 为数据）
> **分页格式**: `TableDataInfo`（`rows` 数据列表，`total` 总数，`code`，`msg`）

---

## 认证接口

### 1. 获取验证码

- **路径**: `GET /captchaImage`
- **权限**: 匿名
- **响应**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "uuid": "a1b2c3d4e5f6...",
  "img": "data:image/png;base64,iVBOR..."
}
```

### 2. 用户登录

- **路径**: `POST /login`
- **权限**: 匿名
- **请求体** (`LoginBody`):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| code | String | 是 | 验证码 |
| uuid | String | 是 | 验证码 UUID（从 /captchaImage 获取） |

```json
{
  "username": "admin",
  "password": "admin123",
  "code": "5",
  "uuid": "a1b2c3d4..."
}
```

- **响应**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

### 3. 用户注册

- **路径**: `POST /register`
- **权限**: 匿名
- **请求体** (`RegisterBody`):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| code | String | 是 | 验证码 |
| uuid | String | 是 | 验证码 UUID |

### 4. 获取当前用户信息

- **路径**: `GET /getInfo`
- **权限**: 已认证
- **响应**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "user": { "userId": 1, "userName": "admin", ... },
  "roles": ["admin"],
  "permissions": ["*:*:*"]
}
```

### 5. 获取路由菜单

- **路径**: `GET /getRouters`
- **权限**: 已认证
- **响应**: 返回当前用户有权访问的菜单树，格式为 `RouterVo` 列表

### 6. 退出登录

- **路径**: `POST /logout`
- **权限**: 已认证
- **说明**: 清除 Redis 中的 Token

---

## 用户管理 `/system/user`

### 1. 用户列表

- **路径**: `GET /system/user/list`
- **权限**: `system:user:list`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |
| userName | String | 否 | 用户名模糊搜索 |
| phonenumber | String | 否 | 手机号模糊搜索 |
| status | String | 否 | 状态筛选 |
| deptId | Long | 否 | 部门 ID |
| beginTime | String | 否 | 开始时间 |
| endTime | String | 否 | 结束时间 |

### 2. 用户详情

- **路径**: `GET /system/user/{userId}`
- **权限**: `system:user:query`

### 3. 新增用户

- **路径**: `POST /system/user`
- **权限**: `system:user:add`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userName | String | 是 | 用户名 |
| nickName | String | 是 | 昵称 |
| password | String | 是 | 密码 |
| deptId | Long | 否 | 部门 ID |
| phonenumber | String | 否 | 手机号 |
| email | String | 否 | 邮箱 |
| sex | String | 否 | 性别（0男 1女 2未知） |
| status | String | 否 | 状态（0正常 1停用） |
| roleIds | Long[] | 否 | 角色 ID 数组 |
| postIds | Long[] | 否 | 岗位 ID 数组 |
| remark | String | 否 | 备注 |

### 4. 修改用户

- **路径**: `PUT /system/user`
- **权限**: `system:user:edit`
- **请求体**: 同新增，需包含 `userId`

### 5. 删除用户

- **路径**: `DELETE /system/user/{userIds}`
- **权限**: `system:user:remove`
- **说明**: `userIds` 为逗号分隔的 ID 字符串

### 6. 重置密码

- **路径**: `PUT /system/user/resetPwd`
- **权限**: `system:user:resetPwd`
- **请求体**: `{ userId, password }`

### 7. 修改状态

- **路径**: `PUT /system/user/changeStatus`
- **权限**: `system:user:edit`
- **请求体**: `{ userId, status }`

### 8. 用户导入

- **路径**: `POST /system/user/importData`
- **权限**: `system:user:import`
- **Content-Type**: `multipart/form-data`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | MultipartFile | 是 | Excel 文件 |
| updateSupport | Boolean | 否 | 是否更新已存在用户 |

### 9. 下载导入模板

- **路径**: `POST /system/user/importTemplate`
- **权限**: `system:user:import`

### 10. 用户导出

- **路径**: `POST /system/user/export`
- **权限**: `system:user:export`
- **请求体**: 查询条件同用户列表

### 11. 查询部门树

- **路径**: `GET /system/user/deptTree`
- **权限**: `system:user:list`

### 12. 用户授权角色

- **路径**: `PUT /system/user/authRole/{userId}`
- **权限**: `system:user:edit`
- **查询参数**: `roleIds`（逗号分隔）

### 13. 根据角色 ID 查询用户列表

- **路径**: `GET /system/user/authRole/allocatedList`
- **权限**: `system:user:query`

### 14. 根据角色 ID 查询未授权用户

- **路径**: `GET /system/user/authRole/unallocatedList`
- **权限**: `system:user:query`

### 15. 批量授权用户

- **路径**: `PUT /system/user/authRole/selectAll`
- **权限**: `system:user:edit`

### 16. 批量取消授权

- **路径**: `PUT /system/user/authRole/cancelAll`
- **权限**: `system:user:edit`

---

## 角色管理 `/system/role`

### 1. 角色列表

- **路径**: `GET /system/role/list`
- **权限**: `system:role:list`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页条数 |
| roleName | String | 否 | 角色名模糊搜索 |
| roleKey | String | 否 | 角色权限字符串模糊搜索 |
| status | String | 否 | 状态筛选 |

### 2. 角色详情

- **路径**: `GET /system/role/{roleId}`
- **权限**: `system:role:query`

### 3. 新增角色

- **路径**: `POST /system/role`
- **权限**: `system:role:add`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleName | String | 是 | 角色名称 |
| roleKey | String | 是 | 角色权限字符串 |
| roleSort | Integer | 是 | 显示顺序 |
| status | String | 否 | 状态（0正常 1停用） |
| dataScope | String | 否 | 数据范围（1-5） |
| menuIds | Long[] | 否 | 菜单 ID 数组 |
| deptIds | Long[] | 否 | 部门 ID 数组（dataScope=2时） |
| remark | String | 否 | 备注 |

### 4. 修改角色

- **路径**: `PUT /system/role`
- **权限**: `system:role:edit`

### 5. 修改数据权限

- **路径**: `PUT /system/role/dataScope`
- **权限**: `system:role:edit`
- **请求体**: 同修改角色，重点关注 `dataScope` 和 `deptIds`

### 6. 删除角色

- **路径**: `DELETE /system/role/{roleIds}`
- **权限**: `system:role:remove`

### 7. 角色授权用户

- **路径**: `GET /system/role/authUser/allocatedList`
- **权限**: `system:role:edit`
- **参数**: `roleId`, `userName`, `phonenumber`

### 8. 角色未授权用户

- **路径**: `GET /system/role/authUser/unallocatedList`
- **权限**: `system:role:edit`

### 9. 角色授权用户（选中）

- **路径**: `PUT /system/role/authUser/selectAll`
- **权限**: `system:role:edit`
- **参数**: `roleId`, `userIds`（逗号分隔）

### 10. 角色取消授权

- **路径**: `PUT /system/role/authUser/cancel`
- **权限**: `system:role:edit`

---

## 菜单管理 `/system/menu`

### 1. 菜单列表

- **路径**: `GET /system/menu/list`
- **权限**: `system:menu:list`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| menuName | String | 否 | 菜单名模糊搜索 |
| status | String | 否 | 状态筛选 |

### 2. 菜单详情

- **路径**: `GET /system/menu/{menuId}`
- **权限**: `system:menu:query`

### 3. 新增菜单

- **路径**: `POST /system/menu`
- **权限**: `system:menu:add`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| menuName | String | 是 | 菜单名称 |
| parentId | Long | 否 | 父菜单 ID，默认 0 |
| menuType | String | 否 | 类型（M目录 C菜单 F按钮） |
| orderNum | Integer | 否 | 显示顺序 |
| path | String | 否 | 路由地址 |
| component | String | 否 | 组件路径 |
| perms | String | 否 | 权限标识 |
| icon | String | 否 | 菜单图标 |
| isFrame | Integer | 否 | 是否外链（0否 1是） |
| isCache | Integer | 否 | 是否缓存（0缓存 1不缓存） |
| visible | String | 否 | 是否显示（0显示 1隐藏） |
| status | String | 否 | 状态（0正常 1停用） |
| remark | String | 否 | 备注 |

### 4. 修改菜单

- **路径**: `PUT /system/menu`
- **权限**: `system:menu:edit`

### 5. 删除菜单

- **路径**: `DELETE /system/menu/{menuId}`
- **权限**: `system:menu:remove`

---

## 部门管理 `/system/dept`

### 1. 部门列表

- **路径**: `GET /system/dept/list`
- **权限**: `system:dept:list`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deptName | String | 否 | 部门名模糊搜索 |
| status | String | 否 | 状态筛选 |

### 2. 新增/修改/删除部门

路径与菜单类似，核心字段：`deptName`、`parentId`、`orderNum`、`leader`、`phone`、`email`、`status`。

---

## 岗位管理 `/system/post`

- **列表**: `GET /system/post/list`
- **详情**: `GET /system/post/{postId}`
- **新增**: `POST /system/post`
- **修改**: `PUT /system/post`
- **删除**: `DELETE /system/post/{postIds}`
- **导出**: `POST /system/post/export`

核心字段：`postCode`、`postName`、`postSort`、`status`、`remark`。

---

## 字典管理

### 字典类型 `/system/dict/type`

- **列表**: `GET /system/dict/type/list`
- **详情**: `GET /system/dict/type/{dictId}`
- **新增**: `POST /system/dict/type`
- **修改**: `PUT /system/dict/type`
- **删除**: `DELETE /system/dict/type/{dictIds}`
- **导出**: `POST /system/dict/type/export`
- **刷新缓存**: `DELETE /system/dict/type/refreshCache`

核心字段：`dictName`、`dictType`、`status`、`remark`。

### 字典数据 `/system/dict/data`

- **列表**: `GET /system/dict/data/list`
- **详情**: `GET /system/dict/data/{dictCode}`
- **新增**: `POST /system/dict/data`
- **修改**: `PUT /system/dict/data`
- **删除**: `DELETE /system/dict/data/{dictCodes}`
- **按类型查询**: `GET /system/dict/data/type/{dictType}`

核心字段：`dictSort`、`dictLabel`、`dictValue`、`dictType`、`cssClass`、`listClass`、`isDefault`、`status`。

---

## 参数配置 `/system/config`

- **列表**: `GET /system/config/list`
- **详情**: `GET /system/config/{configId}`
- **按 Key 查询**: `GET /system/config/configKey/{configKey}`
- **新增**: `POST /system/config`
- **修改**: `PUT /system/config`
- **删除**: `DELETE /system/config/{configIds}`
- **刷新缓存**: `DELETE /system/config/refreshCache`
- **导出**: `POST /system/config/export`

核心字段：`configName`、`configKey`、`configValue`、`configType`（Y内置 N自定义）、`remark`。

---

## 通知公告 `/system/notice`

- **列表**: `GET /system/notice/list`
- **详情**: `GET /system/notice/{noticeId}`
- **新增**: `POST /system/notice`
- **修改**: `PUT /system/notice`
- **删除**: `DELETE /system/notice/{noticeIds}`

核心字段：`noticeTitle`、`noticeType`（1通知 2公告）、`noticeContent`（HTML内容）、`status`、`remark`。

---

## 个人信息 `/system/user/profile`

### 1. 获取个人信息

- **路径**: `GET /system/user/profile`

### 2. 修改个人信息

- **路径**: `PUT /system/user/profile`
- **请求体**: `nickName`、`phonenumber`、`email`、`sex`

### 3. 修改密码

- **路径**: `PUT /system/user/profile/updatePwd`
- **请求体**: `{ oldPassword, newPassword }`

### 4. 上传头像

- **路径**: `POST /system/user/profile/avatar`
- **Content-Type**: `application/json`
- **请求体**: `{ avatar }`（Base64 或 URL）

### 5. 下载上传资源

- **路径**: `GET /common/download/resource`
- **参数**: `resource`（资源名称）

---

## 监控模块

### 在线用户 `/monitor/online`

- **列表**: `GET /monitor/online/list` — `system:online:list`
- **强制下线**: `DELETE /monitor/online/{tokenId}` — `system:online:forceLogout`

返回字段：`tokenId`、`userName`、`deptName`、`ipaddr`、`loginLocation`、`browser`、`os`、`loginTime`。

### 操作日志 `/monitor/operlog`

- **列表**: `GET /monitor/operlog/list` — `system:operlog:list`
- **删除**: `DELETE /monitor/operlog/{operIds}` — `system:operlog:remove`
- **清空**: `DELETE /monitor/operlog/clean` — `system:operlog:remove`
- **导出**: `POST /monitor/operlog/export` — `system:operlog:export`

记录字段：`title`、`businessType`、`method`、`requestMethod`、`operatorType`、`operName`、`deptName`、`operUrl`、`operIp`、`operLocation`、`operParam`、`jsonResult`、`status`、`error`、`operTime`、`costTime`。

### 登录日志 `/monitor/logininfor`

- **列表**: `GET /monitor/logininfor/list` — `system:logininfor:list`
- **删除**: `DELETE /monitor/logininfor/{infoIds}` — `system:logininfor:remove`
- **清空**: `DELETE /monitor/logininfor/clean` — `system:logininfor:remove`
- **解锁**: `PUT /monitor/logininfor/unlock/{userName}` — `system:logininfor:unlock`
- **导出**: `POST /monitor/logininfor/export` — `system:logininfor:export`

### 缓存监控 `/monitor/cache`

- **概览**: `GET /monitor/cache` — `monitor:cache:list`
- **按名称查询**: `GET /monitor/cache/getNames`
- **按 Key 查询**: `GET /monitor/cache/getKeys/{cacheName}`
- **按 Key 取值**: `GET /monitor/cache/getValue/{cacheName}/{cacheKey}`
- **清理**: `DELETE /monitor/cache/clearCacheName/{cacheName}`
- **清理全部**: `DELETE /monitor/cache/clearCacheAll`

### 服务监控 `/monitor/server`

- **路径**: `GET /monitor/server` — `monitor:server:list`
- **响应**:

```json
{
  "cpu": { "cpuNum": 8, "total": 100.0, "sys": 5.2, "used": 15.3, "wait": 0.5, "free": 79.0 },
  "jvm": { "total": 1024, "used": 512, "free": 512, "usage": 50.0, "name": "Java HotSpot", "version": "17.0.x" },
  "mem": { "total": 16384, "used": 8192, "free": 8192, "usage": 50.0 },
  "sys": { "computerName": "server", "osName": "Linux", "userDir": "/home/ruoyi" },
  "sysFiles": [{ "dirName": "/", "typeName": "ext4", "total": "100GB", "used": "50GB", "free": "50GB", "usage": 50.0 }]
}
```

---

## 定时任务 `/monitor/job`

- **列表**: `GET /monitor/job/list` — `monitor:job:list`
- **详情**: `GET /monitor/job/{jobId}` — `monitor:job:query`
- **新增**: `POST /monitor/job` — `monitor:job:add`
- **修改**: `PUT /monitor/job` — `monitor:job:edit`
- **删除**: `DELETE /monitor/job/{jobIds}` — `monitor:job:remove`
- **导出**: `POST /monitor/job/export` — `monitor:job:export`
- **状态变更**: `PUT /monitor/job/changeStatus` — `monitor:job:changeStatus`
- **立即执行**: `PUT /monitor/job/run` — `monitor:job:changeStatus`

核心字段：`jobName`、`jobGroup`、`invokeTarget`、`cronExpression`、`misfirePolicy`（1立即执行 2执行一次 3放弃执行）、`concurrent`（0允许 1禁止）、`status`。

### 任务日志 `/monitor/job/log`

- **列表**: `GET /monitor/job/log/list` — `monitor:job:list`
- **删除**: `DELETE /monitor/job/log/{jobLogIds}` — `monitor:job:remove`
- **清空**: `DELETE /monitor/job/log/clean` — `monitor:job:remove`
- **导出**: `POST /monitor/job/log/export` — `monitor:job:export`

---

## 代码生成 `/tool/gen`

- **数据库表列表**: `GET /tool/gen/db/list` — `tool:gen:list`
- **已导入表列表**: `GET /tool/gen/list` — `tool:gen:list`
- **详情**: `GET /tool/gen/{tableId}` — `tool:gen:query`
- **导入表**: `POST /tool/gen/importTable` — `tool:gen:import`
- **修改生成配置**: `PUT /tool/gen` — `tool:gen:edit`
- **删除**: `DELETE /tool/gen/{tableIds}` — `tool:gen:remove`
- **预览代码**: `GET /tool/gen/preview/{tableId}` — `tool:gen:preview`
- **生成代码（下载）**: `GET /tool/gen/batchGenCode?tables=xxx` — `tool:gen:code`
- **同步数据库**: `GET /tool/gen/syncDb/{table}` — `tool:gen:edit`

---

## 文件下载 `/common`

- **通用下载**: `GET /common/download` — 已认证
- **资源下载**: `GET /common/download/resource` — 已认证

---

## 权限标识汇总

| 模块 | 权限标识 | 说明 |
|------|---------|------|
| 用户 | `system:user:list` / `query` / `add` / `edit` / `remove` / `export` / `import` / `resetPwd` | 用户管理 |
| 角色 | `system:role:list` / `query` / `add` / `edit` / `remove` / `export` | 角色管理 |
| 菜单 | `system:menu:list` / `query` / `add` / `edit` / `remove` | 菜单管理 |
| 部门 | `system:dept:list` / `query` / `add` / `edit` / `remove` | 部门管理 |
| 岗位 | `system:post:list` / `query` / `add` / `edit` / `remove` / `export` | 岗位管理 |
| 字典类型 | `system:dict:type:list` / `query` / `add` / `edit` / `remove` / `export` | 字典类型 |
| 字典数据 | `system:dict:data:list` / `query` / `add` / `edit` / `remove` | 字典数据 |
| 参数 | `system:config:list` / `query` / `add` / `edit` / `remove` / `export` | 参数配置 |
| 公告 | `system:notice:list` / `query` / `add` / `edit` / `remove` | 通知公告 |
| 在线用户 | `system:online:list` / `forceLogout` | 在线用户 |
| 操作日志 | `system:operlog:list` / `remove` / `export` | 操作日志 |
| 登录日志 | `system:logininfor:list` / `remove` / `export` / `unlock` | 登录日志 |
| 缓存 | `monitor:cache:list` / `getNames` | 缓存监控 |
| 服务 | `monitor:server:list` | 服务监控 |
| 任务 | `monitor:job:list` / `query` / `add` / `edit` / `remove` / `export` / `changeStatus` | 定时任务 |
| 代码生成 | `tool:gen:list` / `query` / `import` / `edit` / `preview` / `remove` / `code` | 代码生成 |
| 超级权限 | `*:*:*` | 匹配所有权限标识 |
