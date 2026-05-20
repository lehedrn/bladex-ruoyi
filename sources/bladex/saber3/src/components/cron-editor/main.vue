<template>
  <div class="cron-editor">
    <el-card shadow="never" class="cron-card">
      <div class="cron-mode-tabs">
        <el-radio-group v-model="cronMode" @change="handleModeChange" :disabled="disabled">
          <el-radio-button label="daily">每天</el-radio-button>
          <el-radio-button label="weekly">每周</el-radio-button>
          <el-radio-button label="monthly">每月</el-radio-button>
          <el-radio-button label="custom">自定义</el-radio-button>
        </el-radio-group>
      </div>

      <div class="cron-config-area">
        <!-- 每天模式 -->
        <div v-if="cronMode === 'daily'" class="mode-content">
          <el-form :model="dailyConfig" label-width="100px" size="default">
            <el-form-item label="执行时间">
              <el-time-picker
                v-model="dailyConfig.time"
                format="HH:mm:ss"
                value-format="HH:mm:ss"
                placeholder="选择时间"
                :disabled="disabled"
                @change="generateCron"
              />
              <span class="tip-text">每天在指定时间执行</span>
            </el-form-item>
          </el-form>
        </div>

        <!-- 每周模式 -->
        <div v-if="cronMode === 'weekly'" class="mode-content">
          <el-form :model="weeklyConfig" label-width="100px" size="default">
            <el-form-item label="星期选择">
              <el-checkbox-group v-model="weeklyConfig.weekDays" @change="generateCron" :disabled="disabled">
                <el-checkbox label="1">周一</el-checkbox>
                <el-checkbox label="2">周二</el-checkbox>
                <el-checkbox label="3">周三</el-checkbox>
                <el-checkbox label="4">周四</el-checkbox>
                <el-checkbox label="5">周五</el-checkbox>
                <el-checkbox label="6">周六</el-checkbox>
                <el-checkbox label="7">周日</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="执行时间">
              <el-time-picker
                v-model="weeklyConfig.time"
                format="HH:mm:ss"
                value-format="HH:mm:ss"
                placeholder="选择时间"
                :disabled="disabled"
                @change="generateCron"
              />
              <span class="tip-text">在选定的星期几执行</span>
            </el-form-item>
          </el-form>
        </div>

        <!-- 每月模式 -->
        <div v-if="cronMode === 'monthly'" class="mode-content">
          <el-form :model="monthlyConfig" label-width="100px" size="default">
            <el-form-item label="日期选择">
              <el-select
                v-model="monthlyConfig.days"
                multiple
                placeholder="选择日期"
                :disabled="disabled"
                @change="generateCron"
                style="width: 400px"
              >
                <el-option
                  v-for="day in 31"
                  :key="day"
                  :label="`${day} 号`"
                  :value="day"
                />
              </el-select>
              <span class="tip-text">每月的哪几天</span>
            </el-form-item>
            <el-form-item label="执行时间">
              <el-time-picker
                v-model="monthlyConfig.time"
                format="HH:mm:ss"
                value-format="HH:mm:ss"
                placeholder="选择时间"
                :disabled="disabled"
                @change="generateCron"
              />
              <span class="tip-text">在选定的日期执行</span>
            </el-form-item>
          </el-form>
        </div>

        <!-- 自定义模式 -->
        <div v-if="cronMode === 'custom'" class="mode-content">
          <el-form :model="customConfig" label-width="100px" size="default">
            <el-form-item label="秒">
              <el-input
                v-model="customConfig.second"
                placeholder="0-59 或 * 或 */5"
                :disabled="disabled"
                @input="generateCron"
              />
            </el-form-item>
            <el-form-item label="分">
              <el-input
                v-model="customConfig.minute"
                placeholder="0-59 或 * 或 */10"
                :disabled="disabled"
                @input="generateCron"
              />
            </el-form-item>
            <el-form-item label="时">
              <el-input
                v-model="customConfig.hour"
                placeholder="0-23 或 * 或 8-18"
                :disabled="disabled"
                @input="generateCron"
              />
            </el-form-item>
            <el-form-item label="日">
              <el-input
                v-model="customConfig.day"
                placeholder="1-31 或 * 或 ?"
                :disabled="disabled"
                @input="generateCron"
              />
            </el-form-item>
            <el-form-item label="月">
              <el-input
                v-model="customConfig.month"
                placeholder="1-12 或 * 或 JAN-DEC"
                :disabled="disabled"
                @input="generateCron"
              />
            </el-form-item>
            <el-form-item label="周">
              <el-input
                v-model="customConfig.week"
                placeholder="0-7 或 * 或 ? 或 MON-SUN"
                :disabled="disabled"
                @input="generateCron"
              />
            </el-form-item>
          </el-form>
          <div class="cron-help">
            <div class="cron-help-title">
              <el-icon class="help-icon"><InfoFilled /></el-icon>
              <span>Cron 表达式说明</span>
            </div>
            <ul class="cron-help-list">
              <li><strong>*</strong> <span>表示所有值</span></li>
              <li><strong>?</strong> <span>表示不指定值（仅日和周可用）</span></li>
              <li><strong>-</strong> <span>表示范围，如：8-18</span></li>
              <li><strong>,</strong> <span>表示列举，如：1,3,5</span></li>
              <li><strong>/</strong> <span>表示增量，如：*/5 表示每5个单位</span></li>
              <li><strong>周</strong> <span>1-7 或 MON-SUN（1=周一，7=周日）</span></li>
            </ul>
          </div>
        </div>
      </div>

      <div class="cron-result">
        <el-form label-width="120px">
          <el-form-item label="Cron 表达式">
            <el-input
              v-model="cronExpression"
              readonly
              placeholder="自动生成的 Cron 表达式"
              class="cron-expression-input"
            >
              <template #append>
                <el-button
                  v-clipboard:copy="cronExpression"
                  v-clipboard:success="onCopySuccess"
                  v-clipboard:error="onCopyError">
                  复制
                </el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="执行说明" v-if="cronDescription" class="description-item">
            <el-tag type="primary" effect="plain" class="cron-description">{{ cronDescription }}</el-tag>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
