import website from '@/config/website';
import { getToken } from '@/utils/auth';
import store from '@/store';
import { generateIframePath, processUrlForQuery, isURL } from './router';
const modules = import.meta.glob('../**/**/*.vue');

let RouterPlugin = function () {
  this.$router = null;
  this.$store = null;
};
RouterPlugin.install = function (option = {}) {
  this.$router = option.router;
  this.$store = option.store;
  let i18n = option.i18n.global;
  this.$router.$avueRouter = {
    safe: this,
    // 设置标题
    setTitle: title => {
      const defaultTitle = i18n.t('title');
      title = title ? `${title} | ${defaultTitle}` : defaultTitle;
      document.title = title;
    },
    closeTag: value => {
      let tag = value || this.$store.getters.tag;
      if (typeof value === 'string') {
        tag = this.$store.getters.tagList.find(ele => ele.fullPath === value);
      }
      this.$store.commit('DEL_TAG', tag);
    },
    generateTitle: (item, props = {}) => {
      let query = item[props.query || 'query'] || {};
      let title = query.name || item[props.label || 'label'];
      let meta = item[props.meta || 'meta'] || {};
      let key = meta.i18n;
      if (key) {
        const hasKey = i18n.te('route.' + key);
        if (hasKey) return i18n.t('route.' + key);
      }
      return title ? title.split(',')[0] : title;
    },
    //动态路由
    formatRoutes: function (aMenu = [], first) {
      const aRouter = [];
      const propsDefault = website.menu;

      if (aMenu && aMenu.length === 0) return;
      for (let i = 0; i < aMenu.length; i++) {
        const oMenu = aMenu[i];
        let path = oMenu[propsDefault.path],
          isComponent = true,
          component = oMenu.component,
          name = oMenu[propsDefault.label] + ',' + oMenu.id,
          icon = oMenu[propsDefault.icon],
          children = oMenu[propsDefault.children],
          query = oMenu[propsDefault.query],
          meta = oMenu[propsDefault.meta];

        // 处理iframe场景，如果path是URL且是一级路由，需要生成一个内部路径
        const isUrlPath = isURL(path);
        if (isUrlPath && first && !children?.length) {
          // 保存原始URL到query中（如果formatPath还没处理）
          if (!query || !query.url) {
            query = { url: processUrlForQuery(path) };
          }
          // 生成一个内部路径用于路由
          const generatedPath = generateIframePath(oMenu, propsDefault.path);
          if (generatedPath !== path) {
            path = generatedPath;
            oMenu[propsDefault.path] = path;
          }
          // 确保使用iframe组件
          if (!component || component.indexOf('iframe') === -1) {
            component = 'components/iframe/main';
            oMenu.component = component;
          }
        }
        if (option.keepAlive) {
          meta.keepAlive = option.keepAlive;
        }
        const isChild = !!(children && children.length !== 0);
        const oRouter = {
          path: path,
          component: (() => {
            // 判断是否为首路由
            if (first) {
              return modules[
                option.store.getters.isMacOs || !website.setting.menu
                  ? '../page/index/layout.vue'
                  : '../page/index/index.vue'
              ];
              // 判断是否为多层路由
            } else if (isChild && !first) {
              return modules['../page/index/layout.vue'];
              // 判断是否为最终的页面视图
            } else {
              let result = modules[`../${component}.vue`];
              if (!result) {
                isComponent = false;
              }
              return result;
            }
          })(),
          name,
          icon,
          meta,
          query,
          redirect: (() => {
            if (!isChild && first) return `${path}`;
            else return '';
          })(),
          // 处理是否为一级路由
          children: !isChild
            ? (() => {
                if (first) {
                  oMenu[propsDefault.path] = `${path}`;
                  let componentPath = oMenu.component || component;
                  let result = modules[`../${componentPath}.vue`];
                  if (!result) {
                    isComponent = false;
                  }
                  let childName = name + '_index';
                  let childQuery = oMenu[propsDefault.query] || query;
                  return [
                    {
                      component: result,
                      icon: icon,
                      name: childName,
                      meta: meta,
                      query: childQuery,
                      path: '',
                    },
                  ];
                }
                return [];
              })()
            : (() => {
                return this.formatRoutes(children, false);
              })(),
        };
        const isIframeRoute =
          component === 'components/iframe/main' || oMenu.component === 'components/iframe/main';
        if ((!isURL(path) || isIframeRoute) && isComponent) aRouter.push(oRouter);
      }
      if (first) {
        aRouter.forEach(ele => this.safe.$router.addRoute(ele));
      } else {
        return aRouter;
      }
    },
  };
};
export const formatPath = (ele, first) => {
  const propsDefault = website.menu;
  const icon = ele[propsDefault.icon];
  ele[propsDefault.icon] = !icon ? propsDefault.iconDefault : icon;
  ele.meta = {
    keepAlive: ele.isOpen === 2,
  };
  const iframeComponent = 'components/iframe/main';
  const iframeSrc = href => {
    // 替换&为#
    let processedHref = href.replace(/&/g, '#');

    // 获取用户信息
    const userInfo = store.getters.userInfo || {};
    const userToken = getToken() || '';

    // 定义替换参数映射
    const replacements = {
      token: userToken,
      userId: userInfo.userId || '',
      userName: userInfo.userName || '',
      roleName: userInfo.roleName || '',
    };

    // 统一替换所有参数
    Object.entries(replacements).forEach(([key, value]) => {
      const pattern = new RegExp(`\\$\\{${key}\\}`, 'g');
      processedHref = processedHref.replace(pattern, value);
    });

    return processedHref;
  };
  const isChild = !!(ele[propsDefault.children] && ele[propsDefault.children].length !== 0);
  if (!isChild && first) {
    ele.component = 'views' + ele[propsDefault.path];
    if (isURL(ele[propsDefault.href])) {
      let href = ele[propsDefault.href];
      ele.component = iframeComponent;
      ele[propsDefault.query] = {
        url: iframeSrc(href),
      };
    }
  } else {
    ele[propsDefault.children] &&
      ele[propsDefault.children].forEach(child => {
        child.component = 'views' + child[propsDefault.path];
        child.meta = {
          keepAlive: child.isOpen === 2,
        };
        if (isURL(child[propsDefault.href])) {
          let href = child[propsDefault.href];
          child[propsDefault.path] = ele[propsDefault.path] + '/' + child.code;
          child.component = iframeComponent;
          child[propsDefault.query] = {
            url: iframeSrc(href),
          };
        }
        formatPath(child);
      });
  }
};
export default RouterPlugin;
