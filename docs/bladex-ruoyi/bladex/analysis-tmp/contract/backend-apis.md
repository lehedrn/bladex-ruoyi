# 后端接口清单（中间数据）

## 认证模块（/blade-auth/*）
POST /blade-auth/oauth/token - OAuth2 Token（多种 grant_type）
GET /blade-auth/oauth/captcha - 验证码
GET /blade-auth/oauth/user-info - 用户信息
GET /blade-auth/oauth/logout - 登出
POST /blade-auth/oauth/logout - 日志上报
GET /blade-auth/oauth/clear-cache - 清除缓存
POST /blade-auth/oauth/sms/send-validate - 短信验证码

## 用户管理（/blade-system/user/*）
GET /blade-system/user/detail
GET /blade-system/user/info
GET /blade-system/user/list
GET /blade-system/user/page
POST /blade-system/user/submit
POST /blade-system/user/update
POST /blade-system/user/remove
POST /blade-system/user/grant
POST /blade-system/user/reset-password
POST /blade-system/user/update-password
POST /blade-system/user/update-info
GET /blade-system/user/user-list
POST /blade-system/user/import-user
GET /blade-system/user/export-user
GET /blade-system/user/export-template
POST /blade-system/user/register-guest
POST /blade-system/user/update-platform
GET /blade-system/user/platform-detail
POST /blade-system/user/unlock
POST /blade-system/user/audit-pass
POST /blade-system/user/audit-refuse
POST /blade-system/user/set-leader
GET /blade-system/user/leader-info
GET /blade-system/user/leader-list

## 菜单管理（/blade-system/menu/*）
GET /blade-system/menu/detail
GET /blade-system/menu/list
GET /blade-system/menu/lazy-list
GET /blade-system/menu/menu-list
GET /blade-system/menu/lazy-menu-list
POST /blade-system/menu/submit
POST /blade-system/menu/remove
GET /blade-system/menu/routes
GET /blade-system/menu/routes-ext
GET /blade-system/menu/buttons
GET /blade-system/menu/tree
GET /blade-system/menu/grant-tree
GET /blade-system/menu/role-tree-keys
GET /blade-system/menu/grant-top-tree
GET /blade-system/menu/top-tree-keys
GET /blade-system/menu/top-menu
GET /blade-system/menu/auth-routes

## 角色管理（/blade-system/role/*）
GET /blade-system/role/detail
GET /blade-system/role/list
GET /blade-system/role/tree
GET /blade-system/role/tree-by-id
POST /blade-system/role/submit
POST /blade-system/role/remove
POST /blade-system/role/grant
GET /blade-system/role/select
GET /blade-system/role/alias

## 部门管理（/blade-system/dept/*）
GET /blade-system/dept/detail
GET /blade-system/dept/list
GET /blade-system/dept/lazy-list
GET /blade-system/dept/tree
GET /blade-system/dept/lazy-tree
POST /blade-system/dept/submit
POST /blade-system/dept/remove
GET /blade-system/dept/select
GET /blade-system/dept/leader-info

## 字典管理（/blade-system/dict/*）
GET /blade-system/dict/detail
GET /blade-system/dict/list
GET /blade-system/dict/parent-list
GET /blade-system/dict/child-list
GET /blade-system/dict/tree
GET /blade-system/dict/parent-tree
POST /blade-system/dict/submit
POST /blade-system/dict/remove
GET /blade-system/dict/dictionary
GET /blade-system/dict/dictionary-full
GET /blade-system/dict/dictionary-tree
GET /blade-system/dict/select
GET /blade-system/dict/select-all

## 业务字典（/blade-system/dict-biz/*）
GET /blade-system/dict-biz/detail
GET /blade-system/dict-biz/list
GET /blade-system/dict-biz/parent-list
GET /blade-system/dict-biz/child-list
GET /blade-system/dict-biz/tree
GET /blade-system/dict-biz/parent-tree
POST /blade-system/dict-biz/submit
POST /blade-system/dict-biz/remove
GET /blade-system/dict-biz/dictionary
GET /blade-system/dict-biz/dictionary-tree
GET /blade-system/dict-biz/select
GET /blade-system/dict-biz/select-all

## 岗位管理（/blade-system/post/*）
GET /blade-system/post/detail
GET /blade-system/post/list
GET /blade-system/post/page
POST /blade-system/post/save
POST /blade-system/post/update
POST /blade-system/post/submit
POST /blade-system/post/remove
GET /blade-system/post/select

## 参数配置（/blade-system/param/*）
GET /blade-system/param/list
GET /blade-system/param/detail
POST /blade-system/param/submit
POST /blade-system/param/remove

## 租户管理（/blade-system/tenant/*）
GET /blade-system/tenant/detail
GET /blade-system/tenant/list
GET /blade-system/tenant/select
GET /blade-system/tenant/page
POST /blade-system/tenant/submit
POST /blade-system/tenant/recycle
POST /blade-system/tenant/pass
POST /blade-system/tenant/remove
POST /blade-system/tenant/setting
POST /blade-system/tenant/datasource
GET /blade-system/tenant/find-by-name
GET /blade-system/tenant/info
GET /blade-system/tenant/package-detail
POST /blade-system/tenant/package-setting

## 数据/接口权限（/blade-system/data-scope, /blade-system/api-scope）
GET /blade-system/data-scope/list
GET /blade-system/data-scope/detail
POST /blade-system/data-scope/submit
POST /blade-system/data-scope/remove
GET /blade-system/api-scope/list
GET /blade-system/api-scope/detail
POST /blade-system/api-scope/submit
POST /blade-system/api-scope/remove

