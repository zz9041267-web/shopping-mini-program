const { request } = require('../../utils/api')
const { getLanguageData, toggleLanguage, pick } = require('../../utils/i18n')
const app = getApp()

Page({
  data: { ...getLanguageData(), product: null, rawProduct: null, cart: {}, quantity: 0, loading: true, loadFailed: false },
  onLoad(options) { this.productId = options.id; this.load() },
  onShow() {
    if (!app.requireUser()) return
    this.setData(getLanguageData())
    if (this.data.rawProduct) this.renderProduct(this.data.rawProduct)
    this.syncCart()
  },
  load() {
    this.setData({ loading: true, loadFailed: false })
    request('/api/catalog').then(catalog => {
      const allProducts = Object.keys(catalog.products || {}).flatMap(key => catalog.products[key])
      const product = allProducts.find(item => String(item.id) === String(this.productId))
      if (!product) { this.setData({ loading: false, loadFailed: true }); return }
      this.setData({ rawProduct: product, loading: false })
      this.renderProduct(product)
      this.syncCart()
    }).catch(() => this.setData({ loading: false, loadFailed: true }))
  },
  renderProduct(rawProduct) {
    const displayImages = rawProduct.displayImagesBase64 && rawProduct.displayImagesBase64.length ? rawProduct.displayImagesBase64 : (rawProduct.imageBase64 ? [rawProduct.imageBase64] : [])
    this.setData({ product: { ...rawProduct, displayName: pick(rawProduct, 'name', this.data.lang), displayDescription: pick(rawProduct, 'description', this.data.lang), displayImages, detailImages: rawProduct.detailImagesBase64 || [] } })
  },
  toggleLanguage() { const language = toggleLanguage(); Object.assign(app.globalData, language); this.setData(language); if (this.data.rawProduct) this.renderProduct(this.data.rawProduct) },
  syncCart() { const cart = wx.getStorageSync('shop_cart') || {}; const quantity = this.data.product && cart[this.data.product.id] ? cart[this.data.product.id].quantity : 0; this.setData({ cart, quantity }) },
  refreshCart(cart) { wx.setStorageSync('shop_cart', cart); const quantity = this.data.product && cart[this.data.product.id] ? cart[this.data.product.id].quantity : 0; this.setData({ cart, quantity }) },
  add() {
    const product = this.data.product
    if (!product) return
    const cart = { ...this.data.cart }
    const current = cart[product.id]
    const quantity = current ? Number(current.quantity) || 0 : 0
    if (quantity >= product.stock) { wx.showToast({ title: this.data.t.stockInsufficient, icon: 'none' }); return }
    cart[product.id] = { ...(current || { id: product.id, name: product.name, nameZh: product.nameZh, nameEn: product.nameEn, price: product.price, stock: product.stock }), quantity: quantity + 1 }
    this.refreshCart(cart)
  },
  minus() {
    const product = this.data.product
    const cart = { ...this.data.cart }
    const current = product && cart[product.id]
    if (!current) return
    const quantity = (Number(current.quantity) || 0) - 1
    if (quantity <= 0) delete cart[product.id]
    else cart[product.id] = { ...current, quantity }
    this.refreshCart(cart)
  },
  setQuantity(e) {
    const product = this.data.product
    if (!product) return
    const parsed = Number.parseInt(e.detail.value, 10)
    const quantity = Number.isFinite(parsed) ? Math.max(0, Math.min(parsed, product.stock)) : 0
    const cart = { ...this.data.cart }
    const current = cart[product.id]
    if (!quantity) delete cart[product.id]
    else cart[product.id] = { ...(current || { id: product.id, name: product.name, nameZh: product.nameZh, nameEn: product.nameEn, price: product.price, stock: product.stock }), quantity }
    if (parsed > product.stock) wx.showToast({ title: this.data.t.stockInsufficient, icon: 'none' })
    this.refreshCart(cart)
  },
  previewImage(e) { const image = e.currentTarget.dataset.image; if (image) wx.previewImage({ current: image, urls: this.data.product.detailImages }) },
  toCart() { wx.navigateTo({ url: '/pages/cart/cart' }) }
})
