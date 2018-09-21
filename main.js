import electron from 'electron'
import path from 'path'
import url from 'url'

const app = electron.app
const BrowserWindow = electron.BrowserWindow

let mainWindow
app.on('ready', createWindow)
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') app.quit()
})

app.on('active', () => {
  if (mainWindow === null) createWindow()
})

function createWindow() {
  mainWindow = new BrowserWindow({width: 800, height: 600})
  mainWindow.loadURL(url.format({
    pathname: path.join(__dirname, 'index.html'),
    protocol: 'file',
    slashes: true
  }))

  mainWindow.on('closed', () => {
    mainWindow = null
  })
}
