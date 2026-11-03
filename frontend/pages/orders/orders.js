const { request } = require('../../utils/api')
const { getLanguageData, toggleLanguage, statusLabel } = require('../../utils/i18n')
const app = getApp()

Page({
  data: { ...getLanguageData(), rawOrders: [], orders: [], loading: true, shop: {} },
  onShow() { if (!app.requireUser()) return; this.setData(getLanguageData()); Promise.all([request('/api/orders'), request('/api/catalog')]).then(([rawOrders, catalog]) => { this.setData({ rawOrders, shop: catalog.shop, loading: false }); this.renderOrders() }).catch(message => wx.showToast({ title: message, icon: 'none' })) },
  renderOrders() { const { lang, t } = this.data; this.setData({ orders: this.data.rawOrders.map(order => ({ ...order, createdAtText: order.createdAt ? order.createdAt.replace('T', ' ').slice(0, 16) : '', statusText: statusLabel(order.status, t), items: order.items.map(item => ({ ...item, displayName: lang === 'en' ? (item.productNameEn || item.productName) : item.productName })) })) }) },
  toggleLanguage() { const language = toggleLanguage(); Object.assign(app.globalData, language); this.setData(language); this.renderOrders() },
  previewWechat() { const image = this.data.shop.wechatQrBase64; if (!image) { wx.showToast({ title: this.data.t.noWechat, icon: 'none' }); return } wx.previewImage({ current: image, urls: [image] }) }
})
