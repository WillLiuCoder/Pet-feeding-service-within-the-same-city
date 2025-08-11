核心数据
1. `pet_service_order`（服务订单表）
    - order_id, user_id, pet_id, service_type（喂养/遛狗）, address, scheduled_time
    - status（0待支付/1已派单/2服务中/3已完成/4已评价）
    - special_notes（如“猫怕生人需轻声进门”）

2. `pet_profile`（宠物档案表）
    - pet_id, user_id, name, type（猫/狗）, breed, age
    - health_notes（病史/过敏食物）, photo_url

3. `caregiver_info`（服务人员表）
    - caregiver_id, real_name, id_card, service_zone（海淀区/朝阳区）
    - certification（宠物急救证书编号）, avg_rating（平均评分）

4. `service_log`（服务过程表）
    - log_id, order_id, check_in_time, checkout_time
    - feed_photo, walk_route_map（遛狗GPS轨迹

5. `customer_info` （客户信息表）
    - user_id, name, address, phone



服务模块 
- pet-admin 管理后台后端服务
- pet-client 客户端后端服务


功能设计

|      | 程序形式  | 功能模块     |   |   |
|------|-------|----------|---|---|
| 管理端  | web页面 | 订单模块     |   |   |
|      |       | 服务内容管理   |   |   |
|      |       | 服务人员管理   |   |   |
| 客户端  | 小程序   | 服务人员闲忙查询 |   |   |
|      |       | 服务交易模块   |   |   |
|      |       | 用户信息模块   |   |   |
|      |       | 服务消息推送         |   |   |
| 业务员端 | 小程序   | 服务消息推送   |   |   |
