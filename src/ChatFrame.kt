import java.awt.event.ActionListener
import javax.swing.*

// 窗体，只用于显示，逻辑处理在Main里，这个类并不重要
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
    private val fileButton = JButton("文件")

    init {
        initUI()
    }

    // 设置UI事件
    fun handleUIEvent(listener: ActionListener) {
        sendButton.addActionListener(listener)
        fileButton.addActionListener(listener)
    }

    // 初始化 UI
    private fun initUI() {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setDefaultLookAndFeelDecorated(true)
        isResizable = false

        textArea.lineWrap = true
        textArea.wrapStyleWord = true
        textArea.isEditable = false

        scrollPanel.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        scrollPanel.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER

        mainPanel.add(scrollPanel)
        mainPanel.add(sendInput)
        mainPanel.add(sendButton)
        mainPanel.add(fileButton)
        contentPane.add(mainPanel)
        setSize(600, 420)
        setLocationRelativeTo(null)
    }

    fun showUI() {
        isVisible = true
    }

    fun setSendFileButtonActive(active: Boolean) {
        sendButton.isEnabled = active
    }
}