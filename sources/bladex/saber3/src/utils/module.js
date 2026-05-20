let flowInstance = null;
let formInstance = null;
let formDesignInstance = null;

export function loadFlowModule(app) {
  if (!flowInstance) {
    flowInstance = import('@saber/nf-design-base-elp')
      .then(module => {
        app.use(module.default);
        return module;
      })
      .catch(error => console.error('failed to load module:', error));
  }
  return flowInstance;
}

export function loadFormModule(app) {
  if (!formInstance) {
    formInstance = import('@saber/nf-form-elp')
      .then(module => {
        app.use(module.default);
        return module;
      })
      .catch(error => console.error('failed to load module:', error));
  }
  return formInstance;
}

export function loadFormDesignModule(app) {
  if (!formDesignInstance) {
    formDesignInstance = import('@saber/nf-form-design-elp')
      .then(module => {
        app.use(module.default);
        return module;
      })
      .catch(error => console.error('failed to load module:', error));
  }
  return formDesignInstance;
}
