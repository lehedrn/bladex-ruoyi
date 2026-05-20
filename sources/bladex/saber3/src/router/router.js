/**
 * 路由工具函数
 */

// 统一的URL正则表达式
export const URL_REGEX = /^https?:\/\//;

/**
 * 生成iframe路径
 * @param {Object} item - 菜单项
 * @param {String} pathKey - 路径字段名
 * @returns {String} 生成的路径
 */
export function generateIframePath(item, pathKey = 'path') {
  const path = item[pathKey];
  if (!path) return path;

  // 如果是URL且有ID，生成iframe路径
  if (URL_REGEX.test(path) && item.id) {
    return `/iframe/${item.id}`;
  }
  return path;
}

/**
 * 处理URL参数，将&替换为#以避免路由参数问题
 * @param {String} url - 原始URL
 * @returns {String} 处理后的URL
 */
export function processUrlForQuery(url) {
  if (!url) return url;
  return url.replace(/&/g, '#');
}

/**
 * 生成iframe的query参数
 * @param {Object} item - 菜单项
 * @param {String} pathKey - 路径字段名
 * @param {String} queryKey - query字段名
 * @returns {Object} query参数
 */
export function generateIframeQuery(item, pathKey = 'path', queryKey = 'query') {
  const originalPath = item[pathKey];
  const existingQuery = item[queryKey] || {};

  // 如果是URL且没有url参数，添加处理后的URL
  if (URL_REGEX.test(originalPath)) {
    // 保留现有query，只在没有url时添加
    if (!existingQuery.url) {
      return {
        ...existingQuery,
        url: processUrlForQuery(originalPath)
      };
    }
  }

  return existingQuery;
}

/**
 * 判断是否为iframe路由
 * @param {String} path - 当前路径
 * @param {String} originalPath - 原始路径
 * @returns {Boolean} 是否为iframe路由
 */
export function isIframeRoute(path, originalPath) {
  // 路径以/iframe/开头且原始路径是URL
  return path && path.startsWith('/iframe/') && URL_REGEX.test(originalPath);
}

/**
 * 判断路径是否为URL
 * @param {String} path - 路径
 * @returns {Boolean} 是否为URL
 */
export function isURL(path) {
  return URL_REGEX.test(path);
}