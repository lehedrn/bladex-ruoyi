<template>
  <basic-container>
    <avue-crud
      :option="option"
      :table-loading="loading"
      :data="data"
      ref="crud"
      v-model="form"
      :permission="permissionList"
      :before-open="beforeOpen"
      @row-del="rowDel"
      @row-update="rowUpdate"
      @row-save="rowSave"
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
          type="danger"
          icon="el-icon-delete"
          v-if="permission.role_delete"
          plain
          @click="handleDelete"
          >删 除
        </el-button>
        <el-button
          icon="el-icon-setting"
          @click="handleRole"
          v-if="userInfo.authority.includes('admin')"
          plain
          >权限设置
        </el-button>
      </template>
      <template #menu="{ row }">
        <el-button
          type="primary"
          text
          icon="el-icon-setting"
          v-if="userInfo.authority.includes('admin')"
          plain
          style="border: 0; background-color: transparent !important"
          @click.stop="handleRowRole(row)"
          >权限设置
        </el-button>
      </template>
    </avue-crud>
    <el-dialog
      title="角色权限配置"
      append-to-body
      v-model="box"
      width="345px"
      @close="handleDialogClose"
    >
      <el-tabs type="border-card">
        <el-tab-pane label="菜单权限">
          <el-tree
            :data="menuGrantList"
            show-checkbox
            node-key="id"
            ref="treeMenu"
            :default-checked-keys="menuTreeObj"
            :props="props"
          >
          </el-tree>
        </el-tab-pane>
        <el-tab-pane label="数据权限">
          <el-tree
            :data="dataScopeGrantList"
            show-checkbox
            node-key="id"
            ref="treeDataScope"
            :default-checked-keys="dataScopeTreeObj"
            :props="props"
          >
          </el-tree>
        </el-tab-pane>
        <el-tab-pane label="接口权限">
          <el-tree
            :data="apiScopeGrantList"
            show-checkbox
            node-key="id"
            ref="treeApiScope"
            :default-checked-keys="apiScopeTreeObj"
            :props="props"
          >
          </el-tree>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="box = false">取 消</el-button>
          <el-button type="primary" @click="submit">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </basic-container>
</template>

<script>
import {
  add,
  getList,
  getRole,
  getRoleAlias,
  getRoleTreeById,
  grant,
  grantTree,
  remove,
  update,
} from '@/api/system/role';
import { mapGetters } from 'vuex';
import website from '@/config/website';
import { validatenull } from '@/utils/validate';

