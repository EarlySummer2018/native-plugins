## cx-getAppList 安卓原生插件

本插件为安卓原生插件，实现根据文件类型获取安卓手机已安装且可以打开该文件类型的APP列表并返回
### 参数说明

`getOpenAppList(options, callbakc, errCallback)：` 该方法接收三个参数。
- `options：` 参数列表
	+ `type：` 文件类型，根据类型获取可以打开该类型文件的App。**`必填`**
	+ `quality：` 压缩质量，0-100，值越大,压缩后图片质量越高,但图片文件也会越大。100表示无损压缩,也就是不做任何压缩,保留图片原始质量。**`必填`**
	+ `width：` App 图标宽度。**`必填`**
	+ `height：` App 图标高度。**`必填`**
- `callbakc：`成功回调，返回App列表，图标为 `base64`	格式，渲染时需要自行添加前缀 `data:image/png;base64,`
- `errCallback`失败回调，返回错误信息

### 使用方法

##### vue2

```vue
<template>
	<view>
		<button @click="getAppList()"></button>
	</view>
</template>
```
```js
export default {
	// #ifdef APP-PLUS
	// requireNativePlugin 是 app 端才有的api，最好加上标识，以免报错导致我编译失败
	const plugins = uni.requireNativePlugin('cx-getAppList')
	// #endif
	data(){
		return {
			appList: []
		}
	}
	methods:{
		getAppList() {
			const that = this
			plugin.getOpenAppList({
				type: 'image/*',
				quality: 100,
				width: 60,
				height: 60
			}, res => {
				if (!res.length) {
					uni.showModal({
						title: '温馨提示',
						content: '未找到可打开该文件的APP',
						icon: 'none'
					})
				} else {
					that.appList = res
				}
			},
			err=>{
				console.log(err)
			})
		}
	}
}
```

##### vue3

```vue
<template>
	<view>
		<button @click="getAppList()"></button>
	</view>
</template>
```
```ts
<script lang="ts" setup>
// #ifdef APP-PLUS
// requireNativePlugin 是 app 端才有的api，最好加上标识，以免报错导致我编译失败
const plugins = uni.requireNativePlugin('cx-getAppList')
// #endif

const getAppList = () => {
	const that = this
	plugin.getOpenAppList({
		type: 'image/*',
		quality: 100,
		width: 60,
		height: 60
	}, res => {
		if (!res.length) {
			uni.showModal({
				title: '温馨提示',
				content: '未找到可打开该文件的APP',
				icon: 'none'
			})
		} else {
			that.appList = res
		}
	},
	err=>{
		console.log(err)
	})
}
</script>
```