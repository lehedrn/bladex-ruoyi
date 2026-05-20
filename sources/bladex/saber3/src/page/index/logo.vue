<template>
  <div class="avue-logo">
    <transition name="fade">
      <span v-if="getScreen(isCollapse)" class="avue-logo_subtitle" key="0">
        <img class="logo-img" src="/img/logo.png" />
      </span>
    </transition>
    <transition-group name="fade">
      <template v-if="getScreen(!isCollapse)">
        <span class="logo-title" key="1">{{ website.indexTitle }} </span>
      </template>
    </transition-group>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  name: 'logo',
  data() {
    return {};
  },
  created() {},
  computed: {
    ...mapGetters(['isCollapse']),
  },
  methods: {},
};
</script>
<style scoped>
.logo-title {
  font-size: 20px;
  background-image: linear-gradient(120deg, #54b6d0 16%, #3f8bdb, #2c77f1);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 700;
  padding-left: 30px;
}
.logo-img {
  width: 40px;
  margin-top: 5px;
}
</style>
