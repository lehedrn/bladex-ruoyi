<template>
  <div class="code-editor">
    <textarea ref="textarea" v-show="false"></textarea>
  </div>
</template>

<script>
import CodeMirror from 'codemirror';
import 'codemirror/lib/codemirror.css';
// 主题
import 'codemirror/theme/idea.css';
import 'codemirror/theme/nord.css';
import 'codemirror/theme/xq-light.css';
import 'codemirror/mode/clike/clike';
import 'codemirror/mode/javascript/javascript';
import 'codemirror/addon/display/autorefresh';
// 搜索
import 'codemirror/addon/scroll/annotatescrollbar.js';
import 'codemirror/addon/search/matchesonscrollbar.js';
import 'codemirror/addon/search/match-highlighter.js';
import 'codemirror/addon/search/jump-to-line.js';
import 'codemirror/addon/dialog/dialog.js';
import 'codemirror/addon/dialog/dialog.css';
import 'codemirror/addon/search/searchcursor.js';
import 'codemirror/addon/search/search.js';
// 折叠
import 'codemirror/addon/fold/foldgutter.css';
import 'codemirror/addon/fold/foldcode';
import 'codemirror/addon/fold/foldgutter';
import 'codemirror/addon/fold/brace-fold';
import 'codemirror/addon/fold/comment-fold';
// 格式化
import formatter from '@/utils/formatter';
import { validatejson, validatenull } from '@/utils/validate';

export default {
  name: 'CodeEditor',
  props: {
    modelValue: {
      type: String,
      required: true,
      default: '',
    },
    height: {
      type: String,
      required: true,
      default: '450px',
    },
    mode: {
      type: String,
      default: 'javascript',
    },
    theme: {
      type: String,
      default: 'idea',
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    json: {
      type: Boolean,
      default: false,
    },
  },
  mounted() {
    this.editor = CodeMirror.fromTextArea(this.$refs.textarea, {
      mode: this.mode, // 默认设置为 'javascript' 或根据需要配置
      theme: this.theme, // 直接设置主题或根据需要配置
      readOnly: this.readonly,
      autoRefresh: true,
      lineNumbers: true,
      lineWrapping: true,
      tabSize: 2,
      foldGutter: true,
      gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter', 'CodeMirror-lint-markers'],
      extraKeys: {
        // 绑定快捷键 Ctrl-F / Cmd-F 开启搜索
        'Ctrl-F': 'findPersistent',
        'Cmd-F': 'findPersistent',
        // 如果需要，您还可以添加 "Ctrl-R"/"Cmd-R" 绑定替换功能
        'Ctrl-R': 'replace',
        'Cmd-R': 'replace',
      },
    });

    // 设置高度
    this.editor.setSize('auto', this.height);

    // 设置文本
    const editorValue = this.json ? formatter.prettyCode(this.modelValue) : this.modelValue;
    this.editor.setValue(editorValue);

    this.editor.on('change', () => {
      this.$emit('update:modelValue', this.editor.getValue()); // 更新 modelValue
    });
  },
  watch: {
    modelValue(newVal) {
      if (newVal !== this.editor.getValue()) {
        const editorValue = this.json ? formatter.prettyCode(this.modelValue) : this.modelValue;
        this.editor.setValue(editorValue);
      }
    },
    height(newHeight) {
      this.editor.setSize('auto', newHeight);
    },
  },
  methods: {
    prettyCode() {
      if (this.json) {
        const val = this.editor.getValue();
        if (validatenull(val)) {
          this.$message.warning('请先填写数据');
          return;
        }
        if (!validatejson(val)) {
          this.$message.warning('数据 JSON 格式错误');
          return;
        }
        this.editor.setValue(formatter.prettyCode(val));
      }
    },
  },
};
</script>

<style scoped>
.code-editor {
  line-height: 1.2 !important;
  width: calc(100% - 4px);
  height: 100%;
  border: 1px solid #ccc; /* 添加边框样式 */
}
</style>
