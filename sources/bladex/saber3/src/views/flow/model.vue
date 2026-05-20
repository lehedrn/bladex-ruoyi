<template>
  <basic-container>
    <avue-crud
      :option="option"
      :table-loading="loading"
      :data="data"
      ref="crud"
      v-model="form"
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
          type="primary"
          size="default"
          icon="el-icon-circle-plus"
          v-if="permission.flow_model_create"
          plain
          @click="handleCreate"
          >创 建
        </el-button>
        <el-button
          type="danger"
          size="default"
          icon="el-icon-delete"
          v-if="permission.flow_model_delete"
          plain
          @click="handleDelete"
          >删 除
        </el-button>
      </template>
      <template #menu="scope">
        <el-button
          type="primary"
          text
          size="default"
          icon="el-icon-setting"
          v-if="permission.flow_model_update"
          @click.stop="handleUpdate(scope.row, scope.index)"
          >配置
        </el-button>
        <el-button
          type="primary"
          text
          size="default"
          icon="el-icon-upload"
          v-if="permission.flow_model_deploy"
          @click.stop="handleDeploy(scope.row, scope.index)"
          >部署
        </el-button>
        <el-button
          type="primary"
          text
          size="default"
          icon="el-icon-delete"
          v-if="permission.flow_model_delete"
          @click.stop="handleSlotDelete(scope.row, scope.index)"
          >删除
        </el-button>
      </template>
      <template #version="{ row }">
        <el-tag>v{{ row.version }}</el-tag>
      </template>
    </avue-crud>
    <flow-design-step ref="bpmn" @loadData="onLoad"></flow-design-step>
    <el-dialog
      v-if="!this.website.design.designMode"
      title="流程配置"
      append-to-body
      v-model="flowBox"
      :fullscreen="true"
    >
      <iframe
        :src="flowUrl"
        width="100%"
        height="700"
        title="流程设计器"
        frameBorder="no"
        border="0"
        marginWidth="0"
        marginHeight="0"
        scrolling="no"
        allowTransparency="yes"
      >
      </iframe>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="flowBox = false">取 消</el-button>
          <el-button type="primary" @click="handleRefresh">确 定</el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog title="流程部署" append-to-body v-model="deployBox" width="20%">
      <avue-form ref="form" :option="optionDeploy" v-model="form" @submit="handleSubmit" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deployBox = false">取 消</el-button>
          <el-button type="primary" @click="handleDoDeploy" :loading="deployLoading"
            >确 定</el-button
          >
        </span>
      </template>
    </el-dialog>
  </basic-container>
</template>

<script>
import { mapGetters } from 'vuex';
import { modelList, removeModel, deployModel, detail } from '@/api/flow/flow';
import { flowCategory } from '@/utils/flow';

