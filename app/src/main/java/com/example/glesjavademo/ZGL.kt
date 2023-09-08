package com.example.glesjavademo

import android.graphics.Rect
import android.opengl.GLES30.*
import java.nio.Buffer


/**
 * 归纳  总结  opengles  3.0 的 api
 * 常用的 api 列出来.. 并做注释 方便以后用到...
 * 代码示例可以作为参考用.
 */
object ZGL {

    /*************************** common ************************/
    /**
     * 开启 顶点属性 对应的位置.
     */
    fun glEnableVertexAttribArray(index: Int) {
    }

    /**
     * 设置定点属性...
     *@param index  对应 shader 中的 location
     *@param size,
     *@param type,
     *@param normalized,
     *@param stride,
     *@param buffer
     */
    fun glVertexAttribPointer(index: Int, size: Int, type: Int, stride: Int, buffer: Buffer) {

    }

    /******************************* FBO ************************/


    /**
     * 切换 帧缓冲区
     * @param fboId 0: 默认 系统显示在窗口上的
     */
    fun changeFbo(fboId: Int) {

    }


    /**
     * 配置帧缓冲区  位块 传送, 从 fboRead 传送到  fboWrite
     * @param fboRead  要读取数据的  帧缓冲区对象
     * @param fboWrite 要写入数据的  帧缓冲区对象
     */
    fun fboTransferConfig(fboRead: Int, fboWrite: Int) {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fboWrite)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboRead)
    }

    /**
     *
     * 颜色附着点 数据传送 , 传送前需要 调用 [fboTransferConfig] 方法进行配置..
     * @param attachment: 可串色的颜色附着点
     * @param recFrom :  读取帧缓冲区 的区域
     * @param recTo :   写入帧缓冲区 的区域
     */
    fun fboReadBufferColor(attachment: Int, recFrom: Rect, recTo: Rect) {
        glReadBuffer(attachment)
        glBlitFramebuffer(
            recFrom.left, recFrom.top, recFrom.right, recFrom.bottom,
            recTo.left, recTo.top, recTo.right, recTo.bottom,
            GL_COLOR_BUFFER_BIT, GL_LINEAR
        )
    }


}