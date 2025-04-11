# PeaceMall 

## 项目概述

PeaceMall 是一个基于微服务架构的现代化电子商务平台，提供完整的在线购物解决方案。系统支持用户注册登录、商品浏览购买、订单管理、商家入驻、支付结算等核心功能，为消费者、商家和平台管理员提供全方位的服务。

![](https://github.com/Shaco-C/PeaceMall/blob/main/documents/sql_and_configurationsInfo/er.png)

## 技术栈

### 后端技术栈

- **开发语言**: Java 11
- **微服务框架**: Spring Boot 2.7.12 + Spring Cloud 2021.0.3
- **服务治理**: Spring Cloud Alibaba 2021.0.4.0（Nacos 注册中心和配置中心）
- **数据访问层**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0.23
- **搜索引擎**: Elasticsearch 7.12.1
- **API 文档**: Swagger
- **工具库**: Lombok, Hutool
- **缓存**: Redis
- **消息队列**: RocketMQ

## 系统架构

PeaceMall 采用微服务架构，按业务功能划分为多个独立部署的服务，包括：

1. **API 网关**: 统一入口，请求路由，负载均衡
2. **用户服务**: 用户注册、登录、个人信息管理
3. **商品服务**: 商品管理、分类管理
4. **订单服务**: 订单创建、支付、物流
5. **购物车服务**: 购物车管理
6. **支付服务**: 钱包管理、充值、提现
7. **评价服务**: 商品评价、回复
8. **搜索服务**: 基于 Elasticsearch 的商品搜索
9. **优惠券服务**: 优惠券管理、使用
10. **商家服务**: 商家入驻、店铺管理
11. **文件服务**: 图片上传、存储
12. **日志服务**: 系统日志记录

## 技术亮点

### 1. 微服务架构设计

- **服务解耦**: 基于业务领域模型划分微服务，每个服务独立部署、独立扩展
- **服务注册与发现**: 使用 Nacos 实现服务注册与发现，提高系统可用性
- **配置中心**: 集中管理配置，支持动态配置刷新
- **服务网关**: 统一入口，实现路由转发、请求过滤、限流熔断等功能
- **Feign 远程调用**: 简化服务间通信，实现声明式 API 调用

### 2. 高性能搜索引擎

- **Elasticsearch 集成**: 实现商品的高效全文检索
- **自定义分词**: 针对商品名称、描述等进行中文分词优化
- **搜索建议**: 智能搜索提示功能
- **数据同步机制**: MySQL 与 Elasticsearch 数据实时同步

### 3. 分布式事务解决方案

- **TCC 事务**: 在关键业务流程中实现 Try-Confirm-Cancel 模式
- **最终一致性**: 基于消息队列的异步事务处理
- **分布式锁**: 防止并发操作导致的数据不一致

### 4. 安全架构

- **统一认证中心**: 基于 JWT 的用户认证
- **细粒度权限控制**: 基于角色的访问控制 (RBAC)
- **API 权限校验**: 接口级别的权限控制
- **数据脱敏**: 敏感数据自动脱敏处理
- **SQL 注入防护**: MyBatis 参数化查询

### 5. 高可用设计

- **服务集群**: 关键服务多实例部署
- **负载均衡**: 实现服务间的负载均衡
- **熔断降级**: 防止服务雪崩
- **限流保护**: 保护系统免受突发流量冲击
- **异步处理**: 关键业务异步化，提高系统吞吐量

### 6. 缓存策略

- **多级缓存**: 本地缓存 + Redis 分布式缓存
- **缓存穿透防护**: 布隆过滤器实现
- **缓存击穿防护**: 互斥锁 + 热点数据预加载
- **缓存雪崩防护**: 过期时间随机化

### 7. 支付体系

- **内部钱包系统**: 用户余额管理、交易流水记录
- **待确认金额机制**: 确保交易安全，防止资金损失
- **提现审核流程**: 完善的提现申请与审核机制

## 核心业务功能

### 1. 用户中心

- 用户注册、登录、个人信息管理
- 收货地址管理
- 普通用户申请成为商家的流程

### 2. 商品系统

- 三级分类管理
- 多规格商品配置（颜色、尺寸、版本等组合）
- 商品库存实时管理
- 商品上下架、审核机制

### 3. 订单系统

- 购物车结算
- 订单创建、支付、发货、收货完整流程
- 退货申请与处理

### 4. 营销系统

- 多种类型优惠券（满减、折扣、赠品等）
- 不同适用范围（全场、店铺、分类、商品）
- 灵活的有效期设置（固定时段或动态有效期）

### 5. 支付钱包

- 用户钱包余额管理
- 充值、提现功能
- 交易流水记录
- 待确认金额机制，保障交易安全

### 6. 评价系统

- 商品评分与文字评价
- 层级评价回复
- 评价管理与审核

### 7. 搜索系统

- 智能商品搜索
- 多维度筛选（分类、价格、品牌等）
- 搜索结果排序优化

### 8. 商家管理

- 商家入驻申请与审核
- 店铺信息管理
- 商品发布与管理
- 订单处理与物流管理

## 系统特点

1. **完整的电商业务链路**: 从用户注册到下单支付，再到评价反馈，覆盖电商全流程
2. **灵活的商品规格体系**: 支持复杂的商品多规格配置，满足多样化商品需求
3. **细致的权限控制**: 基于不同角色（普通用户、商家、管理员）的权限精细管理
4. **可靠的订单处理**: 完善的订单状态流转和异常处理机制
5. **安全的支付体系**: 内置钱包系统，待确认金额机制保障交易安全
6. **丰富的营销工具**: 多种类型优惠券，促进销售转化

## 项目特色

1. **微服务架构**: 松耦合设计，每个服务可独立演进
2. **高性能**: 合理的缓存策略和异步处理机制
3. **高可用**: 服务熔断、限流、降级保障系统稳定性
4. **可扩展**: 模块化设计，易于扩展新功能
5. **安全可靠**: 完善的安全机制和数据一致性保障



# PeaceMall电商系统需求规格说明书

## 1. 引言

### 1.1 目的
本文档详细描述PeaceMall电商系统的功能需求和技术规格，作为系统开发和验收的依据。它旨在为开发团队、项目管理者和利益相关者提供明确的系统功能理解。

### 1.2 范围
PeaceMall是一个完整的电子商务平台，支持用户注册登录、商品浏览购买、订单管理、商家入驻、支付结算等核心功能。本系统的目标用户包括普通消费者、商家和平台管理员。

### 1.3 系统概述
系统采用微服务架构，基于Spring Cloud和Spring Cloud Alibaba构建，包含用户服务、商品服务、订单服务、支付服务等多个微服务模块，通过服务网关统一对外提供API接口。

## 2. 总体描述

### 2.1 产品背景
随着电子商务的迅速发展，市场需要一个功能完善、架构现代化的电商平台，满足各类用户的在线购物需求。PeaceMall旨在提供安全、高效的电商服务体验。

### 2.2 产品功能概述
- 用户管理：注册、登录、个人信息管理
- 商品管理：商品发布、管理、搜索、分类展示
- 订单管理：下单、支付、配送、退款
- 购物车：添加商品、管理购物车
- 支付系统：余额支付、第三方支付接口集成
- 评价系统：商品评价、评分
- 商家管理：商家入驻、商品管理、订单处理
- 搜索功能：基于ElasticSearch的商品搜索
- 优惠券系统：发放、使用、管理优惠券

### 2.3 用户类型
1. **普通用户**：消费者，可以浏览、购买商品，管理个人订单
2. **商家用户**：可以在平台上开店并管理商品和订单
3. **平台管理员**：管理整个平台的运营，包括用户管理、商家审核等

## 3. 系统架构

### 3.1 技术架构
- **开发语言**：Java 11
- **微服务框架**：Spring Boot 2.7.12 + Spring Cloud 2021.0.3
- **服务治理**：Spring Cloud Alibaba 2021.0.4.0
- **数据持久层**：MyBatis-Plus 3.5.5
- **数据库**：MySQL 8.0.23
- **搜索引擎**：Elasticsearch 7.12.1
- **其他工具**：Lombok, Hutool

### 3.2 微服务模块划分
- **pm-gateway**：API网关，请求路由和统一入口
- **user-service**：用户服务，处理用户注册、登录等功能
- **product-service**：商品服务，管理商品信息
- **order-service**：订单服务，处理订单相关逻辑
- **cart-service**：购物车服务
- **shop-service**：商家店铺服务
- **wallet-service**：钱包服务，处理用户余额
- **coupon-service**：优惠券服务
- **review-service**：评价服务
- **search-service**：搜索服务
- **file-service**：文件服务，处理上传文件
- **log-service**：日志服务，记录系统日志
- **favorite-service**：收藏服务
- **pm-common**：公共模块，存放共享代码
- **pm-api**：API定义模块，定义微服务间的接口

## 4. 详细功能需求

### 4.1 用户管理
#### 4.1.1 用户注册
- 用户可以通过用户名、密码、手机号、邮箱进行注册
- 系统需验证用户信息的唯一性
- 用户注册后状态为待审核（PENDING）

#### 4.1.2 用户登录
- 支持用户名、手机号或邮箱与密码组合的登录方式
- 记录用户最后登录时间
- 返回JWT令牌用于后续接口认证

#### 4.1.3 用户信息管理
- 支持修改用户基本信息（昵称、签名、头像）
- 支持修改密码、手机号、邮箱（需验证）
- 用户可以注销账号（状态变为CLOSED）

#### 4.1.4 地址管理
- 用户可添加多个收货地址
- 支持设置默认地址
- 地址信息包括：收件人、电话、国家、省市区、详细地址

### 4.2 商家管理
#### 4.2.1 商家入驻
- 普通用户可申请成为商家
- 需提交店铺名称、简介、头像等信息
- 管理员审核通过后，用户角色变更为MERCHANT

#### 4.2.2 店铺管理
- 商家可以管理自己的店铺信息
- 店铺状态包括：正常、冻结、注销
- 店铺与用户是一对一关系

### 4.3 商品管理
#### 4.3.1 商品分类
- 系统支持三级商品分类
- 每个商品必须属于某一分类
- 分类支持图标设置

#### 4.3.2 商品发布
- 商家可发布商品，包括基本信息、价格、库存等
- 支持多规格配置（如颜色、尺寸等不同组合）
- 商品发布后需经平台审核

#### 4.3.3 商品图片
- 支持商品多图片上传
- 可设置主图和排序
- 图片通过file-service存储和管理

#### 4.3.4 库存管理
- 支持普通库存和预售模式
- 记录库存变动日志
- 订单支付时自动扣减库存

### 4.4 购物车
#### 4.4.1 添加商品
- 用户可将商品添加到购物车
- 支持选择商品规格和数量
- 同一商品可重复添加（数量累加）

#### 4.4.2 购物车管理
- 用户可查看、修改购物车中商品数量
- 可删除购物车中的商品
- 支持商品批量结算

### 4.5 订单系统
#### 4.5.1 订单创建
- 用户可从购物车或直接购买创建订单
- 订单包含商品信息、收货地址、支付信息等
- 订单按商家拆分为多个子订单

#### 4.5.2 订单状态
- 正常订单状态流转：待支付→待发货→已发货→运输中→已送达→已收货
- 可取消订单（限未发货状态）

#### 4.5.3 退货管理
- 支持申请退货流程
- 退货状态：未申请→已申请→审核通过/拒绝→已退货→商家已收到

### 4.6 支付系统
#### 4.6.1 钱包管理
- 用户拥有个人钱包，包含可用余额和待确认金额
- 支持充值、提现、消费记录
- 交易记录完整保存

#### 4.6.2 支付方式
- 支持余额支付
- 预留支付宝、微信支付接口

#### 4.6.3 提现申请
- 用户可申请提现到外部账户
- 提现需经平台审核
- 审核通过后进行实际转账操作

### 4.7 评价系统
#### 4.7.1 商品评价
- 用户可对已购买商品进行评价
- 支持评分（1-5星）和文字评价
- 评价可分层级（支持回复）

#### 4.7.2 评价管理
- 商家可回复评价
- 平台可审核举报的评价
- 评价默认状态为已通过

### 4.8 优惠券系统
#### 4.8.1 优惠券类型
- 支持全场、店铺、分类、单品等多种适用范围
- 支持满减、折扣、赠品等多种优惠方式
- 支持固定时段和动态有效期

#### 4.8.2 优惠券发放
- 商家或平台可创建优惠券
- 支持公开领取和定向发放
- 可设置每人限领张数

#### 4.8.3 优惠券使用
- 用户下单时可选择符合条件的优惠券
- 记录优惠券使用状态
- 优惠券有效期管理

### 4.9 搜索功能
- 基于Elasticsearch实现商品搜索
- 支持关键词、分类、价格等多维度筛选
- 支持搜索结果排序和分页

### 4.10 收藏功能
- 用户可收藏感兴趣的商品
- 支持查看和管理收藏列表
- 收藏商品有库存或价格变动时可通知用户

## 5. 非功能需求

### 5.1 性能需求
- 系统响应时间：普通请求响应时间不超过1秒
- 并发处理能力：支持每秒至少1000个并发请求
- 系统可用性：99.9%的可用时间

### 5.2 安全需求
- 用户密码加密存储
- API接口权限控制
- 敏感操作需二次验证
- 防止SQL注入、XSS等常见攻击

### 5.3 可靠性需求
- 系统故障自恢复能力
- 重要数据定期备份
- 关键操作事务保证
- 死信队列处理消息异常

### 5.4 扩展性需求
- 微服务架构支持横向扩展
- 模块间低耦合，便于功能扩展
- 支持未来业务增长的可扩展性

## 6. 数据库设计

系统包含多个数据表，主要包括：

### 6.1 用户相关
- users: 用户基本信息
- user_address: 用户地址信息

### 6.2 商家相关
- shops: 商家店铺信息
- merchant_applications: 商家入驻申请

### 6.3 商品相关
- products: 商品基本信息
- product_configurations: 商品规格配置
- product_images: 商品图片
- categories: 商品分类
- stock_change_logs: 库存变动日志

### 6.4 交易相关
- cart_items: 购物车项
- orders: 订单信息
- order_details: 订单商品详情

### 6.5 支付相关
- wallet: 用户钱包
- flow_logs: 资金流水
- withdraw_request: 提现申请

### 6.6 营销相关
- coupon_types: 优惠券类型
- coupons: 优惠券实例
- user_coupons: 用户领取的优惠券

### 6.7 其他功能
- reviews: 商品评价
- favorites: 收藏商品
- dead_letter_log: 消息死信日志

## 7. 接口规范

### 7.1 API响应格式
所有API统一返回以下格式：
```json
{
  "code": 200,  // 状态码
  "msg": "success",  // 消息
  "data": {}  // 数据负载
}
```

### 7.2 错误码规范
- 200: 成功
- 4xx: 客户端错误
- 5xx: 服务器错误

### 7.3 认证方式
- 基于JWT的接口认证
- Token通过请求头Authorization传递

## 8. 部署要求

### 8.1 环境要求
- JDK 11+
- MySQL 8.0+
- Elasticsearch 7.x
- Redis
- Nacos (服务发现和配置)

### 8.2 硬件推荐
- CPU: 8核+
- 内存: 16GB+
- 硬盘: 100GB+

## 9. 未来规划

### 9.1 功能扩展
- 会员VIP系统
- 直播带货功能
- 社区互动功能
- 智能推荐系统

### 9.2 技术演进
- 容器化部署
- 性能优化
- 安全加固

## 10. 附录

### 10.1 术语表
- PeaceMall: 本电商系统名称
- 微服务: 一种分布式系统架构模式
- JWT: JSON Web Token，用于身份验证的令牌格式

### 10.2 参考文档
- Spring Boot官方文档
- Spring Cloud官方文档
- MySQL数据库设计最佳实践 
