<template>
  <basic-container>
    <avue-crud
      :option="option"
      :table-loading="loading"
      :data="data"
      ref="crud"
      v-model="form"
      :permission="permissionList"
      v-model:page="page"
      @row-del="rowDel"
      @row-update="rowUpdate"
      @row-save="rowSave"
      :before-open="beforeOpen"
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
          v-if="permission.code_delete"
          plain
          @click="handleDelete"
          >删 除
        </el-button>
        <el-tooltip
          class="item"
          effect="dark"
          content="将选中的多重配置集合批量生成代码"
          placement="top"
        >
          <el-button type="primary" plain icon="el-icon-refresh" @click="handleBuild"
            >代码批量生成
          </el-button>
        </el-tooltip>
        <el-tooltip
          class="item"
          effect="dark"
          content="不通过多重配置直接生成最简化的CRUD代码"
          placement="top"
        >
          <el-button type="info" plain icon="el-icon-cpu" @click="handleCodeGen"
            >代码快速生成
          </el-button>
        </el-tooltip>
      </template>
      <template #menu-right>
        <el-tooltip
          class="item"
          effect="dark"
          content="通过可视化表单设计器快速生成单表模块"
          placement="top"
        >
          <el-button
            plain
            v-if="userInfo.authority.includes('administrator')"
            :loading="!componentLoaded"
            icon="el-icon-connection"
            @click="handleFormSetting"
            >可视化表单设计器
          </el-button>
        </el-tooltip>
        <el-tooltip
          class="item"
          effect="dark"
          content="配置代码生成的默认参数后自动绑定"
          placement="top"
        >
          <el-button
            plain
            v-if="userInfo.authority.includes('administrator')"
            icon="el-icon-message-box"
            @click="handleCodeSetting"
            >默认配置管理
          </el-button>
        </el-tooltip>
      </template>
      <template #menu="scope">
        <el-button
          type="primary"
          text
          icon="el-icon-document-copy"
          v-if="permission.code_edit"
          class="none-border"
          @click.stop="handleCopy(scope.row)"
          >复制
        </el-button>
        <el-button
          type="primary"
          text
          icon="el-icon-refresh"
          v-if="userInfo.authority.includes('administrator')"
          class="none-border"
          @click.stop="handleBuildSingle(scope.row)"
          >生成
        </el-button>
      </template>
    </avue-crud>
    <el-drawer title="代码快速生成" append-to-body v-model="codeGenBox" direction="rtl" size="50%">
      <avue-form :option="genOption" v-model="genForm" @submit="handleCodeGenSubmit" />
    </el-drawer>
    <el-drawer
      title="可视化表单管理"
      append-to-body
      v-model="formSettingBox"
      direction="rtl"
      size="1000px"
    >
      <form-setting></form-setting>
    </el-drawer>
    <el-drawer
      title="默认配置管理"
      append-to-body
      v-model="codeSettingBox"
      direction="rtl"
      size="1000px"
    >
      <code-setting></code-setting>
    </el-drawer>
  </basic-container>
</template>

<script>
import { getList, getCode, build, remove, add, update, copy, buildFast } from '@/api/tool/code';
import { getEnableDetail, getTableForm } from '@/api/tool/codesetting';
import {
  getDetail as modelDetail,
  getTableInfoByName,
  getTableList,
  prototypeDetail,
} from '@/api/tool/model';
import { codeOption, genOption } from '@/option/tool/code';
import { validatejson, validatenull } from '@/utils/validate';
import { mapGetters } from 'vuex';
import { getMenuTree } from '@/api/system/menu';
import { loadFormDesignModule, loadFormModule } from '@/utils/module';

