<template>
  <el-row>
    <el-col :span="5">
      <div class="box">
        <el-scrollbar>
          <basic-container>
            <avue-tree :option="treeOption" :data="treeData" @node-click="nodeClick" />
          </basic-container>
        </el-scrollbar>
      </div>
    </el-col>
    <el-col :span="19">
      <basic-container>
        <avue-crud
          :option="userOption"
          v-model:search="search"
          :table-loading="loading"
          :data="data"
          ref="crud"
          v-model="form"
          :permission="permissionList"
          @row-del="rowDel"
          @row-update="rowUpdate"
          @row-save="rowSave"
          :before-open="beforeOpen"
          v-model:page="page"
          @search-change="searchChange"
          @search-reset="searchReset"
          @selection-change="selectionChange"
          @current-change="currentChange"
          @size-change="sizeChange"
          @refresh-change="refreshChange"
          @on-load="onLoad"
        >
          <template #menu-left>
            <el-button
              v-if="permission.user_add && !auditMode"
              type="primary"
              icon="el-icon-plus"
              @click="$refs.crud.rowAdd()"
              >新 增
            </el-button>
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              v-if="permission.user_delete && !auditMode"
              @click="handleDelete"
              >删 除
            </el-button>
            <el-button
              type="primary"
              plain
              icon="el-icon-operation"
              v-if="userInfo.authority.includes('admin') && !auditMode"
              @click="handleAudit"
              >审 核
            </el-button>
            <el-button
              type="success"
              plain
              icon="el-icon-check"
              v-if="userInfo.authority.includes('admin') && auditMode"
              @click="handleAuditPass"
              >通 过
            </el-button>
            <el-button
              type="danger"
              plain
              icon="el-icon-close"
              v-if="userInfo.authority.includes('admin') && auditMode"
              @click="handleAuditRefuse"
              >拒 绝
            </el-button>
            <el-button
              type="primary"
              plain
              icon="el-icon-refresh-left"
              v-if="userInfo.authority.includes('admin') && auditMode"
              @click="handleAuditBack"
              >返 回
            </el-button>
            <el-button
              type="info"
              plain
              v-if="permission.user_role && !auditMode"
              icon="el-icon-user"
              @click="handleGrant"
              >角色配置
            </el-button>
            <el-button
              type="info"
              plain
              v-if="permission.user_reset && !auditMode"
              icon="el-icon-refresh"
              @click="handleReset"
              >密码重置
            </el-button>
            <el-button
              type="info"
              plain
              v-if="userInfo.authority.includes('admin') && !auditMode"
              icon="el-icon-setting"
              @click="handlePlatform"
              >平台配置
            </el-button>
            <el-button
              type="info"
              plain
              v-if="userInfo.authority.includes('admin') && !auditMode"
              icon="el-icon-coordinate"
              @click="handleLock"
              >账号解封
            </el-button>
            <el-button
              type="success"
              plain
              v-if="userInfo.authority.includes('admin') && !auditMode"
              icon="el-icon-upload"
              @click="handleImport"
              >导入
            </el-button>
            <el-button
              type="warning"
              plain
              v-if="userInfo.authority.includes('admin') && !auditMode"
              icon="el-icon-download"
              @click="handleExport"
              >导出
            </el-button>
          </template>
          <template #menu="{ row, index, size }">
            <el-button
              text
              type="primary"
              icon="el-icon-view"
              v-if="this.permission.user_view"
              @click.stop="$refs.crud.rowView(row, index)"
              >查 看
            </el-button>
            <el-button
              text
              type="primary"
              icon="el-icon-edit"
              v-if="this.permission.user_edit"
              @click.stop="$refs.crud.rowEdit(row, index)"
              >编 辑
            </el-button>
            <el-dropdown @command="command => handleDropdownCommand(command, row, index)">
              <el-button text type="primary" icon="el-icon-setting">更 多 </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="setLeader" v-if="userInfo.authority.includes('admin')">
                    <el-icon><circle-close v-if="row.isLeader === 1" /><user v-else /></el-icon>
                    {{ row.isLeader === 1 ? '取消主管' : '设置主管' }}
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" v-if="this.permission.user_delete">
                    <el-icon><delete /></el-icon>
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template #tenantName="{ row }">
            <el-tag>{{ row.tenantName }}</el-tag>
          </template>
          <template #roleName="{ row }">
            <el-tag>{{ row.roleName }}</el-tag>
          </template>
          <template #deptName="{ row }">
            <el-tag>{{ row.deptName }}</el-tag>
          </template>
          <template #userTypeName="{ row }">
            <el-tag>{{ row.userTypeName }}</el-tag>
          </template>
        </avue-crud>
        <el-dialog title="用户角色配置" append-to-body v-model="roleBox" width="345px">
          <el-tree
            :data="roleGrantList"
            show-checkbox
            check-strictly
            default-expand-all
            node-key="id"
            ref="treeRole"
            :default-checked-keys="roleTreeObj"
            :props="props"
          >
          </el-tree>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="roleBox = false">取 消</el-button>
              <el-button type="primary" @click="submitRole">确 定</el-button>
            </span>
          </template>
        </el-dialog>
        <el-dialog title="用户数据导入" append-to-body v-model="excelBox" width="555px">
          <avue-form :option="excelOption" v-model="excelForm" :upload-after="uploadAfter">
            <template #excelTemplate>
              <el-button type="primary" @click="handleTemplate">
                点击下载<i class="el-icon-download el-icon--right"></i>
              </el-button>
            </template>
          </avue-form>
        </el-dialog>
        <el-dialog title="用户平台配置" append-to-body v-model="platformBox">
          <avue-crud
            :option="platformOption"
            :table-loading="platformLoading"
            :data="platformData"
            ref="platformCrud"
            v-model="platformForm"
            :before-open="platformBeforeOpen"
            v-model:page="platformPage"
            :permission="platformPermissionList"
            @row-update="platformRowUpdate"
            @search-change="platformSearchChange"
            @search-reset="platformSearchReset"
            @selection-change="platformSelectionChange"
            @current-change="platformCurrentChange"
            @size-change="platformSizeChange"
            @refresh-change="platformRefreshChange"
            @on-load="platformOnLoad"
          >
            <template #tenantName="{ row }">
              <el-tag>{{ row.tenantName }}</el-tag>
            </template>
            <template #userTypeName="{ row }">
              <el-tag>{{ row.userTypeName }}</el-tag>
            </template>
          </avue-crud>
        </el-dialog>
      </basic-container>
    </el-col>
  </el-row>
