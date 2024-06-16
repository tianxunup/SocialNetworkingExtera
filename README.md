# 简介
`SocialNetworkingExtera`是一款Bukkit插件，对玩家社交进行了扩展。

主要功能：`tpa`、`killself`(自杀)、称号系统

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

* `awarded <玩家> <称号>`(管理员)：授予玩家称号（可使用&代替§）

* `delepithet <玩家>`(管理员)：删除玩家称号

* `epithets [玩家(仅管理员可填)]`：查看玩家称号，不填默认查看自己的

* `setepithet <数字id>(可多个)`：设置要佩戴的称号，数字id可通过 `/epithets` 查看

* `awardedme <称号>`：授予自己称号（可使用&代替§）（如果服务器允许玩家自定义称号）

* `delmyepithet <数字id>`：删除自己的称号（如果服务器允许玩家自定义称号），数字id可通过 `/epithets` 查看

## 配置文件
### `config.yml`
服务器主要配置文件。
```yaml
enable_tpa: 是否启用tpa,默认为true
enable_killself: 是否启用killself,默认为true

max_epithets_worn: 玩家最大佩戴的称号数，默认为1

allow_customize_epithet: 是否允许玩家自定义称号，默认为true
```

## 数据存储
### `data.yml`
储存玩家数据的主要文件（除称号列表以外相关功能尚未开发）。
```yaml
steve(玩家名字):
  register_stamp: 注册时间戳
  last_login_stamp: 上一次登录时间戳
  password_hash: 密码的hash值(Object.hashCode())
  epithets: 称号列表
  epithets_worn: 玩家佩戴称号列表（数字id）
```