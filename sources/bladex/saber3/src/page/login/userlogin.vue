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
        @keyup.enter="handleLogin"
        v-model="loginForm.tenantId"
        auto-complete="off"
        :placeholder="$t('login.tenantId')"
      >
        <template #prefix>
          <i class="icon-quanxian" />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="username">
      <el-input
        @keyup.enter="handleLogin"
        v-model="loginForm.username"
        auto-complete="off"
        :placeholder="$t('login.username')"
      >
        <template #prefix>
          <i class="icon-yonghu" />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input
        @keyup.enter="handleLogin"
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
    <el-form-item prop="code" class="login-code" v-if="captchaMode">
      <el-input
        @keyup.enter="handleLogin"
        v-model="loginForm.code"
        auto-complete="off"
        :placeholder="$t('login.code')"
      >
        <template #prefix>
          <i class="icon-yanzhengma"></i>
        </template>
        <template #append>
          <div class="login-code-box">
            <img :src="loginForm.image" class="login-code-img" @click="refreshCode" />
          </div>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click.prevent="handleLogin" class="login-submit"
        >{{ $t('login.submit') }}
      </el-button>
      <el-button
        v-if="this.registerMode"
        type="danger"
        @click.prevent="handleRegister"
        class="register-submit"
        >{{ $t('login.register') }}
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { mapGetters } from 'vuex';
import { info } from '@/api/system/tenant';
import { getCaptcha } from '@/api/user';
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
        tenantId: '000000',
        //部门ID
        deptId: '',
        //角色ID
        roleId: '',
        //用户名
        username: 'admin',
        //密码
        password: 'admin',
        //账号类型
        type: 'account',
        //验证码的值
        code: '',
        //验证码的索引
        key: '',
        //预加载白色背景
        image: 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7',
      },
      loginRules: {
        tenantId: [{ required: false, message: '请输入租户ID', trigger: 'blur' }],
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 1, message: '密码长度最少为6位', trigger: 'blur' },
        ],
      },
      passwordType: 'password',
    };
  },
  created() {
    this.getTenant();
    this.refreshCode();
  },
  mounted() {
    this.$nextTick(() => {});
  },
  computed: {
    ...mapGetters(['tagWel', 'userInfo']),
  },
  props: [],
  methods: {
    refreshCode() {
      if (this.website.captchaMode) {
        getCaptcha().then(res => {
          const data = res.data;
          this.loginForm.key = data.key;
          this.loginForm.image = data.image;
        });
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          const loading = this.$loading({
            lock: true,
            text: '登录中,请稍后',
            background: 'rgba(0, 0, 0, 0.7)',
          });
          this.$store
            .dispatch('LoginByUsername', this.loginForm)
            .then(() => {
              loading.close();
              //加载工作流路由集
              this.loadFlowRoutes();
              this.$router.push(this.tagWel);
            })
            .catch(err => {
              console.log(err);
              loading.close();
              this.refreshCode();
            });
        }
      });
    },
    handleRegister() {
      this.$parent.activeName = 'register';
    },
    loadFlowRoutes() {
      this.$store.dispatch('FlowRoutes').then(() => {});
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
