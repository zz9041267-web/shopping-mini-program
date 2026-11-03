const { getLanguageData, toggleLanguage } = require('../../utils/i18n')
const app = getApp()
Page({
  data: { ...getLanguageData(), selected: 'user' },
  onShow() { this.setData(getLanguageData()) },
  toggleLanguage() { const language = toggleLanguage(); Object.assign(app.globalData, language); this.setData(language) },
  choose(e) { this.setData({ selected: e.currentTarget.dataset.role }) },
  login() { const role = this.data.selected; const user = role === 'admin' ? { name: 'Administrator', role, auth: 'Basic YWRtaW46YWRtaW4xMjM=' } : { name: 'Customer', role, auth: 'Basic dXNlcjp1c2VyMTIz' }; app.globalData.user = user; wx.setStorageSync('shop_user', user); wx.reLaunch({ url: role === 'admin' ? '/pages/admin/admin' : '/pages/index/index' }) }
})
