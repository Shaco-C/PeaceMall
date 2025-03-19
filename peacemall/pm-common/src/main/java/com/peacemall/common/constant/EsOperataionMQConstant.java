package com.peacemall.common.constant;

public interface EsOperataionMQConstant {
    //用户、商店、商品的增删改队列

    //================== 通用死信队列 ==================
    String ES_OPERATION_DLX_EXCHANGE_NAME = "es.operation.dlx.direct.exchange";
    String ES_OPERATION_DLX_QUEUE_NAME = "es.operation.dlx.queue";
    String ES_OPERATION_DLX_ROUTING_KEY = "es.operation.dlx";

    // ================== 用户（user） ==================
    String ES_OPERATION_USER_EXCHANGE_NAME = "es.operation.user.direct.exchange";
    String ES_USER_ADD_QUEUE_NAME = "es.user.add.queue";
    String ES_USER_DELETE_QUEUE_NAME = "es.user.delete.queue";
    String ES_USER_UPDATE_QUEUE_NAME = "es.user.update.queue";
    String ES_ADD_USER_ROUTING_KEY = "es.user.add";
    String ES_DELETE_USER_ROUTING_KEY = "es.user.delete";
    String ES_UPDATE_USER_ROUTING_KEY = "es.user.update";

    // ================== 店铺（Shop） ==================
    String ES_OPERATION_SHOP_EXCHANGE_NAME = "es.operation.shop.direct.exchange";

    // 正常队列
    String ES_SHOP_ADD_QUEUE_NAME = "es.shop.add.queue";
    String ES_SHOP_DELETE_QUEUE_NAME = "es.shop.delete.queue";
    String ES_SHOP_UPDATE_QUEUE_NAME = "es.shop.update.queue";

    // 正常路由键
    String ES_ADD_SHOP_ROUTING_KEY = "es.shop.add";
    String ES_DELETE_SHOP_ROUTING_KEY = "es.shop.delete";
    String ES_UPDATE_SHOP_ROUTING_KEY = "es.shop.update";


    // ================== 产品（Product） ==================
    String ES_OPERATION_PRODUCT_EXCHANGE_NAME = "es.operation.product.direct.exchange";

    // 正常队列
    String ES_PRODUCT_ADD_QUEUE_NAME = "es.product.add.queue";
    String ES_PRODUCT_DELETE_QUEUE_NAME = "es.product.delete.queue";
    String ES_PRODUCT_UPDATE_QUEUE_NAME = "es.product.update.queue";
    // 正常路由键
    String ES_ADD_PRODUCT_ROUTING_KEY = "es.product.add";
    String ES_DELETE_PRODUCT_ROUTING_KEY = "es.product.delete";
    String ES_UPDATE_PRODUCT_ROUTING_KEY = "es.product.update";
}