## API Key（/blade-system/api-key/*）
GET /blade-system/api-key/list
GET /blade-system/api-key/detail
POST /blade-system/api-key/remove
POST /blade-system/api-key/save
POST /blade-system/api-key/update
POST /blade-system/api-key/submit
POST /blade-system/api-key/enable
POST /blade-system/api-key/disable

## 顶部菜单（/blade-system/topmenu/*）
GET /blade-system/topmenu/list
GET /blade-system/topmenu/detail
POST /blade-system/topmenu/remove
POST /blade-system/topmenu/submit
GET /blade-system/topmenu/grant-tree
GET /blade-system/topmenu/top-tree-keys
POST /blade-system/topmenu/grant

## OAuth2 应用（/blade-system/client/*）
GET /blade-system/client/list
GET /blade-system/client/detail
POST /blade-system/client/remove
POST /blade-system/client/submit

## 租户产品包（/blade-system/tenant-package/*）
GET /blade-system/tenant-package/list
GET /blade-system/tenant-package/detail
POST /blade-system/tenant-package/remove
POST /blade-system/tenant-package/submit

## 租户数据源（/blade-system/tenant-datasource/*）
GET /blade-system/tenant-datasource/list
GET /blade-system/tenant-datasource/detail
POST /blade-system/tenant-datasource/remove
POST /blade-system/tenant-datasource/submit

## 行政区划（/blade-system/region/*）
GET /blade-system/region/list
GET /blade-system/region/lazy-tree
GET /blade-system/region/detail
POST /blade-system/region/remove
POST /blade-system/region/submit

## 数据审计（/blade-system/record-data/*）
GET /blade-system/record-data/list
GET /blade-system/record-data/detail
POST /blade-system/record-data/save
POST /blade-system/record-data/update
POST /blade-system/record-data/submit
POST /blade-system/record-data/remove

## 日志管理（/blade-log/*）
GET /blade-log/usual/list
GET /blade-log/usual/detail
GET /blade-log/api/list
GET /blade-log/api/detail
GET /blade-log/error/list
GET /blade-log/error/detail

## 工作台（/blade-desk/*）
GET /blade-desk/notice/list
GET /blade-desk/notice/detail
POST /blade-desk/notice/submit
POST /blade-desk/notice/remove
POST /blade-desk/process/leave/start-process
GET /blade-desk/process/leave/detail

## 资源管理（/blade-resource/*）
GET /blade-resource/oss/list
GET /blade-resource/oss/detail
POST /blade-resource/oss/remove
POST /blade-resource/oss/submit
POST /blade-resource/oss/enable
GET /blade-resource/attach/list
GET /blade-resource/attach/detail
POST /blade-resource/attach/remove
POST /blade-resource/attach/submit
GET /blade-resource/sms/list
GET /blade-resource/sms/detail
POST /blade-resource/sms/remove
POST /blade-resource/sms/submit
POST /blade-resource/sms/enable
POST /blade-resource/sms/endpoint/send-message
POST /oauth/sms/send-validate

## 开发工具（/blade-develop/*）
GET /blade-develop/code/list
GET /blade-develop/code/detail
POST /blade-develop/code/remove
POST /blade-develop/code/submit
POST /blade-develop/code/copy
POST /blade-develop/code/gen-code
POST /blade-develop/code/gen-code-fast
GET /blade-develop/code-setting/list
GET /blade-develop/code-setting/detail
POST /blade-develop/code-setting/remove
POST /blade-develop/code-setting/submit
POST /blade-develop/code-setting/enable
GET /blade-develop/code-setting/enable-detail
GET /blade-develop/code-setting/table-form
GET /blade-develop/code-setting/table-prototype
GET /blade-develop/datasource/list
GET /blade-develop/datasource/detail
POST /blade-develop/datasource/remove
POST /blade-develop/datasource/submit
GET /blade-develop/model/list
GET /blade-develop/model/detail
POST /blade-develop/model/remove
POST /blade-develop/model/submit
GET /blade-develop/model/table-list
GET /blade-develop/model/table-info
GET /blade-develop/model/model-prototype
GET /blade-develop/model-prototype/select
POST /blade-develop/model-prototype/submit-list

## 工作流（/blade-flow/*）
GET /blade-flow/work/start-list
GET /blade-flow/work/claim-list
GET /blade-flow/work/todo-list
GET /blade-flow/work/send-list
GET /blade-flow/work/done-list
POST /blade-flow/work/claim-task
POST /blade-flow/work/complete-task
GET /blade-flow/process/history-flow-list
GET /blade-flow/process/model-view
GET /blade-flow/model/list
POST /blade-flow/model/remove
POST /blade-flow/model/deploy
POST /blade-flow/model/submit
GET /blade-flow/model/detail
GET /blade-flow/manager/list
POST /blade-flow/manager/change-state
POST /blade-flow/manager/deploy-upload
POST /blade-flow/manager/delete-deployment
GET /blade-flow/follow/list
POST /blade-flow/follow/delete-process-instance

## 定时任务（/blade-job/*）
GET /blade-job/job-info/list
GET /blade-job/job-info/detail
POST /blade-job/job-info/remove
POST /blade-job/job-info/submit
POST /blade-job/job-info/change
POST /blade-job/job-info/run
POST /blade-job/job-info/sync
GET /blade-job/job-server/list
GET /blade-job/job-server/detail
POST /blade-job/job-server/remove
POST /blade-job/job-server/submit
POST /blade-job/job-server/sync

## 报表管理（/blade-report/*）
GET /blade-report/report/rest/list
POST /blade-report/report/rest/remove

## 信息查询（/blade-system/search/*）
GET /blade-system/search/role-list
GET /blade-system/search/dept-list
GET /blade-system/search/post-list
GET /blade-system/search/user-list
GET /blade-system/search/role-by-name
GET /blade-system/search/dept-by-name
GET /blade-system/search/user-by-name