import { InfoFilled } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

export default {
  name: 'CronEditor',
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      InfoFilled,
      cronMode: 'daily',
      cronExpression: '',
      cronDescription: '',

      // 每天配置
      dailyConfig: {
        time: '00:00:00'
      },

      // 每周配置
      weeklyConfig: {
        weekDays: ['1'],
        time: '00:00:00'
      },

      // 每月配置
      monthlyConfig: {
        days: [1],
        time: '00:00:00'
      },

      // 自定义配置
      customConfig: {
        second: '0',
        minute: '0',
        hour: '0',
        day: '*',
        month: '*',
        week: '?'
      }
    };
  },
  watch: {
    modelValue: {
      handler(newVal) {
        if (newVal && newVal !== this.cronExpression) {
          this.parseCron(newVal);
        }
      },
      immediate: true
    },
    cronExpression(newVal) {
      this.$emit('update:modelValue', newVal);
    }
  },
  mounted() {
    if (!this.modelValue) {
      this.generateCron();
    }
  },
  methods: {
    /**
     * 切换模式时重置配置
     */
    handleModeChange() {
      this.generateCron();
    },

    /**
     * 生成 Cron 表达式
     */
    generateCron() {
      let cron = '';
      let description = '';

      switch (this.cronMode) {
        case 'daily':
          cron = this.generateDailyCron();
          description = `每天 ${this.dailyConfig.time} 执行`;
          break;
        case 'weekly':
          cron = this.generateWeeklyCron();
          description = this.getWeeklyDescription();
          break;
        case 'monthly':
          cron = this.generateMonthlyCron();
          description = this.getMonthlyDescription();
          break;
        case 'custom':
          cron = this.generateCustomCron();
          description = '自定义表达式';
          break;
      }

      this.cronExpression = cron;
      this.cronDescription = description;
    },

    /**
     * 生成每天的 Cron 表达式
     */
    generateDailyCron() {
      const time = this.dailyConfig.time || '00:00:00';
      const [hour, minute, second] = time.split(':');
      return `${second} ${minute} ${hour} * * ?`;
    },

    /**
     * 生成每周的 Cron 表达式
     */
    generateWeeklyCron() {
      if (!this.weeklyConfig.weekDays || this.weeklyConfig.weekDays.length === 0) {
        return '0 0 0 ? * 1';
      }

      const time = this.weeklyConfig.time || '00:00:00';
      const [hour, minute, second] = time.split(':');
      const weekDays = this.weeklyConfig.weekDays.sort((a, b) => a - b).join(',');

      return `${second} ${minute} ${hour} ? * ${weekDays}`;
    },

    /**
     * 生成每月的 Cron 表达式
     */
    generateMonthlyCron() {
      if (!this.monthlyConfig.days || this.monthlyConfig.days.length === 0) {
        return '0 0 0 1 * ?';
      }

      const time = this.monthlyConfig.time || '00:00:00';
      const [hour, minute, second] = time.split(':');
      const days = this.monthlyConfig.days.sort((a, b) => a - b).join(',');

      return `${second} ${minute} ${hour} ${days} * ?`;
    },

    /**
     * 生成自定义的 Cron 表达式
     */
    generateCustomCron() {
      const { second, minute, hour, day, month, week } = this.customConfig;
      return `${second || '0'} ${minute || '0'} ${hour || '0'} ${day || '*'} ${month || '*'} ${week || '?'}`;
    },

    /**
     * 获取每周执行说明
     */
    getWeeklyDescription() {
      const weekMap = {
        '1': '周一',
        '2': '周二',
        '3': '周三',
        '4': '周四',
        '5': '周五',
        '6': '周六',
        '7': '周日'
      };

      const weekDays = this.weeklyConfig.weekDays.map(day => weekMap[day]).join('、');
      return `每周 ${weekDays} ${this.weeklyConfig.time} 执行`;
    },

    /**
     * 获取每月执行说明
     */
    getMonthlyDescription() {
      const days = this.monthlyConfig.days.sort((a, b) => a - b).join('、');
      return `每月 ${days} 号 ${this.monthlyConfig.time} 执行`;
    },

    /**
     * 解析 Cron 表达式（反向解析，用于回显）
     */
    parseCron(cron) {
      if (!cron) return;

      const parts = cron.trim().split(/\s+/);
      if (parts.length !== 6) {
        // 如果不是标准6位表达式，设置为自定义模式
        this.cronMode = 'custom';
        this.customConfig = {
          second: parts[0] || '0',
          minute: parts[1] || '0',
          hour: parts[2] || '0',
          day: parts[3] || '*',
          month: parts[4] || '*',
          week: parts[5] || '?'
        };
        this.cronExpression = cron;
        return;
      }

      const [second, minute, hour, day, month, week] = parts;

      // 尝试识别模式
      if (day === '*' && week === '?') {
        // 可能是每天
        this.cronMode = 'daily';
        this.dailyConfig.time = `${hour.padStart(2, '0')}:${minute.padStart(2, '0')}:${second.padStart(2, '0')}`;
      } else if (day === '?' && week !== '?' && week !== '*') {
        // 可能是每周
        this.cronMode = 'weekly';
        this.weeklyConfig.weekDays = week.split(',');
        this.weeklyConfig.time = `${hour.padStart(2, '0')}:${minute.padStart(2, '0')}:${second.padStart(2, '0')}`;
      } else if (day !== '*' && day !== '?' && week === '?') {
        // 可能是每月
        this.cronMode = 'monthly';
        this.monthlyConfig.days = day.split(',').map(d => parseInt(d));
        this.monthlyConfig.time = `${hour.padStart(2, '0')}:${minute.padStart(2, '0')}:${second.padStart(2, '0')}`;
      } else {
        // 自定义模式
        this.cronMode = 'custom';
        this.customConfig = { second, minute, hour, day, month, week };
      }

      this.cronExpression = cron;
      this.generateCron();
    },

    /**
     * 复制成功回调
     */
    onCopySuccess() {
      ElMessage.success('Cron 表达式已复制到剪贴板');
    },

    /**
     * 复制失败回调
     */
    onCopyError() {
      ElMessage.error('复制失败，请手动复制');
    },

    /**
     * 获取当前 Cron 表达式（供父组件调用）
     */
    getCronExpression() {
      return this.cronExpression;
    }
  }
};
</script>