export default {
  data() {
    return {
      form: {},
      box: false,
      props: {
        label: 'title',
        value: 'key',
      },
      menuGrantList: [],
      dataScopeGrantList: [],
      apiScopeGrantList: [],
      apiGrantList: [],
      menuTreeObj: [],
      dataScopeTreeObj: [],
      apiScopeTreeObj: [],
      currentRoleIds: [],
      selectionList: [],
      query: {},
      loading: true,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      option: {
        tip: false,
        simplePage: true,
        searchShow: true,
        searchMenuSpan: 6,
        tree: true,
        border: true,
        index: true,
        selection: true,
        viewBtn: true,
        labelWidth: 120,
        searchLabelWidth: 120,
        menuWidth: 350,
        dialogWidth: 600,
        dialogClickModal: false,
        column: [
          {
            label: '上级角色',
            prop: 'parentId',
            dicData: [],
            type: 'tree',
            hide: true,
            span: 24,
            props: {
              label: 'title',
            },
            rules: [
              {
                required: false,
                message: '请选择上级角色',
                trigger: 'click',
              },
            ],
          },
          {
            label: '角色名称',
            prop: 'roleName',
            search: true,
            span: 24,
            rules: [
              {
                required: true,
                message: '请输入角色名称',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '所属租户',
            prop: 'tenantId',
            type: 'tree',
            dicUrl: '/blade-system/tenant/select',
            addDisplay: false,
            editDisplay: false,
            viewDisplay: website.tenantMode,
            span: 24,
            props: {
              label: 'tenantName',
              value: 'tenantId',
            },
            hide: !website.tenantMode,
            search: website.tenantMode,
            rules: [
              {
                required: true,
                message: '请输入所属租户',
                trigger: 'click',
              },
            ],
          },
          {
            label: '角色别名',
            labelTip: '用于权限校验与标注的属性,可手动录入也可点击现有别名快速录入',
            prop: 'roleAlias',
            search: true,
            span: 24,
            rules: [
              {
                required: true,
                message: '请输入角色别名',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '现有别名',
            labelTip: '当前系统已存在的角色别名,点击可快速录入',
            prop: 'currentAlias',
            type: 'radio',
            dicData: [],
            props: {
              label: 'roleName',
              value: 'roleAlias',
            },
            hide: true,
            span: 24,
          },
          {
            label: '角色排序',
            prop: 'sort',
            type: 'number',
            span: 24,
            rules: [
              {
                required: true,
                message: '请输入角色排序',
                trigger: 'blur',
              },
            ],
          },
        ],
      },
      data: [],
    };
  },
  computed: {
    ...mapGetters(['userInfo', 'permission']),
    permissionList() {
      return {
        addBtn: this.validData(this.permission.role_add, false),
        viewBtn: this.validData(this.permission.role_view, false),
        delBtn: this.validData(this.permission.role_delete, false),
        editBtn: this.validData(this.permission.role_edit, false),
      };
    },
    ids() {
      let ids = [];
      this.selectionList.forEach(ele => {
        ids.push(ele.id);
      });
      return ids.join(',');
    },
    idsArray() {
      let ids = [];
      this.selectionList.forEach(ele => {
        ids.push(ele.id);
      });
      return ids;
    },
  },
  watch: {
    'form.currentAlias'() {
      const alias = this.form.currentAlias;
      if (!validatenull(alias)) {
        this.form.roleAlias = alias;
      }
    },
  },
  methods: {
    initData(roleId) {
      getRoleTreeById(roleId).then(res => {
        const column = this.findObject(this.option.column, 'parentId');
        column.dicData = res.data.data;
      });
    },
    submit() {
      const menuList = this.$refs.treeMenu.getCheckedKeys();
      const dataScopeList = this.$refs.treeDataScope.getCheckedKeys();
      const apiScopeList = this.$refs.treeApiScope.getCheckedKeys();
      grant(this.currentRoleIds, menuList, dataScopeList, apiScopeList).then(() => {
        this.box = false;
        this.$message({
          type: 'success',
          message: '操作成功!',
        });
        this.currentRoleIds = [];
        this.selectionList = [];
        this.onLoad(this.page);
      });
    },
    rowSave(row, done, loading) {
      add(row).then(
        () => {
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
      update(row).then(
        () => {
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
    beforeOpen(done, type) {
      if (['add', 'edit'].includes(type)) {
        this.initData(this.form.id);
      }
      if (['edit', 'view'].includes(type)) {
        if (this.form.parentId === '0') {
          this.form.parentId = '';
        }
      }
      getRoleAlias().then(res => {
        const column = this.findObject(this.option.column, 'currentAlias');
        column.dicData = res.data.data;
      });
      done();
    },
    handleRole() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择一条数据');
        return;
      }
      if (this.selectionList.length > 1) {
        this.$message.warning('只能选择一条数据');
        return;
      }
      this.menuTreeObj = [];
      this.dataScopeTreeObj = [];
      this.apiScopeTreeObj = [];
      // 保存当前要配置权限的角色ID
      this.currentRoleIds = this.idsArray;
      grantTree().then(res => {
        this.menuGrantList = res.data.data.menu;
        this.dataScopeGrantList = res.data.data.dataScope;
        this.apiScopeGrantList = res.data.data.apiScope;
        getRole(this.ids).then(res => {
          this.menuTreeObj = res.data.data.menu;
          this.dataScopeTreeObj = res.data.data.dataScope;
          this.apiScopeTreeObj = res.data.data.apiScope;
          this.box = true;
        });
      });
    },
    handleRowRole(row) {
      this.menuTreeObj = [];
      this.dataScopeTreeObj = [];
      this.apiScopeTreeObj = [];
      // 保存当前要配置权限的角色ID（单个行操作）
      this.currentRoleIds = [row.id];
      grantTree().then(res => {
        this.menuGrantList = res.data.data.menu;
        this.dataScopeGrantList = res.data.data.dataScope;
        this.apiScopeGrantList = res.data.data.apiScope;
        getRole(row.id).then(res => {
          this.menuTreeObj = res.data.data.menu;
          this.dataScopeTreeObj = res.data.data.dataScope;
          this.apiScopeTreeObj = res.data.data.apiScope;
          this.box = true;
        });
      });
    },
    handleDialogClose() {
      // 对话框关闭时清空选择状态和权限树数据
      this.menuTreeObj = [];
      this.dataScopeTreeObj = [];
      this.apiScopeTreeObj = [];
      this.selectionClear();
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
      getList(page.currentPage, page.pageSize, Object.assign(params, this.query)).then(res => {
        this.data = res.data.data;
        this.loading = false;
        this.selectionClear();
      });
    },
  },
};
</script>
