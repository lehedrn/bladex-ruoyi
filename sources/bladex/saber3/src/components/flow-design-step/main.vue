<template>
  <div v-if="componentLoaded">
    <el-dialog
      v-if="website.design.designMode"
      title="流程配置"
      append-to-body
      destroy-on-close
      v-model="visible"
      :close-on-press-escape="false"
      :fullscreen="true"
      :before-close="handleNutflowClose"
      class="nf-dialog"
    >
      <nf-design-base
        v-if="nutflowOption.step === 1"
        class="animated fadeIn"
        style="height: calc(100vh - 108px)"
        ref="nf-design"
        :options="nutflowOption.step1"
      ></nf-design-base>
      <nf-design-base
        v-if="nutflowOption.step === 2"
        class="animated fadeIn"
        style="height: calc(100vh - 108px)"
        ref="nf-design-view"
        :options="nutflowOption.step2"
      ></nf-design-base>
      <template #footer>
        <span class="avue-dialog__footer">
          <el-button @click="handleNutflowClose(() => {}, true)">取 消</el-button>
          <el-button v-if="nutflowOption.step === 1" type="success" @click="handleStep(1)"
            >下 一 步</el-button
          >
          <el-button v-if="nutflowOption.step === 2" type="success" @click="handleStep(-1)"
            >上 一 步</el-button
          >
          <el-button v-if="nutflowOption.step === 2" type="primary" @click="handleSubmitModel"
            >确 定</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { loadFlowModule } from '@/utils/module';
import { submitModel } from '@/api/flow/flow';

export default {
  name: 'flowDesign',
  props: {
    isDisplay: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      visible: false,
      componentLoaded: false,
      nutflowOption: {
        process: {},
        step: 1,
        step1: {
          toolbar: [
            'open',
            'create',
            'fit',
            'zoom-in',
            'zoom-out',
            'undo',
            'redo',
            'import',
            'preview',
          ],
        },
        step2: {
          mode: 'view',
          simulation: true,
          minimap: true,
        },
      },
    };
  },
  created() {
    // 懒加载流程设计器模块
    loadFlowModule(this.$app).then(() => {
      this.componentLoaded = true;
    });
  },
  watch: {
    isDisplay: {
      handler(val) {
        this.visible = val;
      },
      immediate: true,
    },
    visible: {
      handler(val) {
        this.$emit('update:is-display', val);
      },
    },
  },
  methods: {
    handleSubmitModel() {
      const registry = this.$refs['nf-design-view'].getElementRegistry().getAll();
      const { businessObject } = registry[0];
      const { id, name, documentation } = businessObject;
      const description = documentation && documentation.length > 0 ? documentation[0].text : null;
      const params = {
        ...this.nutflowOption.process,
        modelKey: id,
        name,
        description,
        modelEditorXml: this.nutflowOption.process.xml,
      };
      submitModel(params).then(() => {
        this.$message.success('操作成功');
        this.handleNutflowClose();
        this.$emit('loadData');
      });
    },
    handleStep(step) {
      if (step === 1) {
        // 下一步
        this.$refs['nf-design'].getData('xml').then(data => {
          this.nutflowOption.step1.xml = data;
          this.nutflowOption.step2.xml = data;
          this.nutflowOption.process.xml = data;
          this.nutflowOption.step = 2;
        });
      } else this.nutflowOption.step = 1;
    },
    handleNutflowClose(done, flag) {
      const initOption = {
        process: {},
        step: 1,
        step1: {
          toolbar: [
            'open',
            'create',
            'fit',
            'zoom-in',
            'zoom-out',
            'undo',
            'redo',
            'import',
            'preview',
          ],
        },
        step2: {
          mode: 'view',
          simulation: true,
          minimap: true,
        },
      };
      if (done || flag) {
        this.$confirm('确定要关闭吗？关闭未保存的修改都会丢失。', '警告', {
          type: 'warning',
        })
          .then(() => {
            this.nutflowOption = initOption;
            if (typeof done == 'function') done();
            this.visible = false;
          })
          .catch(() => {});
      } else {
        this.nutflowOption = initOption;
        this.visible = false;
      }
    },
  },
};
</script>

<style lang="scss">
.flow-design-dialog {
  display: flex;
  flex-direction: column;
  margin: 0 !important;
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -40%);
  max-height: calc(100% - 30px);
  max-width: calc(100% - 30px);

  .el-dialog__body {
    flex: 1;
    overflow: auto;
  }
}
</style>
