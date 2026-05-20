<template>
  <div class="code-block-container">
    <pre v-if="highlightedCode"><code v-html="highlightedCode" class="hljs"></code></pre>
    <button v-if="clipboard"
        v-clipboard:copy="this.code"
        v-clipboard:success="onCopySuccess"
        class="copy-btn">
      一键复制
    </button>
  </div>
</template>

<script>
import hljs from 'highlight.js';
import formatter from '@/utils/formatter';
// 引入语言支持
import json from 'highlight.js/lib/languages/json';
import java from 'highlight.js/lib/languages/java';
import javascript from 'highlight.js/lib/languages/javascript';
import 'highlight.js/styles/github.css';

export default {
  props: {
    code: {
      type: String,
      required: true
    },
    language: {
      type: String,
      default: 'javascript'
    },
    clipboard: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    highlightedCode() {
      hljs.registerLanguage('json', json);
      hljs.registerLanguage('java', java);
      hljs.registerLanguage('javascript', javascript);
      let code = this.code;
      if (this.language === 'json') {
        code = formatter.prettyCode(this.code);
      }
      return hljs.highlight(code, {language: this.language, ignoreIllegals: true}).value;
    }
  },
  methods: {
    onCopySuccess() {
      this.$message.success('复制成功');
    }
  }
};
</script>

<style scoped>
.code-block-container {
  position: relative;
  background-color: #f0f0f0;
  width: 100%;
  overflow-y: auto; /* 只显示纵向滚动条 */
  overflow-x: hidden; /* 隐藏横向滚动条 */
  white-space: pre-wrap; /* 防止内容溢出 */
  word-wrap: break-word; /* 自动换行 */
}

pre {
  margin: 0;
  padding: 0.5em;
  border-radius: 8px;
}

.copy-btn {
  position: absolute;
  top: 0.5em;
  right: 0.5em;
  padding: 0.25em 0.5em;
  font-size: 0.75em;
  color: #fff;
  background-color: #606266;
  border: none;
  cursor: pointer;
  border-radius: 4px;
}

.copy-btn:hover {
  background-color: #909399;
}
</style>
