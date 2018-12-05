interface ChatClientHandler {
    // 用于设置具体要 handle 哪一个 client
    fun setClient(chatClient: ChatClient)
    // 更新消息
    fun appendMessage(msg: String)
    // 显示消息
    fun showMessage(msg: String)
    // 设置文件发送状态
    fun setFileSendState(start: Boolean)
}