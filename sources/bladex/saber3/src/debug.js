import DisableDevtool from 'disable-devtool';
import { ElMessageBox } from 'element-plus';

/**
 * 安全警告的HTML模板, 使用Tailwind CSS样式
 */
const SECURITY_WARNING_TEMPLATE = `
<div class='flex flex-col items-center gap-4 p-2'>
  <div class='text-red-500 dark:text-red-400'>
    <i class='el-icon-warning text-2xl'></i>
  </div>
  <div class='text-center'>
    <p class='text-base font-medium text-gray-900 dark:text-gray-100'>
      不合规操作，系统将在5秒后自动关闭退出！
    </p>
    <p class='mt-2 text-sm text-gray-600 dark:text-gray-400'>
      如您频繁此类操作，系统将记录上报。
    </p>
  </div>
</div>`;

/**
 * Element Plus 消息框配置选项
 */
const ALERT_OPTIONS = {
  type: 'error',
  showClose: false,
  center: true,
  closeOnClickModal: false,
  closeOnPressEscape: false,
  dangerouslyUseHTMLString: true,
};

export default () => {
  if (import.meta.env.VITE_APP_DEBUG_SWITCH === 'false') return;

  const debugKey = import.meta.env.VITE_APP_DEBUG_KEY;
  let flag = false;
  DisableDevtool({
    md5: DisableDevtool.md5(debugKey),
    disableMenu: false,
    ondevtoolopen: (type, next) => {
      if (!flag) ElMessageBox.alert(SECURITY_WARNING_TEMPLATE, '安全警告', ALERT_OPTIONS);
      flag = true;
      setTimeout(() => {
        next();
      }, 5000);
    },
  });
};