</template>

<script>
import {
  getList,
  getUser,
  getUserPlatform,
  remove,
  update,
  updatePlatform,
  add,
  grant,
  resetPassword,
  unlock,
  auditPass,
  auditRefuse,
  setLeader,
  getLeaderList,
} from '@/api/system/user';
import { exportBlob } from '@/api/common';
import { getDeptTree } from '@/api/system/dept';
import { getRoleTree } from '@/api/system/role';
import { getPostList } from '@/api/system/post';
import { mapGetters } from 'vuex';
import website from '@/config/website';
import { getToken } from '@/utils/auth';
import { downloadXls } from '@/utils/util';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import func from '@/utils/func';
import { sensitive } from '@/utils/sensitive';
import { excelOption, platformOption, treeOption, userOption } from '@/option/system/user';

export default {
  data() {
    return {
      form: {},
      search: {},
      sensitiveManager: null,
      roleBox: false,
      excelBox: false,
      platformBox: false,
      initFlag: true,
      auditMode: false,
      selectionList: [],
      query: {},
      loading: true,
      platformLoading: false,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      platformPage: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      init: {
        roleTree: [],
        deptTree: [],
      },
      props: {
        label: 'title',
        value: 'key',
      },
      roleGrantList: [],
      roleTreeObj: [],
      treeDeptId: '',
      treeData: [],
      treeOption: treeOption,
      userOption: userOption(this),
      data: [],
      platformQuery: {},
      platformSelectionList: [],
      platformData: [],
      platformForm: {},
      platformOption: platformOption,
      excelForm: {},
      excelOption: excelOption,
    };
  },
  watch: {
    'form.tenantId'() {
      if (this.form.tenantId !== '' && this.initFlag) {
        this.initData(this.form.tenantId);
      }
    },
    'excelForm.isCovered'() {
      if (this.excelForm.isCovered !== '') {
        const column = this.findObject(this.excelOption.column, 'excelFile');
        column.action = `/blade-system/user/import-user?isCovered=${this.excelForm.isCovered}`;
      }
    },
  },
  computed: {
    ...mapGetters(['userInfo', 'permission']),
    permissionList() {
      return {
        addBtn: this.validData(this.permission.user_add, false),
        viewBtn: false,
        delBtn: false,
        editBtn: false,
      };
    },
    platformPermissionList() {
      return {
        addBtn: false,
        viewBtn: false,
        delBtn: false,
        editBtn: this.validData(this.permission.user_edit, false),
      };
    },
    ids() {
      let ids = [];
      this.selectionList.forEach(ele => {
        ids.push(ele.id);
      });
      return ids.join(',');
    },
  },
  mounted() {
    // 非租户模式默认加载管理组数据
    if (!website.tenantMode) {
      this.initData(website.tenantId);
    } else {
      this.initData();
    }
  },
  created() {
    // 创建脱敏工具实例
    this.sensitiveManager = sensitive.create({
      fields: ['phone', 'email'], // 配置需要脱敏的字段
    });
  },
  methods: {
    nodeClick(data) {
      this.treeDeptId = data.id;
      this.page.currentPage = 1;
      this.onLoad(this.page);
    },
    handleDropdownCommand(command, row, index) {
      if (command === 'setLeader') {
        this.handleSetLeader(row);
      } else if (command === 'delete') {
        this.$refs.crud.rowDel(row, index);
      }
    },
    handleSetLeader(row) {
      const tip = row.isLeader === 1 ? '确定取消用户的主管职务?' : '确定设置用户为主管职务?';
      const message = row.isLeader === 1 ? '取消主管成功!' : '设置主管成功!';
      this.$confirm(tip, {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return setLeader(row.id);
        })
        .then(() => {
          this.$message({
            type: 'success',
            message: message,
          });
          this.onLoad(this.page);
        });
    },
    initData(tenantId) {
      getRoleTree(tenantId).then(res => {
        const column = this.findObject(this.userOption.group, 'roleId');
        column.dicData = res.data.data;
      });
      getDeptTree(tenantId).then(res => {
        const column = this.findObject(this.userOption.group, 'deptId');
        column.dicData = res.data.data;
      });
      getPostList(tenantId).then(res => {
        const column = this.findObject(this.userOption.group, 'postId');
        column.dicData = res.data.data;
      });
      getLeaderList(tenantId).then(res => {
        const column = this.findObject(this.userOption.group, 'leaderId');
        column.dicData = res.data.data;
      });
    },
    submitRole() {
      const roleList = this.$refs.treeRole.getCheckedKeys().join(',');
      grant(this.ids, roleList).then(() => {
        this.roleBox = false;
        this.$message({
          type: 'success',
          message: '操作成功!',
        });
        this.onLoad(this.page);
      });
    },
    rowSave(row, done, loading) {
      row.deptId = func.join(row.deptId);
      row.roleId = func.join(row.roleId);
      row.postId = func.join(row.postId);
      row.leaderId = func.join(row.leaderId);
      add(row).then(
        () => {
          this.initFlag = false;
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
          done();
        },
        error => {
          window.console.log(error);
          loading();
        }
      );
    },
    rowUpdate(row, index, done, loading) {
      row.deptId = func.join(row.deptId);
      row.roleId = func.join(row.roleId);
      row.postId = func.join(row.postId);
      row.leaderId = func.join(row.leaderId);
      // 获取需要提交的数据
      const submitData = this.sensitiveManager.getSubmitData(row);
      update(submitData).then(
        () => {
          this.initFlag = false;
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
          done();
        },
        error => {
          window.console.log(error);
          loading();
        }
      );
    },
    rowDel(row) {
      this.$confirm('确定将选择数据删除?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return remove(row.id);
        })
        .then(() => {
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
        });
    },
    searchReset() {
      this.query = {};
      this.treeDeptId = '';
      this.onLoad(this.page);
    },
    searchChange(params, done) {
      this.query = params;
      this.page.currentPage = 1;
      this.onLoad(this.page, params);
      done();
    },
    selectionChange(list) {
      this.selectionList = list;
    },
    selectionClear() {
      this.selectionList = [];
      this.$refs.crud.toggleSelection();
    },
    handleDelete() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择至少一条数据');
        return;
      }
      this.$confirm('确定将选择数据删除?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return remove(this.ids);
        })
        .then(() => {
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
          this.$refs.crud.toggleSelection();
        });
    },
    handleAudit() {
      this.auditMode = true;
      this.onLoad(this.page, this.query);
    },
    handleAuditPass() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择至少一条数据');
        return;
      }
      this.$confirm('确定将选择数据通过审核?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return auditPass(this.ids);
        })
        .then(() => {
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
          this.$refs.crud.toggleSelection();
        });
    },
    handleAuditRefuse() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择至少一条数据');
        return;
      }
      this.$confirm('确定将选择数据拒绝审核?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return auditRefuse(this.ids);
        })
        .then(() => {
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
          this.$refs.crud.toggleSelection();
        });
    },
    handleAuditBack() {
      this.auditMode = false;
      this.onLoad(this.page, this.query);
    },
    handleReset() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择至少一条数据');
        return;
      }
      this.$confirm('确定将选择账号密码重置为初始密码?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return resetPassword(this.ids);
        })
        .then(() => {
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
          this.$refs.crud.toggleSelection();
        });
    },
    handleGrant() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择至少一条数据');
        return;
      }
      this.roleTreeObj = [];
      if (this.selectionList.length === 1) {
        this.roleTreeObj = this.selectionList[0].roleId.split(',');
      }
      getRoleTree().then(res => {
        this.roleGrantList = res.data.data;
        this.roleBox = true;
      });
    },
    handlePlatform() {
      this.platformBox = true;
    },
    handleLock() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择至少一条数据');
        return;
      }
      this.$confirm('确定将选择账号解封？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return unlock(this.ids);
        })
        .then(() => {
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
        });
    },
    handleImport() {
      this.excelBox = true;
    },
    uploadAfter(res, done, loading, column) {
      window.console.log(column);
      this.excelBox = false;
      this.refreshChange();
      done();
    },
    handleExport() {
      const account = func.toStr(this.search.account);
      const realName = func.toStr(this.search.realName);
      this.$confirm('是否导出用户数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        NProgress.start();
        exportBlob(
          `/blade-system/user/export-user?${
            this.website.tokenHeader
          }=${getToken()}&account=${account}&realName=${realName}`
        ).then(res => {
          downloadXls(res.data, `用户数据表${this.$dayjs().format('YYYY-MM-DD')}.xlsx`);
          NProgress.done();
        });
      });
    },
    handleTemplate() {
      exportBlob(
        `/blade-system/user/export-template?${this.website.tokenHeader}=${getToken()}`
      ).then(res => {
        downloadXls(res.data, '用户数据模板.xlsx');
      });
    },
    beforeOpen(done, type) {
      if (['edit', 'view'].includes(type)) {
        getUser(this.form.id).then(res => {
          this.form = res.data.data;
          if (this.form.hasOwnProperty('deptId')) {
            this.form.deptId = func.split(this.form.deptId);
          }
          if (this.form.hasOwnProperty('roleId')) {
            this.form.roleId = func.split(this.form.roleId);
          }
          if (this.form.hasOwnProperty('postId')) {
            this.form.postId = func.split(this.form.postId);
          }
          if (this.form.hasOwnProperty('leaderId')) {
            this.form.leaderId = func.split(this.form.leaderId);
          }
          if (this.form.sex === -1) {
            this.form.sex = '';
          }
          // 保存初始脱敏数据
          this.sensitiveManager.saveInitialData(this.form);
        });
      }
      this.initFlag = true;
      done();
    },
    currentChange(currentPage) {
      this.page.currentPage = currentPage;
    },
    sizeChange(pageSize) {
      this.page.pageSize = pageSize;
    },
    refreshChange() {
      this.onLoad(this.page, this.query);
    },
    onLoad(page, params = {}) {
      this.loading = true;
      getList(
        page.currentPage,
        page.pageSize,
        Object.assign(params, this.query, { status: this.auditMode ? 0 : 1 }),
        this.treeDeptId
      ).then(res => {
        const data = res.data.data;
        this.page.total = data.total;
        this.data = data.records;
        this.loading = false;
        this.selectionClear();
      });
    },
    platformRowUpdate(row, index, done, loading) {
      updatePlatform(row.id, row.userType, row.userExt).then(
        () => {
          this.platformOnLoad(this.platformPage);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
          done();
        },
        error => {
          window.console.log(error);
          loading();
        }
      );
    },
    platformBeforeOpen(done, type) {
      if (['edit', 'view'].includes(type)) {
        getUserPlatform(this.platformForm.id).then(res => {
          this.platformForm = res.data.data;
        });
      }
      done();
    },
    platformSearchReset() {
      this.platformQuery = {};
      this.platformOnLoad(this.platformPage);
    },
    platformSearchChange(params, done) {
      this.platformQuery = params;
      this.platformPage.currentPage = 1;
      this.platformOnLoad(this.platformPage, params);
      done();
    },
    platformSelectionChange(list) {
      this.platformSelectionList = list;
    },
    platformSelectionClear() {
      this.platformSelectionList = [];
      this.$refs.platformCrud.toggleSelection();
    },
    platformCurrentChange(currentPage) {
      this.platformPage.currentPage = currentPage;
    },
    platformSizeChange(pageSize) {
      this.platformPage.pageSize = pageSize;
    },
    platformRefreshChange() {
      this.platformOnLoad(this.platformPage, this.platformQuery);
    },
    platformOnLoad(page, params = {}) {
      this.platformLoading = true;
      getList(
        page.currentPage,
        page.pageSize,
        Object.assign(params, this.query),
        this.treeDeptId
      ).then(res => {
        const data = res.data.data;
        this.platformPage.total = data.total;
        this.platformData = data.records;
        this.platformLoading = false;
        this.selectionClear();
      });
    },
  },
};
</script>

<style>
.box {
  height: 800px;
}

.el-scrollbar {
  height: 100%;
}

.box .el-scrollbar__wrap {
  overflow: scroll;
}
</style>
