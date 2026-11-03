const app = getApp()
const request = (path, method = 'GET', data) => new Promise((resolve, reject) => {
  const user = app.globalData.user
  const headers = { 'content-type': 'application/json' }
  if (user) headers.Authorization = user.auth
  wx.request({ url: app.globalData.apiBase + path, method, data, header: headers, success: res => {
    if (res.statusCode >= 200 && res.statusCode < 300) resolve(res.data)
    else reject(res.data && res.data.message ? res.data.message : '请求失败，请检查服务是否已启动')
  }, fail: () => reject('无法连接到服务，请确认后端地址和服务状态') })
})
module.exports = { request }
