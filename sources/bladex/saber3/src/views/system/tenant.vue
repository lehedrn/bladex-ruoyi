<template>
  <basic-container>
    <avue-crud
      :option="option"
      :table-loading="loading"
      :data="data"
      ref="crud"
      v-model="form"
      v-model:page="page"
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
          v-if="permission.tenant_add && !recycleMode"
          type="primary"
          icon="el-icon-plus"
          @click="$refs.crud.rowAdd()"
          >新 增
        </el-button>
        <el-button
          type="danger"
          icon="el-icon-delete"
          v-if="permission.tenant_delete && !recycleMode"
          plain
          @click="handleDelete"
          >删 除
        </el-button>
        <el-button
          type="primary"
          plain
          icon="el-icon-refresh"
          v-if="userInfo.authority.includes('administrator') && !recycleMode"
          @click="handleRecycle"
          >回收站
        </el-button>
        <el-button
          type="success"
          plain
          icon="el-icon-check"
          v-if="userInfo.authority.includes('administrator') && recycleMode"
          @click="handleRecyclePass"
          >恢 复
        </el-button>
        <el-button
          type="danger"
          plain
          icon="el-icon-close"
          v-if="userInfo.authority.includes('administrator') && recycleMode"
          @click="handleRecycleRemove"
          >删 除
        </el-button>
        <el-button
          type="primary"
          plain
          icon="el-icon-refresh-left"
          v-if="userInfo.authority.includes('administrator') && recycleMode"
          @click="handleRecycleBack"
          >返 回
        </el-button>
        <el-tooltip
          class="item"
          effect="dark"
          content="给租户配置账号额度、过期时间等授权信息"
          placement="top"
        >
          <el-button
            plain
            type="info"
            v-if="userInfo.authority.includes('administrator') && !recycleMode"
            icon="el-icon-setting"
            @click="handleSetting"
            >批量授权配置
          </el-button>
        </el-tooltip>
      </template>
      <template #menu-right>
        <el-tooltip
          class="item"
          effect="dark"
          content="将自定义的数据源定制为租户绑定的独立数据源"
          placement="top"
        >
          <el-button
            plain
            v-if="userInfo.authority.includes('administrator') && !recycleMode"
            icon="el-icon-message-box"
            @click="handleDatasourceSetting"
            >数据源管理
          </el-button>
        </el-tooltip>
        <el-tooltip
          class="item"
          effect="dark"
          content="将自定义的菜单集合定制为租户绑定的菜单产品包"
          placement="top"
        >
          <el-button
            plain
            v-if="userInfo.authority.includes('administrator') && !recycleMode"
            icon="el-icon-set-up"
            @click="handlePackageSetting"
            >产品包管理
          </el-button>
        </el-tooltip>
      </template>
      <template #menu="scope">
        <el-button
          v-if="userInfo.authority.includes('administrator') && !recycleMode"
          type="primary"
          text
          icon="el-icon-coin"
          @click.stop="handleRowDatasource(scope.row)"
          >数据源
        </el-button>
        <el-button
          v-if="userInfo.authority.includes('administrator') && !recycleMode"
          type="primary"
          text
          icon="el-icon-notebook"
          @click.stop="handleRowPackage(scope.row)"
          >产品包
        </el-button>
      </template>
      <template #accountNumber="{ row }">
        <el-tag>{{ row.accountNumber > 0 ? row.accountNumber : '不限制' }}</el-tag>
      </template>
      <template #expireTime="{ row }">
        <el-tag>{{ row.expireTime ? row.expireTime : '不限制' }}</el-tag>
      </template>
    </avue-crud>
    <el-dialog title="租户授权配置" append-to-body v-model="box" width="450px">
      <avue-form :option="settingOption" v-model="settingForm" @submit="handleSubmit" />
    </el-dialog>
    <el-dialog title="租户数据源配置" append-to-body v-model="datasourceBox" width="450px">
      <avue-form
        ref="formDatasource"
        :option="datasourceOption"
        v-model="datasourceForm"
        @submit="handleDatasourceSubmit"
      />
    </el-dialog>
    <el-drawer
      title="租户数据源管理"
      append-to-body
      v-model="datasourceSettingBox"
      direction="rtl"
      size="50%"
    >
      <tenant-datasource></tenant-datasource>
    </el-drawer>
    <el-dialog title="租户产品包配置" append-to-body v-model="packageBox" width="450px">
      <avue-form
        ref="formPackage"
        :option="packageOption"
        v-model="packageForm"
        @submit="handlePackageSubmit"
      >
        <template #menuId>
          <el-tree
            ref="packageMenuTree"
            :data="packageMenuTreeData"
            :props="packageMenuTreeProps"
            node-key="id"
            default-expand-all
          />
        </template>
      </avue-form>
    </el-dialog>
    <el-drawer
      title="租户产品包管理"
      append-to-body
      v-model="packageSettingBox"
      direction="rtl"
      size="50%"
    >
      <tenant-package></tenant-package>
    </el-drawer>
  </basic-container>
