<template>
  <avue-crud
    :option="option"
    :table-loading="loading"
    :data="data"
    v-model:page="page"
    :permission="permissionList"
    :before-open="beforeOpen"
    v-model="form"
    ref="crud"
    @row-update="rowUpdate"
    @row-save="rowSave"
    @row-del="rowDel"
    @search-change="searchChange"
    @search-reset="searchReset"
    @selection-change="selectionChange"
    @current-change="currentChange"
    @size-change="sizeChange"
    @refresh-change="refreshChange"
    @on-load="onLoad"
  >
    <template #menu-left>
      <el-button type="danger" icon="el-icon-delete" plain @click="handleDelete">删 除</el-button>
    </template>
    <template #menuId-form>
      <el-tree
        ref="menuTree"
        :data="menuTreeData"
        :props="menuTreeProps"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedMenuKeys"
        :default-expand-all="false"
        :check-strictly="false"
        @check="handleMenuCheck"
      />
    </template>
  </avue-crud>
</template>

<script>
import { getList, getDetail, add, update, remove } from '@/api/system/tenantpackage';
import { mapGetters } from 'vuex';
import { getMenuTree } from '@/api/system/menu';

export default {
  name: 'tenantPackage',
  data() {
    return {
      form: {},
      query: {},
      loading: true,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      selectionList: [],
      menuTreeData: [],
      checkedMenuKeys: [],
      menuTreeProps: {
        label: 'title',
        value: 'key',
        children: 'children',
      },
      option: {
        height: 'auto',
        calcHeight: 32,
        tip: false,
        searchShow: true,
        searchMenuSpan: 6,
        border: true,
        index: true,
        viewBtn: true,
        selection: true,
        dialogClickModal: false,
        dialogWidth: 600,
        column: [
          {
            label: '产品包名',
            prop: 'packageName',
            search: true,
            span: 24,
            rules: [
              {
                required: true,
                message: '请输入产品包名称',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '菜单列表',
            prop: 'menuId',
            span: 24,
            hide: true,
            formslot: true,
            rules: [
              {
                required: true,
                message: '请选择菜单',
                trigger: 'change',
              },
            ],
          },
          {
            label: '备注',
            prop: 'remark',
            span: 24,
          },
        ],
      },
      data: [],
    };
  },
  computed: {
    ...mapGetters(['permission']),
    permissionList() {
      return {
        addBtn: true,
        viewBtn: false,
        delBtn: true,
        editBtn: true,
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
  methods: {
    initData() {
      getMenuTree().then(res => {
        this.menuTreeData = res.data.data;
      });
    },
    handleMenuCheck(node, data) {
      // 只保存全选的节点，不保存半选的节点
      // 恢复时半选状态会根据子节点自动计算
      this.form.menuId = data.checkedKeys;
    },
    rowSave(row, done, loading) {
      // 将 menuId 数组转为逗号分隔的字符串
      if (Array.isArray(row.menuId)) {
        row.menuId = row.menuId.join(',');
      }
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
          loading();
          window.console.log(error);
        }
      );
    },
    rowUpdate(row, index, done, loading) {
      // 将 menuId 数组转为逗号分隔的字符串
      if (Array.isArray(row.menuId)) {
        row.menuId = row.menuId.join(',');
      }
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
          loading();
          window.console.log(error);
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
    beforeOpen(done, type) {
      // 重置选中状态和树数据
      this.checkedMenuKeys = [];
      this.menuTreeData = [];
      if (['add', 'edit'].includes(type)) {
        this.initData();
      }
      if (['edit'].includes(type)) {
        getDetail(this.form.id).then(res => {
          this.form = res.data.data;
          // 设置选中的菜单
          if (this.form.menuId) {
            this.checkedMenuKeys = Array.isArray(this.form.menuId)
              ? this.form.menuId
              : this.form.menuId.split(',').map(id => id.trim());
          }
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
        const data = res.data.data;
        this.page.total = data.total;
        this.data = data.records;
        this.loading = false;
        this.selectionClear();
      });
    },
  },
};
</script>

<style></style>
