const MAX_IMAGE_SIZE = 3 * 1024 * 1024

function readBase64Image(path, file) {
  return new Promise((resolve, reject) => {
    if (file && file.size > MAX_IMAGE_SIZE) { reject('每张图片不能超过 3 MB'); return }
    const extension = (path.split('.').pop() || 'jpeg').toLowerCase()
    const mime = extension === 'png' ? 'image/png' : extension === 'gif' ? 'image/gif' : extension === 'webp' ? 'image/webp' : 'image/jpeg'
    wx.getFileSystemManager().readFile({ filePath: path, encoding: 'base64', success: read => resolve(`data:${mime};base64,${read.data}`), fail: () => reject('无法读取图片') })
  })
}

function chooseBase64Images(count = 1) {
  return new Promise((resolve, reject) => {
    wx.chooseImage({ count: Math.max(1, Math.min(count, 9)), sizeType: ['compressed'], sourceType: ['album', 'camera'], success: result => {
      Promise.all(result.tempFilePaths.map((path, index) => readBase64Image(path, result.tempFiles[index]))).then(resolve).catch(reject)
    }, fail: () => reject('') })
  })
}

function chooseBase64Image() { return chooseBase64Images(1).then(images => images[0]) }

module.exports = { chooseBase64Image, chooseBase64Images }
