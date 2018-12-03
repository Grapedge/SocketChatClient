import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import javax.swing.*

const val HOST = "127.0.0.1"
const val PORT = 3366

fun main(args : Array<String>) {
    try {
        ChatClient("Grapes Chat", HOST, PORT)
    } catch (e: Exception) {
        println(e.message)
    }
}

class ChatFrame(title: String) : JFrame(title) {

    val textArea = JTextArea(20, 50)
    val inputText: String
        get() {
            var str = sendInput.text
            sendInput.text = ""
            return str
        }

    private val sendInput = JTextField(30)
    private val scrollPanel = JScrollPane(textArea)
    private val mainPanel = JPanel()

    private val sendButton = JButton("发送")



    init {
        initUI()
    }

    // 设置UI事件
    fun handleUI(listener: ActionListener) {
        sendButton.addActionListener(listener)
    }

    // 初始化 UI
    private fun initUI() {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        textArea.lineWrap = true
        textArea.wrapStyleWord = true
        textArea.isEditable = false

        scrollPanel.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        scrollPanel.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER

        mainPanel.add(scrollPanel)
        mainPanel.add(sendInput)
        mainPanel.add(sendButton)
        contentPane.add(mainPanel)
        setSize(600, 420)
        setLocationRelativeTo(null)
    }

    fun showUI() {
        isVisible = true
    }
}

class ChatClient(title: String, host: String, port: Int) : Socket(host, port), ActionListener {

    var reader = BufferedReader(InputStreamReader(inputStream))
    var writer = PrintWriter(outputStream, true)
    // Click event here
    override fun actionPerformed(e: ActionEvent?) {
        println("Send message here")
        writer.println(frame.inputText)
    }

    var frame = ChatFrame(title)
    init {
        frame.showUI()
        frame.handleUI(this)
        ChatReader(reader, frame.textArea)
        println("加载完成")
    }


}

class ChatReader(var reader: BufferedReader, var textArea: JTextArea) : Thread() {
    init {
        start()
    }

    override fun run() {
        try {
            var message = reader.readLine()
            while (message != null) {
                println("receive: $message")
                textArea.append("$message\n")
                message = reader.readLine()
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
