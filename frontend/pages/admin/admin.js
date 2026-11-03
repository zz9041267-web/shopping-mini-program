const { request } = require('../../utils/api')
const { chooseBase64Image, chooseBase64Images } = require('../../utils/image')
const { getLanguageData, toggleLanguage, pick, statusLabel } = require('../../utils/i18n')
const app = getApp()

const emptyProduct = () => ({ nameZh: '', nameEn: '', descriptionZh: '', descriptionEn: '', price: '', stock: '', categoryId: '', displayImagesBase64: [], detailImagesBase64: [] })
const emptySizeSet = () => ({ nameZh: '', nameEn: '', options: [{ code: '', lengthValue: '', widthValue: '' }] })
const emptyColorSet = () => ({ nameZh: '', nameEn: '', options: [{ code: '', nameZh: '', nameEn: '', hexValue: '#000000' }] })

Page({
  data: { ...getLanguageData(), tab: 'shop', categories: [], products: [], orders: [], sizeSets: [], colorSets: [], rawCategories: [], rawProducts: [], rawOrders: [], shopForm: {}, form: emptyProduct(), categoryForm: { nameZh: '', nameEn: '' }, sizeSetForm: emptySizeSet(), colorSetForm: emptyColorSet(), editingProductId: null, editingCategoryId: null, categoryIndex: 0, sizeSetIndex: 0, colorSetIndex: 0, orderSearch: '', loading: true },
  onShow() {
    if (!app.requireUser()) return
    if (app.globalData.user.role !== 'admin') { wx.reLaunch({ url: '/pages/index/index' }); return }
    this.setData(getLanguageData())
    this.load()
  },
  toggleLanguage() { const language = toggleLanguage(); Object.assign(app.globalData, language); this.setData(language); this.renderLocalized() },
  load() {
    this.setData({ loading: true })
    const orderPath = this.data.orderSearch.trim() ? `/api/admin/orders?orderNo=${encodeURIComponent(this.data.orderSearch.trim())}` : '/api/admin/orders'
    Promise.all([request('/api/admin/shop'), request('/api/admin/categories'), request('/api/admin/products'), request('/api/admin/size-sets'), request('/api/admin/color-sets'), request(orderPath)]).then(([shop, rawCategories, rawProducts, rawSizeSets, rawColorSets, rawOrders]) => {
      this.setData({ shopForm: shop, rawCategories, rawProducts, rawSizeSets, rawColorSets, rawOrders, loading: false })
      this.renderLocalized()
    }).catch(message => wx.showToast({ title: message, icon: 'none' }))
  },
  renderLocalized() {
    const lang = this.data.lang
    const categories = this.data.rawCategories.map(item => ({ ...item, displayName: pick(item, 'name', lang) }))
    const products = this.data.rawProducts.map(item => { const displayName = pick(item, 'name', lang); return { ...item, displayImagesBase64: item.displayImagesBase64 || [], detailImagesBase64: item.detailImagesBase64 || [], displayName, displayDescription: pick(item, 'description', lang), initial: (displayName || '?').slice(0, 1), sizeSetName: item.sizeSet ? pick(item.sizeSet, 'name', lang) : '', colorSetName: item.colorSet ? pick(item.colorSet, 'name', lang) : '' } })
    const sizeSets = (this.data.rawSizeSets || []).map(item => ({ ...item, displayName: pick(item, 'name', lang) }))
    const colorSets = (this.data.rawColorSets || []).map(item => ({ ...item, displayName: pick(item, 'name', lang) }))
    const orders = this.data.rawOrders.map(order => ({ ...order, statusText: statusLabel(order.status, this.data.t), items: order.items.map(item => ({ ...item, displayName: lang === 'en' ? (item.productNameEn || item.productName) : item.productName })) }))
    const categoryIndex = Math.max(0, categories.findIndex(item => String(item.id) === String(this.data.form.categoryId)))
    const categoryId = this.data.form.categoryId || (categories[0] && String(categories[0].id)) || ''
    const sizeSetId = this.data.form.sizeSetId || (sizeSets[0] && String(sizeSets[0].id)) || ''
    const colorSetId = this.data.form.colorSetId || (colorSets[0] && String(colorSets[0].id)) || ''
    const sizeSetIndex = Math.max(0, sizeSets.findIndex(item => String(item.id) === String(sizeSetId)))
    const colorSetIndex = Math.max(0, colorSets.findIndex(item => String(item.id) === String(colorSetId)))
    this.setData({ categories, products, orders, sizeSets, colorSets, categoryIndex, sizeSetIndex, colorSetIndex, shopDisplayName: pick(this.data.shopForm, 'name', lang), 'form.categoryId': categoryId, 'form.sizeSetId': sizeSetId, 'form.colorSetId': colorSetId })
  },
  tab(e) { this.setData({ tab: e.currentTarget.dataset.tab }) },
  orderSearchField(e) { this.setData({ orderSearch: e.detail.value }) },
  searchOrders() { this.load() },
  clearOrderSearch() { this.setData({ orderSearch: '' }); this.load() },
  field(e) { this.setData({ [`form.${e.currentTarget.dataset.key}`]: e.detail.value }) },
  shopField(e) { this.setData({ [`shopForm.${e.currentTarget.dataset.key}`]: e.detail.value }) },
  categoryField(e) { this.setData({ [`categoryForm.${e.currentTarget.dataset.key}`]: e.detail.value }) },
  category(e) { const categoryIndex = Number(e.detail.value); this.setData({ categoryIndex, 'form.categoryId': String(this.data.categories[categoryIndex].id) }) },
  sizeSet(e) { const sizeSetIndex = Number(e.detail.value); this.setData({ sizeSetIndex, 'form.sizeSetId': String(this.data.sizeSets[sizeSetIndex].id) }) },
  colorSet(e) { const colorSetIndex = Number(e.detail.value); this.setData({ colorSetIndex, 'form.colorSetId': String(this.data.colorSets[colorSetIndex].id) }) },
  sizeSetField(e) { const { index, key } = e.currentTarget.dataset; this.setData({ [`sizeSetForm.options[${index}].${key}`]: e.detail.value }) },
  colorSetField(e) { const { index, key } = e.currentTarget.dataset; this.setData({ [`colorSetForm.options[${index}].${key}`]: e.detail.value }) },
  sizeSetNameField(e) { this.setData({ [`sizeSetForm.${e.currentTarget.dataset.key}`]: e.detail.value }) },
  colorSetNameField(e) { this.setData({ [`colorSetForm.${e.currentTarget.dataset.key}`]: e.detail.value }) },
  addSizeOption() { this.setData({ 'sizeSetForm.options': this.data.sizeSetForm.options.concat({ code: '', lengthValue: '', widthValue: '' }) }) },
  addColorOption() { this.setData({ 'colorSetForm.options': this.data.colorSetForm.options.concat({ code: '', nameZh: '', nameEn: '', hexValue: '#000000' }) }) },
  saveSizeSet() { const form = this.data.sizeSetForm; if (!form.nameZh || !form.nameEn || form.options.some(item => !item.code || item.lengthValue === '' || item.widthValue === '')) { wx.showToast({ title: this.data.t.fieldRequired, icon: 'none' }); return } request('/api/admin/size-sets', 'POST', { ...form, options: form.options.map(item => ({ ...item, lengthValue: Number(item.lengthValue), widthValue: Number(item.widthValue) })) }).then(() => { this.setData({ sizeSetForm: emptySizeSet() }); this.load() }).catch(message => wx.showToast({ title: message, icon: 'none' })) },
  saveColorSet() { const form = this.data.colorSetForm; if (!form.nameZh || !form.nameEn || form.options.some(item => !item.code || !item.nameZh || !item.nameEn || !item.hexValue)) { wx.showToast({ title: this.data.t.fieldRequired, icon: 'none' }); return } request('/api/admin/color-sets', 'POST', form).then(() => { this.setData({ colorSetForm: emptyColorSet() }); this.load() }).catch(message => wx.showToast({ title: message, icon: 'none' })) },
  chooseProductImages(e) {
    const key = e.currentTarget.dataset.key
    const current = this.data.form[key] || []
    const remaining = 5 - current.length
    if (remaining <= 0) { wx.showToast({ title: this.data.t.imageLimit, icon: 'none' }); return }
    chooseBase64Images(remaining).then(images => this.setData({ [`form.${key}`]: current.concat(images).slice(0, 5) })).catch(message => message && wx.showToast({ title: message, icon: 'none' }))
  },
  removeProductImage(e) {
    const { key, index } = e.currentTarget.dataset
    const images = (this.data.form[key] || []).filter((_, imageIndex) => imageIndex !== Number(index))
    this.setData({ [`form.${key}`]: images })
  },
  chooseShopImage(e) { const key = e.currentTarget.dataset.key; chooseBase64Image().then(image => this.setData({ [`shopForm.${key}`]: image })).catch(message => message && wx.showToast({ title: message, icon: 'none' })) },
  saveShop() {
    const s = this.data.shopForm
    if (!s.nameZh || !s.nameEn) { wx.showToast({ title: this.data.t.fieldRequired, icon: 'none' }); return }
    request('/api/admin/shop', 'PUT', s).then(shop => { this.setData({ shopForm: shop }); wx.showToast({ title: this.data.t.saved }) }).catch(message => wx.showToast({ title: message, icon: 'none' }))
  },
  addProduct() {
    const f = this.data.form
    if (!f.nameZh || !f.nameEn || f.price === '' || f.stock === '' || !f.categoryId || !f.sizeSetId || !f.colorSetId) { wx.showToast({ title: this.data.t.fieldRequired, icon: 'none' }); return }
    const path = this.data.editingProductId ? `/api/admin/products/${this.data.editingProductId}` : '/api/admin/products'
    const method = this.data.editingProductId ? 'PUT' : 'POST'
    request(path, method, { ...f, imageBase64: (f.displayImagesBase64 || [])[0] || '', price: Number(f.price), stock: Number(f.stock), categoryId: Number(f.categoryId) }).then(() => { wx.showToast({ title: this.data.editingProductId ? this.data.t.saved : this.data.t.productAdded }); this.setData({ editingProductId: null, form: { ...emptyProduct(), categoryId: f.categoryId } }); this.load() }).catch(message => wx.showToast({ title: message, icon: 'none' }))
  },
  editProduct(e) { const p = e.currentTarget.dataset.product; const categoryIndex = this.data.categories.findIndex(item => item.id === p.category.id); const sizeSetIndex = this.data.sizeSets.findIndex(item => p.sizeSet && item.id === p.sizeSet.id); const colorSetIndex = this.data.colorSets.findIndex(item => p.colorSet && item.id === p.colorSet.id); this.setData({ editingProductId: p.id, categoryIndex: Math.max(0, categoryIndex), sizeSetIndex: Math.max(0, sizeSetIndex), colorSetIndex: Math.max(0, colorSetIndex), form: { nameZh: p.nameZh || p.name || '', nameEn: p.nameEn || '', descriptionZh: p.descriptionZh || p.description || '', descriptionEn: p.descriptionEn || '', price: String(p.price), stock: String(p.stock), categoryId: String(p.category.id), sizeSetId: String(p.sizeSet && p.sizeSet.id), colorSetId: String(p.colorSet && p.colorSet.id), displayImagesBase64: (p.displayImagesBase64 && p.displayImagesBase64.length ? p.displayImagesBase64 : (p.imageBase64 ? [p.imageBase64] : [])), detailImagesBase64: p.detailImagesBase64 || [] } }) },
  cancelProductEdit() { this.setData({ editingProductId: null, form: { ...emptyProduct(), categoryId: this.data.form.categoryId } }) },
  addCategory() {
    const f = this.data.categoryForm
    if (!f.nameZh || !f.nameEn) { wx.showToast({ title: this.data.t.fieldRequired, icon: 'none' }); return }
    const path = this.data.editingCategoryId ? `/api/admin/categories/${this.data.editingCategoryId}` : '/api/admin/categories'
    const method = this.data.editingCategoryId ? 'PUT' : 'POST'
    request(path, method, { ...f, sortOrder: f.sortOrder !== undefined ? f.sortOrder : this.data.categories.length + 1 }).then(() => { wx.showToast({ title: this.data.editingCategoryId ? this.data.t.saved : this.data.t.categoryAdded }); this.setData({ editingCategoryId: null, categoryForm: { nameZh: '', nameEn: '' } }); this.load() }).catch(message => wx.showToast({ title: message, icon: 'none' }))
  },
  editCategory(e) { const category = e.currentTarget.dataset.category; this.setData({ editingCategoryId: category.id, categoryForm: { nameZh: category.nameZh || category.name || '', nameEn: category.nameEn || '', sortOrder: category.sortOrder } }) },
  cancelCategoryEdit() { this.setData({ editingCategoryId: null, categoryForm: { nameZh: '', nameEn: '' } }) },
  toggleSale(e) { const product = e.currentTarget.dataset.product; request(`/api/admin/products/${product.id}/sale`, 'PATCH', { onSale: !product.onSale }).then(() => this.load()).catch(message => wx.showToast({ title: message, icon: 'none' })) },
  nextStatus(e) { const order = e.currentTarget.dataset.order; const status = order.status === 'CREATED' ? 'ACCEPTED' : order.status === 'ACCEPTED' ? 'COMPLETED' : ''; if (status) request(`/api/admin/orders/${order.id}/status`, 'PATCH', { status }).then(() => this.load()).catch(message => wx.showToast({ title: message, icon: 'none' })) },
  logout() { app.logout() }
})
