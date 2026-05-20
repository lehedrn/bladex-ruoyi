import website from '@/config/website';
import { getDeptLazyTree } from '@/api/system/dept';

export const userOption = safe => {
  const validatePass = (rule, value, callback) => {
    if (value === '') {
      callback(new Error('请输入密码'));
    } else {
      callback();
    }
  };
  const validatePass2 = (rule, value, callback) => {
    if (value === '') {
      callback(new Error('请再次输入密码'));
    } else if (value !== safe.form.password) {
      callback(new Error('两次输入密码不一致!'));
    } else {
      callback();
    }
  };
  return {
    height: 'auto',
    calcHeight: 150,
    tip: false,
    searchShow: true,
    searchMenuSpan: 6,
    border: true,
    index: true,
    selection: true,
    addBtn: false,
    viewBtn: true,
    dialogType: 'drawer',
    dialogWidth: 1000,
    dialogClickModal: false,
    column: [
      {
        label: '登录账号',
        prop: 'account',
        search: true,
        display: false,
      },
      {
        label: '所属租户',
        prop: 'tenantName',
        slot: true,
        display: false,
      },
      {
        label: '用户姓名',
        prop: 'realName',
        search: true,
        display: false,
      },
      {
        label: '所属角色',
        prop: 'roleName',
        slot: true,
        overHidden: true,
        display: false,
      },
      {
        label: '所属部门',
        prop: 'deptName',
        slot: true,
        overHidden: true,
        display: false,
      },
      {
        label: '用户平台',
        prop: 'userTypeName',
        width: 100,
        slot: true,
        display: false,
      },
      {
        label: '用户平台',
        type: 'select',
        dicUrl: '/blade-system/dict/dictionary?code=user_type',
        props: {
          label: 'dictValue',
          value: 'dictKey',
        },
        dataType: 'number',
        search: true,
        hide: true,
        display: false,
        prop: 'userType',
        rules: [
          {
            required: true,
            message: '请选择用户平台',
            trigger: 'blur',
          },
        ],
      },
    ],
    group: [
      {
        label: '基础信息',
        prop: 'baseInfo',
        icon: 'el-icon-user-solid',
        column: [
          {
            label: '所属租户',
            prop: 'tenantId',
            type: 'tree',
            dicUrl: '/blade-system/tenant/select',
            props: {
              label: 'tenantName',
              value: 'tenantId',
            },
            hide: !website.tenantMode,
            addDisplay: website.tenantMode,
            editDisplay: website.tenantMode,
            viewDisplay: website.tenantMode,
            rules: [
              {
                required: true,
                message: '请输入所属租户',
                trigger: 'click',
              },
            ],
            span: 24,
          },
          {
            label: '登录账号',
            prop: 'account',
            rules: [
              {
                required: true,
                message: '请输入登录账号',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '用户平台',
            type: 'select',
            dicUrl: '/blade-system/dict/dictionary?code=user_type',
            props: {
              label: 'dictValue',
              value: 'dictKey',
            },
            dataType: 'number',
            slot: true,
            prop: 'userType',
            rules: [
              {
                required: true,
                message: '请选择用户平台',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '密码',
            prop: 'password',
            hide: true,
            editDisplay: false,
            viewDisplay: false,
            rules: [{ required: true, validator: validatePass, trigger: 'blur' }],
          },
          {
            label: '确认密码',
            prop: 'password2',
            hide: true,
            editDisplay: false,
            viewDisplay: false,
            rules: [{ required: true, validator: validatePass2, trigger: 'blur' }],
          },
        ],
      },
      {
        label: '详细信息',
        prop: 'detailInfo',
        icon: 'el-icon-s-order',
        column: [
          {
            label: '用户昵称',
            prop: 'name',
            hide: true,
            rules: [
              {
                required: true,
                message: '请输入用户昵称',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '用户姓名',
            prop: 'realName',
            rules: [
              {
                required: true,
                message: '请输入用户姓名',
                trigger: 'blur',
              },
              {
                min: 2,
                max: 10,
                message: '姓名长度在2到5个字符',
              },
            ],
          },
          {
            label: '手机号码',
            prop: 'phone',
            overHidden: true,
          },
          {
            label: '电子邮箱',
            prop: 'email',
            hide: true,
            overHidden: true,
          },
          {
            label: '用户性别',
            prop: 'sex',
            type: 'select',
            dicData: [
              {
                label: '男',
                value: 1,
              },
              {
                label: '女',
                value: 2,
              },
              {
                label: '未知',
                value: 3,
              },
            ],
            hide: true,
          },
          {
            label: '用户生日',
            type: 'date',
            prop: 'birthday',
            format: 'YYYY-MM-DD HH:mm:ss',
            valueFormat: 'YYYY-MM-DD HH:mm:ss',
            hide: true,
          },
          {
            label: '账号状态',
            prop: 'statusName',
            hide: true,
            display: false,
          },
        ],
      },
      {
        label: '职责信息',
        prop: 'dutyInfo',
        icon: 'el-icon-s-custom',
        column: [
          {
            label: '用户编号',
            prop: 'code',
          },
          {
            label: '所属角色',
            prop: 'roleId',
            multiple: true,
            type: 'tree',
            dicData: [],
            props: {
              label: 'title',
            },
            checkStrictly: true,
            slot: true,
            rules: [
              {
                required: true,
                message: '请选择所属角色',
                trigger: 'click',
              },
            ],
          },
          {
            label: '所属部门',
            prop: 'deptId',
            type: 'tree',
            multiple: true,
            dicData: [],
            props: {
              label: 'title',
            },
            checkStrictly: true,
            slot: true,
            rules: [
              {
                required: true,
                message: '请选择所属部门',
                trigger: 'click',
              },
            ],
          },
          {
            label: '所属岗位',
            prop: 'postId',
            type: 'tree',
            multiple: true,
            dicData: [],
            props: {
              label: 'postName',
              value: 'id',
            },
            rules: [
              {
                required: true,
                message: '请选择所属岗位',
                trigger: 'click',
              },
            ],
          },
          {
            label: '直属主管',
            prop: 'leaderId',
            type: 'tree',
            multiple: true,
            filterable: true,
            dicData: [],
            props: {
              label: 'realName',
              value: 'id',
            },
            clearable: true,
          },
          {
            label: '是否主管',
            prop: 'isLeader',
            type: 'switch',
            dicData: [
              {
                label: '否',
                value: 0,
              },
              {
                label: '是',
                value: 1,
              },
            ],
            value: 0,
          },
        ],
      },
    ],
  };
};

export const platformOption = {
  tip: false,
  searchShow: true,
  searchMenuSpan: 6,
  border: true,
  index: true,
  selection: true,
  viewBtn: true,
  dialogClickModal: false,
  menuWidth: 120,
  editBtnText: '配置',
  column: [
    {
      label: '登录账号',
      prop: 'account',
      search: true,
      display: false,
    },
    {
      label: '所属租户',
      prop: 'tenantName',
      slot: true,
      display: false,
    },
    {
      label: '用户姓名',
      prop: 'realName',
      search: true,
      display: false,
    },
    {
      label: '用户平台',
      prop: 'userTypeName',
      slot: true,
      display: false,
    },
    {
      label: '用户平台',
      type: 'select',
      dicUrl: '/blade-system/dict/dictionary?code=user_type',
      props: {
        label: 'dictValue',
        value: 'dictKey',
      },
      dataType: 'number',
      search: true,
      hide: true,
      display: false,
      prop: 'userType',
      rules: [
        {
          required: true,
          message: '请选择用户平台',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '用户拓展',
      prop: 'userExt',
      type: 'textarea',
      minRows: 8,
      span: 24,
      overHidden: true,
      row: true,
      hide: true,
    },
  ],
};

export const excelOption = {
  submitBtn: false,
  emptyBtn: false,
  column: [
    {
      label: '模板上传',
      prop: 'excelFile',
      type: 'upload',
      drag: true,
      loadText: '模板上传中，请稍等',
      span: 24,
      propsHttp: {
        res: 'data',
      },
      tip: '请上传 .xls,.xlsx 标准格式文件',
      action: '/blade-system/user/import-user',
    },
    {
      label: '数据覆盖',
      prop: 'isCovered',
      type: 'switch',
      align: 'center',
      width: 80,
      dicData: [
        {
          label: '否',
          value: 0,
        },
        {
          label: '是',
          value: 1,
        },
      ],
      value: 0,
      slot: true,
      rules: [
        {
          required: true,
          message: '请选择是否覆盖',
          trigger: 'blur',
        },
      ],
    },
    {
      label: '模板下载',
      prop: 'excelTemplate',
      formslot: true,
      span: 24,
    },
  ],
};

export const treeOption = {
  nodeKey: 'id',
  lazy: true,
  treeLoad: function (node, resolve) {
    const parentId = node.level === 0 ? 0 : node.data.id;
    getDeptLazyTree(parentId).then(res => {
      resolve(
        res.data.data.map(item => {
          return {
            ...item,
            leaf: !item.hasChildren,
          };
        })
      );
    });
  },
  addBtn: false,
  menu: false,
  size: 'small',
  props: {
    labelText: '标题',
    label: 'title',
    value: 'value',
    children: 'children',
  },
};
