import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import javax.swing.JOptionPane
import kotlin.system.exitProcess

// 全局变量
const val HOST = "127.0.0.1"
const val PORT = 3366   // 聊天端口
const val FILE_PORT = PORT + 1  // 文件传输端口
const val FILE_PATH = "D:\\\\GrapesChat\\ClientFile\\"

fun main(args : Array<String>) {
    val main = Main()
    // 尝试加载客户端
    try {
        ChatClient(main, HOST, PORT, FILE_PORT, FILE_PATH)
    } catch (e: Exception) {
        println(e.message)
        main.showMessage("网络连接错误：${e.message}")
        exitProcess(0)
    }
}

// 负责 UI 更新以及逻辑处理
class Main : ActionListener, ChatClientHandler {
    private lateinit var client: ChatClient

    // 设置 Socket
    override fun setClient(chatClient: ChatClient) {
        client = chatClient
    }

    // 窗体
    private val frame = ChatFrame("Grapes Chat")

    init {
        frame.showUI()
        frame.handleUIEvent(this)
    }

    // 在聊天区域增加消息
    override fun appendMessage(msg: String) {
        frame.textArea.append(msg)
    }

    // 各种按钮（或其他）的动作事件
    override fun actionPerformed(e: ActionEvent?) {
        when (e?.actionCommand) {
            "发送" -> {
                // 发送信息
                client.send(Message(0, frame.inputText, null))
            }
            "文件" -> {
                // 在输入框输入文件地址后将其发送
                val file = File(frame.inputText)
                if (file.exists()) {
                    client.send(Message(1, "发送文件：${file.name}", file))
                } else {
                    showMessage("文件不存在，请检查。\n发送示例：E:\\\\test.txt")
                }
            }
        }
    }

    // 显示一个对话框
    override fun showMessage(msg: String) {
        JOptionPane.showMessageDialog(frame, msg)
    }

    // 如果文件开始发送，禁用这个按钮
    override fun setFileSendState(start: Boolean) {
        frame.setSendFileButtonActive(!start)
    }
}

