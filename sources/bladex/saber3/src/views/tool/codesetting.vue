<template>
  <basic-container>
    <avue-crud
      :option="option"
      v-model:search="search"
      v-model:page="page"
      v-model="form"
      :table-loading="loading"
      :data="data"
      :permission="permissionList"
      :before-open="beforeOpen"
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
        <el-button type="danger" icon="el-icon-delete" plain @click="handleDelete"
          >删 除
        </el-button>
      </template>
      <template #menu="scope">
        <el-button
          type="primary"
          text
          icon="el-icon-circle-check"
          @click.stop="handleEnable(scope.row)"
          >启 用
        </el-button>
      </template>
      <template #status="{ row }">
        <el-tag>{{ row.status === 1 ? '否' : '是' }}</el-tag>
      </template>
    </avue-crud>
  </basic-container>
</template>

<script>
import { getList, getDetail, add, update, remove, enable } from '@/api/tool/codesetting';
import { getMenuTree } from '@/api/system/menu';
import option from '@/option/tool/codesetting';
import { mapGetters } from 'vuex';

export default {
  data() {
    return {
      form: {},
      query: {},
      search: {},
      loading: true,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      selectionList: [],
      option: option,
      data: [],
    };
  },
  computed: {
    ...mapGetters(['userInfo', 'permission']),
    permissionList() {
      return {
        addBtn: true,
        viewBtn: true,
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
        const column = this.findObject(this.option.column, 'menuId');
        column.dicData = res.data.data;
      });
    },
    rowSettings(row) {
      return JSON.stringify({
        serviceName: row.serviceName,
        packageName: row.packageName,
        menuId: row.menuId,
        baseMode: row.baseMode,
        wrapMode: row.wrapMode,
        feignMode: row.feignMode,
        codeStyle: row.codeStyle,
        apiPath: row.apiPath,
        webPath: row.webPath,
      });
    },
    rowSettingsForm(data) {
      const row = JSON.parse(data);
      return {
        serviceName: row.serviceName,
        packageName: row.packageName,
        menuId: row.menuId,
        baseMode: row.baseMode,
        wrapMode: row.wrapMode,
        feignMode: row.feignMode,
        codeStyle: row.codeStyle,
        apiPath: row.apiPath,
        webPath: row.webPath,
      };
    },
    rowSave(row, done, loading) {
      const data = {
        name: row.name,
        code: row.code,
        settings: this.rowSettings(row),
      };
      add(data).then(
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
      const data = {
        id: row.id,
        name: row.name,
        code: row.code,
        settings: this.rowSettings(row),
      };
      update(data).then(
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
          console.log(error);
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
    handleEnable(row) {
      this.$confirm('是否确定启用这条配置?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return enable(row.id);
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
      if (['add', 'edit'].includes(type)) {
        this.initData();
      }
      if (['edit', 'view'].includes(type)) {
        getDetail(this.form.id).then(res => {
          this.form = {
            ...this.form,
            ...this.rowSettingsForm(res.data.data.settings),
          };
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

      let values = {
        ...params,
        ...this.query,
        category: 1,
      };

      getList(page.currentPage, page.pageSize, values).then(res => {
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
