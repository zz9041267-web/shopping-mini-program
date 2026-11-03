const { request } = require('../../utils/api')
const { getLanguageData, toggleLanguage } = require('../../utils/i18n')
const app = getApp()

const emptyForm = () => ({ recipientName: '', contactPhone: '', shippingAddress: '', defaultAddress: false })

Page({
  data: { ...getLanguageData(), addresses: [], form: emptyForm(), editingId: null, saving: false },
  onShow() { if (!app.requireUser()) return; this.setData(getLanguageData()); this.load() },
  toggleLanguage() { const language = toggleLanguage(); Object.assign(app.globalData, language); this.setData(language) },
  load() { request('/api/addresses').then(addresses => this.setData({ addresses })).catch(message => wx.showToast({ title: message, icon: 'none' })) },
  field(e) { this.setData({ [`form.${e.currentTarget.dataset.key}`]: e.detail.value }) },
  toggleDefault() { this.setData({ 'form.defaultAddress': !this.data.form.defaultAddress }) },
  edit(e) { const address = e.currentTarget.dataset.address; this.setData({ editingId: address.id, form: { recipientName: address.recipientName, contactPhone: address.contactPhone, shippingAddress: address.shippingAddress, defaultAddress: address.defaultAddress } }) },
  cancel() { this.setData({ editingId: null, form: emptyForm() }) },
  save() {
    const form = this.data.form
    if (!form.recipientName.trim() || !form.contactPhone.trim() || !form.shippingAddress.trim()) { wx.showToast({ title: this.data.t.fieldRequired, icon: 'none' }); return }
    this.setData({ saving: true })
    const path = this.data.editingId ? `/api/addresses/${this.data.editingId}` : '/api/addresses'
    request(path, this.data.editingId ? 'PUT' : 'POST', form).then(address => { wx.setStorageSync('selected_address_id', address.id); wx.showToast({ title: this.data.t.addressSaved }); wx.navigateBack() }).catch(message => wx.showToast({ title: message, icon: 'none' })).finally(() => this.setData({ saving: false }))
  },
  remove(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({ title: this.data.t.deleteAddress, content: this.data.t.deleteAddressHint, success: result => { if (result.confirm) request(`/api/addresses/${id}`, 'DELETE').then(() => { if (String(wx.getStorageSync('selected_address_id')) === String(id)) wx.removeStorageSync('selected_address_id'); this.load() }).catch(message => wx.showToast({ title: message, icon: 'none' })) } })
  }
})
