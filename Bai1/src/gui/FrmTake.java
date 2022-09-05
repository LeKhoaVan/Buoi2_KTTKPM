package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.BasicConfigurator;


public class FrmTake extends JFrame{
	private String text;
	private JTextField txtManv;
	public FrmTake() {
		setTitle("Nhận thông tin");
		setSize(900, 600);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		buildUI();
	}
	
	public void buildUI() {
		JPanel jpanelTieude = new JPanel();
		JLabel lblManv = new JLabel("Thông tin nhận được");
		txtManv = new JTextField(72);
		jpanelTieude.add(lblManv);
		jpanelTieude.add(txtManv);
		add(jpanelTieude,BorderLayout.CENTER);
		try {
			String tam = queueReceiver();
			txtManv.setText(queueReceiver());
			txtManv.setEnabled(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	
	public String queueReceiver() throws Exception {
	//thiết lập môi trường cho JMS
			BasicConfigurator.configure();
	//thiết lập môi trường cho JJNDI
			Properties settings = new Properties();
			settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
	//tạo context
			Context ctx = new InitialContext(settings);
	//lookup JMS connection factory
			Object obj = ctx.lookup("ConnectionFactory");
			ConnectionFactory factory = (ConnectionFactory) obj;
	//lookup destination
			Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
	//tạo connection
			Connection con = factory.createConnection("admin", "admin");
	//nối đến MOM
			con.start();
	//tạo session
			Session session = con.createSession(/* transaction */false, /* ACK */Session.CLIENT_ACKNOWLEDGE);
	//tạo consumer
			MessageConsumer receiver = session.createConsumer(destination);
	//blocked-method for receiving message - sync
	//receiver.receive();
	//Cho receiver lắng nghe trên queue, chừng có message thì notify - async
			System.out.println("Ty was listened on queue...");
			receiver.setMessageListener(new MessageListener() {
				@Override
				// có message đến queue, phương thức này được thực thi
				public void onMessage(Message msg) {// msg là message nhận được
					try {
						if (msg instanceof TextMessage) {
							TextMessage tm = (TextMessage) msg;
							String txt = tm.getText();
							text = tm.getText();
							System.out.println("Nhan Duoc " + txt);
							txtManv.setText(queueReceiver());
							msg.acknowledge();// gửi tín hiệu ack
						} else if (msg instanceof ObjectMessage) {
							ObjectMessage om = (ObjectMessage) msg;
							System.out.println(om);
						}
						// others message type....
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			return text;
		}
	
}
