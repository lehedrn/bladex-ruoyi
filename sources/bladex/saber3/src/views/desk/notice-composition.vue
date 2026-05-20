<template>
  <basic-container>
    <avue-crud
      :option="option"
      :table-loading="loading"
      :data="data"
      v-model:page="page"
      v-model:search="search"
      ref="crud"
      @row-del="rowDel"
      v-model="form"
      :permission="permissionList"
      @row-update="rowUpdate"
      @row-save="rowSave"
      :before-open="beforeOpen"
      @search-change="searchChange"
      @search-reset="searchReset"
      @selection-change="selectionChange"
      @refresh-change="refreshChange"
      @on-load="onLoad"
    >
      <template #menu-left>
        <el-button
          type="danger"
          icon="el-icon-delete"
          plain
          v-if="permission.notice_delete"
          @click="handleDelete"
          >删 除
        </el-button>
      </template>
      <template #category="{ row }">
        <el-tag>{{ row.categoryName }}</el-tag>
      </template>
    </avue-crud>
  </basic-container>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus';
import { ref, reactive, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { getList, remove, update, add, getNotice } from '@/api/desk/notice';

// 定义响应式数据
const form = ref({});
const search = ref({});
const loading = ref(true);
const page = ref({
  pageSize: 10,
  currentPage: 1,
  total: 0,
});
const selectionList = ref([]);
const data = ref([]);
const option = reactive({
  height: 'auto',
  calcHeight: 32,
  dialogWidth: 950,
  tip: false,
  searchShow: true,
  searchMenuSpan: 6,
  border: true,
  index: true,
  viewBtn: true,
  selection: true,
  excelBtn: true,
  dialogClickModal: false,
  grid: false,
  column: [
    {
      label: '通知标题',
      prop: 'title',
      span: 24,
      row: true,
      search: true,
      rules: [
        {
          required: true,
          message: '请输入通知标题',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '通知类型',
      type: 'select',
      dicUrl: '/blade-system/dict/dictionary?code=notice',
      props: {
        label: 'dictValue',
        value: 'dictKey',
      },
      dataType: 'number',
      slot: true,
      prop: 'category',
      search: true,
      rules: [
        {
          required: true,
          message: '请输入通知类型',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '通知时间',
      prop: 'releaseTimeRange',
      type: 'datetime',
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
      searchRange: true,
      hide: true,
      addDisplay: false,
      editDisplay: false,
      viewDisplay: false,
      search: true,
      rules: [
        {
          required: true,
          message: '请输入通知时间',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '通知日期',
      prop: 'releaseTime',
      type: 'date',
      gridRow: true,
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
      rules: [
        {
          required: true,
          message: '请输入通知日期',
          trigger: 'click',
        },
      ],
    },
    {
      label: '通知内容',
      prop: 'content',
      component: 'avue-ueditor',
      action: '/blade-resource/oss/endpoint/put-file',
      propsHttp: {
        res: 'data',
        url: 'link',
      },
      hide: true,
      minRows: 4,
      span: 24,
    },
  ],
});

// 使用 vuex store
const store = useStore();
const permission = computed(() => store.getters.permission);

// 计算权限
const permissionList = computed(() => {
  const validData = (value, defaultVal = false) => (value ? true : defaultVal);
  return {
    addBtn: validData(permission.value.notice_add, false),
    viewBtn: validData(permission.value.notice_view, false),
    delBtn: validData(permission.value.notice_delete, false),
    editBtn: validData(permission.value.notice_edit, false),
  };
});

// 计算选中的id
const ids = computed(() => {
  return selectionList.value.map(ele => ele.id).join(',');
});

// 新增
const rowSave = (row, done, loading) => {
  add(row).then(
    () => {
      onLoad();
      ElMessage.success('操作成功!');
      done();
    },
    error => {
      console.error(error);
      loading();
    }
  );
};

// 修改
const rowUpdate = (row, index, done, loading) => {
  update(row).then(
    () => {
      onLoad();
      ElMessage.success('操作成功!');
      done();
    },
    error => {
      console.error(error);
      loading();
    }
  );
};

// 删除
const rowDel = row => {
  ElMessageBox.confirm('确定将选择数据删除?', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => remove(row.id))
    .then(() => {
      onLoad();
      ElMessage.success('操作成功!');
    });
};

// 重置查询
const searchReset = () => {
  search.value = {};
  page.value.currentPage = 1;
  onLoad();
};

// 查询
const searchChange = (params, done) => {
  page.value.currentPage = 1;
  onLoad();
  done();
};

// 勾选事件
const selectionChange = list => {
  selectionList.value = list;
};

// 重置勾选
const selectionClear = () => {
  selectionList.value = [];
  // 假设你有一个 ref 为 'crud' 的组件，可以调用 toggleSelection 方法
  // if (crudRef.value) crudRef.value.toggleSelection();
};

// 批量删除
const handleDelete = () => {
  if (selectionList.value.length === 0) {
    ElMessage.success('请选择至少一条数据');
    return;
  }
  ElMessageBox.confirm('确定将选择数据删除?', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => remove(ids.value))
    .then(() => {
      onLoad();
      ElMessage.success('操作成功!');
    });
};

// 打开前事件
const beforeOpen = (done, type) => {
  if (['edit', 'view'].includes(type)) {
    getNotice(form.value.id).then(res => {
      form.value = res.data.data;
    });
  }
  done();
};

// 刷新事件
const refreshChange = () => {
  onLoad();
};

// 加载数据
const onLoad = () => {
  const { releaseTimeRange } = search.value;
  let values = { ...search.value };
  if (releaseTimeRange) {
    values = {
      ...values,
      releaseTime_datege: releaseTimeRange[0],
      releaseTime_datelt: releaseTimeRange[1],
    };
    values.releaseTimeRange = null;
  }
  loading.value = true;
  getList(page.value.currentPage, page.value.pageSize, values).then(res => {
    const { total, records } = res.data.data;
    page.value.total = total;
    data.value = records;
    loading.value = false;
    selectionClear();
  });
};

// 生命周期钩子
onMounted(() => {
  // 可以在这里执行组件挂载后的逻辑
});
</script>

<style scoped>
/* 你的样式代码 */
</style>
