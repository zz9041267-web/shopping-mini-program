const { request } = require('../../utils/api')
const { getLanguageData, toggleLanguage } = require('../../utils/i18n')
const app = getApp()
Page({
 data:{...getLanguageData(),orderNo:'',shop:{}},
 onLoad(options){this.setData({orderNo:options.orderNo||''})},
 onShow(){this.setData(getLanguageData());request('/api/catalog').then(data=>this.setData({shop:data.shop})).catch(()=>{})},
 toggleLanguage(){const language=toggleLanguage();Object.assign(app.globalData,language);this.setData(language)},
 previewWechat(){const image=this.data.shop.wechatQrBase64;if(!image){wx.showToast({title:this.data.t.noWechat,icon:'none'});return}wx.previewImage({current:image,urls:[image]})},
 toOrders(){wx.redirectTo({url:'/pages/orders/orders'})}
})
