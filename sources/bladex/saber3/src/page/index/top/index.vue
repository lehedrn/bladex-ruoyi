<template>
  <div class="avue-top">
    <div class="top-bar__left">
      <div
        class="avue-breadcrumb"
        :class="[{ 'avue-breadcrumb--active': isCollapse }]"
        v-if="setting.collapse && !isHorizontal"
      >
        <i class="icon-navicon" @click="setCollapse"></i>
      </div>
    </div>
    <div class="top-bar__title">
      <top-menu ref="topMenu" v-if="setting.menu"></top-menu>
      <top-search class="top-bar__item" v-if="setting.search"></top-search>
    </div>
    <div class="top-bar__right">
      <div v-if="setting.color" class="top-bar__item">
        <top-color></top-color>
      </div>
      <div v-if="setting.theme" class="top-bar__item">
        <top-theme></top-theme>
      </div>
      <div v-if="setting.lock" class="top-bar__item">
        <top-lock></top-lock>
      </div>
      <div class="top-bar__item">
        <top-lang></top-lang>
      </div>
      <div class="top-bar__item" v-if="setting.fullscreen">
        <top-full></top-full>
      </div>
      <div class="top-bar__item" v-if="setting.debug">
        <top-logs></top-logs>
      </div>
      <div class="top-user">
        <img class="top-bar__img" :src="userInfo.avatar" />
        <el-dropdown :hide-on-click="false">
          <span class="el-dropdown-link">
            {{ userInfo.real_name }}
            <el-icon class="el-icon--right">
              <arrow-down />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="toDashboard">
                <i class="icon-zuzhixiaxia" /> {{ $t('navbar.dashboard') }}
              </el-dropdown-item>
              <el-dropdown-item @click="toInfo">
                <i class="icon-yonghu" />{{ $t('navbar.userinfo') }}
              </el-dropdown-item>
              <el-dropdown-item divided>
                <i class="icon-hezuohuobanmiyueguanli" />
                <el-tooltip :content="roleName" placement="top" trigger="click">
                  <strong>
                    {{
                      func.contains(roleId, ',')
                        ? $t('navbar.roleName')
                        : $t('navbar.currentRoleName')
                    }}: {{ func.truncateString(roleName) }}
                  </strong>
                </el-tooltip>
              </el-dropdown-item>
              <el-dropdown-item
                v-if="Object.keys(roleObj).length < 5"
                v-for="(name, id) in roleObj"
                :key="id"
                @click="toRole(id, name)"
              >
                <i class="icon-changjingguanli" />
                {{ $t('navbar.switchRoleTo') }}: {{ name }}
              </el-dropdown-item>
              <el-dropdown-item v-if="Object.keys(roleObj).length >= 5" @click="switchRole">
                <i class="icon-changjingguanli" />
                {{ $t('navbar.switchRole') }}
              </el-dropdown-item>
              <el-dropdown-item divided>
                <i class="iconfont iconicon_group" />
                <el-tooltip :content="deptName" placement="top" trigger="click">
                  <strong>
                    {{
                      func.contains(deptId, ',')
                        ? $t('navbar.deptName')
                        : $t('navbar.currentDeptName')
                    }}: {{ func.truncateString(deptName) }}
                  </strong>
                </el-tooltip>
              </el-dropdown-item>
              <el-dropdown-item
                v-if="Object.keys(deptObj).length < 5"
                v-for="(name, id) in deptObj"
                :key="id"
                @click="toDept(id, name)"
              >
                <i class="icon-guanlianshebei" />
                {{ $t('navbar.switchDeptTo') }}: {{ name }}
              </el-dropdown-item>
              <el-dropdown-item v-if="Object.keys(deptObj).length >= 5" @click="switchDept">
                <i class="icon-guanlianshebei" />
                {{ $t('navbar.switchDept') }}
              </el-dropdown-item>
              <el-dropdown-item @click="logout" divided>
                <i class="icon-tuichu" /> {{ $t('navbar.logOut') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <top-setting></top-setting>
        <el-dialog title="请选择身份信息后切换" append-to-body v-model="userBox" width="400px">
          <avue-form :option="userOption" v-model="userForm" @submit="toAll" />
        </el-dialog>
      </div>
    </div>
  </div>
</template>
<script>
import { mapGetters } from 'vuex';
import topLock from './top-lock.vue';
import topMenu from './top-menu.vue';
import topSearch from './top-search.vue';
import topTheme from './top-theme.vue';
import topLogs from './top-logs.vue';
import topColor from './top-color.vue';
import topLang from './top-lang.vue';
import topFull from './top-full.vue';
import topSetting from '../setting.vue';
import { getUserInfo as getUerDetailInfo } from '@/api/system/user';
import { getUserInfo as getUerOauthInfo } from '@/api/user';
import { validatenull } from '@/utils/validate';
import func from '@/utils/func';

export default {
  components: {
    topLock,
    topMenu,
    topSearch,
    topTheme,
    topLogs,
    topColor,
    topLang,
    topFull,
    topSetting,
  },
  name: 'top',
  data() {
    return {
      userId: '',
      roleId: '',
      roleName: '',
      roleObj: {},
      deptId: '',
      deptName: '',
      deptObj: {},
      userBox: false,
      userForm: {
        deptId: '',
        roleId: '',
      },
      userOption: {
        labelWidth: 70,
        submitBtn: true,
        emptyBtn: false,
        submitText: '切 换',
        column: [
          {
            label: '部门',
            prop: 'deptId',
            type: 'select',
            props: {
              label: 'deptName',
              value: 'id',
            },
            dicUrl: '/blade-system/dept/select',
            filterable: true,
            span: 24,
            display: false,
            rules: [
              {
                required: true,
                message: '请选择部门',
                trigger: 'blur',
              },
            ],
          },
          {
            label: '角色',
            prop: 'roleId',
            type: 'select',
            props: {
              label: 'roleName',
              value: 'id',
            },
            dicUrl: '/blade-system/role/select',
            filterable: true,
            span: 24,
            display: false,
            rules: [
              {
                required: true,
                message: '请选择角色',
                trigger: 'blur',
              },
            ],
          },
        ],
      },
    };
  },
  filters: {},
  created() {
    this.init();
  },
  computed: {
    func() {
      return func;
    },
    ...mapGetters([
      'setting',
      'userInfo',
      'tagWel',
      'tagList',
      'isCollapse',
      'tag',
      'logsLen',
      'logsFlag',
      'isHorizontal',
    ]),
  },
  methods: {
    init() {
      getUerDetailInfo().then(res => {
        // 获取绑定信息
        const roleId = res.data.data.roleId;
        const roleName = res.data.data.roleName;
        const roleIds = roleId.split(',');
        const roleNames = roleName.split(',');
        const deptId = res.data.data.deptId;
        const deptName = res.data.data.deptName;
        const deptIds = deptId.split(',');
        const deptNames = deptName.split(',');
        roleIds.forEach((id, index) => {
          if (id === this.roleId) {
            this.roleName = roleNames[index];
          } else {
            this.roleObj[id] = roleNames[index];
          }
        });
        deptIds.forEach((id, index) => {
          if (id === this.deptId) {
            this.deptName = deptNames[index];
          } else {
            this.deptObj[id] = deptNames[index];
          }
        });
        if (validatenull(this.roleName)) {
          this.roleName = roleName;
        }
        if (validatenull(this.deptName)) {
          this.deptName = deptName;
        }
      });
      getUerOauthInfo().then(res => {
        this.userId = res.data.userId;
        this.roleId = res.data.roleId;
        this.deptId = res.data.deptId;
      });
    },
    toDashboard() {
      this.$router.push({ path: '/' });
    },
    toInfo() {
      this.$router.push({ path: '/info/index' });
    },
    toRole(roleId, name) {
      const userInfo = { roleId, deptId: this.deptId };
      this.switchTo(userInfo, name);
    },
    toDept(deptId, name) {
      const userInfo = { roleId: this.roleId, deptId };
      this.switchTo(userInfo, name);
    },
    toAll(form, done) {
      let userInfo;
      if (!validatenull(form.deptId)) {
        userInfo = { roleId: this.roleId, deptId: form.deptId };
      }
      if (!validatenull(form.roleId)) {
        userInfo = { roleId: form.roleId, deptId: this.deptId };
      }
      this.switchTo(userInfo);
      done();
    },
    switchRole() {
      const columnRole = this.findObject(this.userOption.column, 'roleId');
      const columnDept = this.findObject(this.userOption.column, 'deptId');
      columnRole.dicUrl = `/blade-system/role/select?userId=${this.userId}`;
      columnRole.display = true;
      columnDept.dicUrl = '';
      columnDept.display = false;
      this.userBox = true;
    },
    switchDept() {
      const columnDept = this.findObject(this.userOption.column, 'deptId');
      const columnRole = this.findObject(this.userOption.column, 'roleId');
      columnDept.dicUrl = `/blade-system/dept/select?userId=${this.userId}`;
      columnDept.display = true;
      columnRole.dicUrl = '';
      columnRole.display = false;
      this.userBox = true;
    },
    switchTo(userInfo, name) {
      const tipMessage = validatenull(name)
        ? this.$t('navbar.switchTip') + ' ? '
        : this.$t('navbar.switchTip') + ` [${name}] ?`;
      this.$confirm(tipMessage, this.$t('confirmTip'), {
        confirmButtonText: this.$t('submitText'),
        cancelButtonText: this.$t('cancelText'),
        type: 'warning',
      }).then(() => {
        this.$store
          .dispatch('RefreshToken', userInfo)
          .then(() => {
            this.$message.success(this.$t('navbar.switchSuccess'));
            location.reload();
          })
          .catch(err => {
            console.log(err);
          });
      });
    },
    setCollapse() {
      this.$store.commit('SET_COLLAPSE');
    },
    logout() {
      this.$confirm(this.$t('logoutTip'), this.$t('confirmTip'), {
        confirmButtonText: this.$t('submitText'),
        cancelButtonText: this.$t('cancelText'),
        type: 'warning',
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          if (this.website.oauth2.ssoMode) {
            window.location.href =
              this.website.oauth2.ssoBaseUrl +
              this.website.oauth2.ssoLogoutUrl +
              this.website.oauth2.redirectUri;
          } else {
            this.$router.push({ path: '/login' });
          }
        });
      });
    },
  },
};
</script>

<style lang="scss" scoped></style>
