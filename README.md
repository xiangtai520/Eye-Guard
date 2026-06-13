# Eye Guard 👁️

<p align="center">
  <img src="https://github.com/xiangtai520/Eye-Guard/blob/main/Eye%20Guard_1.0.1714.apk_icon.webp" width="120" height="120" alt="Eye Guard Logo"/>
</p>

<p align="center">
  <strong>您的视力健康管家 —— 严格践行 20-20-20 护眼黄金法则</strong>
</p>

<p align="center">
  <a href="https://developer.android.com/about/versions/12"><img src="https://img.shields.io/badge/Android-12%2B-green.svg?style=flat-flat" alt="Android MinSDK"/></a>
  <a href="https://m3.material.io/"><img src="https://img.shields.io/badge/Design-Material%203-blue.svg?style=flat-flat" alt="Material Design 3"/></a>
  <a href="https://github.com/xiangtai520/Eye-Guard/releases"><img src="https://img.shields.io/github/v/release/xiangtai520/Eye-Guard?color=orange" alt="Release"/></a>
</p>

---

## 📖 简介

**Eye Guard** 是一款基于 **20-20-20 护眼黄金法则**打造的极简、无打扰安卓护眼辅助工具。应用通过前台服务与强提醒机制，在您沉浸于屏幕工作或娱乐时，适时引导您抬起头，远眺放松，告别数字视觉疲劳。

本应用全面遵循 **Material Design 3 (M3)** 设计规范，支持 Android 12+ 的 **Material You 动态色彩** 提取，拥有像素级的系统原生质感。

> 💡 **什么是 20-20-20 法则？**
> 每持续看屏幕用眼 **20 分钟**，往 **20 英尺**外（约 6 米）处远眺，让双眼聚焦点移向远方，舒缓精细肌肉至少 **20 秒**。

---

## 📸 软件截图

| 1. 护眼主页 | 2. 偏好设置 | 3. 强提醒界面 | 4. 关于页面 |
| :---: | :---: | :---: | :---: |
| <img src="1000031602.png" width="200"/> | <img src="1000031604.png" width="200"/> | <img src="Strong reminder - take a screenshot.png" width="200"/> | <img src="1000031606.png" width="200"/> |

---

## ✨ 核心功能

*   **⏳ 黄金法则定时器：** 启动后自动开启 20 分钟工作倒计时，主页配备优雅的渐变科技感呼吸圆环，状态一目了然。
*   **🚨 穿透式强提醒：** 倒计时结束时，自动拉起全屏“眼部深呼吸时间”强制全屏覆盖，强制执行 20 秒远眺。支持紧急情况下“跳过当前休眠”。
*   **🎨 M3 原生调色盘：** 内置高还原度的 Android 13+ 系统级四色圆盘主题选择器。提供**极客暗黑**、**森林护眼**、**深海沉静**三款预设，并支持一键开启**跟随系统动态色彩 (Material You)**。
*   **🎛️ 自由定制时段：** 支持通过拖动条动态微调“专注工作时长（10-60分钟）”与“远眺放松时长”。
*   **🎵 提示音管理：** 支持放松提示音试听与管理（当前内置优雅的 Chimes 铃声）。
*   **🔒 100% 纯本地运行：** 基于 Google AI Studio 辅助构建，**应用无任何联网权限**，不收集任何用户隐私与数据，纯净安心。

---

## 🛠️ 技术栈

*   **开发语言：** Kotlin
*   **UI 框架：** Jetpack Compose (Material Design 3)
*   **后台调度：** Android Foreground Service (前台服务，保证后台定时器不被系统误杀)
*   **图形绘制：** Compose Canvas (用于绘制四色圆盘和主页呼吸圆环)

---

## ⚙️ 必要权限说明

为了保证定时器在后台不被系统恶意杀死，以及到期时能正常弹出提醒，首次启动时请授予应用以下权限：
1.  **通知权限**：用于常驻后台计时并在到期时发出声音横幅。
2.  **悬浮窗/后台弹出权限**：允许 20 分钟到期时，应用能从后台直接拉起全屏休眠界面。
3.  **电池优化改为「无限制」**：防止系统为了省电杀掉前台服务定时器。

---

## 📥 下载安装

您可以前往 [Releases 页面](https://github.com/xiangtai520/Eye-Guard/releases) 下载最新的 APK 安装包。

---

## 📝 免责声明与致谢

*   本软件基于 **Google AI Studio** 辅助构建。
*   应用不含任何广告、无联网行为。如果觉得好用，欢迎点一个 **Star 🌟** 支持作者！
