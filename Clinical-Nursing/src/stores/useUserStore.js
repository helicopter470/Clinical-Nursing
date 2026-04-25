import { defineStore } from 'pinia';

// 定义 Store：id 必须唯一
export const useUserStore = defineStore('user', {
  // 状态：存储用户信息（从本地存储初始化）
  state: () => ({
    userInfo: JSON.parse(localStorage.getItem('system-user') || '{}')
  }),
  // 操作：修改用户信息的方法
  actions: {
    // 更新用户信息（全量更新）
    updateUserInfo(newUserInfo) {
      this.userInfo = { ...this.userInfo, ...newUserInfo };
      // 同步更新本地存储（防止刷新丢失）
      localStorage.setItem('system-user', JSON.stringify(this.userInfo));
    },

    // 仅更新头像（兼容旧代码调用）
    updateAvatar(avatarUrl) {
      this.updateUserInfo({ avatar: avatarUrl });
    },

    logout() {
      this.userInfo = {};
      localStorage.removeItem('system-user');
    }
  },
  // 计算属性（如获取用户角色、头像）
  getters: {
    getAvatar: (state) => state.userInfo.avatar || '默认头像地址',
    getUserName: (state) => state.userInfo.name || '未知用户'
  }
});