</template>

<script>
import {
  getList,
  getDetail,
  update,
  add,
  setting,
  datasource,
  packageInfo,
  packageSetting,
  recycle,
  pass,
  remove,
} from '@/api/system/tenant';
import { getDetail as packageDetail } from '@/api/system/tenantpackage';
import { mapGetters } from 'vuex';
import { getMenuTree } from '@/api/system/menu';
import { validatenull } from '@/utils/validate';

export default {
  data() {
    return {
      form: {},
      selectionList: [],
      query: {},
      loading: true,
      box: false,
      datasourceBox: false,
      datasourceSettingBox: false,
      packageBox: false,
      packageSettingBox: false,
      recycleMode: false,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      option: {
        height: 'auto',
        calcHeight: 32,
        tip: false,
        searchShow: true,
        searchMenuSpan: 6,
        border: true,
        index: true,
        selection: true,
        addBtn: false,
        viewBtn: true,
        menuWidth: 380,
        dialogWidth: 900,
        dialogClickModal: false,
        column: [
          {
            label: '租户ID',
            prop: 'tenantId',
            width: 100,
            search: true,
            addDisplay: false,
            editDisplay: false,
            span: 24,
            rules: [
              {
                required: true,
                message: '请输入租户ID',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '租户名称',
            prop: 'tenantName',
            search: true,
            width: 180,
            span: 24,
            rules: [
              {
                required: true,
                message: '请输入参数名称',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '联系人',
            prop: 'linkman',
            width: 100,
            search: true,
            rules: [
              {
                required: true,
                message: '请输入联系人',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '联系电话',
            prop: 'contactNumber',
            width: 150,
          },
          {
            label: '联系地址',
            prop: 'address',
            span: 24,
            minRows: 2,
            type: 'textarea',
            hide: true,
          },
          {
            label: '账号额度',
            prop: 'accountNumber',
            width: 90,
            slot: true,
            addDisplay: false,
            editDisplay: false,
          },
          {
            label: '过期时间',
            prop: 'expireTime',
            width: 180,
            slot: true,
            addDisplay: false,
            editDisplay: false,
          },
          {
            label: '绑定域名',
            prop: 'domainUrl',
            span: 24,
          },
          {
            label: '系统背景',
            prop: 'backgroundUrl',
            type: 'upload',
            listType: 'picture-img',
            dataType: 'string',
            action: '/blade-resource/oss/endpoint/put-file',
            propsHttp: {
              res: 'data',
              url: 'link',
            },
            hide: true,
            span: 24,
          },
        ],
      },
      data: [],
      settingForm: {},
      settingOption: {
        column: [
          {
            label: '账号额度',
            labelTip: '代表租户可创建的最大额度，若不限制则默认为-1',
            prop: 'accountNumber',
            type: 'number',
            span: 24,
          },
          {
            label: '过期时间',
            labelTip: '代表租户可使用的最后日期，若不限制则默认为空',
            prop: 'expireTime',
            type: 'date',
            format: 'YYYY-MM-DD HH:mm:ss',
            valueFormat: 'YYYY-MM-DD HH:mm:ss',
            span: 24,
          },
        ],
      },
      datasourceForm: {},
      datasourceOption: {
        column: [
          {
            label: '数据源',
            prop: 'datasourceId',
            search: true,
            span: 24,
            type: 'select',
            dicUrl: '/blade-system/tenant-datasource/select',
            props: {
              label: 'name',
              value: 'id',
            },
            rules: [
              {
                required: true,
                message: '请选择数据源',
                trigger: 'blur',
              },
            ],
          },
        ],
      },
      packageForm: {},
      packageMenuTreeData: [],
      packageMenuTreeProps: {
        label: 'title',
        value: 'key',
        children: 'children',
      },
      packageOption: {
        column: [
          {
            label: '产品包',
            prop: 'packageId',
            search: true,
            span: 24,
            type: 'select',
            dicUrl: '/blade-system/tenant-package/select',
            props: {
              label: 'packageName',
              value: 'id',
            },
          },
          {
            label: '菜单预览',
            prop: 'menuId',
            span: 24,
            formslot: true,
          },
        ],
      },
    };
  },
  watch: {
    'packageForm.packageId'() {
      if (!validatenull(this.packageForm.packageId)) {
        packageDetail(this.packageForm.packageId).then(res => {
          const menuId = res.data.data.menuId;
          this.packageForm.menuId = menuId;

          // 解析选中的菜单ID列表
          const selectedIds = this.parseMenuIds(menuId);

          // 先加载完整菜单树，再根据选中ID筛选
          getMenuTree().then(treeRes => {
            const fullTreeData = treeRes.data.data;
            const selectedIdSet = new Set(selectedIds);
            // 筛选树：只保留选中节点及其所有祖先节点
            this.packageMenuTreeData = this.filterTreeBySelectedIds(fullTreeData, selectedIdSet);
          });
        });
      }
    },
  },
  computed: {
    ...mapGetters(['userInfo', 'permission']),
    permissionList() {
      return {
        addBtn: this.validData(this.permission.tenant_add, false),
        viewBtn: this.validData(this.permission.tenant_view, false),
        delBtn: this.validData(this.permission.tenant_delete, false),
        editBtn: this.validData(this.permission.tenant_edit, false),
      };
    },
    ids() {
      let ids = [];
      this.selectionList.forEach(ele => {
        ids.push(ele.id);
      });
      return ids.join(',');
    },
    tenantId() {
      return this.selectionList[0].tenantId;
    },
  },
  methods: {
    initData() {
      getMenuTree().then(res => {
        this.packageMenuTreeData = res.data.data;
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
      this.$confirm('删除后所选租户将进入回收站，是否继续?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return recycle(row.id);
        })
        .then(() => {
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
        });
    },
    beforeOpen(done, type) {
      if (['view'].includes(type)) {
        getDetail(this.form.id).then(res => {
          const data = res.data.data;
          if (!(data.accountNumber > 0)) {
            data.accountNumber = '不限制';
          }
          if (!data.expireTime) {
            data.expireTime = '不限制';
          }
          this.form = data;
        });
      }
      done();
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
    handleDelete() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择至少一条数据');
        return;
      }
      this.$confirm('删除后所选租户将进入回收站，是否继续?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return recycle(this.ids);
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
    handleRecycle() {
      this.recycleMode = true;
      this.$refs.crud.option.editBtn = false;
      this.$refs.crud.option.delBtn = false;
      this.onLoad(this.page, this.query);
    },
    handleRecyclePass() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择至少一条数据');
        return;
      }
      this.$confirm('确定将所选租户进行恢复?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return pass(this.ids);
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
    handleRecycleRemove() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择至少一条数据');
        return;
      }
      this.$confirm('确定将所选租户永久删除?', {
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
    handleRecycleBack() {
      this.recycleMode = false;
      this.$refs.crud.option.editBtn = true;
      this.$refs.crud.option.delBtn = true;
      this.onLoad(this.page, this.query);
    },
    handleSetting() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择至少一条数据');
        return;
      }
      if (this.selectionList.length === 1) {
        getDetail(this.selectionList[0].id).then(res => {
          const data = res.data.data;
          this.settingForm.accountNumber = data.accountNumber;
          this.settingForm.expireTime = data.expireTime;
        });
      } else {
        this.settingForm.accountNumber = -1;
        this.settingForm.expireTime = '';
      }
      this.box = true;
    },
    handleRowDatasource(row) {
      getDetail(row.id).then(res => {
        const data = res.data.data;
        this.datasourceForm.tenantId = row.tenantId;
        this.datasourceForm.datasourceId = data.datasourceId;
      });
      this.datasourceBox = true;
      this.$nextTick(() => {
        this.$refs.formDatasource.updateDic('datasourceId');
      });
    },
    handleDatasource() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择至少一条数据');
        return;
      }
      if (this.selectionList.length !== 1) {
        this.$message.warning('只能选择一条数据');
        return;
      }
      getDetail(this.selectionList[0].id).then(res => {
        const data = res.data.data;
        this.datasourceForm.tenantId = this.selectionList[0].tenantId;
        this.datasourceForm.datasourceId = data.datasourceId;
      });
      this.datasourceBox = true;
    },
    handleRowPackage(row) {
      // 重置数据
      this.packageMenuTreeData = [];
      packageInfo(row.id).then(res => {
        const data = res.data.data;
        this.packageForm.tenantId = row.tenantId;
        this.packageForm.packageId = data.id;
        this.packageForm.menuId = data.menuId;
        // 加载菜单树数据
        this.initData();
      });
      this.packageBox = true;
      //更新字典远程数据
      setTimeout(() => {
        const form = this.$refs.formPackage;
        form.updateDic('packageId');
      }, 10);
    },
    handleDatasourceSetting() {
      this.datasourceSettingBox = true;
    },
    handlePackageSetting() {
      this.packageSettingBox = true;
    },
    handleSubmit(form, done) {
      setting(this.ids, form).then(
        () => {
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '配置成功!',
          });
          done();
          this.box = false;
        },
        error => {
          window.console.log(error);
          done();
        }
      );
    },
    handleDatasourceSubmit(form, done) {
      datasource(form.tenantId, form.datasourceId).then(
        () => {
          this.$message({
            type: 'success',
            message: '配置成功!',
          });
          done();
          this.datasourceBox = false;
        },
        error => {
          window.console.log(error);
          done();
        }
      );
    },
    handlePackageSubmit(form, done) {
      packageSetting(form.tenantId, form.packageId).then(
        () => {
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '配置成功!',
          });
          done();
          this.packageBox = false;
        },
        error => {
          window.console.log(error);
          done();
        }
      );
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
        Object.assign(params, this.query, {
          status_equal: this.recycleMode ? -1 : 1,
        })
      ).then(res => {
        const data = res.data.data;
        this.page.total = data.total;
        this.data = data.records;
        this.loading = false;
        this.selectionClear();
      });
    },
    /**
     * 解析菜单ID为字符串数组
     * @param {string|Array} menuId - 菜单ID，可能是逗号分隔的字符串或数组
     * @returns {string[]} - 菜单ID字符串数组
     */
    parseMenuIds(menuId) {
      if (!menuId) return [];
      if (Array.isArray(menuId)) {
        return menuId.map(id => String(id).trim());
      }
      return menuId
        .split(',')
        .map(id => id.trim())
        .filter(Boolean);
    },
    /**
     * 递归筛选树节点，只保留选中的节点及其所有祖先节点
     * @param {Array} tree - 原始树数据
     * @param {Set<string>} selectedIdSet - 选中的节点ID集合
     * @returns {Array} - 筛选后的树数据
     */
    filterTreeBySelectedIds(tree, selectedIdSet) {
      if (!tree || !tree.length) return [];

      return tree.reduce((result, node) => {
        // 递归处理子节点
        const filteredChildren = this.filterTreeBySelectedIds(node.children, selectedIdSet);

        // 判断当前节点是否需要保留：自身被选中 或 有子节点被保留（作为祖先节点）
        const nodeId = String(node.id);
        const isSelected = selectedIdSet.has(nodeId);
        const hasRetainedChildren = filteredChildren.length > 0;

        if (isSelected || hasRetainedChildren) {
          result.push({
            ...node,
            children: hasRetainedChildren ? filteredChildren : undefined,
          });
        }

        return result;
      }, []);
    },
  },
};
</script>

<style></style>
