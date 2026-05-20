import {
  defineConfig,
  loadEnv
} from 'vite';
import { resolve } from 'path'
import createVitePlugins from './vite/plugins';
// https://vitejs.dev/config/
export default ({
  mode,
  command
}) => {
  const env = loadEnv(mode, process.cwd())
  const {
    VITE_APP_ENV,
    VITE_APP_BASE
  } = env
  // 判断是打生产环境包
  const isProd = VITE_APP_ENV === 'production'

  // 根据是否生产环境，动态设置压缩配置
  const buildConfig = {
    target: 'esnext',
    minify: isProd ? 'terser' : 'esbuild', // 根据环境选择压缩工具
  };

  // 如果是生产环境，添加Terser的配置
  if (isProd) {
    buildConfig.terserOptions = {
      compress: {
        drop_console: true, // 删除 console
        drop_debugger: true, // 删除 debugger
      },
      format: {
        comments: false // 删除所有注释
      }
    };
    buildConfig.rollupOptions = {
      output: {
        manualChunks: {
          'element-plus': ['element-plus'],
          '@smallwei/avue': ['@smallwei/avue']
        },
      }
    }
  }
  return defineConfig({
    base: VITE_APP_BASE,
    define: {
      __VUE_I18N_FULL_INSTALL__: true,
      __VUE_I18N_LEGACY_API__: true,
      __INTLIFY_PROD_DEVTOOLS__: false
    },
    server: {
      port: 2888,
      proxy: {
        '/api': {
          target: 'http://localhost',
          //target: 'https://saber3.bladex.cn/api',
          changeOrigin: true,
          rewrite: path => path.replace(/^\/api/, ''),
        },
      },
    },
    resolve: {
      alias: {
        '~': resolve(__dirname, './'),
        '@': resolve(__dirname, './src'),
        components: resolve(__dirname, './src/components'),
        styles: resolve(__dirname, './src/styles'),
        utils: resolve(__dirname, './src/utils'),
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern-compiler',
          additionalData: `@use "@/styles/variables.scss" as *;`,
        },
      },
    },
    plugins: createVitePlugins(env, command === 'build'),
    build: buildConfig,
    optimizeDeps: {
      esbuildOptions: {
        target: 'esnext',
      },
    },
  });
};
