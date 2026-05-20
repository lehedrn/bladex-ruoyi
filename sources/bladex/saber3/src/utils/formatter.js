import {validatenull} from "@/utils/validate";

/**
 * 格式化工具类
 */
export default class formatter {
  static prettyCode(str) {
    try {
      // 为空则返回空
      if (validatenull(str)) {
        return '';
      }
      // 解析并格式化JSON字符串
      str = JSON.stringify(JSON.parse(str), null, 2);

      // 先处理 < 符号，然后特殊处理 > 符号
      // 处理所有 < 符号（因为 <- 中的 < 转换不影响显示）
      str = str.replace(/</g, '&lt;');
      
      // 只处理不是箭头的 > 符号
      // 将所有 > 替换，但排除 -> 的情况
      str = str.replace(/([^-])>/g, '$1&gt;');
      // 处理行首的 > 符号
      str = str.replace(/^>/gm, '&gt;');

      // 返回格式化的字符串，并添加样式类
      return str.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        let cls = 'number';
        if (/^"/.test(match)) {
          if (/:$/.test(match)) {
            cls = 'key';
          } else {
            cls = 'string';
          }
        } else if (/true|false/.test(match)) {
          cls = 'boolean';
        } else if (/null/.test(match)) {
          cls = 'null';
        }
        return match;
      });
    } catch (e) {
      return str;
    }
  }
}
