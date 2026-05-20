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
        <el-button type="primary" text icon="el-icon-setting" @click.stop="handleDesign(scope.row)"
          >设 计
        </el-button>
      </template>
    </avue-crud>
    <el-dialog
      title="可视化表单设计"
      v-model="designBox"
      :fullscreen="true"
      :before-close="handleDesignClose"
      append-to-body
    >
      <nf-form-design
        ref="formDesign"
        style="height: 90vh"
        :options="options"
        :toolbar="['clear', 'preview', 'import', 'generate']"
        :includeFields="[
          'group', // 分组
          'dynamic', // 子表单
          'title', // 标题
          'table', // 表格
          'input', // 输入框
          'password', // 密码
          'textarea', // 文本域
          'number', // 数字
          'ueditor', // 富文本
          'map', // 地图选择器
          'radio', // 单选
          'checkbox', // 多选
          'select', // 下拉选择
          'tree', // 树形选择
          'cascader', // 级联选择
          'table-select', // 表格选择
          'upload', // 上传
          'year', // 年
          'month', // 月
          'week', // 周
          'date', // 日期
          'time', // 时间
          'datetime', // 日期时间
          'daterange', // 日期范围
          'datetimerange', // 日期时间范围
          'timerange', // 时间范围
          'sign', // 签名
          'switch', // 开关
          'rate', // 评价
          'color', // 颜色
          'icon', // 图标
          'slider', // 滑块
        ]"
        :is-crud="isCrud"
      >
        <template #toolbar>
          <el-button
            style="padding: 0"
            text
            type="primary"
            size="default"
            icon="el-icon-download"
            @click="handleSubmit"
          >
            保存
          </el-button>
        </template>
      </nf-form-design>
    </el-dialog>
  </basic-container>
</template>

<script>
import { getList, getDetail, add, update, remove, getTablePrototype } from '@/api/tool/codesetting';
import option from '@/option/tool/formsetting';
import { mapGetters } from 'vuex';
import { validatenull } from '@/utils/validate';
import { getTableInfoByName, getTableList } from '@/api/tool/model';
import func from '@/utils/func';

export default {
  data() {
    return {
      form: {},
      query: {},
      search: {},
      loading: true,
      loadingOption: {
        lock: true,
        text: '物理表读取中',
        background: 'rgba(0, 0, 0, 0.7)',
      },
      designId: '',
      designBox: false,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      selectionList: [],
      option: option,
      data: [],
      modelTable: '',
      datasourceId: '',
      isCrud: true,
      options: {},
      // 默认不需要显示的字段名
      hideFields: [
        'id',
        'tenant_id',
        'create_user',
        'create_dept',
        'create_time',
        'update_user',
        'update_time',
        'status',
        'is_deleted',
      ],
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
  watch: {
    'form.datasourceId'() {
      if (!validatenull(this.form.datasourceId)) {
        this.datasourceId = this.form.datasourceId;
        const fullLoading = this.$loading(this.loadingOption);
        getTableList(this.form.datasourceId)
          .then(res => {
            const column = this.findObject(this.option.column, 'modelTable');
            column.dicData = res.data.data;
            fullLoading.close();
          })
          .catch(() => {
            fullLoading.close();
          });
      }
    },
    'form.modelTable'() {
      if (!validatenull(this.form.modelTable)) {
        this.modelTable = this.form.modelTable;
        const fullLoading = this.$loading(this.loadingOption);
        getTableInfoByName(this.form.modelTable, this.form.datasourceId)
          .then(res => {
            const result = res.data;
            if (result.success) {
              // 赋默认值
              const { comment } = result.data;
              this.form.name = comment;
              this.form.code = this.form.modelTable;
              fullLoading.close();
            }
          })
          .catch(() => {
            fullLoading.close();
          });
      }
    },
  },
  methods: {
    rowSave(row, done, loading) {
      const data = {
        name: row.name,
        code: row.code,
        category: 2,
        settings: row.settings,
      };
      add(data).then(
        res => {
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
          let design = {
            id: res.data.data.id,
            name: data.name,
            code: data.code,
          };
          this.handleDesign(design);
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
        settings: row.settings,
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
    handleDesign(design) {
      this.designBox = true;
      this.designId = design.id;
      getDetail(design.id).then(res => {
        const settings = res.data.data.settings;
        if (!validatenull(settings)) {
          this.options = JSON.parse(settings);
        } else if (!validatenull(this.modelTable) && !validatenull(this.datasourceId)) {
          getTablePrototype(this.modelTable, this.datasourceId).then(res => {
            const result = res.data;
            if (result.success) {
              const column = result.data
                .filter(field => {
                  return !validatenull(field.name) && !this.hideFields.includes(field.name);
                })
                .map(field => {
                  return {
                    type: 'input',
                    label: field.comment,
                    display: true,
                    prop: func.camelCaseString(field.name),
                  };
                });
              this.options = {
                column: column,
              };
              this.loading = false;
            }
          });
        }
      });
    },
    handleDesignClose() {
      this.modelTable = '';
      this.datasourceId = '';
      this.options = {};
      this.designBox = false;
    },
    // 可视化表单提交
    handleSubmit() {
      // json, string, app
      this.$refs.formDesign.getData('json').then(data => {
        // 表单/表格option
        console.log(data);
        const row = {
          id: this.designId,
          settings: JSON.stringify(data),
        };
        update(row).then(
          () => {
            this.onLoad(this.page);
            this.$message({
              type: 'success',
              message: '操作成功!',
            });
            this.designBox = false;
          },
          error => {
            console.log(error);
          }
        );
        this.$refs.formDesign.getChangeList().then(changeList => {
          // 字段prop, type, label修改记录
          console.log(changeList);
        });
      });
    },
    beforeOpen(done, type) {
      if (['add', 'edit'].includes(type)) {
      }
      if (['edit', 'view'].includes(type)) {
        getDetail(this.form.id).then(res => {
          this.form = {
            ...this.form,
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
        category: 2,
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
