package com.geely.callrecord.bean

/**
 * @author zdh
 * date 2019/12/4.
 */
data class BaseResultBean(var status: Int, var message: String) {
    override fun toString(): String {
        return "BaseResultBean(state=$status, message='$message')"
    }
}