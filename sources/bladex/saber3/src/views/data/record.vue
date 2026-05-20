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
      v-model:page="page"
      @search-change="searchChange"
      @search-reset="searchReset"
      @current-change="currentChange"
      @size-change="sizeChange"
      @refresh-change="refreshChange"
      @on-load="onLoad"
    >
      <template #userAgent-form="{}">
        <code-editor v-model="form.userAgent" :readonly="true" theme="nord" height="50px" />
      </template>
      <template #oldData-form="{}">
        <code-editor
          v-model="form.oldData"
          :json="true"
          :readonly="true"
          theme="nord"
          height="200px"
        />
      </template>
      <template #newData-form="{}">
        <code-editor
          v-model="form.newData"
          :json="true"
          :readonly="true"
          theme="nord"
          height="200px"
        />
      </template>
      <template #recordResult-form="{}">
        <code-editor
          v-model="form.recordResult"
          :json="true"
          :readonly="true"
          theme="nord"
          height="350px"
        />
      </template>
      <template #recordMessage-form="{}">
        <code-editor v-model="form.recordMessage" :readonly="true" theme="nord" height="350px" />
      </template>
      <template #env="{ row }">
        <el-tag>{{ row.env }}</el-tag>
      </template>
      <template #tableName="{ row }">
        <el-tag>{{ row.tableName }}</el-tag>
      </template>
      <template #method="{ row }">
        <el-tag>{{ row.method }}</el-tag>
      </template>
      <template #recordLevel="{ row }">
        <el-tag>{{ row.recordLevel }}</el-tag>
      </template>
      <template #remoteIp="{ row }">
        <el-tag>{{ row.remoteIp }}</el-tag>
      </template>
    </avue-crud>
  </basic-container>
</template>

<script>
import { getRecordList, getRecordDetail } from '@/api/data/record';
import { mapGetters } from 'vuex';

export default {
  data() {
    return {
      form: {},
      selectionList: [],
      query: {},
      loading: true,
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
        searchIndex: 3,
        searchIcon: true,
        border: true,
        index: true,
        viewBtn: true,
        editBtn: false,
        addBtn: false,
        delBtn: false,
        menuWidth: 120,
        labelWidth: 120,
        dialogType: 'drawer',
        column: [
          {
            label: '服务ID',
            prop: 'serviceId',
            width: '120',
            search: true,
          },
          {
            label: '服务器名',
            prop: 'serverHost',
            width: '140',
            search: true,
          },
          {
            label: '服务器IP',
            prop: 'serverIp',
            width: '180',
            search: true,
          },
          {
            label: '环境',
            prop: 'env',
            width: '80',
            search: true,
            slot: true,
          },
          {
            label: '审计级别',
            prop: 'recordLevel',
            width: '100',
            search: true,
            slot: true,
          },
          {
            label: '数据表名',
            prop: 'tableName',
            width: '150',
            search: true,
            slot: true,
          },
          {
            label: '操作方式',
            prop: 'method',
            width: '100',
            slot: true,
          },
          {
            label: '请求URI',
            prop: 'requestUri',
            search: true,
            overHidden: true,
          },
          {
            label: '操作类型',
            prop: 'operation',
            width: '100',
            hide: true,
          },
          {
            label: '操作IP',
            prop: 'remoteIp',
            width: '140',
            slot: true,
          },
          {
            label: '记录人',
            prop: 'recordUserName',
            width: '120',
            hide: true,
          },
          {
            label: '记录耗时',
            prop: 'recordCost',
            width: '100',
            suffix: 'ms',
            hide: true,
          },
          {
            label: '记录时间',
            prop: 'recordTime',
            width: '180',
            type: 'datetime',
            format: 'YYYY-MM-DD HH:mm:ss',
            valueFormat: 'YYYY-MM-DD HH:mm:ss',
            span: 24,
          },
          {
            label: '用户代理',
            prop: 'userAgent',
            span: 24,
            hide: true,
          },
          {
            label: '操作前数据',
            prop: 'oldData',
            type: 'textarea',
            span: 24,
            minRows: 3,
            hide: true,
          },
          {
            label: '操作后数据',
            prop: 'newData',
            type: 'textarea',
            span: 24,
            minRows: 3,
            hide: true,
          },
          {
            label: '审计结果',
            prop: 'recordResult',
            type: 'textarea',
            span: 24,
            minRows: 3,
            hide: true,
          },
          {
            label: '审计消息',
            prop: 'recordMessage',
            type: 'textarea',
            span: 24,
            minRows: 2,
            hide: true,
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
        viewBtn: this.validData(this.permission.record_view, false),
      };
    },
  },

  methods: {
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
    beforeOpen(done, type) {
      if (['edit', 'view'].includes(type)) {
        getRecordDetail(this.form.id).then(res => {
          this.form = res.data.data;
        });
      }
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
      getRecordList(page.currentPage, page.pageSize, Object.assign(params, this.query))
        .then(res => {
          const data = res.data.data;
          this.page.total = data.total;
          this.data = data.records;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
  },
};
</script>

<style scoped>
/* 可以根据需要添加自定义样式 */
</style>
