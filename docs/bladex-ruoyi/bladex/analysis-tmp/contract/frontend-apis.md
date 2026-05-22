# 前端 API 调用清单（中间数据）

## api/user.js（认证）
POST /blade-auth/oauth/token - loginByUsername（密码登录）
POST /blade-auth/oauth/token - loginBySocial（社交登录）
POST /blade-auth/oauth/token - loginBySso（SSO登录）
POST /blade-auth/oauth/token - loginByPhone（手机登录）
POST /blade-auth/oauth/token - refreshToken（Token刷新）
POST /blade-auth/oauth/token - registerUser（用户注册）
POST /blade-system/user/register-guest - registerGuest（第三方注册补充）
GET /blade-system/menu/buttons - getButtons（按钮权限）
GET /blade-auth/oauth/captcha - getCaptcha（验证码）
GET /blade-auth/oauth/logout - logout（登出）
GET /blade-auth/oauth/user-info - getUserInfo（用户信息）
POST /blade-auth/oauth/logout - sendLogs（日志上报）
GET /blade-auth/oauth/clear-cache - clearCache（清除缓存）
POST /blade-auth/oauth/sms/send-validate - sendSms（短信验证码）

## api/system/user.js（用户管理）
GET /blade-system/user/page - getList
POST /blade-system/user/remove - remove
POST /blade-system/user/submit - add
POST /blade-system/user/update - update
POST /blade-system/user/update-platform - updatePlatform
GET /blade-system/user/detail - getUser
GET /blade-system/user/platform-detail - getUserPlatform
GET /blade-system/user/info - getUserInfo
POST /blade-system/user/reset-password - resetPassword
POST /blade-system/user/update-password - updatePassword
POST /blade-system/user/update-info - updateInfo
POST /blade-system/user/grant - grant
POST /blade-system/user/unlock - unlock
POST /blade-system/user/audit-pass - auditPass
POST /blade-system/user/audit-refuse - auditRefuse
POST /blade-system/user/set-leader - setLeader
GET /blade-system/user/leader-list - getLeaderList

## api/system/menu.js（菜单管理）
GET /blade-system/menu/list - getList
GET /blade-system/menu/lazy-list - getLazyList
GET /blade-system/menu/lazy-menu-list - getLazyMenuList
GET /blade-system/menu/menu-list - getMenuList
GET /blade-system/menu/tree - getMenuTree
POST /blade-system/menu/remove - remove
POST /blade-system/menu/submit - add
POST /blade-system/menu/submit - update
GET /blade-system/menu/detail - getMenu
GET /blade-system/menu/top-menu - getTopMenu
GET /blade-system/menu/routes - getRoutes

## api/system/role.js（角色管理）
GET /blade-system/role/list - getList
GET /blade-system/menu/grant-tree - grantTree
POST /blade-system/role/grant - grant
POST /blade-system/role/remove - remove
POST /blade-system/role/submit - add
POST /blade-system/role/submit - update
GET /blade-system/menu/role-tree-keys - getRole
GET /blade-system/role/tree - getRoleTree
GET /blade-system/role/tree-by-id - getRoleTreeById
GET /blade-system/role/alias - getRoleAlias

## api/system/dept.js（部门管理）
GET /blade-system/dept/list - getList
GET /blade-system/dept/lazy-list - getLazyList
POST /blade-system/dept/remove - remove
POST /blade-system/dept/submit - add
POST /blade-system/dept/submit - update
GET /blade-system/dept/detail - getDept
GET /blade-system/dept/tree - getDeptTree
GET /blade-system/dept/lazy-tree - getDeptLazyTree

## api/system/dict.js（字典）
GET /blade-system/dict/list - getList
GET /blade-system/dict/parent-list - getParentList
GET /blade-system/dict/child-list - getChildList
POST /blade-system/dict/remove - remove
POST /blade-system/dict/submit - add
POST /blade-system/dict/submit - update
GET /blade-system/dict/detail - getDict
GET /blade-system/dict/tree - getDictTree
GET /blade-system/dict/dictionary - getDictionary

