<template>
  <basic-container>
    <avue-crud
      :option="option"
      :table-loading="loading"
      :data="data"
      v-model:page="page"
      :permission="permissionList"
      :before-open="beforeOpen"
      v-model="form"
      ref="crud"
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
          type="primary"
          plain
          v-if="permission.attach_upload"
          icon="el-icon-upload"
          @click="handleUpload"
          >上 传
        </el-button>
        <el-button
          type="danger"
          icon="el-icon-delete"
          plain
          v-if="permission.attach_delete"
          @click="handleDelete"
          >删 除
        </el-button>
      </template>
      <template #menu="scope">
        <el-button
          type="primary"
          text
          icon="el-icon-download"
          v-if="permission.attach_download"
          @click="handleDownload(scope.row)"
          >下载
        </el-button>
      </template>
      <template #attachSize="{ row }">
        <el-tag>{{ formatFileSize(row.attachSize) }}</el-tag>
      </template>
    </avue-crud>
    <el-dialog title="附件管理" append-to-body v-model="attachBox" width="555px">
      <el-upload
        drag
        action="/api/blade-resource/oss/endpoint/put-file-attach"
        :show-file-list="false"
        :headers="uploadHeaders"
        :on-progress="handleUploadProgress"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">拖动文件上传 <em>或者点击上传</em></div>
      </el-upload>
      <el-progress
        v-if="uploading"
        :percentage="uploadPercent"
        :indeterminate="uploadPercent === 100"
        :format="progressFormat"
        style="margin-top: 16px"
      />
    </el-dialog>
  </basic-container>
</template>

<script>
import { getList, getDetail, remove } from '@/api/resource/attach';
import { mapGetters } from 'vuex';
import { getToken } from '@/utils/auth';
import { UploadFilled } from '@element-plus/icons-vue';
import func from '@/utils/func';

export default {
  components: {
    UploadFilled,
  },
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
      attachBox: false,
      uploading: false,
      uploadPercent: 0,
      selectionList: [],
      uploadHeaders: {
        'Blade-Auth': `bearer ${getToken()}`,
        'Blade-Requested-With': 'BladeHttpRequest',
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
        grid: true,
        selection: true,
        dialogClickModal: false,
        column: [
          {
            label: '附件地址',
            prop: 'link',
            rules: [
              {
                required: true,
                message: '请输入附件地址',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '附件域名',
            prop: 'domainUrl',
            search: true,
            rules: [
              {
                required: true,
                message: '请输入附件域名',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '附件名称',
            prop: 'name',
            search: true,
            rules: [
              {
                required: true,
                message: '请输入附件名称',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '附件原名',
            prop: 'originalName',
            search: true,
            rules: [
              {
                required: true,
                message: '请输入附件原名',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '附件拓展名',
            prop: 'extension',
            rules: [
              {
                required: true,
                message: '请输入附件拓展名',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '附件大小',
            prop: 'attachSize',
            slot: true,
            rules: [
              {
                required: true,
                message: '请输入附件大小',
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
    func() {
      return func;
    },
    ...mapGetters(['permission']),
    permissionList() {
      return {
        addBtn: false,
        editBtn: false,
        viewBtn: false,
        delBtn: this.validData(this.permission.attach_delete, false),
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
    formatFileSize(bytes) {
      if (!bytes || bytes === 0) return '0 KB';
      const mb = bytes / (1024 * 1024);
      if (mb >= 1) {
        return `${mb.toFixed(2)} MB`;
      }
      const kb = bytes / 1024;
      return `${kb.toFixed(2)} KB`;
    },
    progressFormat(percentage) {
      return percentage === 100 ? '服务器处理中' : `${percentage}%`;
    },
    handleUpload() {
      this.attachBox = true;
      this.uploading = false;
      this.uploadPercent = 0;
    },
    handleUploadProgress(event) {
      this.uploading = true;
      this.uploadPercent = Math.round(event.percent) || 0;
    },
    handleUploadSuccess(response) {
      this.uploading = false;
      this.uploadPercent = 0;
      if (response.success) {
        this.$message.success('上传成功');
        this.attachBox = false;
        this.refreshChange();
      } else {
        this.$message.error(response.msg || '上传失败');
      }
    },
    handleUploadError() {
      this.uploading = false;
      this.uploadPercent = 0;
      this.$message.error('上传失败');
    },
    handleDownload(row) {
      window.open(`${row.link}`);
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