<style scoped>
.cron-editor {
  width: 100%;
}

.cron-card {
  border-radius: 8px;
}

.cron-mode-tabs {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.cron-mode-tabs :deep(.el-radio-button__inner) {
  padding: 10px 20px;
  font-size: 14px;
}

.cron-config-area {
  margin-bottom: 0;
}

.mode-content {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.tip-text {
  margin-left: 12px;
  color: #909399;
  font-size: 13px;
}

.cron-help {
  margin-top: 18px;
  padding: 16px;
  background: linear-gradient(135deg, #fefce8 0%, #fef9c3 100%);
  border: 1px solid #fde047;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(234, 179, 8, 0.1);
}

.cron-help-title {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #854d0e;
}

.help-icon {
  margin-right: 6px;
  font-size: 16px;
  color: #ca8a04;
}

.cron-help-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.cron-help-list li {
  display: flex;
  align-items: baseline;
  line-height: 26px;
  color: #713f12;
  font-size: 13px;
  padding: 4px 0;
}

.cron-help-list li strong {
  display: inline-block;
  min-width: 36px;
  margin-right: 8px;
  padding: 2px 8px;
  background-color: #fef3c7;
  border: 1px solid #fde047;
  border-radius: 4px;
  color: #b45309;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  font-weight: 700;
  text-align: center;
}

.cron-help-list li span {
  flex: 1;
  color: #78716c;
}

.cron-result {
  margin-top: 25px;
  padding: 18px 20px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border: 1px solid #bae6fd;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.08);
}

.cron-description {
  font-size: 14px;
  padding: 8px 16px;
  background-color: #f0f9ff;
  border-color: #bfdbfe;
  color: #3b82f6;
  font-weight: 500;
}

.cron-expression-input :deep(.el-input__inner) {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  font-weight: 600;
  color: #409eff;
}

:deep(.el-checkbox) {
  margin-right: 20px;
  margin-bottom: 12px;
}

.mode-content :deep(.el-form-item) {
  margin-bottom: 18px;
}

.mode-content :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

:deep(.el-select .el-input__inner) {
  height: 36px;
}

:deep(.el-time-picker) {
  width: 200px;
}

.description-item {
  margin-top: 16px;
}
</style>