## api/system/dictbiz.js（业务字典）
GET /blade-system/dict-biz/list - getList
GET /blade-system/dict-biz/parent-list - getParentList
GET /blade-system/dict-biz/child-list - getChildList
POST /blade-system/dict-biz/remove - remove
POST /blade-system/dict-biz/submit - add
POST /blade-system/dict-biz/submit - update
GET /blade-system/dict-biz/detail - getDict
GET /blade-system/dict-biz/tree - getDictTree
GET /blade-system/dict-biz/dictionary - getDictionary

## api/system/param.js（参数配置）
GET /blade-system/param/list
GET /blade-system/param/detail
POST /blade-system/param/submit - add
POST /blade-system/param/submit - update
POST /blade-system/param/remove

## api/system/post.js（岗位）
GET /blade-system/post/list
GET /blade-system/post/select
GET /blade-system/post/detail
POST /blade-system/post/submit - add
POST /blade-system/post/submit - update
POST /blade-system/post/remove

## api/system/tenant.js（租户）
GET /blade-system/tenant/list
GET /blade-system/tenant/detail
POST /blade-system/tenant/submit - add
POST /blade-system/tenant/submit - update
POST /blade-system/tenant/setting
POST /blade-system/tenant/datasource
GET /blade-system/tenant/info
GET /blade-system/tenant/package-detail
POST /blade-system/tenant/package-setting
POST /blade-system/tenant/recycle
POST /blade-system/tenant/pass
POST /blade-system/tenant/remove

## api/system/tenantpackage.js（租户产品包）
GET /blade-system/tenant-package/list
GET /blade-system/tenant-package/detail
POST /blade-system/tenant-package/submit - add
POST /blade-system/tenant-package/submit - update
POST /blade-system/tenant-package/remove

## api/system/tenantdatasource.js（租户数据源）
GET /blade-system/tenant-datasource/list
GET /blade-system/tenant-datasource/detail
POST /blade-system/tenant-datasource/submit - add
POST /blade-system/tenant-datasource/submit - update
POST /blade-system/tenant-datasource/remove

## api/system/topmenu.js（顶部菜单）
GET /blade-system/topmenu/list
GET /blade-system/topmenu/detail
POST /blade-system/topmenu/remove
POST /blade-system/topmenu/submit - add
POST /blade-system/topmenu/submit - update
GET /blade-system/menu/grant-top-tree - grantTree
GET /blade-system/menu/top-tree-keys - getTopTree
POST /blade-system/topmenu/grant

## api/system/client.js（OAuth2应用）
GET /blade-system/client/list
GET /blade-system/client/detail
POST /blade-system/client/remove
POST /blade-system/client/submit - add
POST /blade-system/client/submit - update

## api/system/scope.js（数据/接口权限）
GET /blade-system/data-scope/list
POST /blade-system/data-scope/remove
POST /blade-system/data-scope/submit - add
POST /blade-system/data-scope/submit - update
GET /blade-system/data-scope/detail
GET /blade-system/api-scope/list
POST /blade-system/api-scope/remove
POST /blade-system/api-scope/submit - add
POST /blade-system/api-scope/submit - update
GET /blade-system/api-scope/detail

## api/system/apikey.js（API Key）
GET /blade-system/api-key/list
GET /blade-system/api-key/detail
POST /blade-system/api-key/remove
POST /blade-system/api-key/save - add
POST /blade-system/api-key/update - update
POST /blade-system/api-key/submit
POST /blade-system/api-key/enable
POST /blade-system/api-key/disable

## api/base/region.js（行政区划）
GET /blade-system/region/list
GET /blade-system/region/lazy-tree
GET /blade-system/region/detail
POST /blade-system/region/remove
POST /blade-system/region/submit

## api/resource/oss.js
GET /blade-resource/oss/list
GET /blade-resource/oss/detail
POST /blade-resource/oss/remove
POST /blade-resource/oss/submit - add
POST /blade-resource/oss/submit - update
POST /blade-resource/oss/enable

## api/resource/attach.js
GET /blade-resource/attach/list
GET /blade-resource/attach/detail
POST /blade-resource/attach/remove
POST /blade-resource/attach/submit - add
POST /blade-resource/attach/submit - update