export default {
  data() {
    return {
      form: {},
      optionDeploy: {
        menuBtn: false,
        column: [
          {
            label: '流程类型',
            type: 'select',
            dicUrl: '/blade-system/dict/dictionary?code=flow',
            props: {
              label: 'dictValue',
              value: 'dictKey',
            },
            dataType: 'number',
            slot: true,
            prop: 'categoryValue',
            search: true,
            span: 24,
            rules: [
              {
                required: true,
                message: '请选择流程类型',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '流程模式',
            prop: 'flowMode',
            type: 'radio',
            dicData: [
              {
                label: '通用流程',
                value: 1,
              },
              {
                label: '定制流程',
                value: 2,
              },
            ],
            value: 1,
            span: 24,
            rules: [
              {
                required: true,
                message: '请选择流程模式',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '所属租户',
            prop: 'tenantId',
            type: 'tree',
            multiple: true,
            dicUrl: '/blade-system/tenant/select',
            props: {
              label: 'tenantName',
              value: 'tenantId',
            },
            display: false,
            span: 24,
            rules: [
              {
                required: true,
                message: '请选择所属租户',
                trigger: 'blur',
              },
            ],
          },
        ],
      },
      selectionId: '',
      selectionList: [],
      query: {},
      loading: true,
      deployLoading: false,
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      deployBox: false,
      flowBox: false,
      flowUrl: '',
      option: {
        height: 'auto',
        calcHeight: 32,
        tip: false,
        searchShow: true,
        searchMenuSpan: 6,
        border: true,
        index: true,
        selection: true,
        editBtn: false,
        addBtn: false,
        viewBtn: false,
        delBtn: false,
        dialogWidth: 900,
        menuWidth: 220,
        dialogClickModal: false,
        size: 'default',
        searchSize: 'default',
        column: [
          {
            label: '模型主键',
            prop: 'id',
          },
          {
            label: '模型标识',
            prop: 'modelKey',
            search: true,
            overHidden: true,
          },
          {
            label: '模型名称',
            prop: 'name',
            search: true,
            overHidden: true,
          },
          {
            label: '流程版本',
            prop: 'version',
            slot: true,
            overHidden: true,
          },
          {
            label: '创建时间',
            prop: 'created',
            overHidden: true,
          },
          {
            label: '更新时间',
            prop: 'lastUpdated',
            overHidden: true,
          },
        ],
      },
      data: [],
    };
  },
  watch: {
    'form.flowMode'() {
      this.$refs.form.option.column.filter(item => {
        if (item.prop === 'tenantId') {
          item.display = this.form.flowMode === 2;
        }
      });
    },
  },
  computed: {
    ...mapGetters(['permission']),
    ids() {
      let ids = [];
      this.selectionList.forEach(ele => {
        ids.push(ele.id);
      });
      return ids.join(',');
    },
  },
  methods: {
    handleSubmit(form, done) {
      this.deployLoading = true;
      deployModel({
        modelId: this.selectionId,
        category: flowCategory(form.categoryValue),
        tenantIds: form.tenantId ? form.tenantId.join(',') : '',
      }).then(res => {
        const data = res.data;
        if (data.success) {
          this.$message({
            type: 'success',
            message: data.msg,
          });
          done();
          this.$refs.form.resetForm();
          this.deployBox = false;
          this.deployLoading = false;
        } else {
          done();
          this.deployLoading = false;
          this.$message({
            type: 'warn',
            message: data.msg,
          });
        }
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
          return removeModel(this.ids);
        })
        .then(() => {
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
          this.$refs.crud.toggleSelection();
          this.onLoad(this.page);
        });
    },
    handleCreate() {
      if (!this.website.design.designMode) {
        this.flowUrl = `${this.website.design.designUrl}/index.html`;
        this.flowBox = true;
      } else {
        this.$refs.bpmn.visible = true;
      }
    },
    handleRefresh() {
      this.flowBox = false;
      this.onLoad(this.page);
    },
    handleUpdate(row) {
      if (this.website.design.designMode) {
        detail({ id: row.id }).then(res => {
          const data = res.data.data;
          const { modelEditorXml } = data;
          this.$refs.bpmn.nutflowOption.step1.xml = modelEditorXml;
          this.$refs.bpmn.nutflowOption.process = data;
          this.$refs.bpmn.visible = true;
        });
      } else {
        this.flowUrl = `${this.website.design.designUrl}/index.html#/editor/${row.id}`;
        this.flowBox = true;
      }
    },
    handleDeploy(row) {
      this.deployBox = true;
      this.selectionId = row.id;
    },
    handleDoDeploy() {
      this.$refs.form.submit();
    },
    handleSlotDelete(row) {
      this.$confirm('确定将选择数据删除?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          return removeModel(row.id);
        })
        .then(() => {
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
          this.$refs.crud.toggleSelection();
          this.onLoad(this.page);
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
      page = page || this.page;
      modelList(page.currentPage, page.pageSize, Object.assign(params, this.query)).then(res => {
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

<style lang="scss">
.nf-dialog {
  .el-dialog__body {
    padding: 5px;
  }

  .avue-dialog__footer {
    text-align: center;
  }
}
</style>
