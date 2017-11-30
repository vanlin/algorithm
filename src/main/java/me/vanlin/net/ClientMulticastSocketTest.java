package me.vanlin.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class ClientMulticastSocketTest {
    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        MulticastSocket multicastSocket = new MulticastSocket(1234); // 接收数据时需要指定监听的端口号
        InetAddress address = InetAddress.getByName("224.5.6.7");
        multicastSocket.joinGroup(address);
        byte[] buf = new byte[1024];

        while (true) {
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
            multicastSocket.receive(datagramPacket); // 接收数据，同样会进入阻塞状态
            byte[] message = new byte[datagramPacket.getLength()]; // 从buffer中截取收到的数据
            System.arraycopy(buf, 0, message, 0, datagramPacket.getLength());
            System.out.println(datagramPacket.getAddress());
            System.out.println(new String(message));
        }
    }
}