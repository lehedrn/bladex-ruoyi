import { templateDic } from '@/const/tool/model';

export const codeOption = {
  height: 'auto',
  calcHeight: 32,
  dialogWidth: 900,
  tip: false,
  searchShow: true,
  searchMenuSpan: 6,
  border: true,
  index: true,
  selection: true,
  labelWidth: 120,
  menuWidth: 350,
  viewBtn: true,
  dialogClickModal: false,
  tabs: true,
  column: [
    {
      label: '模块名',
      prop: 'codeName',
      search: true,
      display: false,
    },
    {
      label: '模版类型',
      prop: 'templateType',
      type: 'select',
      dicData: templateDic,
      display: false,
    },
    {
      label: '表名',
      prop: 'tableName',
      search: true,
      display: false,
    },
    {
      label: '服务名',
      prop: 'serviceName',
      search: true,
      display: false,
    },
    {
      label: '包名',
      prop: 'packageName',
      display: false,
    },
  ],
  group: [
    {
      label: '模型配置',
      prop: 'modelSetting',
      icon: 'el-icon-tickets',
      column: [
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
          label: '数据模型',
          prop: 'modelId',
          search: true,
          span: 24,
          type: 'select',
          dicUrl: '/blade-develop/model/select',
          props: {
            label: 'modelName',
            value: 'id',
          },
          rules: [
            {
              required: true,
              message: '请选择数据模型',
              trigger: 'blur',
            },
          ],
        },
        {
          label: '模块名',
          prop: 'codeName',
          search: true,
          rules: [
            {
              required: true,
              message: '请输入模块名',
              trigger: 'blur',
            },
          ],
        },
        {
          label: '表名',
          prop: 'tableName',
          rules: [
            {
              required: true,
              message: '请输入表名',
              trigger: 'blur',
            },
          ],
        },
        {
          label: '表前缀',
          prop: 'tablePrefix',
          hide: true,
          rules: [
            {
              required: true,
              message: '请输入表前缀',
              trigger: 'blur',
            },
          ],
        },
        {
          label: '主键名',
          prop: 'pkName',
          hide: true,
          rules: [
            {
              required: true,
              message: '请输入主键名',
              trigger: 'blur',
            },
          ],
        },
        {
          label: '服务名',
          prop: 'serviceName',
          search: true,
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
          overHidden: true,
          rules: [
            {
              required: true,
              message: '请输入包名',
              trigger: 'blur',
            },
          ],
        },
      ],
    },
    {
      label: '模版配置',
      prop: 'templateSetting',
      icon: 'el-icon-copy-document',
      column: [
        {
          label: '模版类型',
          prop: 'templateType',
          type: 'select',
          dicData: templateDic,
          value: 'crud',
          rules: [
            {
              required: true,
              message: '请选择模版类型',
              trigger: 'blur',
            },
          ],
        },
        {
          label: '作者信息',
          prop: 'author',
          value: 'BladeX',
          rules: [
            {
              required: true,
              message: '请输入作者',
              trigger: 'blur',
            },
          ],
        },
        {
          label: '子表模型',
          prop: 'subModelId',
          type: 'select',
          dicUrl: '/blade-develop/model/select',
          props: {
            label: 'modelName',
            value: 'id',
          },
          display: false,
          hide: true,
        },
        {
          label: '子表外键',
          prop: 'subFkId',
          display: false,
          hide: true,
        },
        {
          label: '树主键字段',
          prop: 'treeId',
          type: 'select',
          dicData: [],
          props: {
            label: 'jdbcComment',
            value: 'jdbcName',
          },
          display: false,
          hide: true,
        },
        {
          label: '树父主键字段',
          prop: 'treePid',
          type: 'select',
          dicData: [],
          props: {
            label: 'jdbcComment',
            value: 'jdbcName',
          },
          display: false,
          hide: true,
        },
        {
          label: '树名称字段',
          prop: 'treeName',
          type: 'select',
          dicData: [],
          props: {
            label: 'jdbcComment',
            value: 'jdbcName',
          },
          display: false,
          hide: true,
        },
      ],
    },
    {
      label: '生成配置',
      prop: 'codingSetting',
      icon: 'el-icon-printer',
      column: [
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
    },
  ],
};

export const genOption = {
  labelWidth: 120,
  column: [
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
      label: '数据源',
      prop: 'datasourceId',
      search: true,
      span: 24,
      type: 'select',
      dicUrl: '/blade-develop/datasource/select',
      props: {
        label: 'name',
        value: 'id',
      },
      rules: [
        {
          required: true,
          message: '请选择数据源',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '物理表名',
      prop: 'modelTable',
      type: 'tree',
      slot: true,
      filterable: true,
      span: 24,
      display: true,
      dicData: [],
      props: {
        label: 'comment',
        value: 'name',
      },
      rules: [
        {
          required: true,
          message: '请输入数据库表名',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '表单设计',
      prop: 'modelForm',
      type: 'select',
      props: {
        label: 'name',
        value: 'id',
      },
      dicData: [],
      filterable: true,
      display: false,
      span: 24,
    },
    {
      label: '模型类名',
      prop: 'modelClass',
      display: false,
      disabled: true,
      rules: [
        {
          required: true,
          message: '请输入模型类名',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '模型编号',
      prop: 'modelCode',
      display: false,
      rules: [
        {
          required: true,
          message: '请输入模型编号',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '模块名',
      prop: 'codeName',
      display: false,
      rules: [
        {
          required: true,
          message: '请输入模块名',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '表名',
      prop: 'tableName',
      display: false,
      rules: [
        {
          required: true,
          message: '请输入表名',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '表前缀',
      prop: 'tablePrefix',
      display: false,
      rules: [
        {
          required: true,
          message: '请输入表前缀',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '主键名',
      prop: 'pkName',
      display: false,
      rules: [
        {
          required: true,
          message: '请输入主键名',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '服务名',
      prop: 'serviceName',
      search: true,
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
      overHidden: true,
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
