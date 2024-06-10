# 简介
`SocialNetworkingExtera`是一款Bukkit插件，对玩家社交进行了扩展。

主要功能：`tpa`、`killself`(自杀)、称号系统(尚未开发)

目前该插件处于alpha版本，有不足欢迎指出！

# 教程
## 主要指令
* `tpa <玩家>`：向指定玩家发起传送请求，等待对方同意，如果对方在5分钟内没有任何反应会自动取消。
可在`config.yml`中禁用它。

与其相关指令（如果tpa被禁用，这些指令也会被禁用）

| 指令及语法             | 作用                          |
|-------------------|-----------------------------|
| `tpac [玩家(可多个)]`  | 同意输入玩家的传送请求，不填则同意**所有**传送请求 |
| `tpde [玩家(可多个)]`  | 拒绝输入玩家的传送请求，不填则拒绝**所有**传送请求 |
| `tpnel [玩家(可多个)]` | 取消输入玩家的传送进程，不填则取消**所有**传送进程 |

* `killself`：自杀。可在`config.yml`中禁用。

* `awarded`：尚未开发

* `setprefix`：尚未开发

## 配置文件
### `config.yml`
服务器主要配置文件。
````
enable_tpa: 是否启用tpa,默认为true
enable_killself: 是否启用killself,默认为true

max_prefixs_worn: 玩家最大佩戴的称号数，默认为1（相关功能尚未开发）

allow_customize_prefix: 是否允许玩家自定义称号，默认为true（相关功能尚未开发）
````

## 数据存储
### `data.yml`
储存玩家数据的主要文件（相关功能尚未开发）。
````
steve(玩家名字):
  register_stamp: 注册时间戳
  last_login_stamp: 上一次登录时间戳
  password_hash: 密码的hash值(Object.hashCode())
  prefixs: 称号列表
````