export default {
  data() {
    return {
      form: {},
      genForm: {},
      selectionList: [],
      componentLoaded: false,
      loading: true,
      loadingOption: {
        lock: true,
        text: '物理表读取中',
        background: 'rgba(0, 0, 0, 0.7)',
      },
      addMode: false,
      codeGenBox: false,
      codeSettingBox: false,
      codeSetting: {},
      formSettingBox: false,
      query: {},
      page: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
      option: codeOption,
      genOption: genOption,
      data: [],
    };
  },
  created() {
    // 懒加载表单设计器模块
    Promise.all([loadFormModule(this.$app), loadFormDesignModule(this.$app)]).then(() => {
      this.componentLoaded = true;
    });
  },
  watch: {
    'form.modelId'() {
      if (!validatenull(this.form.modelId) && this.addMode) {
        // 获取数据模型信息
        modelDetail(this.form.modelId).then(res => {
          const result = res.data;
          if (result.success) {
            const { modelName, modelTable, modelCode } = result.data;
            const lowerModelCode = modelCode.toLowerCase();
            //if (validatenull(this.form.tablePrefix)) {
            this.form.tablePrefix = modelTable.split('_')[0] + '_';
            //}
            //if (validatenull(this.form.tableName)) {
            this.form.tableName = modelTable;
            //}
            //if (validatenull(this.form.codeName)) {
            this.form.codeName = modelName;
            //}
            if (validatenull(this.form.serviceName)) {
              this.form.serviceName = `blade-${lowerModelCode}`;
            }
            //if (validatenull(this.form.pkName)) {
            this.form.pkName = 'id';
            //}
            if (validatenull(this.form.packageName)) {
              this.form.packageName = `org.springblade.${lowerModelCode}`;
            }
            //if (validatenull(this.form.subFkId) && !validatenull(this.form.tablePrefix)) {
            this.form.subFkId = modelTable.replace(this.form.tablePrefix, '') + '_id';
            //}

            // 获取数据原型信息
            prototypeDetail(this.form.modelId).then(res => {
              const result = res.data;
              if (result.success) {
                const columnTreeId = this.findObject(this.option.group, 'treeId');
                const columnTreePid = this.findObject(this.option.group, 'treePid');
                const columnTreeName = this.findObject(this.option.group, 'treeName');
                columnTreeId.dicData = result.data;
                columnTreePid.dicData = result.data;
                columnTreeName.dicData = result.data;
              }
            });
          }
        });
      }
    },
    'form.templateType'() {
      // 模版类型
      const type = this.form.templateType;

      // 主子表字段显隐
      const columnSubModelId = this.findObject(this.option.group, 'subModelId');
      const columnSubFkId = this.findObject(this.option.group, 'subFkId');
      columnSubModelId.display = type === 'sub';
      columnSubFkId.display = type === 'sub';

      // 树表字段显隐
      const columnTreeId = this.findObject(this.option.group, 'treeId');
      const columnTreePid = this.findObject(this.option.group, 'treePid');
      const columnTreeName = this.findObject(this.option.group, 'treeName');
      columnTreeId.display = type === 'tree';
      columnTreePid.display = type === 'tree';
      columnTreeName.display = type === 'tree';
    },
    'genForm.datasourceId'() {
      if (!validatenull(this.genForm.datasourceId)) {
        const fullLoading = this.$loading(this.loadingOption);
        getTableList(this.genForm.datasourceId)
          .then(res => {
            const column = this.findObject(this.genOption.column, 'modelTable');
            column.dicData = res.data.data;
            fullLoading.close();
          })
          .catch(() => {
            fullLoading.close();
          });
      }
    },
    'genForm.modelTable'() {
      if (!validatenull(this.genForm.modelTable)) {
        const fullLoading = this.$loading(this.loadingOption);
        getTableInfoByName(this.genForm.modelTable, this.genForm.datasourceId)
          .then(res => {
            const result = res.data;
            if (result.success) {
              // 赋默认值
              const { comment, entityName } = result.data;
              this.genForm.modelClass = entityName;
              this.genForm.modelCode = entityName.replace(/^\S/, s => s.toLowerCase());
              const lowerModelCode = this.genForm.modelCode.toLowerCase();
              this.genForm.tablePrefix = this.genForm.modelTable.split('_')[0] + '_';
              this.genForm.tableName = this.genForm.modelTable;
              this.genForm.codeName = comment;
              this.genForm.pkName = 'id';
              if (validatenull(this.genForm.serviceName)) {
                this.genForm.serviceName = `blade-${lowerModelCode}`;
              }
              if (validatenull(this.genForm.packageName)) {
                this.genForm.packageName = `org.springblade.${lowerModelCode}`;
              }
              // 字段显隐
              const columnModelForm = this.findObject(this.genOption.column, 'modelForm');
              const columnModelClass = this.findObject(this.genOption.column, 'modelClass');
              const columnModelCode = this.findObject(this.genOption.column, 'modelCode');
              const columnTablePrefix = this.findObject(this.genOption.column, 'tablePrefix');
              const columnTableName = this.findObject(this.genOption.column, 'tableName');
              const columnCodeName = this.findObject(this.genOption.column, 'codeName');
              const columnPkName = this.findObject(this.genOption.column, 'pkName');
              this.genForm.modelForm = '';
              getTableForm(this.genForm.modelTable).then(res => {
                columnModelForm.dicData = res.data.data;
              });
              columnModelForm.display = true;
              columnModelClass.display = true;
              columnModelCode.display = true;
              columnTablePrefix.display = true;
              columnTableName.display = true;
              columnCodeName.display = true;
              columnPkName.display = true;
              fullLoading.close();
            }
          })
          .catch(() => {
            fullLoading.close();
          });
      }
    },
  },
  computed: {
    ...mapGetters(['userInfo', 'permission']),
    permissionList() {
      return {
        addBtn: this.validData(this.permission.code_add, false),
        viewBtn: this.validData(this.permission.code_view, false),
        delBtn: this.validData(this.permission.code_delete, false),
        editBtn: this.validData(this.permission.code_edit, false),
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
      return getMenuTree().then(res => {
        const column = this.findObject(this.option.group, 'menuId');
        column.dicData = res.data.data;
      });
    },
    initGenData() {
      getMenuTree().then(res => {
        const column = this.findObject(this.genOption.column, 'menuId');
        column.dicData = res.data.data;
      });
      getEnableDetail().then(res => {
        if (validatejson(res.data.data.settings)) {
          this.codeSetting = JSON.parse(res.data.data.settings);
          this.genForm = {
            menuId: this.codeSetting.menuId,
            serviceName: this.codeSetting.serviceName,
            packageName: this.codeSetting.packageName,
            baseMode: this.codeSetting.baseMode,
            wrapMode: this.codeSetting.wrapMode,
            feignMode: this.codeSetting.feignMode,
            codeStyle: this.codeSetting.codeStyle,
            apiPath: this.codeSetting.apiPath,
            webPath: this.codeSetting.webPath,
          };
          this.$message({
            type: 'success',
            message: '默认配置加载成功',
          });
        }
      });
    },
    initSetting() {
      return getEnableDetail().then(res => {
        if (validatejson(res.data.data.settings)) {
          this.codeSetting = JSON.parse(res.data.data.settings);
          this.form = {
            menuId: this.codeSetting.menuId,
            serviceName: this.codeSetting.serviceName,
            packageName: this.codeSetting.packageName,
            baseMode: this.codeSetting.baseMode,
            wrapMode: this.codeSetting.wrapMode,
            feignMode: this.codeSetting.feignMode,
            codeStyle: this.codeSetting.codeStyle,
            apiPath: this.codeSetting.apiPath,
            webPath: this.codeSetting.webPath,
          };
          this.$message({
            type: 'success',
            message: '默认配置加载成功',
          });
        }
      });
    },
    initCode(id) {
      return getCode(id).then(res => {
        this.form = res.data.data;
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
    handleCodeGen() {
      this.initGenData();
      this.codeGenBox = true;
    },
    handleFormSetting() {
      this.formSettingBox = true;
    },
    handleCodeSetting() {
      this.codeSettingBox = true;
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
    handleBuild() {
      if (this.selectionList.length === 0) {
        this.$message.warning('请选择至少一条数据');
        return;
      }
      this.$confirm('是否生成选中模块的代码?', {
        title: '代码生成确认',
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        return build(this.ids).then(() => {
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
          this.$refs.crud.toggleSelection();
        });
      });
    },
    handleBuildSingle(row) {
      this.$confirm(`是否生成选中模块 [${row.codeName}] 的代码?`, {
        title: '代码生成确认',
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        return build(row.id).then(() => {
          this.onLoad(this.page);
          this.$message({
            type: 'success',
            message: '操作成功!',
          });
          this.$refs.crud.toggleSelection();
        });
      });
    },
    handleCodeGenSubmit(form, done) {
      buildFast(form).then(
        () => {
          this.$message({
            type: 'success',
            message: '代码生成成功!',
          });
          done();
          this.codeGenBox = false;
        },
        error => {
          window.console.log(error);
          done();
        }
      );
    },
    handleCopy(row) {
      copy(row.id).then(() => {
        this.onLoad(this.page);
        this.$message({
          type: 'success',
          message: '复制成功!',
        });
      });
    },
    async beforeOpen(done, type) {
      const tasks = [];

      if (['add'].includes(type)) {
        this.addMode = true;
        tasks.push(this.initSetting());
      }

      if (['add', 'edit'].includes(type)) {
        tasks.push(this.initData());
      }

      if (['edit', 'view'].includes(type)) {
        this.addMode = false;
        tasks.push(this.initCode(this.form.id));
      }

      await Promise.all(tasks);
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
