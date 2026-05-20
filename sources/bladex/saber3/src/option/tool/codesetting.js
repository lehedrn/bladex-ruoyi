export default {
  height: 'auto',
  calcHeight: 50,
  tip: false,
  searchShow: true,
  searchMenuSpan: 6,
  border: true,
  index: true,
  viewBtn: true,
  grid: true,
  selection: true,
  labelWidth: 120,
  searchLabelWidth: 100,
  menuWidth: 320,
  dialogClickModal: false,
  column: [
    {
      label: '配置名称',
      labelTip: '用于定义本配置的代表名称，实际不参与代码生成',
      prop: 'name',
      gridRow: true,
      search: true,
      rules: [
        {
          required: true,
          message: '请输入配置名称',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '配置编号',
      labelTip: '用于定义本配置的代表编号，实际不参与代码生成',
      prop: 'code',
      gridRow: true,
      search: true,
      rules: [
        {
          required: true,
          message: '请输入配置编号',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '上级菜单',
      prop: 'menuId',
      type: 'tree',
      dicData: [],
      span: 24,
      hide: true,
      addDisabled: false,
      props: {
        label: 'title',
      },
      rules: [
        {
          required: true,
          message: '请选择上级菜单',
          trigger: 'click',
        },
      ],
    },
    {
      label: '是否启用',
      prop: 'status',
      span: 24,
      width: 85,
      align: 'center',
      slot: true,
      gridRow: true,
      addDisplay: false,
      editDisplay: false,
      viewDisplay: false,
    },
    {
      label: '配置参数',
      prop: 'settings',
      display: false,
      gridRow: true,
      overHidden: true,
      rules: [
        {
          required: true,
          message: '请输入服务名',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '服务名',
      prop: 'serviceName',
      hide: true,
      rules: [
        {
          required: true,
          message: '请输入服务名',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '包名',
      prop: 'packageName',
      hide: true,
      rules: [
        {
          required: true,
          message: '请输入包名',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '基础业务',
      labelTip: '配置是否使用BladeX封装的BaseService解锁更多功能',
      prop: 'baseMode',
      type: 'radio',
      dicUrl: '/blade-system/dict/dictionary?code=yes_no',
      props: {
        label: 'dictValue',
        value: 'dictKey',
      },
      value: 2,
      dataType: 'number',
      hide: true,
      rules: [
        {
          required: true,
          message: '请选择基础业务',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '包装器',
      labelTip: '配置是否使用Wrapper包装器来拓展Controller返回列表的字段',
      prop: 'wrapMode',
      type: 'radio',
      dicUrl: '/blade-system/dict/dictionary?code=yes_no',
      props: {
        label: 'dictValue',
        value: 'dictKey',
      },
      value: 2,
      dataType: 'number',
      hide: true,
      rules: [
        {
          required: true,
          message: '请选择包装器',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '远程调用',
      labelTip: '配置是否使用Feign远程调用',
      prop: 'feignMode',
      type: 'radio',
      dicUrl: '/blade-system/dict/dictionary?code=yes_no',
      props: {
        label: 'dictValue',
        value: 'dictKey',
      },
      value: 1,
      dataType: 'number',
      hide: true,
      rules: [
        {
          required: true,
          message: '请选择基础业务',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '代码风格',
      labelTip: '选择不同底层实现的代码模版',
      prop: 'codeStyle',
      type: 'radio',
      dicData: [
        {
          label: 'saber3',
          value: 'saber3',
        },
        {
          label: 'element-plus',
          value: 'element-plus',
        },
      ],
      value: 'saber3',
      hide: true,
      rules: [
        {
          required: true,
          message: '请选择代码风格',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '后端生成路径',
      prop: 'apiPath',
      span: 24,
      hide: true,
      rules: [
        {
          required: true,
          message: '请输入后端生成路径',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '前端生成路径',
      prop: 'webPath',
      span: 24,
      hide: true,
      rules: [
        {
          required: true,
          message: '请输入前端生成路径',
          trigger: 'blur',
        },
      ],
    },
  ],
};