## api/resource/sms.js
GET /blade-resource/sms/list
GET /blade-resource/sms/detail
POST /blade-resource/sms/remove
POST /blade-resource/sms/submit - add
POST /blade-resource/sms/submit - update
POST /blade-resource/sms/enable
POST /blade-resource/sms/endpoint/send-message

## api/tool/code.js
GET /blade-develop/code/list
POST /blade-develop/code/remove
POST /blade-develop/code/submit - add
POST /blade-develop/code/submit - update
POST /blade-develop/code/copy
POST /blade-develop/code/gen-code
POST /blade-develop/code/gen-code-fast
GET /blade-develop/code/detail

## api/tool/codesetting.js
GET /blade-develop/code-setting/list
GET /blade-develop/code-setting/detail
POST /blade-develop/code-setting/remove
POST /blade-develop/code-setting/submit - add
POST /blade-develop/code-setting/submit - update
POST /blade-develop/code-setting/enable
GET /blade-develop/code-setting/enable-detail
GET /blade-develop/code-setting/table-form
GET /blade-develop/code-setting/table-prototype

## api/tool/datasource.js
GET /blade-develop/datasource/list
GET /blade-develop/datasource/detail
POST /blade-develop/datasource/remove
POST /blade-develop/datasource/submit - add
POST /blade-develop/datasource/submit - update

## api/tool/model.js
GET /blade-develop/model/list
GET /blade-develop/model/detail
POST /blade-develop/model/remove
POST /blade-develop/model/submit - add
POST /blade-develop/model/submit - update
GET /blade-develop/model/table-list
GET /blade-develop/model/table-info
GET /blade-develop/model/model-prototype
POST /blade-develop/model-prototype/submit-list
GET /blade-develop/model-prototype/select

## api/flow/flow.js
GET /blade-flow/model/list
GET /blade-flow/manager/list
GET /blade-flow/follow/list
POST /blade-flow/model/remove
POST /blade-flow/model/deploy
POST /blade-flow/manager/change-state
POST /blade-flow/manager/deploy-upload
POST /blade-flow/manager/delete-deployment
POST /blade-flow/follow/delete-process-instance
POST /blade-flow/model/submit
GET /blade-flow/model/detail
GET /blade-flow/process/model-view

## api/work/work.js
GET /blade-flow/work/start-list
GET /blade-flow/work/claim-list
GET /blade-flow/work/todo-list
GET /blade-flow/work/send-list
GET /blade-flow/work/done-list
POST /blade-flow/work/claim-task
POST /blade-flow/work/complete-task

## api/work/process.js
GET /blade-flow/process/history-flow-list
POST /blade-desk/process/leave/start-process
GET /blade-desk/process/leave/detail

## api/desk/notice.js
GET /blade-desk/notice/list
POST /blade-desk/notice/remove
POST /blade-desk/notice/submit - add
POST /blade-desk/notice/submit - update
GET /blade-desk/notice/detail

## api/job/jobinfo.js
GET /blade-job/job-info/list
GET /blade-job/job-info/detail
POST /blade-job/job-info/remove
POST /blade-job/job-info/submit - add
POST /blade-job/job-info/submit - update
POST /blade-job/job-info/change
POST /blade-job/job-info/run
POST /blade-job/job-info/sync

## api/job/jobserver.js
GET /blade-job/job-server/list
GET /blade-job/job-server/detail
POST /blade-job/job-server/remove
POST /blade-job/job-server/submit - add
POST /blade-job/job-server/submit - update
POST /blade-job/job-server/sync

## api/logs.js
GET /blade-log/usual/list
GET /blade-log/api/list
GET /blade-log/error/list
GET /blade-log/usual/detail
GET /blade-log/api/detail
GET /blade-log/error/detail

## api/data/record.js
GET /blade-system/record-data/list
GET /blade-system/record-data/detail
POST /blade-system/record-data/save
POST /blade-system/record-data/update
POST /blade-system/record-data/submit
POST /blade-system/record-data/remove

## api/common.js
GET exportBlob（动态URL）- 文件流下载

## api/report/report.js
GET /blade-report/report/rest/list
POST /blade-report/report/rest/remove
