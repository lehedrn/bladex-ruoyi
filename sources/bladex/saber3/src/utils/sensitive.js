export const sensitive = {
  /**
   * 初始化敏感数据管理器
   * @param {Object} config 配置项
   * @param {Array} config.fields 需要脱敏的字段列表
   * @returns {Object} 敏感数据管理器实例
   */
  create(config = {}) {
    const sensitiveFields = config.fields || [];
    let initialMaskedData = {};

    return {
      /**
       * 保存初始的脱敏数据
       * @param {Object} data 包含脱敏数据的对象
       */
      saveInitialData(data) {
        initialMaskedData = {};
        sensitiveFields.forEach(field => {
          if (data[field]) {
            initialMaskedData[field] = data[field];
          }
        });
      },

      /**
       * 获取要提交的数据，过滤掉未修改的敏感字段
       * @param {Object} formData 表单数据
       * @returns {Object} 过滤后的提交数据
       */
      getSubmitData(formData) {
        const submitData = { ...formData };

        sensitiveFields.forEach(field => {
          // 如果敏感字段的值与初始值相同，删除该字段
          if (submitData[field] === initialMaskedData[field]) {
            delete submitData[field];
          }
        });

        return submitData;
      },

      /**
       * 更新脱敏数据
       * @param {Object} newMaskedData 新的脱敏数据
       */
      updateMaskedData(newMaskedData) {
        Object.assign(initialMaskedData, newMaskedData);
      },

      /**
       * 判断敏感数据是否被修改
       * @returns {boolean}
       */
      isModified(formData) {
        return sensitiveFields.some(field => formData[field] !== initialMaskedData[field]);
      },

      /**
       * 获取已修改的敏感字段列表
       * @returns {Array}
       */
      getModifiedFields(formData) {
        return sensitiveFields.filter(field => formData[field] !== initialMaskedData[field]);
      },

      /**
       * 重置为初始状态
       * @param {Object} formData 表单数据对象
       */
      reset(formData) {
        sensitiveFields.forEach(field => {
          if (initialMaskedData[field]) {
            formData[field] = initialMaskedData[field];
          }
        });
      },
    };
  },
};
