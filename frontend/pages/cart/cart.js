const { request } = require('../../utils/api')
const { getLanguageData, toggleLanguage, pick } = require('../../utils/i18n')
const app = getApp()

Page({
  data: { ...getLanguageData(), items: [], total: '0.00', addresses: [], selectedAddressIndex: -1, submitting: false },
  onShow() { if (!app.requireUser()) return; this.setData(getLanguageData()); this.loadCart(); this.refreshAddresses() },
  toggleLanguage() { const language = toggleLanguage(); Object.assign(app.globalData, language); this.setData(language); this.loadCart(); this.refreshAddresses() },
  loadCart() {
    const cart = wx.getStorageSync('shop_cart') || {}
    const items = Object.keys(cart).map(key => ({ ...cart[key], displayName: pick(cart[key], 'name', this.data.lang), displayColor: this.data.lang === 'en' ? (cart[key].colorNameEn || '') : (cart[key].colorNameZh || '') }))
    const total = items.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0)
    this.setData({ items, total: total.toFixed(2) })
  },
  refreshAddresses() {
    request('/api/addresses').then(rawAddresses => {
      const savedId = wx.getStorageSync('selected_address_id')
      const selectedAddressIndex = Math.max(0, rawAddresses.findIndex(item => String(item.id) === String(savedId)))
      const index = rawAddresses.length ? selectedAddressIndex : -1
      if (index >= 0) wx.setStorageSync('selected_address_id', rawAddresses[index].id)
      this.setData({ addresses: rawAddresses.map(item => ({ ...item, displayAddress: `${item.recipientName} ${item.contactPhone} · ${item.shippingAddress}` })), selectedAddressIndex: index })
    }).catch(message => wx.showToast({ title: message, icon: 'none' }))
  },
  chooseAddress(e) { const selectedAddressIndex = Number(e.detail.value); const address = this.data.addresses[selectedAddressIndex]; if (!address) return; wx.setStorageSync('selected_address_id', address.id); this.setData({ selectedAddressIndex }) },
  manageAddresses() { wx.navigateTo({ url: '/pages/addresses/addresses' }) },
  updateQuantity(id, requestedQuantity) {
    const cart = { ...(wx.getStorageSync('shop_cart') || {}) }
    const current = cart[id]
    if (!current) return
    const stock = Number(current.stock)
    const productId = current.productId || current.id
    const otherQuantity = Object.keys(cart).filter(key => key !== id && String(cart[key].productId || cart[key].id) === String(productId)).reduce((sum, key) => sum + (Number(cart[key].quantity) || 0), 0)
    const maxQuantity = Number.isFinite(stock) && stock >= 0 ? Math.max(0, stock - otherQuantity) : Infinity
    const quantity = Math.max(0, Math.min(requestedQuantity, maxQuantity))
    if (!quantity) delete cart[id]
    else cart[id] = { ...current, quantity }
    if (requestedQuantity > maxQuantity) wx.showToast({ title: this.data.t.stockInsufficient, icon: 'none' })
    wx.setStorageSync('shop_cart', cart)
    this.loadCart()
  },
  change(e) {
    const { id, delta } = e.currentTarget.dataset
    const cart = wx.getStorageSync('shop_cart') || {}
    if (!cart[id]) return
    this.updateQuantity(id, (Number(cart[id].quantity) || 0) + Number(delta))
  },
  setQuantity(e) {
    const id = e.currentTarget.dataset.id
    const parsed = Number.parseInt(e.detail.value, 10)
    this.updateQuantity(id, Number.isFinite(parsed) ? parsed : 0)
  },
  submit() {
    if (!this.data.items.length || this.data.submitting) return
    const address = this.data.addresses[this.data.selectedAddressIndex]
    if (!address) { wx.showToast({ title: this.data.t.selectAddressFirst, icon: 'none' }); return }
    this.setData({ submitting: true })
    request('/api/orders', 'POST', { addressId: address.id, items: this.data.items.map(item => ({ productId: item.productId || item.id, quantity: item.quantity, sizeCode: item.sizeCode, colorCode: item.colorCode })) }).then(order => { wx.removeStorageSync('shop_cart'); this.setData({ items: [], total: '0.00' }); wx.redirectTo({ url: `/pages/order-success/order-success?orderNo=${order.orderNo}` }) }).catch(message => wx.showToast({ title: message, icon: 'none' })).finally(() => this.setData({ submitting: false }))
  }
})
