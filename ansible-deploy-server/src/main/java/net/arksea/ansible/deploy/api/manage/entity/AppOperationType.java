package net.arksea.ansible.deploy.api.manage.entity;

/**
 * Create by xiaohaixing on 2020/10/26
 */
public enum AppOperationType {
    COMMON,  //普通操作，显示在操作栏
    BUILD,   //构建操作，将会生成新的构建号
    STATUS,  //获取状态，显示在状态栏
    DELETE,  //删除主机上部署实例时自动调用，用于清理实例相关资源
    DELETE_APP //删除应用时自动调用，用于清理应用相关资源
}
