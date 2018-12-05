import java.io.*
import java.net.Socket

/**
 * @Auther: Grapes
 * @Description: Socket 客户端
 */
class ChatClient(var handler: ChatClientHandler, var host: String, port: Int, var filePort: Int, var path: String) : Socket(host, port) {

    init {
        handler.setClient(this)
    }

    var reader = DataInputStream(inputStream)
    var writer = DataOutputStream(outputStream)


    // 发送消息
    fun send(msg: Message) {
        // 传入消息类型
        // 定义： 0 为普通消息，1 为文件消息
        writer.writeInt(msg.type)
        when (msg.type) {
            // 0 发送普通消息
            0 -> sendMessage(msg)
            // 1 发送文件，在另一个线程进行
            1 -> Thread {
                sendMessage(msg)
                // 禁用文件发送功能
                handler.setFileSendState(true)
                sendFile(msg)
                // 文件发送结束，启用文件发送功能
                handler.setFileSendState(false)
            }.start()
        }
    }

    // 发送文件
    private fun sendFile(msg: Message) {
        var fileSocket: Socket? = null
        var fileInputStream: FileInputStream? = null
        var fileWriter: DataOutputStream? = null
        try {
            // 打开文件传输端口
            fileSocket = Socket(host, filePort)
            fileWriter = DataOutputStream(fileSocket.getOutputStream())

            // 本地文件流
            fileInputStream = FileInputStream(msg.file)

            if (msg.file != null) {
                // 0 表示想要向服务器发送文件
                fileWriter.writeInt(0)
                fileWriter.flush()
                // 写入文件名
                fileWriter.writeUTF(msg.file!!.name)
                fileWriter.flush()
                // 写入文件长度
                fileWriter.writeLong(msg.file!!.length())
                fileWriter.flush()

                // 以 1 kb 为单位进行传输
                val bytes = ByteArray(1024)
                var length = fileInputStream.read(bytes, 0, bytes.size)
                while (length != -1) {
                    fileWriter.write(bytes)
                    fileWriter.flush()
                    length = fileInputStream.read(bytes, 0, bytes.size)
                }
            } else println("玄学错误：消息中的文件为空")
        } catch (e: Exception) {
            e.printStackTrace()
            handler.showMessage("发生错误：${e.message}")
        } finally {
            // 关闭流和传输socket
            fileInputStream?.close()
            fileWriter?.close()
            fileSocket?.close()
        }

    }

    // 存储文件
    fun saveFile(name: String) {
        Thread {
            var fileSocket: Socket? = null
            var fout: FileOutputStream? = null
            try {
                // 打开文件传输端口
                fileSocket = Socket(host, filePort)
                val fileReader = DataInputStream(fileSocket.getInputStream())
                val fileWriter = DataOutputStream(fileSocket.getOutputStream())

                // 告诉服务器我们要接收文件
                fileWriter.writeInt(1)
                // 传入接收的文件信息
                fileWriter.writeUTF(name)

                handler.appendMessage("正在接收文件：$name\n")
                // 文件名字，这里不需要
                //val fileName = fileReader.readUTF()
                // 文件长度，如果计算接收百分比的话可能用到
                val fileLength = fileReader.readLong()
                // 完整存储路径
                val fullPath = path + name
                println("文件存储至$fullPath")
                // 文件输出流
                fout = FileOutputStream(File(fullPath))
                val bytes = ByteArray(1024)

                var length = fileReader.read(bytes, 0, bytes.size)
                // 这个地方其实有个小问题，不过问题不大
                while (length != -1) {
                    fout.write(bytes, 0, length)
                    fout.flush()
                    println("已接收：$length，共 $fileLength")
                    length = fileReader.read(bytes, 0, bytes.size)
                }
                println("文件$name 接收完成")
                handler.appendMessage("文件：$name 接收完成\n")
            } catch (e: Exception) {
                println(e.message)
            } finally {
                fout?.close()
                fileSocket?.close()
            }
        }.start()
    }



    // 传输消息
    private fun sendMessage(msg: Message) {
        writer.writeUTF(msg.message)
    }

    init {
        ChatReader(reader, handler, this).start()
        println("ChatClient加载完成")
    }

}

class ChatReader(var reader: DataInputStream, var handler: ChatClientHandler, var chatClient: ChatClient) : Thread() {

    override fun run() {
        try {
            var type = reader.readInt()
            while (type != -1) {
                when (type) {
                    0 -> {
                        // 收到普通消息
                        val message = reader.readUTF()
                        println("消息: $message")
                        handler.appendMessage("$message\n")
                    }
                    1 -> {
                        // 收到文件消息
                        println("接收到文件，开始存储")
                        chatClient.saveFile(reader.readUTF())
                    }
                }
                type = reader.readInt()
            }
            var message = reader.readUTF()
            while (message != null) {
                println("收到消息: $message")
                handler.appendMessage("$message\n")
                message = reader.readUTF()
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }


}
