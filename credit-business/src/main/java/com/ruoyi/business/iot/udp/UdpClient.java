package com.ruoyi.business.iot.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.Arrays;
import java.io.IOException;
public class UdpClient {

    // 服务器地址和端口配置
    private static final String SERVER_IP = "121.43.179.245"; // 目标服务器IP，请根据您的实际服务器地址修改
    private static final int SERVER_PORT = 9990;       // 目标服务器端口，请根据您的实际端口修改
    private static final int LISTEN_PORT = 10000;       // 本地监听端口，用于接收服务器回传数据

    public static void main(String[] args) {
        try {
            // 1. 创建 DatagramSocket，绑定到本地端口
            // 客户端需要绑定一个本地端口来接收服务器的回复
            DatagramSocket socket = new DatagramSocket(LISTEN_PORT);
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            System.out.printf("UDP 客户端已启动，监听本地端口: %d\n", LISTEN_PORT);
            System.out.printf("目标服务器地址: %s:%d\n", SERVER_IP, SERVER_PORT);

            // 2. 启动数据接收线程
            Thread receiverThread = new Thread(new UdpReceiver(socket));
            receiverThread.start();

            // 3. 主线程负责发送数据
            Scanner scanner = new Scanner(System.in);
            System.out.println("请开始输入要发送给服务器的消息 (输入 'quit' 退出):");

            while (true) {
                String input = scanner.nextLine();
                if ("quit".equalsIgnoreCase(input)) {
                    break;
                }

                // 示例：将用户输入的文本转换为字节数组
                byte[] sendData = input.getBytes("UTF-8");

                // 封装 DatagramPacket，包含数据、长度、目标地址和端口
                DatagramPacket sendPacket = new DatagramPacket(
                        sendData,
                        sendData.length,
                        serverAddress,
                        SERVER_PORT
                );

                // 发送数据包
                socket.send(sendPacket);
                System.out.println("-> 已发送数据: " + input);
            }

            // 停止接收线程并关闭 socket
            System.out.println("客户端正在退出...");
            receiverThread.interrupt();
            socket.close();
            scanner.close();

        } catch (IOException e) {
            System.err.println("网络操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 内部类：专门用于持续接收服务器数据的线程
     */
    private static class UdpReceiver implements Runnable {
        private final DatagramSocket socket;
        private final int MAX_PACKET_SIZE = 1024; // 最大接收数据包大小

        public UdpReceiver(DatagramSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                byte[] receiveBuffer = new byte[MAX_PACKET_SIZE];

                while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                    // 阻塞等待接收数据
                    socket.receive(receivePacket);

                    // 提取实际接收到的数据
                    byte[] receivedBytes = Arrays.copyOf(
                            receivePacket.getData(),
                            receivePacket.getLength()
                    );

                    // 将字节数据转换为字符串并打印 (如果服务器回复的是文本)
                    String receivedMessage = new String(receivedBytes, "UTF-8").trim();

                    String senderInfo = receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort();

                    System.out.println("\n<- 收到回复 [" + senderInfo + "]: " + receivedMessage);
                    System.out.print("请开始输入要发送给服务器的消息 (输入 'quit' 退出):\n"); // 重新打印提示
                }
            } catch (IOException e) {
                // 忽略 socket 关闭导致的异常，线程正常退出
                if (!socket.isClosed() && !(e instanceof java.net.SocketException && e.getMessage().contains("Socket closed"))) {
                    System.err.println("数据接收异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
