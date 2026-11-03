const { API_BASE_URL } = require('./config/env')
const { getLanguageData } = require('./utils/i18n')

App({
  globalData: { apiBase: API_BASE_URL, user: null, ...getLanguageData() },
  onLaunch() { this.globalData.user = wx.getStorageSync('shop_user') || null; Object.assign(this.globalData, getLanguageData()) },
  requireUser() { if (!this.globalData.user) { wx.reLaunch({ url: '/pages/login/login' }); return false } return true },
  logout() { wx.removeStorageSync('shop_user'); this.globalData.user = null; wx.reLaunch({ url: '/pages/login/login' }) }
})
