# SimpleMusic
简单的用服务播放音乐，并在界面进行控制。

## 截图
![这是截图](https://raw.githubusercontent.com/peerless2012/SimpleMusic/master/ScreenShoots/ScreenGif.gif)

##主要功能点
- 在后台服务（Service）中进行音乐播放。
- 在主界面中对音乐服务进行绑定
- 在主界面进行音乐的控制
- 在主界面中轮询音乐播放的进度等信息
- 通过主线程Handle来更新界面
- 主界面销毁以后回收相应的资源（轮询的Thread，更新UI的Handle），避免内存泄漏
