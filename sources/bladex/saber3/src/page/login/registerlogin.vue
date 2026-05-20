<template>
  <el-form
    class="login-form"
    status-icon
    :rules="loginRules"
    ref="loginForm"
    :model="loginForm"
    label-width="0"
  >
    <el-form-item v-if="tenantMode" prop="tenantId">
      <el-input
        @keyup.enter="handleRegister"
        v-model="loginForm.tenantId"
        auto-complete="off"
        :placeholder="$t('login.tenantId')"
      >
        <template #prefix>
          <i class="icon-quanxian" />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="name">
      <el-input
        @keyup.enter="handleRegister"
        v-model="loginForm.name"
        auto-complete="off"
        :placeholder="$t('login.name')"
      >
        <template #prefix>
          <i class="icon-zhanghaoquanxianguanli" />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="account">
      <el-input
        @keyup.enter="handleRegister"
        v-model="loginForm.account"
        auto-complete="off"
        :placeholder="$t('login.username')"
      >
        <template #prefix>
          <i class="icon-yonghu" />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="phone">
      <el-input
        @keyup.enter="handleRegister"
        v-model="loginForm.phone"
        auto-complete="off"
        :placeholder="$t('login.phone')"
      >
        <template #prefix>
          <i class="icon-shouji" />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="email">
      <el-input
        @keyup.enter="handleRegister"
        v-model="loginForm.email"
        auto-complete="off"
        :placeholder="$t('login.email')"
      >
        <template #prefix>
          <i class="icon-xiaoxitongzhi" />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input
        @keyup.enter="handleRegister"
        type="password"
        show-password
        v-model="loginForm.password"
        auto-complete="off"
        :placeholder="$t('login.password')"
      >
        <template #prefix>
          <i class="icon-mima" />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password1">
      <el-input
        @keyup.enter="handleRegister"
        type="password"
        show-password
        v-model="loginForm.password1"
        auto-complete="off"
        :placeholder="$t('login.password1')"
      >
        <template #prefix>
          <i class="icon-mima" />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click.prevent="handleRegister" class="login-submit"
        >{{ $t('login.register') }}
      </el-button>
      <el-button @click.prevent="handleBack" class="register-submit"
        >{{ $t('login.back') }}
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { mapGetters } from 'vuex';
import { info } from '@/api/system/tenant';
import { getTopUrl } from '@/utils/util';

export default {
  name: 'userlogin',
  data() {
    return {
      tenantMode: this.website.tenantMode,
      captchaMode: this.website.captchaMode,
      registerMode: this.website.oauth2.registerMode,
      loginForm: {
        //租户ID
        tenantId: '',
        //部门ID
        deptId: '',
        //角色ID
        roleId: '',
        //用户名
        account: '',
        //手机号
        phone: '',
        //邮箱
        email: '',
        //密码
        password: '',
        //确认密码
        password1: '',
        //账号类型
        type: 'account',
        //预加载白色背景
        image: 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7',
      },
      loginRules: {
        tenantId: [{ required: true, message: '请输入租户ID', trigger: 'blur' }],
        name: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        account: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        password1: [{ required: true, message: '请输入确认密码', trigger: 'blur' }],
      },
      passwordType: 'password',
    };
  },
  created() {
    this.getTenant();
  },
  mounted() {
    this.$nextTick(() => {});
  },
  watch: {},
  computed: {
    ...mapGetters(['tagWel', 'userInfo']),
  },
  props: [],
  methods: {
    handleRegister() {
      if (this.loginForm.password !== this.loginForm.password1) {
        this.$message.error('两次密码输入不一致');
        return;
      }
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          const loading = this.$loading({
            lock: true,
            text: '注册中,请稍后',
            background: 'rgba(0, 0, 0, 0.7)',
          });
          this.$store
            .dispatch('RegisterUser', this.loginForm)
            .then(() => {
              this.$alert('注册成功，请耐心等待管理员审核后分配权限', '注册成功', {
                confirmButtonText: '确定',
                callback: () => {
                  this.$parent.activeName = 'user';
                },
              });
            })
            .catch(err => {
              console.log(err);
            });
          loading.close();
        }
      });
    },
    handleBack() {
      this.$parent.activeName = 'user';
    },
    getTenant() {
      let domain = getTopUrl();
      // 临时指定域名，方便测试
      //domain = "https://bladex.cn";
      info(domain).then(res => {
        const data = res.data;
        if (data.success && data.data.tenantId) {
          this.tenantMode = false;
          this.loginForm.tenantId = data.data.tenantId;
          this.$parent.$refs.login.style.backgroundImage = `url(${data.data.backgroundUrl})`;
        }
      });
    },
  },
};
</script>

<style></style>
