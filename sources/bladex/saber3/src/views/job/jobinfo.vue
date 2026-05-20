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
        <el-button
          type="danger"
          icon="el-icon-delete"
          plain
          v-if="permission.jobinfo_delete"
          @click="handleDelete"
          >删 除
        </el-button>
        <el-button type="info" plain icon="el-icon-sort" @click="handleSync"
          >数 据 同 步
        </el-button>
      </template>
      <template #menu="scope">
        <el-button type="primary" text icon="el-icon-video-play" @click="handleRun(scope.row)"
          >运 行
        </el-button>
      </template>
      <template #enable="{ row }">
        <el-switch
          v-model="row.enable"
          inline-prompt
          @change="slotChange(row)"
          active-text="启用"
          inactive-text="暂停"
          :active-value="1"
          :inactive-value="0"
        />
      </template>
    </avue-crud>
  </basic-container>
</template>

<script>
import { getList, getDetail, add, update, remove, change, run, sync } from '@/api/job/jobinfo';
import option from '@/option/job/jobinfo';
import { mapGetters } from 'vuex';
import 'nprogress/nprogress.css';
import func from '@/utils/func';

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
    ...mapGetters(['permission']),
    permissionList() {
      return {
        addBtn: this.validData(this.permission.jobinfo_add, false),
        viewBtn: this.validData(this.permission.jobinfo_view, false),
        delBtn: this.validData(this.permission.jobinfo_delete, false),
        editBtn: this.validData(this.permission.jobinfo_edit, false),
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
    rowSave(row, done, loading) {
      if (func.isArrayAndNotEmpty(row.lifecycle)) {
        const lifecycleStart = row.lifecycle[0];
        const lifecycleEnd = row.lifecycle[1];
        if (!func.isUndefined(lifecycleStart) && !func.isUndefined(lifecycleEnd)) {
          row.lifecycle = lifecycleStart + ',' + lifecycleEnd;
        }
      } else {
        row.lifecycle = '';
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
      if (func.isArrayAndNotEmpty(row.lifecycle)) {
        const lifecycleStart = row.lifecycle[0];
        const lifecycleEnd = row.lifecycle[1];
        if (!func.isUndefined(lifecycleStart) && !func.isUndefined(lifecycleEnd)) {
          row.lifecycle = lifecycleStart + ',' + lifecycleEnd;
        }
      } else {
        row.lifecycle = '';
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
    handleSync() {
      this.$confirm('确定进行数据双向同步?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return sync();
        })
        .then(() => {
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
        });
    },
    handleRun(row) {
      this.$confirm('运行后将创建一个实例执行，是否继续?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return run(row);
        })
        .then(() => {
          this.$message({
            type: 'success',
            message: '运行成功!',
          });
        });
    },
    slotChange(row) {
      if (!row.id) {
        return;
      }
      change(row).then(
        () => {
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
        },
        error => {
          window.console.log(error);
        }
      );
    },
    beforeOpen(done, type) {
      if (['edit', 'view'].includes(type)) {
        getDetail(this.form.id).then(res => {
          this.form = res.data.data;
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
    onLoad(page) {
      this.loading = true;

      const { jobServerId, jobName } = this.query;

      let values = {
        jobServerId_like: jobServerId,
        jobName_like: jobName,
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
