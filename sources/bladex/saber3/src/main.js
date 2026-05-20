import { createApp } from 'vue';
import website from './config/website';
import axios from './axios';
import router from './router/';
import store from './store';
import i18n from './lang/';
import { language, messages } from './lang/';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import Avue from '@smallwei/avue';
import '@smallwei/avue/lib/index.css';
import avueUeditor from 'avue-plugin-ueditor';
import crudCommon from '@/mixins/crud.js';
import { getScreen } from './utils/util';
import './permission';
import error from './error';
import App from './App.vue';
import 'animate.css';
import dayjs from 'dayjs';
import 'styles/common.scss';
// 系统组件
import debug from './debug';
import VueClipboard from 'vue3-clipboard';
import highlight from './components/highlight/main.vue';
import codeEditor from './components/code-editor/main.vue';
import cronEditor from './components/cron-editor/main.vue';
import basicBlock from './components/basic-block/main.vue';
import basicContainer from './components/basic-container/main.vue';
import thirdRegister from './components/third-register/main.vue';
import flowDesign from './components/flow-design/main.vue';
import flowDesignStep from './components/flow-design-step/main.vue';
// 业务组件
import codeSetting from './views/tool/codesetting.vue';
import formSetting from './views/tool/formsetting.vue';
import tenantPackage from './views/system/tenantpackage.vue';
import tenantDatasource from './views/system/tenantdatasource.vue';

window.$crudCommon = crudCommon;
debug();
window.axios = axios;
const app = createApp(App);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}
app.component('basicContainer', basicContainer);
app.component('basicBlock', basicBlock);
app.component('highlight', highlight);
app.component('codeEditor', codeEditor);
app.component('cronEditor', cronEditor);
app.component('thirdRegister', thirdRegister);
app.component('flowDesign', flowDesign);
app.component('flowDesignStep', flowDesignStep);
app.component('codeSetting', codeSetting);
app.component('formSetting', formSetting);
app.component('tenantPackage', tenantPackage);
app.component('tenantDatasource', tenantDatasource);
app.config.globalProperties.$app = app;
app.config.globalProperties.$dayjs = dayjs;
app.config.globalProperties.website = website;
app.config.globalProperties.getScreen = getScreen;
app.use(error);
app.use(i18n);
app.use(store);
app.use(router);
app.use(ElementPlus, {
  locale: messages[language],
});
app.use(Avue, {
  axios,
  calcHeight: 10,
  locale: messages[language],
});
app.use(avueUeditor, { axios });
app.use(VueClipboard, {
  autoSetContainer: true,
  appendToBody: true, // 这可以帮助解决一些更复杂的使用场景下的问题
});
app.mount('#app');
